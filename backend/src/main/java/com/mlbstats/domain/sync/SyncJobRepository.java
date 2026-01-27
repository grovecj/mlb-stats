package com.mlbstats.domain.sync;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SyncJobRepository extends JpaRepository<SyncJob, Long> {

    List<SyncJob> findByStatusOrderByCreatedAtDesc(SyncJobStatus status);

    List<SyncJob> findTop20ByOrderByCreatedAtDesc();

    Optional<SyncJob> findFirstByJobTypeAndStatusIn(SyncJobType jobType, List<SyncJobStatus> statuses);

    @Query("SELECT sj FROM SyncJob sj WHERE sj.jobType = :jobType AND sj.status = 'COMPLETED' ORDER BY sj.completedAt DESC LIMIT 1")
    Optional<SyncJob> findLastCompletedByJobType(SyncJobType jobType);

    @Query("SELECT sj FROM SyncJob sj WHERE sj.status IN ('PENDING', 'RUNNING') ORDER BY sj.createdAt DESC")
    List<SyncJob> findActiveJobs();

    boolean existsByJobTypeAndStatusIn(SyncJobType jobType, List<SyncJobStatus> statuses);

    @Query("SELECT sj FROM SyncJob sj WHERE sj.status = 'RUNNING' AND sj.jobType = 'FULL_SYNC'")
    Optional<SyncJob> findRunningFullSync();
}
