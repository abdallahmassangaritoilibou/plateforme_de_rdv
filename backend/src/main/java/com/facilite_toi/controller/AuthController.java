
package com.facilite_toi.controller;


import com.facilite_toi.model.User;
import com.facilite_toi.dto.ApiResponse;
import com.facilite_toi.dto.RegisterRequest;
import com.facilite_toi.dto.AuthResponse;
import com.facilite_toi.dto.LoginRequest;
import com.facilite_toi.dto.TokenRequest;

import com.facilite_toi.repository.UserRepository;
import com.facilite_toi.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuthService authService;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, "Cet email est déjà utilisé"));
            }

            User user = new User();
            user.setForename(registerRequest.getForename());
            user.setSurname(registerRequest.getSurname());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            
            User savedUser = userRepository.save(user);
            String token = authService.generateToken(savedUser);
            
            return ResponseEntity.ok(new AuthResponse(token, savedUser, "Inscription réussie"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Erreur lors de l'inscription"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
            
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Email ou mot de passe incorrect"));
            }
            
            User user = userOptional.get();
            
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Email ou mot de passe incorrect"));
            }
            
            String token = authService.generateToken(user);
            
            return ResponseEntity.ok(new AuthResponse(token, user, "Connexion réussie"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Erreur lors de la connexion"));
        }
    }

   @PostMapping("/verify-token")
public ResponseEntity<?> verifyToken(@RequestBody TokenRequest tokenRequest) {
    try {
        boolean isValid = authService.validateToken(tokenRequest.getToken());
        
        if (isValid) {
            //  Récupérer l'email du token et chercher l'utilisateur
            String email = authService.getEmailFromToken(tokenRequest.getToken());
            Optional<User> userOptional = userRepository.findByEmail(email);
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                return ResponseEntity.ok(new AuthResponse(tokenRequest.getToken(), user, "Token valide"));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Utilisateur non trouvé"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(false, "Token invalide ou expiré"));
        }
        
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ApiResponse(false, "Token invalide"));
    }
}
}