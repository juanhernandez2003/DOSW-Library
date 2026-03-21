package edu.eci.dosw.controller.mapper;

import edu.eci.dosw.controller.dto.BookDTO;
import edu.eci.dosw.core.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {
    public BookDTO toDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        return dto;
    }
}
