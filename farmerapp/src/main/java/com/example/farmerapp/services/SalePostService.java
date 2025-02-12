package com.example.farmerapp.services;

import com.example.farmerapp.models.SalePost;
import com.example.farmerapp.repositories.SalePostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalePostService {

    @Autowired
    private SalePostRepository salePostRepository;

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
}
