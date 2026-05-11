package com.clist.domain.auth.service;

import com.clist.domain.auth.dto.AuthDto;
import com.clist.domain.user.entity.User;
import com.clist.domain.user.repository.UserRepository;
import com.clist.global.config.jwt.JwtProperties;
import com.clist.global.config.jwt.TokenProvider;
import com.clist.global.exception.CustomException;
import com.clist.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final JwtProperties jwtProperties;

    @Transactional
    public void signup(AuthDto.SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS.getStatus(), ErrorCode.EMAIL_ALREADY_EXISTS.getMessage());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public AuthDto.TokenResponse login(AuthDto.LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND.getStatus(), ErrorCode.USER_NOT_FOUND.getMessage()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD.getStatus(), ErrorCode.INVALID_PASSWORD.getMessage());
        }

        String accessToken = tokenProvider.generateToken(user, Duration.ofMinutes(jwtProperties.getAccessExpirationMinutes()));
        String refreshToken = tokenProvider.generateToken(user, Duration.ofDays(jwtProperties.getRefreshExpirationDays()));

        return new AuthDto.TokenResponse(accessToken, refreshToken);
    }
}