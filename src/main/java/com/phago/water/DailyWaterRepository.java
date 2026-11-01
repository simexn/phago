package com.phago.water;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyWaterRepository extends JpaRepository<DailyWater, LocalDate> {}
