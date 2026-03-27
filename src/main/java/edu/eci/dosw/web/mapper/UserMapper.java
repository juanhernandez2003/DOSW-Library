package edu.eci.dosw.persistence.mapper;

import edu.eci.dosw.controller.dto.response.UserResponse;
import edu.eci.dosw.persistence.entity.LibraryUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(LibraryUser user);
}
