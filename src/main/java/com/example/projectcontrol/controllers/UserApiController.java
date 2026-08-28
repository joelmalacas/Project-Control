package com.example.projectcontrol.controllers;

import com.example.projectcontrol.entities.User;
import com.example.projectcontrol.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserApiController {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

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
        Optional<User> findUser = userRepository.findById(id);

        if (findUser.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Utilizador não encontrado");

        return ResponseEntity.ok(findUser.get());
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