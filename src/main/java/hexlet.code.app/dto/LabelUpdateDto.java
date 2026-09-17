package hexlet.code.app.dto;

import jakarta.validation.constraints.Size;

public class LabelUpdateDto {

    @Size(min = 3, max = 1000)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}