package hexlet.code.app.dto;

import jakarta.validation.constraints.NotBlank;

public class TaskStatusCreateDto {

  @NotBlank private String name;

  @NotBlank private String slug;

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
