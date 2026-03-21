package edu.eci.dosw.core.validator;

import edu.eci.dosw.core.util.ValidationUtil;
import org.springframework.stereotype.Component;

@Component
public class LoanValidator {
    public void validate(String bookId, String userId) {
        if (ValidationUtil.isNullOrEmpty(bookId))
            throw new IllegalArgumentException("El id del libro no puede estar vacío");
        if (ValidationUtil.isNullOrEmpty(userId))
            throw new IllegalArgumentException("El id del usuario no puede estar vacío");
    }
}
