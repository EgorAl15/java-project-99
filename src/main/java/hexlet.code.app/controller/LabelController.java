package hexlet.code.app.controller;

import hexlet.code.app.dto.LabelCreateDto;
import hexlet.code.app.dto.LabelResponseDto;
import hexlet.code.app.dto.LabelUpdateDto;
import hexlet.code.app.service.LabelService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
public class LabelController {

    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @GetMapping
    public ResponseEntity<List<LabelResponseDto>> index() {
        var labels = labelService.getAll();

        var headers = new HttpHeaders();
        headers.add(
                "X-Total-Count",
                String.valueOf(labels.size())
        );

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(labels);
    }

    @GetMapping("/{id}")
    public LabelResponseDto show(@PathVariable Long id) {
        return labelService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponseDto create(
            @Valid @RequestBody LabelCreateDto dto) {

        return labelService.create(dto);
    }

    @PutMapping("/{id}")
    public LabelResponseDto update(
            @PathVariable Long id,
            @Valid @RequestBody LabelUpdateDto dto) {

        return labelService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        labelService.delete(id);
    }
}