package com.phago.meal;

import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meals")
@CrossOrigin
public class MealController {
  private final MealEntryRepository repository;

  MealController(MealEntryRepository repository) {
    this.repository = repository;
  }

  @GetMapping
  public List<MealEntry> list(@RequestParam LocalDate date) {
    return repository.findByEntryDateOrderByIdDesc(date);
  }

  @PostMapping
  public MealEntry add(@RequestBody CreateMeal request) {
    double factor = request.grams() / 100d;
    return repository.save(
        new MealEntry(
            request.date(),
            request.mealType(),
            request.productName(),
            request.grams(),
            request.caloriesPer100() * factor,
            request.proteinPer100() * factor,
            request.carbsPer100() * factor,
            request.fatPer100() * factor,
            request.sugarPer100() * factor,
            request.fiberPer100() * factor));
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    repository.deleteById(id);
  }

  public record CreateMeal(
      LocalDate date,
      String mealType,
      String productName,
      double grams,
      double caloriesPer100,
      double proteinPer100,
      double carbsPer100,
      double fatPer100,
      double sugarPer100,
      double fiberPer100) {}
}
