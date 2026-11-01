package com.phago.meal;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class MealEntry {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDate entryDate;
  private String mealType;
  private String productName;
  private double grams;
  private double calories;
  private double protein;
  private double carbs;
  private double fat;
  private Double sugar;
  private Double fiber;

  protected MealEntry() {}

  public MealEntry(
      LocalDate entryDate,
      String mealType,
      String productName,
      double grams,
      double calories,
      double protein,
      double carbs,
      double fat,
      double sugar,
      double fiber) {
    this.entryDate = entryDate;
    this.mealType = mealType;
    this.productName = productName;
    this.grams = grams;
    this.calories = calories;
    this.protein = protein;
    this.carbs = carbs;
    this.fat = fat;
    this.sugar = sugar;
    this.fiber = fiber;
  }

  public Long getId() {
    return id;
  }

  public LocalDate getEntryDate() {
    return entryDate;
  }

  public String getMealType() {
    return mealType;
  }

  public String getProductName() {
    return productName;
  }

  public double getGrams() {
    return grams;
  }

  public double getCalories() {
    return calories;
  }

  public double getProtein() {
    return protein;
  }

  public double getCarbs() {
    return carbs;
  }

  public double getFat() {
    return fat;
  }

  public double getSugar() {
    return sugar == null ? 0 : sugar;
  }

  public double getFiber() {
    return fiber == null ? 0 : fiber;
  }
}
