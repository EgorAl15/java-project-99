package hexlet.code.app.service;

import hexlet.code.app.TaskStatus;
import hexlet.code.app.dto.TaskStatusCreateDto;
import hexlet.code.app.dto.TaskStatusResponseDto;
import hexlet.code.app.dto.TaskStatusUpdateDto;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.repository.TaskStatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatusService {

    private final TaskStatusRepository repository;

    public TaskStatusService(TaskStatusRepository repository) {
        this.repository = repository;
    }

    public List<TaskStatusResponseDto> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public TaskStatusResponseDto getById(Long id) {
        var status = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task status not found")
                );

        return toDto(status);
    }

    public TaskStatusResponseDto create(TaskStatusCreateDto dto) {
        if (repository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Name already exists");
        }

        if (repository.existsBySlug(dto.getSlug())) {
            throw new IllegalArgumentException("Slug already exists");
        }

        var status = new TaskStatus();
        status.setName(dto.getName());
        status.setSlug(dto.getSlug());

        return toDto(repository.save(status));
    }

    public TaskStatusResponseDto update(Long id, TaskStatusUpdateDto dto) {
        var status = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task status not found")
                );

        if (dto.getName() != null) {
            status.setName(dto.getName());
        }

        if (dto.getSlug() != null) {
            status.setSlug(dto.getSlug());
        }

        return toDto(repository.save(status));
    }

    public void delete(Long id) {
        var status = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task status not found")
                );

        repository.delete(status);
    }

    private TaskStatusResponseDto toDto(TaskStatus status) {
        return new TaskStatusResponseDto(
                status.getId(),
                status.getName(),
                status.getSlug(),
                status.getCreatedAt()
        );
    }
}