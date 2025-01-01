package com.ashish.authandsessionmanagment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SignupDto {
    private String name = "Ashish";
    private String email = "ashishjangde";
    private String password = "password";
}
