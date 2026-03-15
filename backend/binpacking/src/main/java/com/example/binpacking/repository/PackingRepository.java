package com.example.binpacking.repository;

import com.example.binpacking.model.PackingJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PackingRepository extends JpaRepository<PackingJob, UUID> {

    @Query("SELECT DISTINCT j FROM PackingJob j " +
            "LEFT JOIN FETCH j.placements p " +
            "LEFT JOIN FETCH p.item " +
            "WHERE j.id = :id")
    Optional<PackingJob> findByIdWithPlacements(@Param("id") UUID id);
}