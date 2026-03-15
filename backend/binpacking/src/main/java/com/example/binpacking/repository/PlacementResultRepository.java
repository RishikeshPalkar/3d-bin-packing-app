package com.example.binpacking.repository;

import com.example.binpacking.model.PlacementResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PlacementResultRepository extends JpaRepository<PlacementResult, UUID> {}