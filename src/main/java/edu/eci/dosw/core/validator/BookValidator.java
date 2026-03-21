package edu.eci.dosw.core.validator;

import edu.eci.dosw.core.util.ValidationUtil;
import org.springframework.stereotype.Component;

@Component
public class BookValidator {
    public void validate(String title, String author, int copies) {
        if (ValidationUtil.isNullOrEmpty(title))
            throw new IllegalArgumentException("El título del libro no puede estar vacío");
        if (ValidationUtil.isNullOrEmpty(author))
            throw new IllegalArgumentException("El autor del libro no puede estar vacío");
        if (copies < 0)
            throw new IllegalArgumentException("La cantidad de ejemplares no puede ser negativa");
    }
}
