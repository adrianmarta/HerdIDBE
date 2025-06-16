package com.example.farmerapp.repositories;

import com.example.farmerapp.models.Interest;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface InterestRepository extends MongoRepository<Interest, String> {
    Optional<Interest> findByUserIdAndSalePostId(String userId, String salePostId);
    List<Interest> findBySalePostId(String salePostId);
    List<Interest> findByUserId(String userId);
    void deleteByUserIdAndSalePostId(String userId, String salePostId);
} 