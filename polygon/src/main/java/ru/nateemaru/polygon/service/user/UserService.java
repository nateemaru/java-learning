package ru.nateemaru.polygon.service.user;

import org.springframework.data.domain.Pageable;
import ru.nateemaru.polygon.dto.response.UserDto;
import ru.nateemaru.polygon.dto.response.UsersPageDto;

public interface UserService {
    UserDto create(String name, String email);
    void delete(Long id);
    UserDto update(Long id, String name, String email);
    UserDto getWithOrders(Long id);
    UsersPageDto getPage(Pageable pageable);
}
