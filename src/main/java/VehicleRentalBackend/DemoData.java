package VehicleRentalBackend;

import VehicleRentalBackend.user.RentalUser;
import VehicleRentalBackend.user.UserRepository;
import VehicleRentalBackend.vehicle.Vehicle;
import VehicleRentalBackend.vehicle.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DemoData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DemoData(UserRepository userRepository, VehicleRepository vehicleRepository) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Seed Admin User
        if (!userRepository.existsByEmailIgnoreCase("admin@gmail.com")) {
            RentalUser admin = new RentalUser(
                "Admin",
                "admin@gmail.com",
                passwordEncoder.encode("admin123"),
                "9999999999",
                "ADMIN"
            );
            userRepository.save(admin);
        }

        // Seed Initial Vehicles
        if (vehicleRepository.count() == 0) {
            Vehicle v1 = new Vehicle();
            v1.setVehicleNumber("TS09AB1234");
            v1.setBrand("Toyota");
            v1.setModel("Fortuner");
            v1.setType("SUV");
            v1.setPricePerDay(3500.00);
            v1.setFuelType("Diesel");
            v1.setSeats(7);
            v1.setStatus("AVAILABLE");
            v1.setImageUrl("https://images.unsplash.com/photo-1533473359331-0135ef1b58bf");
            vehicleRepository.save(v1);

            Vehicle v2 = new Vehicle();
            v2.setVehicleNumber("TS07CD5678");
            v2.setBrand("Honda");
            v2.setModel("City");
            v2.setType("Sedan");
            v2.setPricePerDay(2000.00);
            v2.setFuelType("Petrol");
            v2.setSeats(5);
            v2.setStatus("AVAILABLE");
            v2.setImageUrl("https://images.unsplash.com/photo-1541899481282-d53bffe3c35d");
            vehicleRepository.save(v2);
        }
    }
}