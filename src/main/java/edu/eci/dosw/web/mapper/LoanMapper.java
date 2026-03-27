package edu.eci.dosw.persistence.mapper;

import edu.eci.dosw.controller.dto.response.LoanResponse;
import edu.eci.dosw.persistence.entity.Loan;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    LoanResponse toResponse(Loan loan);

    List<LoanResponse> toResponseList(List<Loan> loans);
}
