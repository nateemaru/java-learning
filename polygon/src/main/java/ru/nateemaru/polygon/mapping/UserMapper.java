package ru.nateemaru.polygon.mapping;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import ru.nateemaru.polygon.dto.response.UserDto;
import ru.nateemaru.polygon.dto.response.UsersPageDto;
import ru.nateemaru.polygon.entity.User;

import java.util.List;

@Mapper(config = MapStructConfig.class,
        uses = {OrderMapper.class})
public interface UserMapper {
    UserDto toDto(User entity);
    List<UserDto> toDto(List<User> entities);

    UsersPageDto toDtoPage(Page<User> page);
}
