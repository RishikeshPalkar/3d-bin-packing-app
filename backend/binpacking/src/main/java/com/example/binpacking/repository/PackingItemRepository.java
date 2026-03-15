package com.example.binpacking.repository;

import com.example.binpacking.model.PackingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PackingItemRepository extends JpaRepository<PackingItem, UUID> {}