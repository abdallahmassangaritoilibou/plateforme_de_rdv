package com.facilite_toi.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rendez_vous")
public class RendezVous {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "availability_id", nullable = false)
    private Long availabilityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutRendezVous statut;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(name = "modification_date")
    private LocalDateTime modificationDate;

    // Énumération pour le statut
    public enum StatutRendezVous {
        EN_ATTENTE,
        CONFIRME,
        ANNULE,
        TERMINE
    }

    // Constructeur par défaut
    public RendezVous() {}

    // Constructeur principal
    public RendezVous(Long userId, Long availabilityId, StatutRendezVous statut) {
        this.userId = userId;
        this.availabilityId = availabilityId;
        this.statut = statut;
        this.dateCreation = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAvailabilityId() {
        return availabilityId;
    }

    public void setAvailabilityId(Long availabilityId) {
        this.availabilityId = availabilityId;
    }

    public StatutRendezVous getStatut() {
        return statut;
    }

    public void setStatut(StatutRendezVous statut) {
        this.statut = statut;
        this.modificationDate = LocalDateTime.now(); // Met à jour automatiquement la date de modification
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

    // Méthode utilitaire pour marquer comme modifié
    public void markAsModified() {
        this.modificationDate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "RendezVous{" +
                "id=" + id +
                ", userId=" + userId +
                ", availabilityId=" + availabilityId +
                ", statut=" + statut +
                ", dateCreation=" + dateCreation +
                ", modificationDate=" + modificationDate +
                '}';
    }
}

