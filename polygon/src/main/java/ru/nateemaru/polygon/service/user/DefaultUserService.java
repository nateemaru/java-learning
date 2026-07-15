package ru.nateemaru.polygon.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.nateemaru.polygon.dto.response.UserDto;
import ru.nateemaru.polygon.dto.response.UsersDto;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {
    @Override
    public UserDto create() {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public UserDto update(UserDto user) {
        return null;
    }

    @Override
    public UserDto getWithOrders(Long id) {
        return null;
    }

    @Override
    public UsersDto getPage(Pageable pageable) {
        return null;
    }
}
