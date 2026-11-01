package com.phago.profile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
  private final ProfileService service;

  ProfileController(ProfileService service) {
    this.service = service;
  }

  @GetMapping
  public ProfileResponse get() {
    return response(service.getProfile(), service.goals());
  }

  @PutMapping
  public ProfileResponse save(@Valid @RequestBody ProfileRequest request) {
    UserProfile profile =
        service.save(
            request.age(),
            request.weightKg(),
            request.heightCm(),
            request.sex(),
            request.activityLevel(),
            request.goal());
    return response(profile, service.goals());
  }

  private ProfileResponse response(UserProfile profile, ProfileService.Goals goals) {
    return new ProfileResponse(
        profile == null ? null : profile.getAge(),
        profile == null ? null : profile.getWeightKg(),
        profile == null ? null : profile.getHeightCm(),
        profile == null ? null : profile.getSex(),
        profile == null ? null : profile.getActivityLevel(),
        profile == null ? null : profile.getGoal(),
        goals);
  }

  public record ProfileRequest(
      @Min(14) @Max(100) int age,
      @Min(30) @Max(400) double weightKg,
      @Min(120) @Max(240) double heightCm,
      @NotBlank String sex,
      @NotBlank String activityLevel,
      @NotBlank String goal) {}

  public record ProfileResponse(
      Integer age,
      Double weightKg,
      Double heightCm,
      String sex,
      String activityLevel,
      String goal,
      ProfileService.Goals goals) {}
}
