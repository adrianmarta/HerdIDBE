package com.example.farmerapp.controllers;

import com.example.farmerapp.dto.AnimalTransferRequest;
import com.example.farmerapp.models.AnimalTransfer;
import com.example.farmerapp.services.AnimalTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfers")
public class AnimalTransferController {

    @Autowired
    private AnimalTransferService animalTransferService;

    @PostMapping
    public ResponseEntity<AnimalTransfer> createTransfer(@RequestBody AnimalTransferRequest request) {
        String senderId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AnimalTransfer createdTransfer = animalTransferService.createTransfer(request, senderId);
        return ResponseEntity.ok(createdTransfer);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<AnimalTransfer>> getPendingTransfers() {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<AnimalTransfer> pendingTransfers = animalTransferService.getPendingTransfersForUser(userId);
        return ResponseEntity.ok(pendingTransfers);
    }

    @PostMapping("/{transferId}/accept")
    public ResponseEntity<?> acceptTransfer(@PathVariable String transferId) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return animalTransferService.acceptTransfer(transferId, userId)
                .map(transfer -> ResponseEntity.ok().body("Transfer completed successfully."))
                .orElse(ResponseEntity.badRequest().body("Failed to accept transfer. It might be invalid or not addressed to you."));
    }

    @GetMapping("/sent")
    public ResponseEntity<List<AnimalTransfer>> getSentTransfers() {
        String senderId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(animalTransferService.getSentTransfers(senderId));
    }

    @GetMapping("/received")
    public ResponseEntity<List<AnimalTransfer>> getReceivedTransfers() {
        String receiverId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(animalTransferService.getReceivedTransfers(receiverId));
    }
} 