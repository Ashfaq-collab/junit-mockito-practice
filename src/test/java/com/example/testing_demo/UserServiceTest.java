package com.example.testing_demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

//    @Test
//    void shouldReturnUserName() {
//
//        User user = new User(1, "Rahul");
//
//        when(userRepository.findById(1))
//                .thenReturn(Optional.of(user));
//
//        String result = userService.getUserName(1);
//
//        assertEquals("Rahul", result);
//    }
    @Test
    void shouldReturnUserNotFound() {

        when(userRepository.findById(99))
                .thenReturn(Optional.empty());

        String result = userService.getUserName(99);

        assertEquals("User not found", result);
    }
    @Test
    void shouldCallRepositoryToFindUser() {

        User user = new User(1, "Rahul");

        when(userRepository.findById(1))
                .thenReturn(Optional.of(user));

        userService.getUserName(1);

        verify(userRepository).findById(1);
    }
    @Test
    void shouldCallRepositoryWhenUserDoesNotExist() {

        when(userRepository.findById(99))
                .thenReturn(Optional.empty());

        userService.getUserName(99);

        verify(userRepository).findById(99);
    }
    @Test
    void shouldReturnUserNameInUppercase() {

        User user = new User(1, "Rahul");

        when(userRepository.findById(1))
                .thenReturn(Optional.of(user));

        String result = userService.getUserName(1);

        assertEquals("RAHUL", result);
        verify(userRepository).findById(1);
    }
    @Test
    void shouldSaveNewUser() {

        User user = new User(0, "Rahul");

        when(userRepository.save(user))
                .thenReturn(user);

        User result = userService.createUser("Rahul");

        assertEquals("Rahul", result.getName());
        verify(userRepository).save(user);
    }
}
