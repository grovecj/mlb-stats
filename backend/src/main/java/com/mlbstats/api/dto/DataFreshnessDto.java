package com.mlbstats.api.dto;

import com.mlbstats.domain.sync.SyncJobType;
import com.mlbstats.ingestion.service.SyncJobService;
import com.mlbstats.ingestion.service.SyncJobService.DataFreshness;
import com.mlbstats.ingestion.service.SyncJobService.FreshnessLevel;

import java.time.LocalDateTime;

public record DataFreshnessDto(
        SyncJobType type,
        String typeDisplay,
        LocalDateTime lastSyncedAt,
        FreshnessLevel level,
        String description
) {
    public static DataFreshnessDto fromDataFreshness(DataFreshness freshness) {
        return new DataFreshnessDto(
                freshness.type(),
                freshness.type().getDisplayName(),
                freshness.lastSyncedAt(),
                freshness.level(),
                freshness.description()
        );
    }
}
