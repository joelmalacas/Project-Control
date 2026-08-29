package com.example.projectcontrol.Services;

import com.example.projectcontrol.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.encoder = passwordEncoder;
    }

    public boolean login(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .map(user -> encoder.matches(rawPassword, user.getPasswordHash()))
                .orElse(false);
    }
}
