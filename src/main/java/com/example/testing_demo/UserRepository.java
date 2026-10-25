package com.example.testing_demo;

import java.util.Optional;

public class UserRepository {
    public Optional<User> findById(int id) {
        // Imagine this method fetches a user from a database.
        return Optional.empty();
    }
    public User save(User user) {
        // Placeholder for database saving
        return user;
    }
}
