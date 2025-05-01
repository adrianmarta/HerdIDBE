package com.example.farmerapp.repositories;




import com.example.farmerapp.models.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByIdAndEmail(String id, String email);
    boolean existsById(@NotNull String id);
    boolean existsByEmail(String email);
}
