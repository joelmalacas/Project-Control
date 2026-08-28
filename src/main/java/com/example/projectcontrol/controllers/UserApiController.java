package com.example.projectcontrol.controllers;

import com.example.projectcontrol.entities.User;
import com.example.projectcontrol.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserApiController {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    private Optional<User> findUser;

    public UserApiController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    @ResponseBody
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {

        if (userRepository.existsByEmail(user.getEmail()))
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Email já existe: { " + user.getEmail() + " }");

       user.setPasswordHash(encoder.encode(user.getPassword()));

        User saveduser = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(saveduser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        findUser = userRepository.findById(id);

        if (findUser.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Utilizador não encontrado");

        return ResponseEntity.ok(findUser.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserById(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        findUser = userRepository.findById(id);

        if (findUser.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Utilizador não encontrado");

        User existingUser = findUser.get();

        //Update Fields
        if (updates.containsKey("name") && updates.get("name") != null) {
            if (updates.get("name").toString().length() < User.getMinNameLength())
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(User.NAME_ERROR);
            existingUser.setName(updates.get("name").toString());
        }

        if (updates.containsKey("email") && updates.get("email") != null){
            if (!User.isValidEmail(updates.get("email").toString()))
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(User.EMAIL_ERROR);
            existingUser.setEmail(updates.get("email").toString());
        }

        if (updates.containsKey("password") && updates.get("password") != null) {
            String newPassword = updates.get("password").toString();

            if (newPassword.length() < User.getMinPassLength())
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(User.PASSWORD_ERROR);

            existingUser.setPasswordHash(encoder.encode(newPassword));
        }

        User savedUser = userRepository.save(existingUser);
        return ResponseEntity.ok(savedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id))
           return ResponseEntity
                   .status(HttpStatus.NOT_FOUND)
                   .body("Utilizador não encontrado");

       userRepository.deleteById(id);

       return ResponseEntity
               .ok("Utilizador eliminado com sucesso");
    }
}