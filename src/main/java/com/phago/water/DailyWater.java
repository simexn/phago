package com.phago.water;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDate;

@Entity
public class DailyWater {
  @Id private LocalDate entryDate;
  private double milliliters;

  protected DailyWater() {}

  public DailyWater(LocalDate entryDate, double milliliters) {
    this.entryDate = entryDate;
    this.milliliters = milliliters;
  }

  public double add(double amount) {
    milliliters = Math.max(0, milliliters + amount);
    return milliliters;
  }

  public double getMilliliters() {
    return milliliters;
  }
}
