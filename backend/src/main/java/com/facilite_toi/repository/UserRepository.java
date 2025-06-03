package com.facilite_toi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.facilite_toi.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Rechercher un utilisateur par email
    Optional<User> findByEmail(String email);
    
    // Vérifier si un email existe déjà
    boolean existsByEmail(String email);
    
    // Rechercher par prénom
    Optional<User> findByForename(String forename);
    
    // Rechercher par nom de famille
    Optional<User> findBySurname(String surname);
    
    // Rechercher par prénom ET nom
    Optional<User> findByForenameAndSurname(String forename, String surname);
}