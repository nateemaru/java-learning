package ru.nateemaru.polygon.dto.response;

import com.fasterxml.jackson.annotation.JsonView;
import ru.nateemaru.polygon.entity.Order;

import java.time.Instant;
import java.util.List;

public record UserDto(
        @JsonView(UserViews.Summary.class)
        Long id,
        @JsonView(UserViews.Summary.class)
        String name,
        @JsonView(UserViews.Summary.class)
        String email,
        @JsonView(UserViews.Summary.class)
        Instant createdAt,
        @JsonView(UserViews.Summary.class)
        Instant updatedAt,
        @JsonView(UserViews.Details.class)
        List<Order> orders) {
}
