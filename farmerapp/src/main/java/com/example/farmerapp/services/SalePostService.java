package com.example.farmerapp.services;

import com.example.farmerapp.models.Bid;
import com.example.farmerapp.models.SalePost;
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
    public SalePost approveWinningBid(String postId, Bid winningBid) {
        Optional<SalePost> optionalSalePost = salePostRepository.findById(postId);
        if (optionalSalePost.isEmpty()) {
            throw new IllegalArgumentException("Sale Post not found.");
        }

        SalePost salePost = optionalSalePost.get();

        // ✅ Ensure only expired posts can be updated
        if (LocalDateTime.now().isBefore(salePost.getExpiryDate())) {
            throw new IllegalStateException("Cannot approve bids before expiry date.");
        }

        salePost.setWinnerBid(winningBid);
        salePost.setSold(true);
        return salePostRepository.save(salePost);
    }
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void autoApproveExpiredBids() {
        List<SalePost> expiredPosts = salePostRepository.findAll().stream()
                .filter(post -> post.getExpiryDate().isBefore(LocalDateTime.now()) && !post.isSold())
                .toList();

        for (SalePost post : expiredPosts) {
            List<Bid> bids = post.getBids();

            if (!bids.isEmpty()) {
                // ✅ Get the highest bid
                Bid highestBid = bids.stream()
                        .max(Comparator.comparingDouble(Bid::getBidAmount))
                        .orElse(null);

                if (highestBid != null && !highestBid.isWinner()) {
                    highestBid.setWinner(true);
                    bidRepository.save(highestBid);

                    post.setWinnerBid(highestBid);
                    post.setSold(true);
                    salePostRepository.save(post);

                    // ✅ Notify the winner
                    smsService.sendSms(
                            highestBid.getBidder().getPhoneNumber(),
                            "🎉 Congrats! You've automatically won the bid for " + post.getTitle() + "."
                    );
                }
            }
        }
    }
    public void deleteSalePost(String id) {
        salePostRepository.deleteById(id);
    }

    public List<SalePost> getSalePostsByOwner(String ownerId) {
        return salePostRepository.findByOwnerId(ownerId);
    }
}
