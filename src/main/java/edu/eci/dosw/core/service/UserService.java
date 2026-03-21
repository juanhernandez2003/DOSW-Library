package edu.eci.dosw.core.service;

import edu.eci.dosw.core.exception.UserNotFoundException;
import edu.eci.dosw.core.model.User;
import edu.eci.dosw.core.util.IdGeneratorUtil;
import edu.eci.dosw.core.validator.UserValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final List<User> users = new ArrayList<>();
    private final UserValidator userValidator;

    public UserService(UserValidator userValidator) {
        this.userValidator = userValidator;
    }

    public User registerUser(String name) {
        userValidator.validate(name);
        User user = new User(IdGeneratorUtil.generate(), name);
        users.add(user);
        return user;
    }

    public List<User> getAllUsers() {
        return users;
    }

    public User getUserById(String id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}