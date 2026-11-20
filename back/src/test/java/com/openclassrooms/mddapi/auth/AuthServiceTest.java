package com.openclassrooms.mddapi.auth;

import com.openclassrooms.mddapi.auth.dto.AuthResponse;
import com.openclassrooms.mddapi.auth.dto.LoginRequest;
import com.openclassrooms.mddapi.auth.service.AuthService;
import com.openclassrooms.mddapi.common.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.security.jwt.JwtService;
import com.openclassrooms.mddapi.user.dto.UserResponse;
import com.openclassrooms.mddapi.user.entity.User;
import com.openclassrooms.mddapi.user.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRejectUnknownUser() {
        LoginRequest request = new LoginRequest(
                "unknown@test.com",
                "Test@123"
        );

        when(userRepository.findByEmailOrUsername(
                request.identifier(),
                request.identifier()
        )).thenReturn(Optional.empty());

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verifyNoInteractions(passwordEncoder, jwtService);
    }

    @Test
    void shouldRejectInvalidPassword() {
        LoginRequest request = new LoginRequest(
                "user@test.com",
                "wrongPassword"
        );

        User user = mock(User.class);

        when(userRepository.findByEmailOrUsername(
                request.identifier(),
                request.identifier()
        )).thenReturn(Optional.of(user));

        when(user.getPassword()).thenReturn("encodedPassword");

        when(passwordEncoder.matches(
                request.password(),
                "encodedPassword"
        )).thenReturn(false);

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldGenerateTokenForValidCredentials() {
        LoginRequest request = new LoginRequest(
                "user@test.com",
                "Test@123"
        );

        User user = mock(User.class);

        when(userRepository.findByEmailOrUsername(
                request.identifier(),
                request.identifier()
        )).thenReturn(Optional.of(user));

        when(user.getPassword()).thenReturn("encodedPassword");
        when(user.getId()).thenReturn(1L);

        when(passwordEncoder.matches(
                request.password(),
                "encodedPassword"
        )).thenReturn(true);

        when(jwtService.generateToken(1L))
                .thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertEquals("jwt-token", response.token());

        verify(jwtService).generateToken(1L);
    }

    @Test
    void shouldRejectNullAuthentication() {
        assertThrows(
                AuthenticationCredentialsNotFoundException.class,
                () -> authService.getCurrentUser(null)
        );

        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldRejectUnauthenticatedUser() {
        Authentication authentication = mock(Authentication.class);

        when(authentication.isAuthenticated()).thenReturn(false);

        assertThrows(
                AuthenticationCredentialsNotFoundException.class,
                () -> authService.getCurrentUser(authentication)
        );

        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldRejectAuthenticatedUserNotFound() {
        Authentication authentication = mock(Authentication.class);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("1");

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> authService.getCurrentUser(authentication)
        );

        verify(userRepository).findById(1L);
    }

    @Test
    void shouldReturnCurrentAuthenticatedUser() {
        Authentication authentication = mock(Authentication.class);
        User user = mock(User.class);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("1");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(user.getId()).thenReturn(1L);
        when(user.getUsername()).thenReturn("thierno");
        when(user.getEmail()).thenReturn("thierno@example.com");

        UserResponse response = authService.getCurrentUser(authentication);

        assertAll(
                () -> assertEquals(1L, response.id()),
                () -> assertEquals("thierno", response.username()),
                () -> assertEquals("thierno@example.com", response.email())
        );

        verify(userRepository).findById(1L);
    }
}
