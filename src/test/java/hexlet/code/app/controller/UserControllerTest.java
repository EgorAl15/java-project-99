package hexlet.code.app.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.LoginRequest;
import hexlet.code.app.dto.UserCreateDto;
import hexlet.code.app.dto.UserUpdateDto;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.UserRepository;
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
class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserRepository userRepository;

  @Autowired private TaskRepository taskRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    taskRepository.deleteAll();
    userRepository.deleteAll();

    var admin = new User();
    admin.setEmail("hexlet@example.com");
    admin.setPassword(passwordEncoder.encode("qwerty"));

    userRepository.save(admin);
  }

  private String getToken(String username, String password) throws Exception {

    var request = new LoginRequest();
    request.setUsername(username);
    request.setPassword(password);

    var result =
        mockMvc
            .perform(
                post("/api/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn();

    return result.getResponse().getContentAsString();
  }

  private String getAdminToken() throws Exception {
    return getToken("hexlet@example.com", "qwerty");
  }

  private User createUser(String email, String password) {

    var user = new User();

    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));

    return userRepository.save(user);
  }

  @Test
  void testCreateUser() throws Exception {
    var request = new UserCreateDto();

    request.setEmail("john@example.com");
    request.setFirstName("John");
    request.setLastName("Doe");
    request.setPassword("secret");

    mockMvc
        .perform(
            post("/api/users")
                .header("Authorization", "Bearer " + getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value("john@example.com"))
        .andExpect(jsonPath("$.firstName").value("John"))
        .andExpect(jsonPath("$.lastName").value("Doe"))
        .andExpect(jsonPath("$.password").doesNotExist());
  }

  @Test
  void testGetUsers() throws Exception {
    var user = createUser("john@example.com", "secret");

    user.setFirstName("John");
    user.setLastName("Doe");

    userRepository.save(user);

    mockMvc
        .perform(get("/api/users").header("Authorization", "Bearer " + getAdminToken()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[1].email").value("john@example.com"))
        .andExpect(jsonPath("$[1].password").doesNotExist());
  }

  @Test
  void testGetUserById() throws Exception {
    var user = createUser("john@example.com", "secret");

    mockMvc
        .perform(
            get("/api/users/" + user.getId()).header("Authorization", "Bearer " + getAdminToken()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("john@example.com"))
        .andExpect(jsonPath("$.password").doesNotExist());
  }

  @Test
  void testUpdateUserPartially() throws Exception {
    var user = createUser("john@example.com", "secret");

    user.setFirstName("John");
    user.setLastName("Doe");

    userRepository.save(user);

    var token = getToken("john@example.com", "secret");

    var request = new UserUpdateDto();
    request.setEmail("new@example.com");

    mockMvc
        .perform(
            put("/api/users/" + user.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("new@example.com"))
        .andExpect(jsonPath("$.firstName").value("John"))
        .andExpect(jsonPath("$.lastName").value("Doe"));
  }

  @Test
  void testDeleteUser() throws Exception {
    var user = createUser("john@example.com", "secret");

    var token = getToken("john@example.com", "secret");

    mockMvc
        .perform(delete("/api/users/" + user.getId()).header("Authorization", "Bearer " + token))
        .andExpect(status().isNoContent());

    mockMvc
        .perform(
            get("/api/users/" + user.getId()).header("Authorization", "Bearer " + getAdminToken()))
        .andExpect(status().isNotFound());
  }

  @Test
  void testInvalidUserReturns400() throws Exception {
    var request = new UserCreateDto();

    request.setEmail("bad-email");
    request.setPassword("12");

    mockMvc
        .perform(
            post("/api/users")
                .header("Authorization", "Bearer " + getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testUserNotFound() throws Exception {
    mockMvc
        .perform(get("/api/users/999999").header("Authorization", "Bearer " + getAdminToken()))
        .andExpect(status().isNotFound());
  }

  @Test
  void testUsersRequireAuthentication() throws Exception {

    mockMvc.perform(get("/api/users")).andExpect(status().isUnauthorized());
  }

  @Test
  void testCannotUpdateAnotherUser() throws Exception {

    var otherUser = createUser("other@example.com", "secret");

    var request = new UserUpdateDto();
    request.setFirstName("Changed");

    mockMvc
        .perform(
            put("/api/users/" + otherUser.getId())
                .header("Authorization", "Bearer " + getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }

  @Test
  void testCannotDeleteAnotherUser() throws Exception {

    var otherUser = createUser("other@example.com", "secret");

    mockMvc
        .perform(
            delete("/api/users/" + otherUser.getId())
                .header("Authorization", "Bearer " + getAdminToken()))
        .andExpect(status().isForbidden());
  }

  @Test
  void testLoginWithWrongPasswordReturns401() throws Exception {

    var request = new LoginRequest();

    request.setUsername("hexlet@example.com");
    request.setPassword("wrong-password");

    mockMvc
        .perform(
            post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void testLoginUnknownUserReturns401() throws Exception {

    var request = new LoginRequest();

    request.setUsername("unknown@example.com");
    request.setPassword("qwerty");

    mockMvc
        .perform(
            post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }
}
