
package VehicleRentalBackend.user;

import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserRepository users;
    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public UserController(UserRepository users) {
        this.users = users;
    }

    // Register a new customer
    @PostMapping("/register")
    public ResponseEntity<RentalUser> register(
            @RequestBody UserInput input) {

        validate(input, true);

        String email = input.email().trim().toLowerCase(Locale.ROOT);

        if (users.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An account with this email already exists.");
        }

        RentalUser user = new RentalUser();
        user.setName(input.name().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(input.password()));
        user.setPhone(input.phone().trim());
        user.setRole("CUSTOMER");

        RentalUser savedUser = users.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedUser);
    }

    // Login
    @PostMapping("/login")
    public RentalUser login(@RequestBody LoginInput input) {

        if (input == null || input.email() == null
                || input.password() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password.");
        }

        return users.findByEmailIgnoreCase(input.email().trim())
                .filter(user -> {
                    String stored = user.getPassword();
                    String entered = input.password();

                    if (stored == null) {
                        return false;
                    }

                    // Check an already encrypted password
                    if (stored.startsWith("$2a$")
                            || stored.startsWith("$2b$")
                            || stored.startsWith("$2y$")) {
                        return passwordEncoder.matches(entered, stored);
                    }

                    // Upgrade a legacy plain-text password after login
                    if (entered.equals(stored)) {
                        user.setPassword(
                                passwordEncoder.encode(entered));
                        users.save(user);
                        return true;
                    }

                    return false;
                })
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password."));
    }

    // Get all users
    @GetMapping
    public List<RentalUser> getAllUsers() {
        return users.findAll();
    }

    // Get user by ID
    @GetMapping("/{id}")
    public RentalUser getUser(@PathVariable Long id) {
        return findUser(id);
    }

    // Update user
    @PutMapping("/{id}")
    public RentalUser updateUser(
            @PathVariable Long id,
            @RequestBody UserInput input) {

        RentalUser user = findUser(id);

        if (input.name() == null || input.name().isBlank()
                || input.email() == null || input.email().isBlank()
                || input.phone() == null || input.phone().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Name, email and phone are required.");
        }

        String email = input.email().trim().toLowerCase(Locale.ROOT);

        users.findByEmailIgnoreCase(email)
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "That email address is already in use.");
                });

        user.setName(input.name().trim());
        user.setEmail(email);
        user.setPhone(input.phone().trim());

        if (input.password() != null
                && !input.password().isBlank()) {

            if (input.password().length() < 6) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Password must be at least 6 characters.");
            }

            user.setPassword(
                    passwordEncoder.encode(input.password()));
        }

        return users.save(user);
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        users.delete(findUser(id));
        return ResponseEntity.noContent().build();
    }

    // Find user by ID
    private RentalUser findUser(Long id) {
        return users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found."));
    }

    // Validate registration details
    private void validate(UserInput input, boolean includePassword) {

        if (input == null
                || input.name() == null || input.name().isBlank()
                || input.email() == null || input.email().isBlank()
                || input.phone() == null || input.phone().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Name, email and phone are required.");
        }

        if (includePassword
                && (input.password() == null
                || input.password().length() < 6)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password must be at least 6 characters.");
        }
    }

    // Request data for registration and updates
    public record UserInput(
            String name,
            String email,
            String password,
            String phone) {
    }

    // Request data for login
    public record LoginInput(
            String email,
            String password) {
    }
}
