package com.example.farmerapp.services;

import com.example.farmerapp.models.Bid;
import com.example.farmerapp.repositories.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BidService {

    @Autowired
    private BidRepository bidRepository;

    public Bid createBid(Bid bid) {
        return bidRepository.save(bid);
    }

    public List<Bid> getBidsByUser(String userId) {
        return bidRepository.findByBidderId(userId);
    }

    public List<Bid> getBidsBySalePost(String postId) {
        return bidRepository.findBySalePostId(postId);
    }

    public Optional<Bid> getBidById(String id) {
        return bidRepository.findById(id);
    }

    public void deleteBid(String id) {
        bidRepository.deleteById(id);
    }
    public Optional<Bid> getWinningBid(String postId) {
        return bidRepository.findBySalePostId(postId)
                .stream()
                .filter(Bid::isWinner)
                .findFirst();
    }


}
