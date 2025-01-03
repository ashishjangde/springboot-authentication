package com.ashish.authandsessionmanagment.controllers;


import com.ashish.authandsessionmanagment.advices.ApiResponse;
import com.ashish.authandsessionmanagment.dto.SigninDto;
import com.ashish.authandsessionmanagment.dto.SignupDto;
import com.ashish.authandsessionmanagment.dto.UserDto;
import com.ashish.authandsessionmanagment.dto.UserVerificationDto;
import com.ashish.authandsessionmanagment.services.AuthService;
import com.ashish.authandsessionmanagment.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {


    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signupDtoResponseEntity(@RequestBody SignupDto signupDto) {
        UserDto userDto = userService.signupUser(signupDto);
        if (userDto == null) return ResponseEntity.notFound().build();
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<UserDto> signinDtoResponseEntity(@RequestBody SigninDto signInDto, HttpServletResponse response) {
        UserDto userDto = authService.signingUser(signInDto, response);
        if (userDto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("verify")
    public ResponseEntity<ApiResponse<String>> verifyUser(@RequestBody UserVerificationDto userVerificationDto) {
        String message = userService.verifyUser(userVerificationDto);
        if (message == null) return ResponseEntity.notFound().build();
        ApiResponse<String> apiResponse = new ApiResponse<>(message);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<UserDto> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        UserDto userDto = authService.refreshToken(request, response);
        if (userDto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Boolean>> logout(HttpServletRequest request, HttpServletResponse response) {
        boolean logout = authService.logout(request, response);
        if (!logout) return ResponseEntity.notFound().build();
        ApiResponse<Boolean> apiResponse = new ApiResponse<>(true);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


}
