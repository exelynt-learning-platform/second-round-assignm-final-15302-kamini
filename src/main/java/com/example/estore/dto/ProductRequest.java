package com.example.estore.dto;

import com.example.estore.enums.Category;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull
    @Positive
    private Double price;

    private String description;
    private String image;

    @Min(0)
    private int stock;

    @NotNull
    private Category category;

}
