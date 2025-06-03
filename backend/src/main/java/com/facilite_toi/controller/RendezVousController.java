package com.facilite_toi.controller;


import com.facilite_toi.model.RendezVous;
import com.facilite_toi.model.RendezVous.StatutRendezVous;
import com.facilite_toi.repository.RendezVousRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rendez-vous")
@CrossOrigin(origins = "*")
public class RendezVousController {

    @Autowired
    private RendezVousRepository rendezVousRepository;

    // Récupérer tous les rendez-vous
    @GetMapping
    public ResponseEntity<List<RendezVous>> getAllRendezVous() {
        try {
            List<RendezVous> rendezVousList = rendezVousRepository.findAll();
            return new ResponseEntity<>(rendezVousList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer un rendez-vous par ID
    @GetMapping("/{id}")
    public ResponseEntity<RendezVous> getRendezVousById(@PathVariable("id") Long id) {
        Optional<RendezVous> rendezVous = rendezVousRepository.findById(id);
        
        if (rendezVous.isPresent()) {
            return new ResponseEntity<>(rendezVous.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Créer un nouveau rendez-vous
    @PostMapping
    public ResponseEntity<RendezVous> createRendezVous(@RequestBody RendezVous rendezVous) {
        try {
            // Vérifier si l'utilisateur a déjà un RDV sur ce créneau
            Optional<RendezVous> existingRdv = rendezVousRepository.findExistingRendezVous(
                rendezVous.getUserId(), 
                rendezVous.getAvailabilityId()
            );
            
            if (existingRdv.isPresent()) {
                return new ResponseEntity<>(null, HttpStatus.CONFLICT); // 409 Conflict
            }
            
            // Par défaut, nouveau RDV en attente
            if (rendezVous.getStatut() == null) {
                rendezVous.setStatut(StatutRendezVous.EN_ATTENTE);
            }
            
            RendezVous savedRendezVous = rendezVousRepository.save(rendezVous);
            return new ResponseEntity<>(savedRendezVous, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Mettre à jour un rendez-vous
    @PutMapping("/{id}")
    public ResponseEntity<RendezVous> updateRendezVous(@PathVariable("id") Long id, @RequestBody RendezVous rendezVous) {
        Optional<RendezVous> existingRendezVous = rendezVousRepository.findById(id);
        
        if (existingRendezVous.isPresent()) {
            RendezVous rdv = existingRendezVous.get();
            
            // Mise à jour des champs modifiables
            if (rendezVous.getStatut() != null) {
                rdv.setStatut(rendezVous.getStatut());
            }
            if (rendezVous.getAvailabilityId() != null) {
                rdv.setAvailabilityId(rendezVous.getAvailabilityId());
            }
            
            rdv.markAsModified();
            
            try {
                return new ResponseEntity<>(rendezVousRepository.save(rdv), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Supprimer un rendez-vous
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteRendezVous(@PathVariable("id") Long id) {
        try {
            rendezVousRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer les rendez-vous d'un utilisateur
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RendezVous>> getRendezVousByUserId(@PathVariable("userId") Long userId) {
        try {
            List<RendezVous> rendezVousList = rendezVousRepository.findByUserId(userId);
            return new ResponseEntity<>(rendezVousList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer les rendez-vous d'un utilisateur par statut
    @GetMapping("/user/{userId}/statut/{statut}")
    public ResponseEntity<List<RendezVous>> getRendezVousByUserIdAndStatut(
            @PathVariable("userId") Long userId, 
            @PathVariable("statut") String statut) {
        try {
            StatutRendezVous statutEnum = StatutRendezVous.valueOf(statut.toUpperCase());
            List<RendezVous> rendezVousList = rendezVousRepository.findByUserIdAndStatut(userId, statutEnum);
            return new ResponseEntity<>(rendezVousList, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer les rendez-vous d'un créneau
    @GetMapping("/availability/{availabilityId}")
    public ResponseEntity<List<RendezVous>> getRendezVousByAvailabilityId(@PathVariable("availabilityId") Long availabilityId) {
        try {
            List<RendezVous> rendezVousList = rendezVousRepository.findByAvailabilityId(availabilityId);
            return new ResponseEntity<>(rendezVousList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Confirmer un rendez-vous
    @PutMapping("/{id}/confirm")
    public ResponseEntity<RendezVous> confirmRendezVous(@PathVariable("id") Long id) {
        Optional<RendezVous> existingRendezVous = rendezVousRepository.findById(id);
        
        if (existingRendezVous.isPresent()) {
            RendezVous rdv = existingRendezVous.get();
            
            if (rdv.getStatut() == StatutRendezVous.EN_ATTENTE) {
                rdv.setStatut(StatutRendezVous.CONFIRME);
                rdv.markAsModified();
                
                try {
                    return new ResponseEntity<>(rendezVousRepository.save(rdv), HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Annuler un rendez-vous
    @PutMapping("/{id}/cancel")
    public ResponseEntity<RendezVous> cancelRendezVous(@PathVariable("id") Long id) {
        Optional<RendezVous> existingRendezVous = rendezVousRepository.findById(id);
        
        if (existingRendezVous.isPresent()) {
            RendezVous rdv = existingRendezVous.get();
            
            if (rdv.getStatut() != StatutRendezVous.ANNULE) {
                rdv.setStatut(StatutRendezVous.ANNULE);
                rdv.markAsModified();
                
                try {
                    return new ResponseEntity<>(rendezVousRepository.save(rdv), HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Obtenir les statistiques
    @GetMapping("/stats")
    public ResponseEntity<List<Object[]>> getStatistiques() {
        try {
            List<Object[]> stats = rendezVousRepository.getStatistiquesByStatut();
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}