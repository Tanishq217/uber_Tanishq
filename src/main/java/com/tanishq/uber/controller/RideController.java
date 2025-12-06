package com.tanishq.uber.controller;

import com.tanishq.uber.dto.RideRequest;
import com.tanishq.uber.model.Ride;
import com.tanishq.uber.model.User;
import com.tanishq.uber.repository.RideRepository;
import com.tanishq.uber.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class RideController {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    public RideController(RideRepository rideRepository, UserRepository userRepository) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/rides")
    public ResponseEntity<?> requestRide(@Valid @RequestBody RideRequest request,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Ride ride = new Ride(user.getId(), request.getPickupLocation(), request.getDropLocation(), "REQUESTED");
        rideRepository.save(ride);
        return ResponseEntity.ok(ride);
    }

    @GetMapping("/user/rides")
    public ResponseEntity<List<Ride>> getMyRides(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(rideRepository.findByUserId(user.getId()));
    }

    @GetMapping("/driver/rides/requests")
    public ResponseEntity<List<Ride>> getRideRequests() {
        return ResponseEntity.ok(rideRepository.findByStatus("REQUESTED"));
    }

    @PostMapping("/driver/rides/{rideId}/accept")
    public ResponseEntity<?> acceptRide(@PathVariable String rideId, @AuthenticationPrincipal UserDetails userDetails) {
        User driver = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found"));

        if (!"REQUESTED".equals(ride.getStatus())) {
            return ResponseEntity.badRequest().body("Ride is not available");
        }

        ride.setDriverId(driver.getId());
        ride.setStatus("ACCEPTED");
        rideRepository.save(ride);
        return ResponseEntity.ok("Ride Accepted");
    }

    @PostMapping("/rides/{rideId}/complete")
    public ResponseEntity<?> completeRide(@PathVariable String rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found"));
        if (!"ACCEPTED".equals(ride.getStatus())) {
            return ResponseEntity.badRequest().body("Ride cannot be completed unless accepted");
        }
        ride.setStatus("COMPLETED");
        rideRepository.save(ride);
        return ResponseEntity.ok("Ride Completed");
    }
}