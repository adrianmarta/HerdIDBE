package com.example.farmerapp.repositories;

import com.example.farmerapp.models.SalePost;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface SalePostRepository extends MongoRepository<SalePost, String> {
    List<SalePost> findByOwnerId(String ownerId);
}
