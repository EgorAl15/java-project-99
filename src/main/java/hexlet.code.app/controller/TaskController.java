package hexlet.code.app.controller;

import hexlet.code.app.dto.TaskCreateDto;
import hexlet.code.app.dto.TaskResponseDto;
import hexlet.code.app.dto.TaskUpdateDto;
import hexlet.code.app.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> index() {
        var tasks = taskService.getAll();

        var headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(tasks.size()));

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(tasks);
    }

    @GetMapping("/{id}")
    public TaskResponseDto show(@PathVariable Long id) {
        return taskService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDto create(
            @Valid @RequestBody TaskCreateDto dto) {
        return taskService.create(dto);
    }

    @PutMapping("/{id}")
    public TaskResponseDto update(
            @PathVariable Long id,
            @RequestBody TaskUpdateDto dto) {
        return taskService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
    }
}