package ru.nateemaru.polygon.dto.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Sort;
import ru.nateemaru.polygon.service.user.UserSortField;

public record SortRequest(
        @NotNull UserSortField field,
        @NotNull Sort.Direction direction) {
}
