package com.ashish.authandsessionmanagment.controllers;


import com.ashish.authandsessionmanagment.dto.SignupDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

   @GetMapping("/signup")
   public ResponseEntity<SignupDto> signupDtoResponseEntity() {
         return ResponseEntity.ok(new SignupDto());
   }

}
