package hexlet.code.app.service;

import hexlet.code.app.Label;
import hexlet.code.app.dto.LabelCreateDto;
import hexlet.code.app.dto.LabelResponseDto;
import hexlet.code.app.dto.LabelUpdateDto;
import hexlet.code.app.exception.ResourceConflictException;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {

    private final LabelRepository labelRepository;
    private final TaskRepository taskRepository;

    public LabelService(
            LabelRepository labelRepository,
            TaskRepository taskRepository) {
        this.labelRepository = labelRepository;
        this.taskRepository = taskRepository;
    }

    public List<LabelResponseDto> getAll() {
        return labelRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public LabelResponseDto getById(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Label not found")
                );

        return toDto(label);
    }

    public LabelResponseDto create(LabelCreateDto dto) {
        if (labelRepository.existsByName(dto.getName())) {
            throw new ResourceConflictException(
                    "Label name already exists"
            );
        }

        var label = new Label();
        label.setName(dto.getName());

        return toDto(labelRepository.save(label));
    }

    public LabelResponseDto update(
            Long id,
            LabelUpdateDto dto) {

        var label = labelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Label not found")
                );

        if (dto.getName() != null) {
            var existingLabel =
                    labelRepository.findByName(dto.getName());

            if (existingLabel.isPresent()
                    && !existingLabel.get().getId().equals(id)) {
                throw new ResourceConflictException(
                        "Label name already exists"
                );
            }

            label.setName(dto.getName());
        }

        return toDto(labelRepository.save(label));
    }

    public void delete(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Label not found")
                );

        if (taskRepository.existsByLabelsContaining(label)) {
            throw new ResourceConflictException(
                    "Cannot delete label used by tasks"
            );
        }

        labelRepository.delete(label);
    }

    private LabelResponseDto toDto(Label label) {
        return new LabelResponseDto(
                label.getId(),
                label.getName(),
                label.getCreatedAt()
        );
    }
}