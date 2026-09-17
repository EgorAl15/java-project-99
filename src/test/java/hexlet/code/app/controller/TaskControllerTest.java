package hexlet.code.app.controller;

import hexlet.code.app.Task;
import hexlet.code.app.TaskStatus;
import hexlet.code.app.User;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;
    private TaskStatus status;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
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
        var request = """
                {
                  "username": "hexlet@example.com",
                  "password": "qwerty"
                }
                """;

        var result = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andReturn();

        return result.getResponse().getContentAsString();
    }

    @Test
    void testCreateTask() throws Exception {
        var request = """
                {
                  "index": 12,
                  "assignee_id": %d,
                  "title": "Test title",
                  "content": "Test content",
                  "status": "draft"
                }
                """.formatted(user.getId());

        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.index").value(12))
                .andExpect(jsonPath("$.assignee_id").value(user.getId()))
                .andExpect(jsonPath("$.title").value("Test title"))
                .andExpect(jsonPath("$.content").value("Test content"))
                .andExpect(jsonPath("$.status").value("draft"));
    }

    @Test
    void testGetTasks() throws Exception {
        var task = createTask();

        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$[0].id").value(task.getId()))
                .andExpect(jsonPath("$[0].title").value("Test title"))
                .andExpect(jsonPath("$[0].status").value("draft"));
    }

    @Test
    void testGetTaskById() throws Exception {
        var task = createTask();

        mockMvc.perform(get("/api/tasks/" + task.getId())
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value("Test title"))
                .andExpect(jsonPath("$.assignee_id").value(user.getId()))
                .andExpect(jsonPath("$.status").value("draft"));
    }

    @Test
    void testUpdateTaskPartially() throws Exception {
        var task = createTask();

        var request = """
                {
                  "title": "Updated title",
                  "content": "Updated content"
                }
                """;

        mockMvc.perform(put("/api/tasks/" + task.getId())
                        .header("Authorization", "Bearer " + getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated title"))
                .andExpect(jsonPath("$.content").value("Updated content"))
                .andExpect(jsonPath("$.status").value("draft"))
                .andExpect(jsonPath("$.assignee_id").value(user.getId()));
    }

    @Test
    void testDeleteTask() throws Exception {
        var task = createTask();

        mockMvc.perform(delete("/api/tasks/" + task.getId())
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks/" + task.getId())
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testInvalidTaskReturns400() throws Exception {
        var request = """
                {
                  "title": "",
                  "status": ""
                }
                """;

        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testTasksRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCannotDeleteAssignedUser() throws Exception {
        createTask();

        mockMvc.perform(delete("/api/users/" + user.getId())
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isConflict());
    }

    @Test
    void testCannotDeleteUsedStatus() throws Exception {
        createTask();

        mockMvc.perform(delete("/api/task_statuses/" + status.getId())
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isConflict());
    }

    private Task createTask() {
        var task = new Task();
        task.setName("Test title");
        task.setIndex(12);
        task.setDescription("Test content");
        task.setTaskStatus(status);
        task.setAssignee(user);

        return taskRepository.save(task);
    }
}