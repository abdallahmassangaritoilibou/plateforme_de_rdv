package com.facilite_toi.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "availabilities")
public class Availability {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAvailability status;

    @Column(name = "service_type")
    private String serviceType;

    @Column(name = "max_capacity")
    private Integer maxCapacity = 1;

    @Column(name = "current_bookings")
    private Integer currentBookings = 0;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(name = "modification_date")
    private LocalDateTime modificationDate;

    // Relation avec les rendez-vous
    @OneToMany(mappedBy = "availabilityId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RendezVous> rendezVousList = new ArrayList<>();

    // Énumération pour le statut de disponibilité
    public enum StatusAvailability {
        DISPONIBLE,
        RESERVE,
        COMPLET,
        ANNULE
    }

    // Constructeur par défaut
    public Availability() {}

    // Constructeur principal
    public Availability(LocalDateTime startTime, LocalDateTime endTime, String serviceType) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.serviceType = serviceType;
        this.status = StatusAvailability.DISPONIBLE;
        this.dateCreation = LocalDateTime.now();
    }

    // Constructeur avec capacité
    public Availability(LocalDateTime startTime, LocalDateTime endTime, String serviceType, Integer maxCapacity) {
        this(startTime, endTime, serviceType);
        this.maxCapacity = maxCapacity;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        this.markAsModified();
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        this.markAsModified();
    }

    public StatusAvailability getStatus() {
        return status;
    }

    public void setStatus(StatusAvailability status) {
        this.status = status;
        this.markAsModified();
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
        this.markAsModified();
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.markAsModified();
    }

    public Integer getCurrentBookings() {
        return currentBookings;
    }

    public void setCurrentBookings(Integer currentBookings) {
        this.currentBookings = currentBookings;
        this.markAsModified();
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public List<RendezVous> getRendezVousList() {
        return rendezVousList;
    }

    public void setRendezVousList(List<RendezVous> rendezVousList) {
        this.rendezVousList = rendezVousList;
    }

    // Méthodes utilitaires
    public void markAsModified() {
        this.modificationDate = LocalDateTime.now();
    }

    public boolean isAvailable() {
        return status == StatusAvailability.DISPONIBLE && currentBookings < maxCapacity;
    }

    public boolean canBook() {
        return isAvailable() && startTime.isAfter(LocalDateTime.now());
    }

    public void addBooking() {
        if (canBook()) {
            this.currentBookings++;
            if (this.currentBookings >= this.maxCapacity) {
                this.status = StatusAvailability.COMPLET;
            }
            this.markAsModified();
        }
    }

    public void removeBooking() {
        if (this.currentBookings > 0) {
            this.currentBookings--;
            if (this.status == StatusAvailability.COMPLET && this.currentBookings < this.maxCapacity) {
                this.status = StatusAvailability.DISPONIBLE;
            }
            this.markAsModified();
        }
    }

    public long getDurationInMinutes() {
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }

    // Méthodes pour gérer les rendez-vous
    public List<RendezVous> getActiveRendezVous() {
        return rendezVousList.stream()
                .filter(rdv -> rdv.getStatut() == RendezVous.StatutRendezVous.CONFIRME || 
                              rdv.getStatut() == RendezVous.StatutRendezVous.EN_ATTENTE)
                .toList();
    }

    public List<RendezVous> getPendingRendezVous() {
        return rendezVousList.stream()
                .filter(rdv -> rdv.getStatut() == RendezVous.StatutRendezVous.EN_ATTENTE)
                .toList();
    }

    public List<RendezVous> getConfirmedRendezVous() {
        return rendezVousList.stream()
                .filter(rdv -> rdv.getStatut() == RendezVous.StatutRendezVous.CONFIRME)
                .toList();
    }

    public boolean cancelRendezVous(Long rendezVousId) {
        RendezVous rdv = rendezVousList.stream()
                .filter(r -> r.getId().equals(rendezVousId))
                .findFirst()
                .orElse(null);
        
        if (rdv != null && rdv.getStatut() != RendezVous.StatutRendezVous.ANNULE) {
            rdv.setStatut(RendezVous.StatutRendezVous.ANNULE);
            this.removeBooking();
            return true;
        }
        return false;
    }

    public boolean confirmRendezVous(Long rendezVousId) {
        RendezVous rdv = rendezVousList.stream()
                .filter(r -> r.getId().equals(rendezVousId))
                .findFirst()
                .orElse(null);
        
        if (rdv != null && rdv.getStatut() == RendezVous.StatutRendezVous.EN_ATTENTE) {
            rdv.setStatut(RendezVous.StatutRendezVous.CONFIRME);
            return true;
        }
        return false;
    }

    public boolean rescheduleRendezVous(Long rendezVousId, Availability newAvailability) {
        RendezVous rdv = rendezVousList.stream()
                .filter(r -> r.getId().equals(rendezVousId))
                .findFirst()
                .orElse(null);
        
        if (rdv != null && newAvailability.canBook()) {
            // Libère le créneau actuel
            this.removeBooking();
            rdv.setAvailabilityId(newAvailability.getId());
            
            // Reserve le nouveau créneau
            newAvailability.addBooking();
            newAvailability.getRendezVousList().add(rdv);
            
            rdv.markAsModified();
            return true;
        }
        return false;
    }

    public int getTotalActiveBookings() {
        return (int) rendezVousList.stream()
                .filter(rdv -> rdv.getStatut() == RendezVous.StatutRendezVous.CONFIRME || 
                              rdv.getStatut() == RendezVous.StatutRendezVous.EN_ATTENTE)
                .count();
    }

    @Override
    public String toString() {
        return "Availability{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status +
                ", serviceType='" + serviceType + '\'' +
                ", maxCapacity=" + maxCapacity +
                ", currentBookings=" + currentBookings +
                ", dateCreation=" + dateCreation +
                ", modificationDate=" + modificationDate +
                '}';
    }
}