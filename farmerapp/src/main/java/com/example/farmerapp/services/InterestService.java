package com.example.farmerapp.services;

import com.example.farmerapp.models.Interest;
import com.example.farmerapp.models.SalePost;
import com.example.farmerapp.models.User;
import com.example.farmerapp.repositories.InterestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InterestService {
    @Autowired
    private InterestRepository interestRepository;

    public Optional<Interest> getInterest(String userId, String salePostId) {
        return interestRepository.findByUserIdAndSalePostId(userId, salePostId);
    }

    public Interest addInterest(User user, SalePost salePost) {
        Optional<Interest> existing = interestRepository.findByUserIdAndSalePostId(user.getId(), salePost.getId());
        if (existing.isPresent()) {
            return existing.get();
        }
        Interest interest = new Interest(user, salePost);
        return interestRepository.save(interest);
    }

    public void removeInterest(String userId, String salePostId) {
        interestRepository.deleteByUserIdAndSalePostId(userId, salePostId);
    }

    public List<Interest> getInterestsBySalePost(String salePostId) {
        return interestRepository.findBySalePostId(salePostId);
    }

    public List<Interest> getInterestsByUser(String userId) {
        return interestRepository.findByUserId(userId);
    }
} 