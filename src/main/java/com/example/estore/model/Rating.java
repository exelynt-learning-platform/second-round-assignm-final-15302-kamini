package com.example.estore.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.*;

import lombok.Data;

@Embeddable
@Data
public class Rating {

    @Min(value = 0, message = "Rating cannot be less than 0")
    @Max(value = 5, message = "Rating cannot be more than 5")
    private double rate;

    @Min(value = 0, message = "Count cannot be negative")
    private int count;
}