package com.ashish.authandsessionmanagment.services;

import com.ashish.authandsessionmanagment.entities.UserEntity;
import com.ashish.authandsessionmanagment.exceptions.ResourceNotFoundException;
import com.ashish.authandsessionmanagment.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

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
}
