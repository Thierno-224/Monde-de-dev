package com.openclassrooms.mddapi.user;

import com.openclassrooms.mddapi.common.exception.BadRequestException;
import com.openclassrooms.mddapi.common.exception.ResourceConflictException;
import com.openclassrooms.mddapi.user.dto.UpdateProfileRequest;
import com.openclassrooms.mddapi.user.dto.UserResponse;
import com.openclassrooms.mddapi.user.entity.User;
import com.openclassrooms.mddapi.user.repository.UserRepository;

import com.openclassrooms.mddapi.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        when(authentication.getName()).thenReturn("1");
    }

    @Test
    void shouldRejectUnknownUser() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        UpdateProfileRequest request = new UpdateProfileRequest(
                "username", "user@test.com", null
        );

        assertThrows(
                BadRequestException.class,
                () -> userService.updateProfile(authentication, request)
        );

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldRejectDuplicateEmail() {
        User user = mock(User.class);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmailAndIdNot(
                "existing@test.com", 1L
        )).thenReturn(true);

        UpdateProfileRequest request = new UpdateProfileRequest(
                "username", "existing@test.com", null
        );

        assertThrows(
                ResourceConflictException.class,
                () -> userService.updateProfile(authentication, request)
        );

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldRejectDuplicateUsername() {
        User user = mock(User.class);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByUsernameAndIdNot(
                "existing_username", 1L
        )).thenReturn(true);

        UpdateProfileRequest request = new UpdateProfileRequest(
                "existing_username", "user@test.com", null
        );

        assertThrows(
                ResourceConflictException.class,
                () -> userService.updateProfile(authentication, request)
        );

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldUpdateProfileAndEncodePassword() {
        User user = mock(User.class);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("NewPassword@123"))
                .thenReturn("encodedPassword");

        UpdateProfileRequest request = new UpdateProfileRequest(
                " new_username ",
                " new@test.com ",
                "NewPassword@123"
        );

        UserResponse response =
                userService.updateProfile(authentication, request);

        verify(user).setUsername("new_username");
        verify(user).setEmail("new@test.com");
        verify(user).setPassword("encodedPassword");

        verify(passwordEncoder).encode("NewPassword@123");
        assertNotNull(response);
    }

    @Test
    void shouldKeepPasswordWhenNull() {
        User user = mock(User.class);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UpdateProfileRequest request = new UpdateProfileRequest(
                "username", "user@test.com", null
        );

        userService.updateProfile(authentication, request);

        verify(user, never()).setPassword(anyString());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldKeepPasswordWhenBlank() {
        User user = mock(User.class);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UpdateProfileRequest request = new UpdateProfileRequest(
                "username", "user@test.com", "   "
        );

        userService.updateProfile(authentication, request);

        verify(user, never()).setPassword(anyString());
        verifyNoInteractions(passwordEncoder);
    }
}
