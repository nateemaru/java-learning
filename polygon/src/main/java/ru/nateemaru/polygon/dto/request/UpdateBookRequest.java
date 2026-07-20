package ru.nateemaru.polygon.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

public record UpdateBookRequest(
        @Nullable
        @Size(min = 1, max = 255)
        @Pattern(regexp = ".*\\S.*")
        String title,
        @Nullable
        @Size(min = 1, max = 255)
        @Pattern(regexp = ".*\\S.*")
        String author,
        @Nullable
        @Positive
        Integer publicationYear) {
}
