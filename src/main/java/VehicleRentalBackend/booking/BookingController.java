
package VehicleRentalBackend.booking;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import VehicleRentalBackend.user.UserRepository;
import VehicleRentalBackend.vehicle.Vehicle;
import VehicleRentalBackend.vehicle.VehicleRepository;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:5173")
public class BookingController {

    private final BookingRepository bookings;
    private final UserRepository users;
    private final VehicleRepository vehicles;

    public BookingController(
            BookingRepository bookings,
            UserRepository users,
            VehicleRepository vehicles) {

        this.bookings = bookings;
        this.users = users;
        this.vehicles = vehicles;
    }

    // Get all bookings
    @GetMapping
    public List<Booking> all() {
        return bookings.findAll();
    }

    // Get bookings for a specific customer
    @GetMapping("/user/{userId}")
    public List<Booking> byUser(@PathVariable Long userId) {
        return bookings.findByUserId(userId);
    }

    // Create a booking
    @PostMapping
    public Booking create(@RequestBody BookingInput input) {

        // Validate required fields and dates
        if (input.userId() == null
                || input.vehicleId() == null
                || input.startDate() == null
                || input.endDate() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Customer, vehicle, pickup date and return date are required."
            );
        }

        // Validate rental dates
        if (!input.endDate().isAfter(input.startDate())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Return date must be after pickup date."
            );
        }

        // Check whether customer exists
        users.findById(input.userId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found."
                ));

        // Check whether vehicle exists
        Vehicle vehicle = vehicles.findById(input.vehicleId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Vehicle not found."
                ));

        // Check vehicle availability
        if (!"AVAILABLE".equalsIgnoreCase(vehicle.getStatus())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This vehicle is not available."
            );
        }

        // Calculate rental duration
        long days = ChronoUnit.DAYS.between(
                input.startDate(),
                input.endDate()
        );

        int totalDays = Math.toIntExact(days);

        // Create booking
        Booking booking = new Booking();

        booking.setUserId(input.userId());
        booking.setVehicleId(input.vehicleId());
        booking.setStartDate(input.startDate());
        booking.setEndDate(input.endDate());
        booking.setTotalDays(totalDays);

        // Calculate total rental amount
        booking.setTotalAmount(
                vehicle.getPricePerDay() * totalDays
        );

        // Set initial booking status
        booking.setStatus("PENDING");

        // Save booking to the database
        return bookings.save(booking);
    }

    // Update booking status
    @PutMapping("/{id}/status")
    public Booking updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        // Find booking by ID
        Booking booking = bookings.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Booking not found."
                ));

        // Get requested status
        String requestedStatus = body.get("status");

        // Validate status
        if (requestedStatus == null
                || !List.of("APPROVED", "REJECTED", "CANCELLED")
                        .contains(requestedStatus.toUpperCase())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid booking status."
            );
        }

        // Store uppercase status in a final variable
        // This fixes the lambda compilation error
        final String status = requestedStatus.toUpperCase();

        // Update booking status
        booking.setStatus(status);

        // Update vehicle status
        vehicles.findById(booking.getVehicleId())
                .ifPresent(vehicle -> {

                    if ("APPROVED".equals(status)) {

                        // Mark vehicle as rented
                        vehicle.setStatus("RENTED");

                    } else if ("RENTED".equalsIgnoreCase(
                            vehicle.getStatus())) {

                        // Make vehicle available again
                        vehicle.setStatus("AVAILABLE");
                    }

                    // Save vehicle changes
                    vehicles.save(vehicle);
                });

        // Save and return updated booking
        return bookings.save(booking);
    }

    // Request body from frontend
    public record BookingInput(
            Long userId,
            Long vehicleId,
            java.time.LocalDate startDate,
            java.time.LocalDate endDate) {
    }
}
