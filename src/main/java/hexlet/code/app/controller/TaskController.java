package hexlet.code.app.controller;

import hexlet.code.app.dto.TaskCreateDto;
import hexlet.code.app.dto.TaskResponseDto;
import hexlet.code.app.dto.TaskUpdateDto;
import hexlet.code.app.service.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private final TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @GetMapping
  public ResponseEntity<List<TaskResponseDto>> index(
      @RequestParam(required = false) String titleCont,
      @RequestParam(required = false) Long assigneeId,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) Long labelId) {

    var tasks = taskService.getAll(titleCont, assigneeId, status, labelId);

    var headers = new HttpHeaders();
    headers.add("X-Total-Count", String.valueOf(tasks.size()));

    return ResponseEntity.ok().headers(headers).body(tasks);
  }

  @GetMapping("/{id}")
  public TaskResponseDto show(@PathVariable Long id) {
    return taskService.getById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TaskResponseDto create(@Valid @RequestBody TaskCreateDto dto) {

    return taskService.create(dto);
  }

  @PutMapping("/{id}")
  public TaskResponseDto update(@PathVariable Long id, @Valid @RequestBody TaskUpdateDto dto) {

    return taskService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    taskService.delete(id);
  }
}
