package com.phago.water;

import com.phago.profile.ProfileService;
import java.time.LocalDate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/water")
public class WaterController {
  private final DailyWaterRepository repository;
  private final ProfileService profileService;

  WaterController(DailyWaterRepository repository, ProfileService profileService) {
    this.repository = repository;
    this.profileService = profileService;
  }

  @GetMapping
  public WaterSummary get(@RequestParam LocalDate date) {
    return summary(date);
  }

  @PostMapping
  public WaterSummary add(@RequestBody WaterChange request) {
    DailyWater water =
        repository.findById(request.date()).orElseGet(() -> new DailyWater(request.date(), 0));
    water.add(request.milliliters());
    repository.save(water);
    return summary(request.date());
  }

  private WaterSummary summary(LocalDate date) {
    double current = repository.findById(date).map(DailyWater::getMilliliters).orElse(0d);
    return new WaterSummary(current, profileService.waterTarget());
  }

  public record WaterChange(LocalDate date, double milliliters) {}

  public record WaterSummary(double milliliters, double target) {}
}
