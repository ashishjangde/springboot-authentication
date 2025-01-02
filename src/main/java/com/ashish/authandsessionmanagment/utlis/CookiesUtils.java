package com.ashish.authandsessionmanagment.utlis;

import com.ashish.authandsessionmanagment.entities.UserEntity;
import com.ashish.authandsessionmanagment.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;


@RequiredArgsConstructor
@Service
public class CookiesUtils {

    @Value("${SPRING.DEPLOYMENT_STATUS}")
    private String DEPLOYMENT_STATUS;

    private  final JwtService jwtService;

    public static String getCookieValue(String cookieName, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public String setCookie(String cookieName, UserEntity userEntity, HttpServletResponse response) {

        if (Objects.isNull(userEntity ) || Objects.isNull(response) || Objects.isNull(cookieName)) {
            return null;
        }

        String cookieValue;
        switch (cookieName) {
            case "refreshToken":
                cookieValue = jwtService.generateRefreshToken(userEntity);
                break;
            case "accessToken":
                cookieValue = jwtService.generateAccessToken(userEntity);
                break;
            default:
                return null;
        }

        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(DEPLOYMENT_STATUS.equals("PRODUCTION"));
        response.addCookie(cookie);
        return cookieValue;
    }


}
