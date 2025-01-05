package com.ashish.authandsessionmanagment.services;

import com.ashish.authandsessionmanagment.dto.SignupDto;
import com.ashish.authandsessionmanagment.dto.UserDto;
import com.ashish.authandsessionmanagment.dto.UserVerificationDto;
import com.ashish.authandsessionmanagment.entities.UserEntity;
import com.ashish.authandsessionmanagment.entities.enums.Roles;
import com.ashish.authandsessionmanagment.exceptions.ResourceNotFoundException;
import com.ashish.authandsessionmanagment.repositories.UserRepository;
import com.ashish.authandsessionmanagment.utlis.VerificationCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.ashish.authandsessionmanagment.entities.enums.Roles.USER;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    public UserEntity getUserById (String id) {
        return userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User not found with id: " + id));
    }
    public UserEntity saveUser(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found with email: " + email));
    }

    public UserDto signupUser(SignupDto signupDto) {
        UserEntity existingUser = userRepository.findByEmail(signupDto.getEmail()).orElse(null);
        String verificationCode = VerificationCodeGenerator.generateCode();
        LocalDateTime codeExpiresAt = LocalDateTime.now().plusMinutes(10);

        if (existingUser != null) {
            if (existingUser.getIsVerified()) {
                throw new BadCredentialsException("User with email: " + signupDto.getEmail() + " already exists");
            } else {
                // Update the existing unverified user's data
                existingUser.setVerificationCode(verificationCode);
                existingUser.setVerificationCodeExpiresAt(codeExpiresAt);
                existingUser.setPassword(passwordEncoder.encode(signupDto.getPassword())); // Optional: reset password
                userRepository.save(existingUser);
                // Send verification email
                return modelMapper.map(existingUser, UserDto.class);
            }
        }

        // Create a new user if no existing user found
        UserEntity userEntity = modelMapper.map(signupDto, UserEntity.class);
        userEntity.setVerificationCode(verificationCode);
        userEntity.setVerificationCodeExpiresAt(codeExpiresAt);
        userEntity.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        userEntity.setRoles(new HashSet<>(List.of(Roles.USER)));
        userRepository.save(userEntity);
        // Send verification email
        return modelMapper.map(userEntity, UserDto.class);
    }



    public String verifyUser(UserVerificationDto userVerificationDto) {
        UserEntity userEntity = userRepository.findByEmail(userVerificationDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userVerificationDto.getEmail()));
        if (userEntity.getIsVerified()) throw new BadCredentialsException("user with " + userVerificationDto.getEmail() + " is already verified");
        if (userEntity.getVerificationCode().equals(userVerificationDto.getVerificationCode()) && userEntity.getVerificationCodeExpiresAt().isAfter(LocalDateTime.now())) {
            userEntity.setIsVerified(true);
            userEntity.setVerificationCode(null);
            userEntity.setVerificationCodeExpiresAt(null);
            userRepository.save(userEntity);
       return "User with email: " + userVerificationDto.getEmail() + " verified successfully";
        } else {
            throw new BadCredentialsException("Invalid verification code");
        }
    }
}
