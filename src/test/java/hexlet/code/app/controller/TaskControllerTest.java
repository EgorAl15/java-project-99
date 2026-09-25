package hexlet.code.app.controller;

import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
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

import java.util.Set;

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
    private LabelRepository labelRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;
    private TaskStatus draftStatus;
    private TaskStatus reviewStatus;
    private Label feature;
    private Label bug;

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

        draftStatus = new TaskStatus();
        draftStatus.setName("Draft");
        draftStatus.setSlug("draft");
        draftStatus = taskStatusRepository.save(draftStatus);

        reviewStatus = new TaskStatus();
        reviewStatus.setName("To Review");
        reviewStatus.setSlug("to_review");
        reviewStatus = taskStatusRepository.save(reviewStatus);

        feature = new Label();
        feature.setName("feature");
        feature = labelRepository.save(feature);

        bug = new Label();
        bug.setName("bug");
        bug = labelRepository.save(bug);
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
                  "status": "draft",
                  "labels": [%d, %d]
                }
                """.formatted(
                user.getId(),
                feature.getId(),
                bug.getId()
        );

        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.index").value(12))
                .andExpect(jsonPath("$.assignee_id").value(user.getId()))
                .andExpect(jsonPath("$.title").value("Test title"))
                .andExpect(jsonPath("$.content").value("Test content"))
                .andExpect(jsonPath("$.status").value("draft"))
                .andExpect(jsonPath("$.labels.length()").value(2));
    }

    @Test
    void testGetTasks() throws Exception {
        var task = createTask(
                "Test title",
                "Test content",
                draftStatus,
                Set.of(feature, bug)
        );

        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$[0].id").value(task.getId()))
                .andExpect(jsonPath("$[0].title").value("Test title"))
                .andExpect(jsonPath("$[0].status").value("draft"))
                .andExpect(jsonPath("$[0].labels.length()").value(2));
    }

    @Test
    void testGetTaskById() throws Exception {
        var task = createTask(
                "Test title",
                "Test content",
                draftStatus,
                Set.of(feature, bug)
        );

        mockMvc.perform(get("/api/tasks/" + task.getId())
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value("Test title"))
                .andExpect(jsonPath("$.assignee_id").value(user.getId()))
                .andExpect(jsonPath("$.status").value("draft"))
                .andExpect(jsonPath("$.labels.length()").value(2));
    }

    @Test
    void testUpdateTaskPartially() throws Exception {
        var task = createTask(
                "Test title",
                "Test content",
                draftStatus,
                Set.of(feature, bug)
        );

        var request = """
                {
                  "title": "Updated title",
                  "content": "Updated content",
                  "labels": [%d]
                }
                """.formatted(bug.getId());

        mockMvc.perform(put("/api/tasks/" + task.getId())
                        .header("Authorization", "Bearer " + getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated title"))
                .andExpect(jsonPath("$.content").value("Updated content"))
                .andExpect(jsonPath("$.status").value("draft"))
                .andExpect(jsonPath("$.assignee_id").value(user.getId()))
                .andExpect(jsonPath("$.labels.length()").value(1))
                .andExpect(jsonPath("$.labels[0]").value(bug.getId()));
    }

    @Test
    void testDeleteTask() throws Exception {
        var task = createTask(
                "Test title",
                "Test content",
                draftStatus,
                Set.of(feature)
        );

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
        createTask(
                "Test title",
                "Test content",
                draftStatus,
                Set.of(feature)
        );

        mockMvc.perform(delete("/api/users/" + user.getId())
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isConflict());
    }

    @Test
    void testCannotDeleteUsedStatus() throws Exception {
        createTask(
                "Test title",
                "Test content",
                draftStatus,
                Set.of(feature)
        );

        mockMvc.perform(delete("/api/task_statuses/" + draftStatus.getId())
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isConflict());
    }

    @Test
    void testCannotDeleteUsedLabel() throws Exception {
        createTask(
                "Test title",
                "Test content",
                draftStatus,
                Set.of(feature)
        );

        mockMvc.perform(delete("/api/labels/" + feature.getId())
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isConflict());
    }

    @Test
    void testFilterByTitle() throws Exception {
        createFilterTasks();

        mockMvc.perform(get("/api/tasks")
                        .param("titleCont", "create")
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$[0].title")
                        .value("Create new version"));
    }

    @Test
    void testFilterByAssignee() throws Exception {
        createFilterTasks();

        var anotherUser = new User();
        anotherUser.setEmail("another@example.com");
        anotherUser.setPassword(passwordEncoder.encode("qwerty"));
        anotherUser = userRepository.save(anotherUser);

        var task = new Task();
        task.setName("Another user task");
        task.setDescription("Other task");
        task.setTaskStatus(draftStatus);
        task.setAssignee(anotherUser);
        task.setLabels(Set.of(feature));
        taskRepository.save(task);

        mockMvc.perform(get("/api/tasks")
                        .param(
                                "assigneeId",
                                user.getId().toString()
                        )
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "2"));
    }

    @Test
    void testFilterByStatus() throws Exception {
        createFilterTasks();

        mockMvc.perform(get("/api/tasks")
                        .param("status", "to_review")
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$[0].title")
                        .value("Fix application bug"))
                .andExpect(jsonPath("$[0].status")
                        .value("to_review"));
    }

    @Test
    void testFilterByLabel() throws Exception {
        createFilterTasks();

        mockMvc.perform(get("/api/tasks")
                        .param(
                                "labelId",
                                bug.getId().toString()
                        )
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$[0].title")
                        .value("Fix application bug"));
    }

    @Test
    void testFilterByAllParameters() throws Exception {
        createFilterTasks();

        mockMvc.perform(get("/api/tasks")
                        .param("titleCont", "fix")
                        .param(
                                "assigneeId",
                                user.getId().toString()
                        )
                        .param("status", "to_review")
                        .param(
                                "labelId",
                                bug.getId().toString()
                        )
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$[0].title")
                        .value("Fix application bug"))
                .andExpect(jsonPath("$[0].status")
                        .value("to_review"))
                .andExpect(jsonPath("$[0].assignee_id")
                        .value(user.getId()));
    }

    private void createFilterTasks() {
        createTask(
                "Create new version",
                "First task",
                draftStatus,
                Set.of(feature)
        );

        createTask(
                "Fix application bug",
                "Second task",
                reviewStatus,
                Set.of(bug)
        );
    }

    private Task createTask(
            String title,
            String content,
            TaskStatus taskStatus,
            Set<Label> labels) {

        var task = new Task();

        task.setName(title);
        task.setIndex(12);
        task.setDescription(content);
        task.setTaskStatus(taskStatus);
        task.setAssignee(user);
        task.setLabels(labels);

        return taskRepository.save(task);
    }
}