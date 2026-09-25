package hexlet.code.app.service;

import hexlet.code.app.dto.LabelCreateDto;
import hexlet.code.app.dto.LabelResponseDto;
import hexlet.code.app.dto.LabelUpdateDto;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {

    private final LabelRepository labelRepository;

    public LabelService(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
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
                        new ResourceNotFoundException(
                                "Label not found"
                        )
                );

        return toDto(label);
    }

    public LabelResponseDto create(LabelCreateDto dto) {
        var label = new Label();
        label.setName(dto.getName());

        return toDto(labelRepository.save(label));
    }

    public LabelResponseDto update(
            Long id,
            LabelUpdateDto dto) {

        var label = labelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Label not found"
                        )
                );

        if (dto.getName() != null) {
            label.setName(dto.getName());
        }

        return toDto(labelRepository.save(label));
    }

    public void delete(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Label not found"
                        )
                );

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