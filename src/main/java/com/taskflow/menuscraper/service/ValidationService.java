package com.taskflow.menuscraper.service;

import com.taskflow.menuscraper.dto.MenuItemRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

@Service
@Validated
public class ValidationService {

    private static final Set<String> VALID_CURRENCIES = new HashSet<>();

    static {
        // Add common ISO currency codes
        VALID_CURRENCIES.add("USD");
        VALID_CURRENCIES.add("EUR");
        VALID_CURRENCIES.add("GBP");
        VALID_CURRENCIES.add("JPY");
        VALID_CURRENCIES.add("CAD");
        VALID_CURRENCIES.add("AUD");
        VALID_CURRENCIES.add("CHF");
        VALID_CURRENCIES.add("CNY");
        VALID_CURRENCIES.add("INR");
        VALID_CURRENCIES.add("BRL");
        VALID_CURRENCIES.add("MXN");
    }

    public void validateMenuItem(@Valid MenuItemRequest request) {
        // Additional business validation
        if (request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be non-negative");
        }

        if (request.getPrice().scale() > 2) {
            throw new IllegalArgumentException("Price must have at most 2 decimal places");
        }

        // Validate currency format
        String currency = request.getCurrency().toUpperCase();
        if (!isValidCurrency(currency)) {
            throw new IllegalArgumentException("Invalid currency code: " + currency);
        }
        request.setCurrency(currency);
    }

    private boolean isValidCurrency(String currencyCode) {
        if (currencyCode == null || currencyCode.length() != 3) {
            return false;
        }

        try {
            // Check if it's a valid ISO currency
            Currency.getInstance(currencyCode);
            return true;
        } catch (IllegalArgumentException e) {
            // Also check against our allowed list
            return VALID_CURRENCIES.contains(currencyCode);
        }
    }
}