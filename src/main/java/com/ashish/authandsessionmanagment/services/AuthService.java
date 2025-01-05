package com.ashish.authandsessionmanagment.services;

import com.ashish.authandsessionmanagment.dto.SigninDto;
import com.ashish.authandsessionmanagment.dto.UserDto;
import com.ashish.authandsessionmanagment.entities.UserEntity;
import com.ashish.authandsessionmanagment.repositories.UserRepository;
import com.ashish.authandsessionmanagment.utlis.CookiesUtils;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CookiesUtils cookiesUtils;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final SessionService sessionService;
    private final UserRepository userRepository;


    public UserDto signingUser(SigninDto signInDto, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInDto.getEmail(), signInDto.getPassword()
                ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        if (!userEntity.getIsVerified()) {
            throw new BadCredentialsException("User not verified");
        }
        String refreshToken = cookiesUtils.setCookie("refreshToken", userEntity, response);
        String accessToken = cookiesUtils.setCookie("accessToken", userEntity, response);
        if (refreshToken != null && accessToken != null) {
            boolean session = sessionService.generateNewSession(userEntity, refreshToken);
            if (!session) {
                throw new RuntimeException("Something went wrong while setting cookies");
            }
            return modelMapper.map(userEntity, UserDto.class);
        } else {
            throw new RuntimeException("Something went wrong while setting cookies");
        }
    }

    public UserDto refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookiesUtils.getCookieValue("refreshToken", request);
        boolean isValidToken = jwtService.validateToken(refreshToken);
        if (!isValidToken) {
            throw new JwtException("Invalid token");
        }
        boolean session = sessionService.validateSession(refreshToken);
        if (!session) {
            throw new AuthenticationCredentialsNotFoundException("Session not found");
        }
        String userId = jwtService.getUserIdFromToken(refreshToken);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        cookiesUtils.setCookie("accessToken", userEntity, response);
        return modelMapper.map(userEntity, UserDto.class);

    }

    public boolean logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookiesUtils.getCookieValue("refreshToken", request);
        boolean session = sessionService.invalidateSession(refreshToken);
        if (!session) {
            throw new RuntimeException("Session not found");
        }
        cookiesUtils.setCookie("refreshToken", null, response);
        cookiesUtils.setCookie("accessToken", null, response);
        return true;
    }
}
