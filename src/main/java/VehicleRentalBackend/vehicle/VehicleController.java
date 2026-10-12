package VehicleRentalBackend.vehicle;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "http://localhost:5173")
public class VehicleController {
    private final VehicleRepository vehicles;
    public VehicleController(VehicleRepository vehicles) { this.vehicles = vehicles; }
    @GetMapping public List<Vehicle> all() { return vehicles.findAll(); }
    @GetMapping("/{id}") public Vehicle get(@PathVariable Long id) { return find(id); }
    @PostMapping public Vehicle create(@RequestBody Vehicle vehicle) { validate(vehicle); return vehicles.save(vehicle); }
    @PutMapping("/{id}") public Vehicle update(@PathVariable Long id, @RequestBody Vehicle input) {
        Vehicle vehicle = find(id); validate(input);
        vehicle.setVehicleNumber(input.getVehicleNumber()); vehicle.setBrand(input.getBrand()); vehicle.setModel(input.getModel());
        vehicle.setType(input.getType()); vehicle.setPricePerDay(input.getPricePerDay()); vehicle.setFuelType(input.getFuelType());
        vehicle.setSeats(input.getSeats()); vehicle.setStatus(input.getStatus()); vehicle.setImageUrl(input.getImageUrl());
        return vehicles.save(vehicle);
    }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { vehicles.delete(find(id)); }
    private Vehicle find(Long id) { return vehicles.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found.")); }
    private void validate(Vehicle v) {
        if (v.getVehicleNumber() == null || v.getVehicleNumber().isBlank() || v.getBrand() == null || v.getBrand().isBlank() ||
            v.getModel() == null || v.getModel().isBlank() || v.getType() == null || v.getType().isBlank() ||
            v.getFuelType() == null || v.getFuelType().isBlank() || v.getPricePerDay() == null || v.getPricePerDay() <= 0 ||
            v.getSeats() == null || v.getSeats() < 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter all required vehicle details and a positive daily price.");
        if (v.getStatus() == null || !List.of("AVAILABLE", "RENTED", "MAINTENANCE").contains(v.getStatus())) v.setStatus("AVAILABLE");
    }
}
