package com.facilite_toi.controller;

import com.facilite_toi.model.Availability;
import com.facilite_toi.model.Availability.StatusAvailability;
import com.facilite_toi.model.RendezVous;
import com.facilite_toi.repository.AvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/availability")
@CrossOrigin(origins = "*")
public class AvailabilityController {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    // Récupérer tous les créneaux
    @GetMapping
    public ResponseEntity<List<Availability>> getAllAvailabilities() {
        try {
            List<Availability> availabilities = availabilityRepository.findAll();
            return new ResponseEntity<>(availabilities, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer un créneau par ID
    @GetMapping("/{id}")
    public ResponseEntity<Availability> getAvailabilityById(@PathVariable("id") Long id) {
        Optional<Availability> availability = availabilityRepository.findById(id);
        
        if (availability.isPresent()) {
            return new ResponseEntity<>(availability.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Créer un nouveau créneau
    @PostMapping
    public ResponseEntity<Availability> createAvailability(@RequestBody Availability availability) {
        try {
            // Vérifier les chevauchements
            List<Availability> overlapping = availabilityRepository.findOverlappingSlots(
                availability.getStartTime(), 
                availability.getEndTime(), 
                0L
            );
            
            if (!overlapping.isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.CONFLICT); // 409 Conflict
            }
            
            // Valeurs par défaut si non définies
            if (availability.getStatus() == null) {
                availability.setStatus(StatusAvailability.DISPONIBLE);
            }
            if (availability.getMaxCapacity() == null) {
                availability.setMaxCapacity(1);
            }
            if (availability.getCurrentBookings() == null) {
                availability.setCurrentBookings(0);
            }
            
            Availability savedAvailability = availabilityRepository.save(availability);
            return new ResponseEntity<>(savedAvailability, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Mettre à jour un créneau
    @PutMapping("/{id}")
    public ResponseEntity<Availability> updateAvailability(@PathVariable("id") Long id, @RequestBody Availability availability) {
        Optional<Availability> existingAvailability = availabilityRepository.findById(id);
        
        if (existingAvailability.isPresent()) {
            Availability slot = existingAvailability.get();
            
            // Mise à jour des champs modifiables
            if (availability.getStartTime() != null) {
                slot.setStartTime(availability.getStartTime());
            }
            if (availability.getEndTime() != null) {
                slot.setEndTime(availability.getEndTime());
            }
            if (availability.getStatus() != null) {
                slot.setStatus(availability.getStatus());
            }
            if (availability.getServiceType() != null) {
                slot.setServiceType(availability.getServiceType());
            }
            if (availability.getMaxCapacity() != null) {
                slot.setMaxCapacity(availability.getMaxCapacity());
            }
            
            slot.markAsModified();
            
            try {
                return new ResponseEntity<>(availabilityRepository.save(slot), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Supprimer un créneau
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteAvailability(@PathVariable("id") Long id) {
        try {
            availabilityRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer les créneaux disponibles
    @GetMapping("/available")
    public ResponseEntity<List<Availability>> getAvailableSlots() {
        try {
            List<Availability> availableSlots = availabilityRepository.findAvailableSlots(LocalDateTime.now());
            return new ResponseEntity<>(availableSlots, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer les créneaux par type de service
    @GetMapping("/service/{serviceType}")
    public ResponseEntity<List<Availability>> getAvailabilitiesByServiceType(@PathVariable("serviceType") String serviceType) {
        try {
            List<Availability> availabilities = availabilityRepository.findByServiceType(serviceType);
            return new ResponseEntity<>(availabilities, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer les créneaux disponibles par type de service
    @GetMapping("/service/{serviceType}/available")
    public ResponseEntity<List<Availability>> getAvailableSlotsByServiceType(@PathVariable("serviceType") String serviceType) {
        try {
            List<Availability> availableSlots = availabilityRepository.findAvailableSlotsByServiceType(serviceType, LocalDateTime.now());
            return new ResponseEntity<>(availableSlots, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer les créneaux par plage de dates
    @GetMapping("/date-range")
    public ResponseEntity<List<Availability>> getAvailabilitiesByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<Availability> availabilities = availabilityRepository.findByDateRange(startDate, endDate);
            return new ResponseEntity<>(availabilities, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer les créneaux disponibles par plage de dates
    @GetMapping("/date-range/available")
    public ResponseEntity<List<Availability>> getAvailableSlotsByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<Availability> availableSlots = availabilityRepository.findAvailableSlotsByDateRange(startDate, endDate);
            return new ResponseEntity<>(availableSlots, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Voir les rendez-vous d'un créneau
    @GetMapping("/{id}/rendez-vous")
    public ResponseEntity<List<RendezVous>> getRendezVousByAvailability(@PathVariable("id") Long id) {
        Optional<Availability> availability = availabilityRepository.findById(id);
        
        if (availability.isPresent()) {
            List<RendezVous> rendezVousList = availability.get().getRendezVousList();
            return new ResponseEntity<>(rendezVousList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Voir les rendez-vous actifs d'un créneau
    @GetMapping("/{id}/rendez-vous/active")
    public ResponseEntity<List<RendezVous>> getActiveRendezVousByAvailability(@PathVariable("id") Long id) {
        Optional<Availability> availability = availabilityRepository.findById(id);
        
        if (availability.isPresent()) {
            List<RendezVous> activeRendezVous = availability.get().getActiveRendezVous();
            return new ResponseEntity<>(activeRendezVous, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Confirmer un rendez-vous depuis le créneau
    @PutMapping("/{availabilityId}/rendez-vous/{rendezVousId}/confirm")
    public ResponseEntity<String> confirmRendezVousFromAvailability(
            @PathVariable("availabilityId") Long availabilityId,
            @PathVariable("rendezVousId") Long rendezVousId) {
        
        Optional<Availability> availability = availabilityRepository.findById(availabilityId);
        
        if (availability.isPresent()) {
            boolean confirmed = availability.get().confirmRendezVous(rendezVousId);
            
            if (confirmed) {
                availabilityRepository.save(availability.get());
                return new ResponseEntity<>("Rendez-vous confirmé avec succès", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Impossible de confirmer le rendez-vous", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Créneau non trouvé", HttpStatus.NOT_FOUND);
        }
    }

    // Annuler un rendez-vous depuis le créneau
    @PutMapping("/{availabilityId}/rendez-vous/{rendezVousId}/cancel")
    public ResponseEntity<String> cancelRendezVousFromAvailability(
            @PathVariable("availabilityId") Long availabilityId,
            @PathVariable("rendezVousId") Long rendezVousId) {
        
        Optional<Availability> availability = availabilityRepository.findById(availabilityId);
        
        if (availability.isPresent()) {
            boolean cancelled = availability.get().cancelRendezVous(rendezVousId);
            
            if (cancelled) {
                availabilityRepository.save(availability.get());
                return new ResponseEntity<>("Rendez-vous annulé avec succès", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Impossible d'annuler le rendez-vous", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Créneau non trouvé", HttpStatus.NOT_FOUND);
        }
    }

    // Obtenir les statistiques
    @GetMapping("/stats")
    public ResponseEntity<List<Object[]>> getStatistiques() {
        try {
            List<Object[]> stats = availabilityRepository.getStatisticsByServiceType();
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer les créneaux avec places disponibles
    @GetMapping("/with-spaces")
    public ResponseEntity<List<Availability>> getSlotsWithAvailableSpaces() {
        try {
            List<Availability> availableSlots = availabilityRepository.findSlotsWithAvailableSpaces();
            return new ResponseEntity<>(availableSlots, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}