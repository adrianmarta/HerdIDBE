package com.example.farmerapp.repositories;

import com.example.farmerapp.models.CountingSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountingSessionRepository extends MongoRepository<CountingSession, String> {
    List<CountingSession> findByOwnerId(String ownerId);
    Page<CountingSession> findByOwnerId(String ownerId, Pageable pageable);
} 