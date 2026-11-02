package com.phago.ai;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AiController {
  private final AiService aiService;

  AiController(AiService aiService) {
    this.aiService = aiService;
  }

  @PostMapping("/chat")
  public CompletableFuture<ChatResponse> chat(@RequestBody ChatRequest request) {
    return aiService
        .answer(request.message(), request.date(), request.history())
        .thenApply(ChatResponse::new);
  }

  public record ChatRequest(String message, LocalDate date, List<ChatMessage> history) {}

  public record ChatResponse(String answer) {}
}
