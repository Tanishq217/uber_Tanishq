package com.tanishq.uber.repository;

import com.tanishq.uber.model.Ride;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface RideRepository extends MongoRepository<Ride, String> {
    // Find all rides belonging to a specific passenger (User)
    List<Ride> findByUserId(String userId);

    // Find all rides with a specific status (e.g., "REQUESTED" for drivers to see)
    List<Ride> findByStatus(String status);
}