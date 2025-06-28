package com.example.farmerapp.services;

import com.example.farmerapp.dto.CountingSessionRequest;
import com.example.farmerapp.models.CountingSession;
import com.example.farmerapp.repositories.CountingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CountingSessionService {

    @Autowired
    private CountingSessionRepository countingSessionRepository;

    public CountingSession createCountingSession(CountingSessionRequest request, String ownerId) {
        CountingSession countingSession = new CountingSession();
        countingSession.setName(request.getName());
        countingSession.setOwnerId(ownerId);
        countingSession.setFolderId(request.getFolderId());
        countingSession.setReadAnimalIds(request.getReadAnimalIds());
        countingSession.setReadDate(LocalDateTime.now());
        return countingSessionRepository.save(countingSession);
    }

    public Optional<CountingSession> updateCountingSession(String sessionId, CountingSessionRequest request) {
        return countingSessionRepository.findById(sessionId)
                .map(session -> {
                    session.setName(request.getName());
                    session.setFolderId(request.getFolderId());
                    session.setReadAnimalIds(request.getReadAnimalIds());
                    session.setReadDate(LocalDateTime.now());
                    return countingSessionRepository.save(session);
                });
    }


    public Page<CountingSession> getCountingSessionsByOwnerId(String ownerId, Pageable pageable) {
        return countingSessionRepository.findByOwnerId(ownerId, pageable);
    }

    public Optional<CountingSession> getCountingSessionById(String id) {
        return countingSessionRepository.findById(id);
    }
    public void deleteSession( String id)
    {
        countingSessionRepository.deleteById(id);
    }
} 