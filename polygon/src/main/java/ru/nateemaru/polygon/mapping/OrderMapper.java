package ru.nateemaru.polygon.mapping;

import org.mapstruct.Mapper;
import ru.nateemaru.polygon.dto.response.OrderDto;
import ru.nateemaru.polygon.entity.Order;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface OrderMapper {
    OrderDto toDto(Order entity);
    List<OrderDto> toDto(List<OrderDto> entities);
}
