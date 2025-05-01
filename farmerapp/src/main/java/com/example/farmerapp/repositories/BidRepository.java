package com.example.farmerapp.repositories;

import com.example.farmerapp.models.Bid;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface BidRepository extends MongoRepository<Bid, String> {
    List<Bid> findByBidderId(String bidderId);
    List<Bid> findBySalePostId(String salePostId);
}
