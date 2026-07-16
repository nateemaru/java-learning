package ru.nateemaru.polygon.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nateemaru.polygon.dto.response.UserDto;
import ru.nateemaru.polygon.dto.response.UsersPageDto;
import ru.nateemaru.polygon.entity.User;
import ru.nateemaru.polygon.exception.UserNotFoundException;
import ru.nateemaru.polygon.mapping.UserMapper;
import ru.nateemaru.polygon.repository.UserRepository;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    @Transactional
    public UserDto create(String name, String email) {
        Instant now = Instant.now();
        User user = new User();
        user.setName(name);
        user.setEmail(email.toLowerCase());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return mapper.toDto(repository.save(user));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public UserDto update(Long id, String name, String email) {
        User persistedUser = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        persistedUser.setName(name);
        persistedUser.setEmail(email);
        persistedUser.setUpdatedAt(Instant.now());
        return mapper.toDto(persistedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getWithOrders(Long id) {
        User persistedUser = repository.findWithOrdersById(id).orElseThrow(() -> new UserNotFoundException(id));
        return mapper.toDto(persistedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UsersPageDto getPage(Pageable pageable) {
        Page<User> page = repository.findAll(pageable);
        return new UsersPageDto(
                mapper.toDto(page.getContent()),
                pageable.getPageNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }
}
