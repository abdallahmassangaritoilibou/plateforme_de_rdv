package com.facilite_toi.repository;

import com.facilite_toi.model.RendezVous;
import com.facilite_toi.model.RendezVous.StatutRendezVous;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
    
    // Trouver tous les RDV d'un utilisateur
    List<RendezVous> findByUserId(Long userId);
    
    // Trouver tous les RDV d'un utilisateur par statut
    List<RendezVous> findByUserIdAndStatut(Long userId, StatutRendezVous statut);
    
    // Trouver tous les RDV d'un créneau
    List<RendezVous> findByAvailabilityId(Long availabilityId);
    
    // Trouver tous les RDV d'un créneau par statut
    List<RendezVous> findByAvailabilityIdAndStatut(Long availabilityId, StatutRendezVous statut);
    
    // Trouver les RDV d'un utilisateur entre deux dates
    @Query("SELECT r FROM RendezVous r WHERE r.userId = :userId AND r.dateCreation BETWEEN :startDate AND :endDate")
    List<RendezVous> findByUserIdAndDateCreationBetween(
        @Param("userId") Long userId, 
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
    
    // Trouver les RDV confirmés d'un utilisateur
    List<RendezVous> findByUserIdAndStatutIn(Long userId, List<StatutRendezVous> statuts);
    
    // Compter les RDV actifs d'un créneau
    @Query("SELECT COUNT(r) FROM RendezVous r WHERE r.availabilityId = :availabilityId AND r.statut IN ('CONFIRME', 'EN_ATTENTE')")
    Long countActiveRendezVousByAvailabilityId(@Param("availabilityId") Long availabilityId);
    
    // Vérifier si un utilisateur a déjà un RDV sur ce créneau
    @Query("SELECT r FROM RendezVous r WHERE r.userId = :userId AND r.availabilityId = :availabilityId AND r.statut IN ('CONFIRME', 'EN_ATTENTE')")
    Optional<RendezVous> findExistingRendezVous(@Param("userId") Long userId, @Param("availabilityId") Long availabilityId);
    
    // Trouver les RDV récents (dernières 24h)
    @Query("SELECT r FROM RendezVous r WHERE r.dateCreation >= :since ORDER BY r.dateCreation DESC")
    List<RendezVous> findRecentRendezVous(@Param("since") LocalDateTime since);
    
    // Trouver les RDV modifiés récemment
    @Query("SELECT r FROM RendezVous r WHERE r.modificationDate >= :since ORDER BY r.modificationDate DESC")
    List<RendezVous> findRecentlyModified(@Param("since") LocalDateTime since);
    
    // Statistiques par statut
    @Query("SELECT r.statut, COUNT(r) FROM RendezVous r GROUP BY r.statut")
    List<Object[]> getStatistiquesByStatut();
}
