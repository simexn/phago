package com.phago.dashboard;

import com.phago.meal.*;
import com.phago.profile.ProfileService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {
  private final MealEntryRepository repository;
  private final ProfileService profileService;

  DashboardController(MealEntryRepository repository, ProfileService profileService) {
    this.repository = repository;
    this.profileService = profileService;
  }

  @GetMapping
  public Summary summary(@RequestParam LocalDate date) {
    List<MealEntry> meals = repository.findByEntryDateOrderByIdDesc(date);
    ProfileService.Goals goals = profileService.goals();
    double sugarTarget = Math.round(goals.calories() * .1 / 4);
    double fiberTarget = Math.round(goals.calories() * .014);
    return new Summary(
        goals.calories(),
        goals.protein(),
        goals.carbs(),
        goals.fat(),
        sugarTarget,
        fiberTarget,
        sum(meals, MealEntry::getCalories),
        sum(meals, MealEntry::getProtein),
        sum(meals, MealEntry::getCarbs),
        sum(meals, MealEntry::getFat),
        sum(meals, MealEntry::getSugar),
        sum(meals, MealEntry::getFiber));
  }

  private double sum(List<MealEntry> list, java.util.function.ToDoubleFunction<MealEntry> value) {
    return list.stream().mapToDouble(value).sum();
  }

  public record Summary(
      double target,
      double proteinTarget,
      double carbsTarget,
      double fatTarget,
      double sugarTarget,
      double fiberTarget,
      double calories,
      double protein,
      double carbs,
      double fat,
      double sugar,
      double fiber) {}
}
