package com.phago.meal;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealEntryRepository extends JpaRepository<MealEntry, Long> {
  List<MealEntry> findByEntryDateOrderByIdDesc(LocalDate entryDate);
}
