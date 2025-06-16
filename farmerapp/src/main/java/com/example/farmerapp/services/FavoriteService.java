package com.example.farmerapp.services;

import com.example.farmerapp.models.Favorite;
import com.example.farmerapp.models.SalePost;
import com.example.farmerapp.models.User;
import com.example.farmerapp.repositories.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepository;

    public Optional<Favorite> getFavorite(String userId, String salePostId) {
        return favoriteRepository.findByUserIdAndSalePostId(userId, salePostId);
    }

    public Favorite addFavorite(User user, SalePost salePost) {
        Favorite favorite = new Favorite(user, salePost);
        return favoriteRepository.save(favorite);
    }

    public void removeFavorite(String userId, String salePostId) {
        favoriteRepository.deleteByUserIdAndSalePostId(userId, salePostId);
    }

    public List<Favorite> getFavoritesByUser(String userId) {
        return favoriteRepository.findByUserId(userId);
    }
}
