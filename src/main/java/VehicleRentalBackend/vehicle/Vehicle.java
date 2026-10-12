package VehicleRentalBackend.vehicle;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String vehicleNumber;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Double pricePerDay;

    @Column(nullable = false)
    private String fuelType;

    @Column(nullable = false)
    private Integer seats;

    @Column(nullable = false)
    private String status;

    @Column(length = 1000)
    private String imageUrl;

    // Default No-Args Constructor
    public Vehicle() {
    }

    // All-Args Constructor
    public Vehicle(Long id, String vehicleNumber, String brand, String model, String type, Double pricePerDay, String fuelType, Integer seats, String status, String imageUrl) {
        this.id = id;
        this.vehicleNumber = vehicleNumber;
        this.brand = brand;
        this.model = model;
        this.type = type;
        this.pricePerDay = pricePerDay;
        this.fuelType = fuelType;
        this.seats = seats;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    // Constructor without ID
    public Vehicle(String vehicleNumber, String brand, String model, String type, Double pricePerDay, String fuelType, Integer seats, String status, String imageUrl) {
        this.vehicleNumber = vehicleNumber;
        this.brand = brand;
        this.model = model;
        this.type = type;
        this.pricePerDay = pricePerDay;
        this.fuelType = fuelType;
        this.seats = seats;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    // Aliases to maintain backward compatibility if other classes use registrationPlate
    public String getRegistrationPlate() {
        return vehicleNumber;
    }

    public void setRegistrationPlate(String registrationPlate) {
        this.vehicleNumber = registrationPlate;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(Double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    // Aliases for price
    public Double getPrice() {
        return pricePerDay;
    }

    public void setPrice(Double price) {
        this.pricePerDay = price;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    // Aliases for seating capacity
    public Integer getSeatingCapacity() {
        return seats;
    }

    public void setSeatingCapacity(Integer seatingCapacity) {
        this.seats = seatingCapacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}