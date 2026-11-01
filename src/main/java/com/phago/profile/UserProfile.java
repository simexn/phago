package com.phago.profile;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class UserProfile {
  @Id private Long id;
  private int age;
  private double weightKg;
  private double heightCm;
  private String sex;
  private String activityLevel;
  private String goal;

  protected UserProfile() {}

  public UserProfile(
      int age, double weightKg, double heightCm, String sex, String activityLevel, String goal) {
    this.id = 1L;
    this.age = age;
    this.weightKg = weightKg;
    this.heightCm = heightCm;
    this.sex = sex;
    this.activityLevel = activityLevel;
    this.goal = goal;
  }

  public Long getId() {
    return id;
  }

  public int getAge() {
    return age;
  }

  public double getWeightKg() {
    return weightKg;
  }

  public double getHeightCm() {
    return heightCm;
  }

  public String getSex() {
    return sex;
  }

  public String getActivityLevel() {
    return activityLevel;
  }

  public String getGoal() {
    return goal;
  }
}
