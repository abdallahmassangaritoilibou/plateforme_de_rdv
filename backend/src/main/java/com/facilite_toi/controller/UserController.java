package com.facilite_toi.controller;

import com.facilite_toi.User;
import com.facilite_toi.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // GET - Récupérer tous les utilisateurs
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET - Récupérer un utilisateur par ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> userData = userRepository.findById(id);
        
        if (userData.isPresent()) {
            return new ResponseEntity<>(userData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // GET - Récupérer un utilisateur par email
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> userData = userRepository.findByEmail(email);
        
        if (userData.isPresent()) {
            return new ResponseEntity<>(userData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // POST - Créer un nouvel utilisateur
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            // Vérifier si l'email existe déjà
            if (userRepository.existsByEmail(user.getEmail())) {
                return new ResponseEntity<>(HttpStatus.CONFLICT); // 409 - Email déjà utilisé
            }
            
            User savedUser = userRepository.save(user);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PUT - Mettre à jour un utilisateur
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        Optional<User> userData = userRepository.findById(id);
        
        if (userData.isPresent()) {
            User existingUser = userData.get();
            
            // Mettre à jour les champs
            existingUser.setForename(user.getForename());
            existingUser.setSurname(user.getSurname());
            existingUser.setEmail(user.getEmail());
            existingUser.setPassword(user.getPassword());
            
            return new ResponseEntity<>(userRepository.save(existingUser), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // DELETE - Supprimer un utilisateur
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long id) {
        try {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // POST - Créer des utilisateurs de test
    @PostMapping("/test-data")
    public ResponseEntity<List<User>> createTestUsers() {
        try {
            // Créer des utilisateurs de test
            User user1 = new User();
            user1.setForename("Jean");
            user1.setSurname("Dupont");
            user1.setEmail("jean.dupont@email.com");
            user1.setPassword("password123");

            User user2 = new User();
            user2.setForename("Marie");
            user2.setSurname("Martin");
            user2.setEmail("marie.martin@email.com");
            user2.setPassword("password456");

            User user3 = new User();
            user3.setForename("Pierre");
            user3.setSurname("Durand");
            user3.setEmail("pierre.durand@email.com");
            user3.setPassword("password789");
            // Set password or other fields as needed

            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);

            List<User> testUsers = List.of(user1, user2, user3);
            return new ResponseEntity<>(testUsers, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET - Statistiques des utilisateurs
    @GetMapping("/stats")
    public ResponseEntity<String> getUserStats() {
        try {
            long totalUsers = userRepository.count();
            return new ResponseEntity<>("Total des utilisateurs: " + totalUsers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur lors du calcul des statistiques", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET - Test simple Hello World
    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return new ResponseEntity<>("Hello World - API Users fonctionne !", HttpStatus.OK);
    }
}



