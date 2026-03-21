package edu.eci.dosw.controller.mapper;

import edu.eci.dosw.controller.dto.LoanDTO;
import edu.eci.dosw.core.model.Loan;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {
    public LoanDTO toDTO(Loan loan) {
        LoanDTO dto = new LoanDTO();
        dto.setBookId(loan.getBook().getId());
        dto.setUserId(loan.getUser().getId());
        return dto;
    }
}
