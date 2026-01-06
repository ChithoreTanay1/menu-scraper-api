package com.taskflow.menuscraper.service;

import com.taskflow.menuscraper.dto.MenuItemRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.Currency;

@Service
@Validated
public class ValidationService {

    public void validateMenuItem(@Valid MenuItemRequest request) {
        // Additional business validation
        if (request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be non-negative");
        }

        if (request.getPrice().scale() > 2) {
            throw new IllegalArgumentException("Price must have at most 2 decimal places");
        }

        // Validate currency format - use ISO currency validation only
        String currency = request.getCurrency();
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code is required");
        }

        String normalizedCurrency = currency.toUpperCase().trim();
        if (!isValidCurrency(normalizedCurrency)) {
            throw new IllegalArgumentException("Invalid currency code: " + normalizedCurrency);
        }
        // Note: Currency normalization should be handled by the service layer, not here
    }

    private boolean isValidCurrency(String currencyCode) {
        if (currencyCode == null || currencyCode.length() != 3) {
            return false;
        }

        try {
            // Validate using ISO 4217 currency codes
            Currency.getInstance(currencyCode);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}