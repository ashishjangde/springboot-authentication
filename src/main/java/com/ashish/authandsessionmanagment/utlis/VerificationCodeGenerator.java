package com.ashish.authandsessionmanagment.utlis;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class VerificationCodeGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateCode(){
        int code = secureRandom.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
