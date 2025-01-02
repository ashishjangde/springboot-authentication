package com.ashish.authandsessionmanagment.controllers;


import com.ashish.authandsessionmanagment.dto.SigninDto;
import com.ashish.authandsessionmanagment.dto.SignupDto;
import com.ashish.authandsessionmanagment.dto.UserDto;
import com.ashish.authandsessionmanagment.services.AuthService;
import com.ashish.authandsessionmanagment.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {


   private final UserService userService;
   private final AuthService authService;

   @PostMapping("/signup")
   public ResponseEntity<UserDto> signupDtoResponseEntity(@RequestBody SignupDto signupDto){
      UserDto userDto = userService.signupUser(signupDto);
      if(userDto == null){
         return ResponseEntity.notFound().build();
      }
        return ResponseEntity.ok(userDto);
   }

   @PostMapping("/signin")
    public ResponseEntity<UserDto> signinDtoResponseEntity(@RequestBody SigninDto signInDto , HttpServletResponse response){
        UserDto userDto = authService.signingUser(signInDto , response);
        if(userDto == null){
            return ResponseEntity.notFound().build();
        }
          return ResponseEntity.ok(userDto);
    }

}
