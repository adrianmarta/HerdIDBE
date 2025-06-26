package com.example.farmerapp.repositories;

import com.example.farmerapp.models.AnimalTransfer;
import com.example.farmerapp.models.TransferStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalTransferRepository extends MongoRepository<AnimalTransfer, String> {
    List<AnimalTransfer> findBySenderId(String senderId);
    List<AnimalTransfer> findByReceiverId(String receiverId);
    List<AnimalTransfer> findByReceiverIdAndStatus(String receiverId, TransferStatus status);
} 