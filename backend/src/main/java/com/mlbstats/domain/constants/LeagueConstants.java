package com.mlbstats.domain.constants;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Stores season-specific league constants used for gWAR calculations.
 * These values change each season based on league-wide offensive environment.
 */
@Entity
@Table(name = "league_constants")
@Getter
@Setter
@NoArgsConstructor
public class LeagueConstants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer season;

    /**
     * League average wOBA - used as baseline for wRAA calculation
     */
    @Column(name = "lg_woba", precision = 5, scale = 4, nullable = false)
    private BigDecimal lgWoba;

    /**
     * wOBA scale - converts wOBA to runs above average
     */
    @Column(name = "woba_scale", precision = 5, scale = 4, nullable = false)
    private BigDecimal wobaScale;

    /**
     * League average runs per plate appearance
     */
    @Column(name = "lg_r_per_pa", precision = 6, scale = 5, nullable = false)
    private BigDecimal lgRPerPa;

    /**
     * FIP constant for the season (used to scale FIP to ERA)
     */
    @Column(name = "fip_constant", precision = 4, scale = 2, nullable = false)
    private BigDecimal fipConstant;

    /**
     * Runs per win (typically around 10)
     */
    @Column(name = "runs_per_win", precision = 4, scale = 2)
    private BigDecimal runsPerWin = new BigDecimal("10.0");

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
