package hexlet.code.app.component;

import hexlet.code.app.model.Label;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final LabelRepository labelRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            TaskStatusRepository taskStatusRepository,
            LabelRepository labelRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.labelRepository = labelRepository;
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

        createLabelIfNotExists("feature");
        createLabelIfNotExists("bug");
    }

    private void createAdminIfNotExists() {
        if (!userRepository.existsByEmail("hexlet@example.com")) {
            var user = new User();

            user.setEmail("hexlet@example.com");
            user.setPassword(passwordEncoder.encode("qwerty"));

            userRepository.save(user);
        }
    }

    private void createStatusIfNotExists(
            String name,
            String slug) {

        if (taskStatusRepository.findBySlug(slug).isEmpty()) {
            var status = new TaskStatus();

            status.setName(name);
            status.setSlug(slug);

            taskStatusRepository.save(status);
        }
    }

    private void createLabelIfNotExists(String name) {
        if (labelRepository.findByName(name).isEmpty()) {
            var label = new Label();
            label.setName(name);

            labelRepository.save(label);
        }
    }
}