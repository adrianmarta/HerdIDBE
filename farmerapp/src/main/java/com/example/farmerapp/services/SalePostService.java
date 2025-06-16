package com.example.farmerapp.services;

import com.example.farmerapp.models.Bid;
import com.example.farmerapp.models.SalePost;
import com.example.farmerapp.models.SalePostFilterDTO;
import com.example.farmerapp.repositories.BidRepository;
import com.example.farmerapp.repositories.SalePostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class SalePostService {

    @Autowired
    private SalePostRepository salePostRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private SmsService smsService;

    public SalePost createSalePost(SalePost salePost) {
        return salePostRepository.save(salePost);
    }

    public List<SalePost> getAllSalePosts() {
        return salePostRepository.findAll();
    }

    public Optional<SalePost> getSalePostById(String id) {
        return salePostRepository.findById(id);
    }

    public SalePost updateSalePost(String id, SalePost salePost) {
        if (salePostRepository.existsById(id)) {
            salePost.setId(id);
            return salePostRepository.save(salePost);
        } else {
            throw new IllegalArgumentException("Sale Post not found.");
        }
    }


    public void deleteSalePost(String id) {
        salePostRepository.deleteById(id);
    }

    public List<SalePost> getSalePostsByOwner(String ownerId) {
        return salePostRepository.findByOwnerId(ownerId);
    }

    public List<SalePost> getFilteredSalePosts(SalePostFilterDTO filter) {
        List<SalePost> allPosts = salePostRepository.findAll();
        
        return allPosts.stream()
            .filter(post -> {
                // Filter out user's own posts
                if (filter.getUserId() != null && post.getOwner().getId().equals(filter.getUserId())) {
                    return false;
                }

                // Price filter
                if (filter.getMinPrice() != null && post.getPrice() < filter.getMinPrice()) {
                    return false;
                }
                if (filter.getMaxPrice() != null && post.getPrice() > filter.getMaxPrice()) {
                    return false;
                }

                // Location filter
                if (filter.getLocation() != null && !filter.getLocation().isEmpty()) {
                    String postLocation = post.getOwner().getAddress();
                    if (postLocation == null || !postLocation.toLowerCase().contains(filter.getLocation().toLowerCase())) {
                        return false;
                    }
                }

                // Species filter
                if (filter.getSpecies() != null && !filter.getSpecies().isEmpty()) {
                    boolean hasMatchingSpecies = post.getAnimals().stream()
                        .anyMatch(animal -> filter.getSpecies().equals(animal.getSpecies()));
                    if (!hasMatchingSpecies) {
                        return false;
                    }
                }

                return true;
            })
            .toList();
    }
}
