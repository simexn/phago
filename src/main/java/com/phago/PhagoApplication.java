package com.phago;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PhagoApplication {

  public static void main(String[] args) {
    SpringApplication.run(PhagoApplication.class, args);
  }
}
