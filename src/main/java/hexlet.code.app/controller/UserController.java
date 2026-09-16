package hexlet.code.app.controller;

import hexlet.code.app.dto.UserCreateDto;
import hexlet.code.app.dto.UserResponseDto;
import hexlet.code.app.dto.UserUpdateDto;
import hexlet.code.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(users);
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
    public UserResponseDto update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDto dto) {
        return userService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}