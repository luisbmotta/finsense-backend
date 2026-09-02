package com.finsense.backend.auth;

import com.finsense.backend.auth.dto.AuthResponse;
import com.finsense.backend.auth.dto.LoginRequest;
import com.finsense.backend.auth.dto.RegisterRequest;
import com.finsense.backend.common.exception.EmailAlreadyInUseException;
import com.finsense.backend.common.exception.InvalidCredentialsException;
import com.finsense.backend.security.JwtService;
import com.finsense.backend.user.User;
import com.finsense.backend.user.UserRepository;
import com.finsense.backend.user.dto.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new EmailAlreadyInUseException(request.email());
        }

        User user = new User(
                request.name(),
                request.email().toLowerCase(),
                passwordEncoder.encode(request.password()),
                request.monthlyIncome()
        );

        user = userRepository.save(user);

        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(user.getId(), user.getEmail());
        return new AuthResponse(token, UserResponse.from(user));
    }
}
