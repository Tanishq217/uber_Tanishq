package com.tanishq.uber.model;

import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;

@Document(collection = "rides")
public class Ride {
    @Id
    private String id;

    private String userId;      // The passenger
    private String driverId;    // The driver (can be null initially)
    private String pickupLocation;
    private String dropLocation;
    private String status;      // REQUESTED, ACCEPTED, COMPLETED
    private LocalDateTime createdAt;

    // Constructors
    public Ride() {
        this.createdAt = LocalDateTime.now();
    }

    public Ride(String userId, String pickupLocation, String dropLocation, String status) {
        this.userId = userId;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }

    public String getDropLocation() { return dropLocation; }
    public void setDropLocation(String dropLocation) { this.dropLocation = dropLocation; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}