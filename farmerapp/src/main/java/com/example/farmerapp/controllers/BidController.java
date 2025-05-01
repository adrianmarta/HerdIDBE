package com.example.farmerapp.controllers;

import com.example.farmerapp.BidRequest;
import com.example.farmerapp.JwtUtil;
import com.example.farmerapp.models.Bid;
import com.example.farmerapp.models.SalePost;
import com.example.farmerapp.models.User;
import com.example.farmerapp.services.BidService;
import com.example.farmerapp.services.SalePostService;
import com.example.farmerapp.services.SmsService;
import com.example.farmerapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/bids")
public class BidController {

    @Autowired
    private BidService bidService;

    @Autowired
    private UserService userService;

    @Autowired
    private SalePostService salePostService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private JwtUtil jwtUtil;

    // ✅ Place a Bid
    @PostMapping
    public ResponseEntity<?> placeBid(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody BidRequest bidRequest) {

        String token = authHeader.replace("Bearer ", "");
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
        System.out.println( bidRequest.getSalePostID());
        String userId = jwtUtil.extractUserId(token);
        Optional<User> user = userService.getUserById(userId);
        Optional<SalePost> salePost = salePostService.getSalePostById(bidRequest.getSalePostID());

        if (user.isEmpty() || salePost.isEmpty()) {
            return ResponseEntity.status(404).body("User or Sale Post not found");
        }

        if (bidRequest.getBidAmount() < salePost.get().getPrice()) {
            return ResponseEntity.status(400).body("Bid must be at least the asking price");
        }

        Bid newBid = new Bid(user.get(), salePost.get(),bidRequest.getBidAmount() );
        Bid savedBid = bidService.createBid(newBid);
        return ResponseEntity.ok(savedBid);
    }

    // ✅ Get Bids for Logged-in User
    @GetMapping("/user")
    public ResponseEntity<List<Bid>> getUserBids(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(null);
        }

        String userId = jwtUtil.extractUserId(token);
        List<Bid> bids = bidService.getBidsByUser(userId);
        return ResponseEntity.ok(bids);
    }

    // ✅ Get Bids for a Specific Sale Post
    @GetMapping("/sale-post/{postId}")
    public ResponseEntity<List<Bid>> getBidsBySalePost(@PathVariable String postId) {
        List<Bid> bids = bidService.getBidsBySalePost(postId);
        return ResponseEntity.ok(bids);
    }

    // ✅ Accept a Bid
    @PostMapping("/approve/{bidId}")
    public ResponseEntity<?> approveBid(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String bidId) {

        String token = authHeader.replace("Bearer ", "");
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        Optional<Bid> bidOptional = bidService.getBidById(bidId);
        if (bidOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Bid not found");
        }

        Bid bid = bidOptional.get();
        SalePost salePost = bid.getSalePost();

        // ✅ Ensure the expiry date has passed
        if (LocalDateTime.now().isBefore(salePost.getExpiryDate())) {
            return ResponseEntity.status(400).body("Cannot approve a bid before expiry date.");
        }

        // ✅ Ensure only the post owner can approve a bid
        String ownerId = jwtUtil.extractUserId(token);
        if (!salePost.getOwner().getId().equals(ownerId)) {
            return ResponseEntity.status(403).body("You are not authorized to approve this bid.");
        }

        // ✅ Mark bid as the winner
        bid.setWinner(true);
        bidService.createBid(bid);

        salePostService.approveWinningBid(salePost.getId(), bid);

        // ✅ Send SMS to the Winner
        sendWinnerNotification(bid);

        return ResponseEntity.ok("Bid approved! Winner has been notified.");
    }


    private void sendWinnerNotification(Bid bid) {
        String phoneNumber = bid.getBidder().getPhoneNumber();
        String paymentLink = "http://localhost:5173/payment/" + bid.getSalePost().getId();

        String message = "🎉 Congrats! You've won the bid for " + bid.getSalePost().getTitle() +
                ". Pay now: " + paymentLink;

        smsService.sendSms(phoneNumber, message);
    }


    // ✅ Delete a Bid
    @DeleteMapping("/{bidId}")
    public ResponseEntity<Void> deleteBid(@PathVariable String bidId) {
        bidService.deleteBid(bidId);
        return ResponseEntity.noContent().build();
    }
}
