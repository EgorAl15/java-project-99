package hexlet.code.app.controller;

import hexlet.code.app.dto.TaskStatusCreateDto;
import hexlet.code.app.dto.TaskStatusResponseDto;
import hexlet.code.app.dto.TaskStatusUpdateDto;
import hexlet.code.app.service.TaskStatusService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task_statuses")
public class TaskStatusController {

    private final TaskStatusService service;

    public TaskStatusController(TaskStatusService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TaskStatusResponseDto>> index() {
        var statuses = service.getAll();

        var headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(statuses.size()));

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(statuses);
    }

    @GetMapping("/{id}")
    public TaskStatusResponseDto show(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatusResponseDto create(
            @Valid @RequestBody TaskStatusCreateDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public TaskStatusResponseDto update(
            @PathVariable Long id,
            @Valid @RequestBody TaskStatusUpdateDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}