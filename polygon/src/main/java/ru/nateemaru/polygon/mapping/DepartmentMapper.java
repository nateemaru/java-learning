package ru.nateemaru.polygon.mapping;

import org.mapstruct.Mapper;
import ru.nateemaru.polygon.dto.response.DepartmentDto;
import ru.nateemaru.polygon.entity.Department;

@Mapper(config = MapStructConfig.class)
public interface DepartmentMapper {
    DepartmentDto toDto(Department entity);
    Department toEntity(DepartmentDto dto);
}
