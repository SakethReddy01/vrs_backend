package VehicleRentalBackend.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<RentalUser, Long> {
    Optional<RentalUser> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
