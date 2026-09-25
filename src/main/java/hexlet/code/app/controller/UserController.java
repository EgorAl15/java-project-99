package hexlet.code.app.controller;

import hexlet.code.app.dto.UserCreateDto;
import hexlet.code.app.dto.UserResponseDto;
import hexlet.code.app.dto.UserUpdateDto;
import hexlet.code.app.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<List<UserResponseDto>> index() {
    var users = userService.getAll();

    var headers = new HttpHeaders();
    headers.add("X-Total-Count", String.valueOf(users.size()));

    return ResponseEntity.ok().headers(headers).body(users);
  }

  @GetMapping("/{id}")
  public UserResponseDto show(@PathVariable Long id) {
    return userService.getById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponseDto create(@Valid @RequestBody UserCreateDto dto) {

    return userService.create(dto);
  }

  @PutMapping("/{id}")
  @PreAuthorize("@userUtils.isCurrentUser(#id)")
  public UserResponseDto update(@PathVariable Long id, @Valid @RequestBody UserUpdateDto dto) {

    return userService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("@userUtils.isCurrentUser(#id)")
  public void delete(@PathVariable Long id) {
    userService.delete(id);
  }
}
