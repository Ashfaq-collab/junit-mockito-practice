package com.example.testing_demo;

import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getUserName(int id) {

        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            return user.get().getName().toUpperCase();
        }

        return "User not found";
    }
    public User createUser(String name) {
        User user = new User(0, name);
        return userRepository.save(user);
    }
}
