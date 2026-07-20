package ru.nateemaru.polygon.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateBookRequest(
        @NotBlank
        String title,
        @NotBlank
        String author,
        @NotNull
        @Positive
        Integer publicationYear) {
}
