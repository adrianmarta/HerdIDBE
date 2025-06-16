package com.example.farmerapp.repositories;

import com.example.farmerapp.models.Favorite;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends MongoRepository<Favorite, String> {
    Optional<Favorite> findByUserIdAndSalePostId(String userId, String salePostId);
    List<Favorite> findByUserId(String userId);
    List<Favorite> findBySalePostId(String salePostId);
    void deleteByUserIdAndSalePostId(String userId, String salePostId);
}
