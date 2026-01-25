package com.mlbstats.domain.player;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mlb_id", unique = true, nullable = false)
    private Integer mlbId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "jersey_number")
    private String jerseyNumber;

    private String position;

    @Column(name = "position_type")
    private String positionType;

    private String bats;

    @Column(name = "throws")
    private String throwsHand;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private String height;

    private Integer weight;

    @Column(name = "mlb_debut_date")
    private LocalDate mlbDebutDate;

    private Boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
