package hexlet.code.app.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class LabelControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private LabelRepository labelRepository;

  @Autowired private TaskRepository taskRepository;

  @Autowired private TaskStatusRepository taskStatusRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  private User user;
  private TaskStatus status;

  @BeforeEach
  void setUp() {
    taskRepository.deleteAll();
    labelRepository.deleteAll();
    taskStatusRepository.deleteAll();
    userRepository.deleteAll();

    user = new User();
    user.setEmail("hexlet@example.com");
    user.setPassword(passwordEncoder.encode("qwerty"));
    user = userRepository.save(user);

    status = new TaskStatus();
    status.setName("Draft");
    status.setSlug("draft");
    status = taskStatusRepository.save(status);
  }

  private String getToken() throws Exception {
    var request =
        """
                {
                  "username": "hexlet@example.com",
                  "password": "qwerty"
                }
                """;

    var result =
        mockMvc
            .perform(post("/api/login").contentType(MediaType.APPLICATION_JSON).content(request))
            .andExpect(status().isOk())
            .andReturn();

    return result.getResponse().getContentAsString();
  }

  @Test
  void testCreateLabel() throws Exception {
    var request =
        """
                {
                  "name": "feature"
                }
                """;

    mockMvc
        .perform(
            post("/api/labels")
                .header("Authorization", "Bearer " + getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("feature"));
  }

  @Test
  void testGetLabels() throws Exception {
    var label = new Label();
    label.setName("feature");
    labelRepository.save(label);

    mockMvc
        .perform(get("/api/labels").header("Authorization", "Bearer " + getToken()))
        .andExpect(status().isOk())
        .andExpect(header().string("X-Total-Count", "1"))
        .andExpect(jsonPath("$[0].name").value("feature"));
  }

  @Test
  void testGetLabelById() throws Exception {
    var label = new Label();
    label.setName("feature");
    label = labelRepository.save(label);

    mockMvc
        .perform(
            get("/api/labels/" + label.getId()).header("Authorization", "Bearer " + getToken()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("feature"));
  }

  @Test
  void testUpdateLabel() throws Exception {
    var label = new Label();
    label.setName("feature");
    label = labelRepository.save(label);

    var request =
        """
                {
                  "name": "updated"
                }
                """;

    mockMvc
        .perform(
            put("/api/labels/" + label.getId())
                .header("Authorization", "Bearer " + getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("updated"));
  }

  @Test
  void testDeleteLabel() throws Exception {
    var label = new Label();
    label.setName("feature");
    label = labelRepository.save(label);

    mockMvc
        .perform(
            delete("/api/labels/" + label.getId()).header("Authorization", "Bearer " + getToken()))
        .andExpect(status().isNoContent());

    mockMvc
        .perform(
            get("/api/labels/" + label.getId()).header("Authorization", "Bearer " + getToken()))
        .andExpect(status().isNotFound());
  }

  @Test
  void testInvalidLabelReturns400() throws Exception {
    var request =
        """
                {
                  "name": "ab"
                }
                """;

    mockMvc
        .perform(
            post("/api/labels")
                .header("Authorization", "Bearer " + getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testDuplicateLabelReturns409() throws Exception {
    var label = new Label();
    label.setName("feature");
    labelRepository.save(label);

    var request =
        """
                {
                  "name": "feature"
                }
                """;

    mockMvc
        .perform(
            post("/api/labels")
                .header("Authorization", "Bearer " + getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isConflict());
  }

  @Test
  void testLabelsRequireAuthentication() throws Exception {
    mockMvc.perform(get("/api/labels")).andExpect(status().isUnauthorized());
  }

  @Test
  void testCannotDeleteUsedLabel() throws Exception {
    var label = new Label();
    label.setName("feature");
    label = labelRepository.save(label);

    var task = new Task();
    task.setName("Task");
    task.setTaskStatus(status);
    task.setAssignee(user);
    task.setLabels(Set.of(label));
    taskRepository.save(task);

    mockMvc
        .perform(
            delete("/api/labels/" + label.getId()).header("Authorization", "Bearer " + getToken()))
        .andExpect(status().isConflict());
  }
}
