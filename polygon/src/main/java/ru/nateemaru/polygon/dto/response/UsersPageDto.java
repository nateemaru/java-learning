package ru.nateemaru.polygon.dto.response;

import com.fasterxml.jackson.annotation.JsonView;

import java.util.List;

public record UsersPageDto(
        @JsonView(UserViews.Summary.class)
        List<UserDto> users,
        @JsonView(UserViews.Summary.class)
        int page,
        @JsonView(UserViews.Summary.class)
        int size,
        @JsonView(UserViews.Summary.class)
        long totalElements,
        @JsonView(UserViews.Summary.class)
        int totalPages,
        @JsonView(UserViews.Summary.class)
        boolean first,
        @JsonView(UserViews.Summary.class)
        boolean last) {
}
