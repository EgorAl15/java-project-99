package hexlet.code.app.controller;

import hexlet.code.app.User;
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
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        var admin = new User();
        admin.setEmail("hexlet@example.com");
        admin.setPassword(passwordEncoder.encode("qwerty"));

        userRepository.save(admin);
    }

    private String getToken(String email, String password) throws Exception {
        var request = """
                {
                  "username": "%s",
                  "password": "%s"
                }
                """.formatted(email, password);

        var result = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andReturn();

        return result.getResponse().getContentAsString();
    }

    private String getAdminToken() throws Exception {
        return getToken("hexlet@example.com", "qwerty");
    }

    @Test
    void testCreateUser() throws Exception {
        var token = getAdminToken();

        var request = """
                {
                  "email": "john@example.com",
                  "firstName": "John",
                  "lastName": "Doe",
                  "password": "secret"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + token)
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
        var token = getAdminToken();

        var user = new User();
        user.setEmail("john@example.com");
        user.setPassword(passwordEncoder.encode("secret"));
        user.setFirstName("John");
        user.setLastName("Doe");

        userRepository.save(user);

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].email")
                        .value(org.hamcrest.Matchers.hasItem("john@example.com")))
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    void testGetUserById() throws Exception {
        var token = getAdminToken();

        var user = new User();
        user.setEmail("john@example.com");
        user.setPassword(passwordEncoder.encode("secret"));

        user = userRepository.save(user);

        mockMvc.perform(get("/api/users/" + user.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void testUpdateUserPartially() throws Exception {
        var user = new User();
        user.setEmail("john@example.com");
        user.setPassword(passwordEncoder.encode("secret"));
        user.setFirstName("John");
        user.setLastName("Doe");

        user = userRepository.save(user);

        var token = getToken("john@example.com", "secret");

        var request = """
                {
                  "email": "new@example.com"
                }
                """;

        mockMvc.perform(put("/api/users/" + user.getId())
                        .header("Authorization", "Bearer " + token)
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
        user.setPassword(passwordEncoder.encode("secret"));

        user = userRepository.save(user);

        var token = getToken("john@example.com", "secret");

        mockMvc.perform(delete("/api/users/" + user.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        var adminToken = getAdminToken();

        mockMvc.perform(get("/api/users/" + user.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void testInvalidUserReturns400() throws Exception {
        var token = getAdminToken();

        var request = """
                {
                  "email": "bad-email",
                  "password": "12"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUserNotFound() throws Exception {
        var token = getAdminToken();

        mockMvc.perform(get("/api/users/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUsersRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginWithWrongPasswordReturns401() throws Exception {
        var request = """
                {
                  "username": "hexlet@example.com",
                  "password": "wrong-password"
                }
                """;

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginUnknownUserReturns401() throws Exception {
        var request = """
                {
                  "username": "unknown@example.com",
                  "password": "qwerty"
                }
                """;

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCannotUpdateAnotherUser() throws Exception {
        var token = getAdminToken();

        var user = new User();
        user.setEmail("john@example.com");
        user.setPassword(passwordEncoder.encode("secret"));

        user = userRepository.save(user);

        var request = """
                {
                  "firstName": "Changed"
                }
                """;

        mockMvc.perform(put("/api/users/" + user.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCannotDeleteAnotherUser() throws Exception {
        var token = getAdminToken();

        var user = new User();
        user.setEmail("john@example.com");
        user.setPassword(passwordEncoder.encode("secret"));

        user = userRepository.save(user);

        mockMvc.perform(delete("/api/users/" + user.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}