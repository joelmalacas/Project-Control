package com.example.projectcontrol.controllers;

import com.example.projectcontrol.Services.JwtService;
import com.example.projectcontrol.Services.TokenBlacklistService;
import com.example.projectcontrol.Services.UserService;
import com.example.projectcontrol.entities.User;
import com.example.projectcontrol.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
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
    private final UserService userService;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    private Optional<User> findUser;

    public UserApiController(UserRepository userRepository,
                             UserService userService,
                             JwtService jwtService,
                             TokenBlacklistService tokenBlacklistService
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.jwtService = jwtService;
        this.tokenBlacklistService = tokenBlacklistService;
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

        User saveduser = userRepository.saveAndFlush(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(saveduser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, Object> credentials) {
        //Key Validation
        if (!credentials.containsKey("email") || !credentials.containsKey("password"))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Email e password são obrigatórios");

        //Email Valid
        Object email = credentials.get("email");
        if (email.toString().isBlank() || credentials.get("email").toString().trim().isEmpty())
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(User.EMAIL_ERROR);

        //Password Valid
        Object password = credentials.get("password");
        if (password.toString().isBlank() || password.toString().length() < User.getMinPassLength())
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(User.PASSWORD_ERROR);

        //Authentication Service
        if (!userService.login(email.toString(), password.toString()))
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");

        String token = jwtService.generateToken(email.toString());

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(Map.of("message", "Login Successful",
                        "token", token
                ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.blacklistToken(token);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Logout com sucesso");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Logout com sucesso");
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