package com.example.manage.service.user;

import com.example.manage.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAll();
    Optional<User> getByEmail(String email);
}
