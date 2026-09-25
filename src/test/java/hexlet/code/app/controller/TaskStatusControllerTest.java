package hexlet.code.app.controller;

import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();

        var admin = new User();
        admin.setEmail("hexlet@example.com");
        admin.setPassword(passwordEncoder.encode("qwerty"));

        userRepository.save(admin);
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
    void testGetStatuses() throws Exception {
        var taskStatus = new TaskStatus();
        taskStatus.setName("Draft");
        taskStatus.setSlug("draft");

        taskStatusRepository.save(taskStatus);

        mockMvc.perform(get("/api/task_statuses")
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$[0].name").value("Draft"))
                .andExpect(jsonPath("$[0].slug").value("draft"));
    }

    @Test
    void testGetStatusById() throws Exception {
        var taskStatus = new TaskStatus();
        taskStatus.setName("Draft");
        taskStatus.setSlug("draft");

        taskStatus = taskStatusRepository.save(taskStatus);

        mockMvc.perform(get("/api/task_statuses/" + taskStatus.getId())
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskStatus.getId()))
                .andExpect(jsonPath("$.name").value("Draft"))
                .andExpect(jsonPath("$.slug").value("draft"));
    }

    @Test
    void testCreateStatus() throws Exception {
        var request = """
                {
                  "name": "Test Status",
                  "slug": "test_status"
                }
                """;

        mockMvc.perform(post("/api/task_statuses")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Status"))
                .andExpect(jsonPath("$.slug").value("test_status"));
    }

    @Test
    void testUpdateStatusPartially() throws Exception {
        var taskStatus = new TaskStatus();
        taskStatus.setName("Old Status");
        taskStatus.setSlug("old_status");

        taskStatus = taskStatusRepository.save(taskStatus);

        var request = """
                {
                  "name": "Updated Status"
                }
                """;

        mockMvc.perform(put("/api/task_statuses/" + taskStatus.getId())
                        .header("Authorization", "Bearer " + getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Status"))
                .andExpect(jsonPath("$.slug").value("old_status"));
    }

    @Test
    void testDeleteStatus() throws Exception {
        var taskStatus = new TaskStatus();
        taskStatus.setName("Temporary");
        taskStatus.setSlug("temporary");

        taskStatus = taskStatusRepository.save(taskStatus);

        mockMvc.perform(delete("/api/task_statuses/" + taskStatus.getId())
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/task_statuses/" + taskStatus.getId())
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testInvalidStatusReturns400() throws Exception {
        var request = """
                {
                  "name": "",
                  "slug": ""
                }
                """;

        mockMvc.perform(post("/api/task_statuses")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testStatusesRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/task_statuses"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testFindBySlug() {
        var taskStatus = new TaskStatus();
        taskStatus.setName("Draft");
        taskStatus.setSlug("draft");

        taskStatusRepository.save(taskStatus);

        var result = taskStatusRepository.findBySlug("draft");

        assertTrue(result.isPresent());
        assertEquals("Draft", result.get().getName());
    }
}