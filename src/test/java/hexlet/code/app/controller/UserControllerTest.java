package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.User;
import hexlet.code.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testCreateUser() throws Exception {
        var request = """
                {
                  "email": "john@example.com",
                  "firstName": "John",
                  "lastName": "Doe",
                  "password": "secret"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void testGetUsers() throws Exception {
        var user = new User();
        user.setEmail("john@example.com");
        user.setPassword("hashed-password");
        user.setFirstName("John");
        user.setLastName("Doe");

        userRepository.save(user);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("john@example.com"))
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    void testGetUserById() throws Exception {
        var user = new User();
        user.setEmail("john@example.com");
        user.setPassword("hashed-password");

        user = userRepository.save(user);

        mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void testUpdateUserPartially() throws Exception {
        var user = new User();
        user.setEmail("old@example.com");
        user.setPassword("hashed-password");
        user.setFirstName("John");
        user.setLastName("Doe");

        user = userRepository.save(user);

        var request = """
                {
                  "email": "new@example.com"
                }
                """;

        mockMvc.perform(put("/api/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void testDeleteUser() throws Exception {
        var user = new User();
        user.setEmail("john@example.com");
        user.setPassword("hashed-password");

        user = userRepository.save(user);

        mockMvc.perform(delete("/api/users/" + user.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testInvalidUserReturns400() throws Exception {
        var request = """
                {
                  "email": "bad-email",
                  "password": "12"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUserNotFound() throws Exception {
        mockMvc.perform(get("/api/users/999999"))
                .andExpect(status().isNotFound());
    }
}