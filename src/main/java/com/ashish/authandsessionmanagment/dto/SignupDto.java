package com.ashish.authandsessionmanagment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SignupDto {
    @NotNull
    private String name ;
    @Email
    @NotNull
    private String email ;
    @Min(6)
    private String password ;
}
