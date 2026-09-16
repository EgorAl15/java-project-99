package hexlet.code.app.component;

import hexlet.code.app.TaskStatus;
import hexlet.code.app.User;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            TaskStatusRepository taskStatusRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createAdminIfNotExists();

        createStatusIfNotExists("Draft", "draft");
        createStatusIfNotExists("To Review", "to_review");
        createStatusIfNotExists("To Be Fixed", "to_be_fixed");
        createStatusIfNotExists("To Publish", "to_publish");
        createStatusIfNotExists("Published", "published");
    }

    private void createAdminIfNotExists() {
        if (!userRepository.existsByEmail("hexlet@example.com")) {
            var user = new User();

            user.setEmail("hexlet@example.com");
            user.setPassword(passwordEncoder.encode("qwerty"));

            userRepository.save(user);
        }
    }

    private void createStatusIfNotExists(String name, String slug) {
        if (taskStatusRepository.findBySlug(slug).isEmpty()) {
            var status = new TaskStatus();

            status.setName(name);
            status.setSlug(slug);

            taskStatusRepository.save(status);
        }
    }
}