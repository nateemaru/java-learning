package ru.nateemaru.polygon.mapping;

import org.mapstruct.Mapper;
import ru.nateemaru.polygon.dto.request.CreateBookRequest;
import ru.nateemaru.polygon.dto.response.BookDto;
import ru.nateemaru.polygon.entity.Book;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface BookMapper {
    BookDto toDto(Book entity);
    Book toEntity(BookDto dto);
    Book toEntity(CreateBookRequest request);

    List<BookDto> toDto(List<Book> entities);
    List<Book> toEntity(List<BookDto> dtoList);
}
