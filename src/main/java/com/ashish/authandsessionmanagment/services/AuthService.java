package com.ashish.authandsessionmanagment.services;

import com.ashish.authandsessionmanagment.dto.SigninDto;
import com.ashish.authandsessionmanagment.dto.UserDto;
import com.ashish.authandsessionmanagment.entities.UserEntity;
import com.ashish.authandsessionmanagment.utlis.CookiesUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
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


    public UserDto signingUser(SigninDto signInDto, HttpServletResponse response) {
        {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signInDto.getEmail(), signInDto.getPassword()
                    ));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserEntity userEntity = (UserEntity) authentication.getPrincipal();
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
    }

    public UserDto refreshToken(HttpServletRequest request , HttpServletResponse response){
          String refreshToken =  CookiesUtils.getCookieValue("refreshToken", request);
            boolean isValidToken =  jwtService.validateToken(refreshToken);
        return null;
    }

}
