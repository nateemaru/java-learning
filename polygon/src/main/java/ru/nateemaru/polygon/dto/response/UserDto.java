package ru.nateemaru.polygon.dto.response;

import ru.nateemaru.polygon.entity.Order;

import java.time.Instant;
import java.util.List;

public record UserDto(
        Long id,
        String name,
        String email,
        Instant createdAt,
        List<Order> orders) {
}
