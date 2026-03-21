package edu.eci.dosw.core.validator;

import edu.eci.dosw.core.util.ValidationUtil;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {
    public void validate(String name) {
        if (ValidationUtil.isNullOrEmpty(name))
            throw new IllegalArgumentException("El nombre del usuario no puede estar vacío");
    }
}
