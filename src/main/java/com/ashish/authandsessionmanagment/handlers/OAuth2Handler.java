package com.ashish.authandsessionmanagment.handlers;

import com.ashish.authandsessionmanagment.entities.UserEntity;
import com.ashish.authandsessionmanagment.entities.enums.Roles;
import com.ashish.authandsessionmanagment.services.JwtService;
import com.ashish.authandsessionmanagment.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@Component
public class OAuth2Handler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    @Value("${SPRING.DEPLOYMENT_MODE}")
    private String DEPLOYMENT_MODE;

    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            DefaultOAuth2User user = (DefaultOAuth2User) token.getPrincipal();

            String email = user.getAttribute("email");
            if (email == null) {
                log.error("Email not found in OAuth2 user attributes");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email not provided by OAuth2 provider");
                return;
            }

            log.info("User with email: {} logged in", email);
            String name = user.getAttribute("name");

            UserEntity userEntity = userService.getUserByEmail(email);
            if (userEntity == null) {
                userEntity = new UserEntity();
                userEntity.setEmail(email);
                userEntity.setName(name);
                userEntity.setIsVerified(true);
                userEntity.setRoles(Set.of(Roles.USER));

                UserEntity savedUser = userService.saveUser(userEntity);
                if (savedUser == null) {
                    log.error("Failed to save user with email: {}", email);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to save user");
                    return;
                }
                userEntity = savedUser;
            }

            String accessToken = jwtService.generateAccessToken(userEntity);
            String refreshToken = jwtService.generateRefreshToken(userEntity);

            Cookie accessTokenCookie = createCookie("accessToken", accessToken);
            Cookie refreshTokenCookie = createCookie("refreshToken", refreshToken);

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            // Set a success response header
            response.setHeader("X-Auth-Status", "Success");

            // Redirect to home page or dashboard
            setDefaultTargetUrl("/");
            super.onAuthenticationSuccess(request, response, authentication);

        } catch (Exception e) {
            log.error("Error occurred while processing OAuth2 login", e);
        }
    }

    private Cookie createCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(DEPLOYMENT_MODE.equals("PRODUCTION"));
        // Add SameSite attribute for additional security
        cookie.setAttribute("SameSite", DEPLOYMENT_MODE.equals("PRODUCTION") ? "Strict" : "Lax");
        return cookie;
    }
}