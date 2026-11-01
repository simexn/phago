package com.phago.product;

import jakarta.annotation.PostConstruct;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {
  private final RestClient client;
  private final OpenFoodFactsProperties properties;

  ProductController(OpenFoodFactsProperties properties) {
    this.properties = properties;
    CookieManager cookies = new CookieManager();
    cookies.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
    HttpClient http = HttpClient.newBuilder().cookieHandler(cookies).build();
    this.client =
        RestClient.builder()
            .baseUrl("https://world.openfoodfacts.org")
            .requestFactory(new JdkClientHttpRequestFactory(http))
            .defaultHeader("User-Agent", "Phago/1.0 (university calorie tracker)")
            .build();
  }

  @PostConstruct
  void login() {
    String body =
        "action=process&user_id="
            + encode(properties.username())
            + "&password="
            + encode(properties.password());
    client
        .post()
        .uri("/cgi/session.pl")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(body)
        .retrieve()
        .toBodilessEntity();
  }

  private String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }

  @GetMapping("/search")
  public List<Product> search(@RequestParam String q) {
    JsonNode root =
        client
            .get()
            .uri(uri ->
                uri.path("/cgi/search.pl")
                    .queryParam("search_terms", q)
                    .queryParam("search_simple", 1)
                    .queryParam("action", "process")
                    .queryParam("json", 1)
                    .queryParam("page_size", 20)
                    .queryParam("fields", "product_name,brands,nutriments")
                    .build())
            .accept(MediaType.APPLICATION_JSON)
            .exchange((request, response) ->
                response.getStatusCode().is2xxSuccessful()
                    ? response.bodyTo(JsonNode.class)
                    : null);
    List<Product> result = new ArrayList<>();
    if (root != null)
      for (JsonNode item : root.path("products")) {
        JsonNode n = item.path("nutriments");
        String name = item.path("product_name").asText();
        String brand = item.path("brands").asText();
        String haystack = (name + " " + brand).toLowerCase();
        boolean matches =
            java.util.Arrays.stream(q.toLowerCase().trim().split("\\s+"))
                .allMatch(haystack::contains);
        if (!name.isBlank() && matches)
          result.add(
              new Product(
                  name,
                  brand,
                  value(n, "energy-kcal_100g"),
                  value(n, "proteins_100g"),
                  value(n, "carbohydrates_100g"),
                  value(n, "fat_100g"),
                  value(n, "sugars_100g"),
                  value(n, "fiber_100g")));
      }
    return result;
  }

  private double value(JsonNode n, String key) {
    return n.path(key).asDouble(0);
  }

  public record Product(
      String name,
      String brand,
      double calories,
      double protein,
      double carbs,
      double fat,
      double sugar,
      double fiber) {}
}
