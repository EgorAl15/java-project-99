package hexlet.code.app.component;

import hexlet.code.app.User;
import hexlet.code.app.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("hexlet@example.com")) {
            var user = new User();

            user.setEmail("hexlet@example.com");
            user.setPassword(passwordEncoder.encode("qwerty"));

            userRepository.save(user);
        }
    }
}