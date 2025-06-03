package com.facilite_toi.repository;

import com.facilite_toi.model.Availability;
import com.facilite_toi.model.Availability.StatusAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    
    // Trouver les créneaux disponibles
    List<Availability> findByStatus(StatusAvailability status);
    
    // Trouver les créneaux disponibles dans le futur
    @Query("SELECT a FROM Availability a WHERE a.status = 'DISPONIBLE' AND a.startTime > :now ORDER BY a.startTime ASC")
    List<Availability> findAvailableSlots(@Param("now") LocalDateTime now);
    
    // Trouver les créneaux par type de service
    List<Availability> findByServiceType(String serviceType);
    
    // Trouver les créneaux disponibles par type de service
    @Query("SELECT a FROM Availability a WHERE a.serviceType = :serviceType AND a.status = 'DISPONIBLE' AND a.startTime > :now ORDER BY a.startTime ASC")
    List<Availability> findAvailableSlotsByServiceType(@Param("serviceType") String serviceType, @Param("now") LocalDateTime now);
    
    // Trouver les créneaux entre deux dates
    @Query("SELECT a FROM Availability a WHERE a.startTime >= :startDate AND a.endTime <= :endDate ORDER BY a.startTime ASC")
    List<Availability> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Trouver les créneaux disponibles entre deux dates
    @Query("SELECT a FROM Availability a WHERE a.startTime >= :startDate AND a.endTime <= :endDate AND a.status = 'DISPONIBLE' AND a.currentBookings < a.maxCapacity ORDER BY a.startTime ASC")
    List<Availability> findAvailableSlotsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Trouver les créneaux par jour (pour un calendrier)
    @Query("SELECT a FROM Availability a WHERE DATE(a.startTime) = DATE(:date) ORDER BY a.startTime ASC")
    List<Availability> findByDate(@Param("date") LocalDateTime date);
    
    // Trouver les créneaux avec des places disponibles
    @Query("SELECT a FROM Availability a WHERE a.currentBookings < a.maxCapacity AND a.status = 'DISPONIBLE' ORDER BY a.startTime ASC")
    List<Availability> findSlotsWithAvailableSpaces();
    
    // Créneaux complets
    @Query("SELECT a FROM Availability a WHERE a.currentBookings >= a.maxCapacity OR a.status = 'COMPLET'")
    List<Availability> findFullSlots();
    
    // Statistiques par type de service
    @Query("SELECT a.serviceType, COUNT(a), SUM(a.currentBookings) FROM Availability a GROUP BY a.serviceType")
    List<Object[]> getStatisticsByServiceType();
    
    // Créneaux créés récemment
    @Query("SELECT a FROM Availability a WHERE a.dateCreation >= :since ORDER BY a.dateCreation DESC")
    List<Availability> findRecentlyCreated(@Param("since") LocalDateTime since);
    
    // Créneaux modifiés récemment
    @Query("SELECT a FROM Availability a WHERE a.modificationDate >= :since ORDER BY a.modificationDate DESC")
    List<Availability> findRecentlyModified(@Param("since") LocalDateTime since);
    
    // Trouver les créneaux qui se chevauchent (pour éviter les conflits)
    @Query("SELECT a FROM Availability a WHERE (a.startTime < :endTime AND a.endTime > :startTime) AND a.id != :excludeId")
    List<Availability> findOverlappingSlots(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("excludeId") Long excludeId);
    
    // Créneaux par statut et type de service
    List<Availability> findByStatusAndServiceType(StatusAvailability status, String serviceType);
    
    // Compter les créneaux disponibles pour un type de service
    @Query("SELECT COUNT(a) FROM Availability a WHERE a.serviceType = :serviceType AND a.status = 'DISPONIBLE' AND a.startTime > :now")
    Long countAvailableSlotsByServiceType(@Param("serviceType") String serviceType, @Param("now") LocalDateTime now);
}