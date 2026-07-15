package ru.nateemaru.polygon.service.user;

import org.springframework.data.domain.Pageable;
import ru.nateemaru.polygon.dto.response.UserDto;
import ru.nateemaru.polygon.dto.response.UsersDto;

public interface UserService {
    UserDto create();
    void delete(Long id);
    UserDto update(UserDto user);
    UserDto getWithOrders(Long id);
    UsersDto getPage(Pageable pageable);
}
