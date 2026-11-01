package com.phago.profile;

import org.springframework.stereotype.Service;

@Service
public class ProfileService {
  private static final Goals DEFAULT_GOALS = new Goals(2000, 120, 250, 65);
  private final UserProfileRepository repository;

  ProfileService(UserProfileRepository repository) {
    this.repository = repository;
  }

  public UserProfile save(
      int age, double weightKg, double heightCm, String sex, String activityLevel, String goal) {
    return repository.save(new UserProfile(age, weightKg, heightCm, sex, activityLevel, goal));
  }

  public UserProfile getProfile() {
    return repository.findById(1L).orElse(null);
  }

  public Goals goals() {
    UserProfile profile = getProfile();
    if (profile == null) return DEFAULT_GOALS;

    double bmr = 10 * profile.getWeightKg() + 6.25 * profile.getHeightCm() - 5 * profile.getAge();
    bmr += "FEMALE".equals(profile.getSex()) ? -161 : 5;
    double calories = bmr * activityMultiplier(profile.getActivityLevel());
    calories += goalAdjustment(profile.getGoal());
    calories = Math.max("FEMALE".equals(profile.getSex()) ? 1200 : 1500, calories);

    double protein = profile.getWeightKg() * 1.8;
    double fat = calories * .25 / 9;
    double carbs = Math.max(0, (calories - protein * 4 - fat * 9) / 4);
    return new Goals(round(calories), round(protein), round(carbs), round(fat));
  }

  private double round(double value) {
    return Math.round(value);
  }

  private double activityMultiplier(String activityLevel) {
    if ("LIGHT".equals(activityLevel)) return 1.375;
    if ("MODERATE".equals(activityLevel)) return 1.55;
    if ("ACTIVE".equals(activityLevel)) return 1.725;
    return 1.2;
  }

  private double goalAdjustment(String goal) {
    return switch (goal) {
      case "LOSE_025" -> -275;
      case "LOSE_050", "LOSE" -> -550;
      case "GAIN_025" -> 275;
      case "GAIN_050", "GAIN_075" -> 550;
      case "GAIN" -> 300;
      default -> 0;
    };
  }

  public double waterTarget() {
    UserProfile profile = getProfile();
    if (profile == null) return 2000;
    return Math.round(profile.getWeightKg() * 35 / 250d) * 250;
  }

  public record Goals(double calories, double protein, double carbs, double fat) {}
}
