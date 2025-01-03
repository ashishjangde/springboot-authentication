package com.ashish.authandsessionmanagment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserVerificationDto {
    @NotNull
    @Email
    private String email;

    @NotNull
    @Min(6)
    @Max(6)
    private String verificationCode;
}
