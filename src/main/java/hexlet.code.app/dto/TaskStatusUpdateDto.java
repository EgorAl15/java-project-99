package hexlet.code.app.dto;

import jakarta.validation.constraints.Size;

public class TaskStatusUpdateDto {

    @Size(min = 1)
    private String name;

    @Size(min = 1)
    private String slug;

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
