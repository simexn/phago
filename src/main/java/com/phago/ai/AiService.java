package com.phago.ai;

import com.phago.meal.MealEntry;
import com.phago.meal.MealEntryRepository;
import com.phago.profile.ProfileService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

@Service
public class AiService {
  private final RestClient client;
  private final MealEntryRepository mealRepository;
  private final ProfileService profileService;

  AiService(
      OpenRouterProperties properties,
      MealEntryRepository mealRepository,
      ProfileService profileService) {
    this.mealRepository = mealRepository;
    this.profileService = profileService;
    this.client =
        RestClient.builder()
            .baseUrl("https://openrouter.ai/api/v1")
            .defaultHeader("Authorization", "Bearer " + properties.apiKey())
            .build();
  }

  public CompletableFuture<String> answer(
      String question, LocalDate date, List<ChatMessage> history) {
    return CompletableFuture.supplyAsync(() -> requestAnswer(question, date, history));
  }

  private String requestAnswer(String question, LocalDate date, List<ChatMessage> history) {
    List<MealEntry> meals = mealRepository.findByEntryDateOrderByIdDesc(date);
    ProfileService.Goals goals = profileService.goals();
    DailySummary summary = DailySummary.from(meals);

    List<Message> messages = new ArrayList<>();
    messages.add(new Message("system", systemPrompt()));
    messages.add(new Message("user", dailyContext(date, goals, summary, meals)));
    if (history != null) {
      history.stream()
          .filter(message -> message != null && isChatRole(message.role()))
          .filter(message -> message.text() != null && !message.text().isBlank())
          .limit(12)
          .forEach(message -> messages.add(new Message(message.role(), message.text().strip())));
    }
    messages.add(new Message("user", question.strip()));

    JsonNode response =
        client
            .post()
            .uri("/chat/completions")
            .body(
                new OpenRouterRequest(
                    "nvidia/nemotron-3.5-super-vl:free",
                    messages,
                    700,
                    new Reasoning(false)))
            .retrieve()
            .body(JsonNode.class);

    String answer =
        response.path("choices").path(0).path("message").path("content").asText().strip();
    if (answer.isBlank()) {
      String model = response.path("model").asText("unknown");
      String finishReason = response.path("choices").path(0).path("finish_reason").asText("unknown");
      String providerError = response.path("error").path("message").asText();
      throw new IllegalStateException(
          "The AI service did not return final text (model=%s, finish_reason=%s%s)."
              .formatted(
                  model,
                  finishReason,
                  providerError.isBlank() ? "" : ", error=" + providerError));
    }
    return answer;
  }

  private String systemPrompt() {
    return """
    You are Phago's nutrition assistant. Give short, practical food and meal-planning advice.
    Use only the provided daily context when making claims about the user's progress.
    Do not diagnose, treat conditions, prescribe diets, or make medical claims. If asked for medical advice,
    encourage the user to speak with a qualified healthcare professional.
    If the user writes in Bulgarian, including Bulgarian written with Latin letters, answer in Bulgarian using Cyrillic.
    Otherwise, answer in English unless the user explicitly requests another language.
    When writing in Bulgarian, address one person informally with "ти". Never use "Вие", "ви", "ние", or "нашите".
    For a simple greeting such as "здрасти", "zdrasti", "zdr", "hello", or "hi", reply with one short greeting
    and ask how you can help. Do not mention calorie targets, missing foods, or a meal plan unless the user asks.
    Answer only what the user asked. Do not add generic nutrition advice or a plan unless it is requested.
    Keep the answer under five concise sentences.
    """;
  }

  private boolean isChatRole(String role) {
    return "user".equals(role) || "assistant".equals(role);
  }

  private String dailyContext(
      LocalDate date, ProfileService.Goals goals, DailySummary summary, List<MealEntry> meals) {
    String foods =
        meals.isEmpty()
            ? "No foods logged."
            : meals.stream()
                .map(MealEntry::getProductName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    return """
    Date: %s
    Daily target: %.0f kcal, %.0fg protein, %.0fg carbs, %.0fg fat.
    Consumed: %.0f kcal, %.0fg protein, %.0fg carbs, %.0fg fat, %.0fg sugar, %.0fg fiber.
    Logged foods: %s

    """
        .formatted(
            date,
            goals.calories(),
            goals.protein(),
            goals.carbs(),
            goals.fat(),
            summary.calories(),
            summary.protein(),
            summary.carbs(),
            summary.fat(),
            summary.sugar(),
            summary.fiber(),
            foods);
  }

  private record OpenRouterRequest(
      String model, List<Message> messages, int max_tokens, Reasoning reasoning) {}

  private record Reasoning(boolean enabled) {}

  private record Message(String role, String content) {}

  private record DailySummary(
      double calories, double protein, double carbs, double fat, double sugar, double fiber) {
    static DailySummary from(List<MealEntry> meals) {
      return new DailySummary(
          sum(meals, MealEntry::getCalories),
          sum(meals, MealEntry::getProtein),
          sum(meals, MealEntry::getCarbs),
          sum(meals, MealEntry::getFat),
          sum(meals, MealEntry::getSugar),
          sum(meals, MealEntry::getFiber));
    }

    private static double sum(
        List<MealEntry> meals, java.util.function.ToDoubleFunction<MealEntry> value) {
      return meals.stream().mapToDouble(value).sum();
    }
  }
}
