package hexlet.code.app.service;

import hexlet.code.app.Task;
import hexlet.code.app.dto.TaskCreateDto;
import hexlet.code.app.dto.TaskResponseDto;
import hexlet.code.app.dto.TaskUpdateDto;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;

    public TaskService(
            TaskRepository taskRepository,
            TaskStatusRepository taskStatusRepository,
            UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.userRepository = userRepository;
    }

    public List<TaskResponseDto> getAll() {
        return taskRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public TaskResponseDto getById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found")
                );

        return toDto(task);
    }

    public TaskResponseDto create(TaskCreateDto dto) {
        var status = taskStatusRepository.findBySlug(dto.getStatusSlug())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task status not found")
                );

        var task = new Task();

        task.setName(dto.getName());
        task.setIndex(dto.getIndex());
        task.setDescription(dto.getDescription());
        task.setTaskStatus(status);

        if (dto.getAssigneeId() != null) {
            var assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Assignee not found")
                    );

            task.setAssignee(assignee);
        }

        return toDto(taskRepository.save(task));
    }

    public TaskResponseDto update(Long id, TaskUpdateDto dto) {
        var task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found")
                );

        if (dto.getName() != null) {
            task.setName(dto.getName());
        }

        if (dto.getIndex() != null) {
            task.setIndex(dto.getIndex());
        }

        if (dto.getDescription() != null) {
            task.setDescription(dto.getDescription());
        }

        if (dto.getStatusSlug() != null) {
            var status = taskStatusRepository.findBySlug(dto.getStatusSlug())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Task status not found")
                    );

            task.setTaskStatus(status);
        }

        if (dto.getAssigneeId() != null) {
            var assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Assignee not found")
                    );

            task.setAssignee(assignee);
        }

        return toDto(taskRepository.save(task));
    }

    public void delete(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found")
                );

        taskRepository.delete(task);
    }

    private TaskResponseDto toDto(Task task) {
        Long assigneeId = null;

        if (task.getAssignee() != null) {
            assigneeId = task.getAssignee().getId();
        }

        return new TaskResponseDto(
                task.getId(),
                task.getIndex(),
                task.getCreatedAt(),
                assigneeId,
                task.getName(),
                task.getDescription(),
                task.getTaskStatus().getSlug()
        );
    }
}