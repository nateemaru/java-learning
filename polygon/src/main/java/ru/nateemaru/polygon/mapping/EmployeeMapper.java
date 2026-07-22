package ru.nateemaru.polygon.mapping;

import org.mapstruct.Mapper;
import ru.nateemaru.polygon.dto.projection.EmployeeProjection;
import ru.nateemaru.polygon.dto.response.EmployeeDto;
import ru.nateemaru.polygon.dto.response.EmployeePreviewDto;
import ru.nateemaru.polygon.entity.Employee;

@Mapper(config = MapStructConfig.class)
public interface EmployeeMapper {
    EmployeePreviewDto toDto(EmployeeProjection projection);
    EmployeeDto toDto(Employee entity);
}
