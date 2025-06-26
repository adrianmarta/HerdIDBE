package com.example.farmerapp.controllers;

import com.example.farmerapp.dto.CountingSessionRequest;
import com.example.farmerapp.models.CountingSession;
import com.example.farmerapp.services.CountingSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/counting-sessions")
public class CountingSessionController {

    @Autowired
    private CountingSessionService countingSessionService;

    @PostMapping
    public CountingSession createCountingSession(@RequestBody CountingSessionRequest request) {
        String ownerId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return countingSessionService.createCountingSession(request, ownerId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CountingSession> updateCountingSession(
            @PathVariable String id,
            @RequestBody CountingSessionRequest request) {
        String ownerId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return countingSessionService.getCountingSessionById(id)
                .map(session -> {
                    if (!session.getOwnerId().equals(ownerId)) {
                        return new ResponseEntity<CountingSession>(HttpStatus.FORBIDDEN);
                    }
                    return countingSessionService.updateCountingSession(id, request)
                            .map(ResponseEntity::ok)
                            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping()
    public Page<CountingSession> getCountingSessionsByOwnerId(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size) {
        String ownerId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "readDate"));
        return countingSessionService.getCountingSessionsByOwnerId(ownerId, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CountingSession> getCountingSessionById(@PathVariable String id) {
        return countingSessionService.getCountingSessionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 