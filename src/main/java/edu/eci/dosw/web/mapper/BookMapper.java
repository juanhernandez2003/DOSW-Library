package edu.eci.dosw.persistence.mapper;

import edu.eci.dosw.controller.dto.response.BookResponse;
import edu.eci.dosw.persistence.entity.Book;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookResponse toResponse(Book book);

    List<BookResponse> toResponseList(List<Book> books);
}
