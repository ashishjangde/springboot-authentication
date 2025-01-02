package com.ashish.authandsessionmanagment.services;

import com.ashish.authandsessionmanagment.dto.SignupDto;
import com.ashish.authandsessionmanagment.dto.UserDto;
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
import java.util.Set;

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

    public UserDto signupUser(SignupDto signupDto){
       UserEntity existingUser = userRepository.findByEmail(signupDto.getEmail()).orElse(null);
        String verificationCode = VerificationCodeGenerator.generateCode();
        LocalDateTime codeExpiresAt = LocalDateTime.now().plusMinutes(10);
        if (existingUser != null){
            if (existingUser.getIsVerified()){
                throw new BadCredentialsException("User with email: " + signupDto.getEmail() + " already exists");
            }
            else {
                return saveAndReturnSignupDto(signupDto, verificationCode, codeExpiresAt);
            }
        }
        return saveAndReturnSignupDto(signupDto, verificationCode, codeExpiresAt);
    }

    private UserDto saveAndReturnSignupDto(SignupDto signupDto, String verificationCode, LocalDateTime codeExpiresAt) {
        UserEntity userEntity = modelMapper.map(signupDto, UserEntity.class);
        userEntity.setVerificationCode(verificationCode);
        userEntity.setVerificationCodeExpiresAt(codeExpiresAt);
        userEntity.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        userEntity.setRoles(Set.of(Roles.USER));
        //send verification email
        userRepository.save(userEntity);
        return modelMapper.map(userRepository.save(userEntity), UserDto.class);
    }
}
