package edu.eci.dosw.controller.mapper;

import edu.eci.dosw.controller.dto.UserDTO;
import edu.eci.dosw.core.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setName(user.getName());
        return dto;
    }
}
