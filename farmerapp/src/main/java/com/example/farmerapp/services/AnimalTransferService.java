package com.example.farmerapp.services;

import com.example.farmerapp.dto.AnimalTransferRequest;
import com.example.farmerapp.models.Animal;
import com.example.farmerapp.models.AnimalTransfer;
import com.example.farmerapp.models.TransferStatus;
import com.example.farmerapp.models.User;
import com.example.farmerapp.repositories.AnimalTransferRepository;
import com.example.farmerapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AnimalTransferService {

    @Autowired
    private AnimalTransferRepository animalTransferRepository;

    @Autowired
    private AnimalService animalService;

    @Autowired
    private UserRepository userRepository;

    public AnimalTransfer createTransfer(AnimalTransferRequest request, String senderId) {
        AnimalTransfer transfer = new AnimalTransfer();
        transfer.setSenderId(senderId);
        transfer.setReceiverId(request.getReceiverId());
        transfer.setAnimalIds(request.getAnimalIds());
        transfer.setTransferDate(LocalDateTime.now());
        transfer.setStatus(TransferStatus.PENDING);
        return animalTransferRepository.save(transfer);
    }

    public List<AnimalTransfer> getPendingTransfersForUser(String userId) {
        return animalTransferRepository.findByReceiverIdAndStatus(userId, TransferStatus.PENDING)
                .stream()
                .sorted((t1, t2) -> t2.getTransferDate().compareTo(t1.getTransferDate()))
                .toList();
    }

    @Transactional
    public Optional<AnimalTransfer> acceptTransfer(String transferId, String receiverId) {
        Optional<AnimalTransfer> transferOptional = animalTransferRepository.findById(transferId);
        if (transferOptional.isEmpty() || !transferOptional.get().getReceiverId().equals(receiverId)
                || transferOptional.get().getStatus() != TransferStatus.PENDING) {
            return Optional.empty();
        }
        Optional<User> receiverOptional = userRepository.findById(receiverId);
        if (receiverOptional.isEmpty()) {
            return Optional.empty();
        }
        AnimalTransfer transfer = transferOptional.get();
        List<Animal> animalsToTransfer = animalService.getAnimalsByIds(transfer.getAnimalIds());
        for (Animal animal : animalsToTransfer) {
           animalService.changeOwner(animal.getId(),receiverId);
        }
        transfer.setStatus(TransferStatus.COMPLETED);
        return Optional.of(animalTransferRepository.save(transfer));
    }

    public List<AnimalTransfer> getSentTransfers(String senderId) {
        return animalTransferRepository.findBySenderId(senderId)
                .stream()
                .sorted((t1, t2) -> t2.getTransferDate().compareTo(t1.getTransferDate()))
                .toList();
    }

    public List<AnimalTransfer> getReceivedTransfers(String receiverId) {
        return animalTransferRepository.findByReceiverId(receiverId)
                .stream()
                .sorted((t1, t2) -> t2.getTransferDate().compareTo(t1.getTransferDate()))
                .toList();
    }

    public Optional<AnimalTransfer> rejectTransfer(String transferId, String receiverId) {
        Optional<AnimalTransfer> transferOptional = animalTransferRepository.findById(transferId);
        if (transferOptional.isEmpty() || !transferOptional.get().getReceiverId().equals(receiverId)
                || transferOptional.get().getStatus() != TransferStatus.PENDING) {
            return Optional.empty();
        }
        AnimalTransfer transfer = transferOptional.get();
        transfer.setStatus(TransferStatus.REJECTED);
        return Optional.of(animalTransferRepository.save(transfer));
    }

    public void deleteTransfer( String transferId)
    {
        Optional<AnimalTransfer> transfer=animalTransferRepository.findById(transferId);
        if(transfer.isPresent())
        {
            AnimalTransfer animalTransfer=transfer.get();
            animalTransferRepository.delete(animalTransfer);
        }
    }
} 