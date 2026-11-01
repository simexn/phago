package com.phago.product;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "open-food-facts")
public record OpenFoodFactsProperties(String username, String password) {}
