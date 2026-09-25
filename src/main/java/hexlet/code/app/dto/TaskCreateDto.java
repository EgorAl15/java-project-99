package hexlet.code.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public class TaskCreateDto {

  @NotBlank
  @JsonProperty("title")
  private String name;

  private Integer index;

  @JsonProperty("content")
  private String description;

  @NotBlank
  @JsonProperty("status")
  private String statusSlug;

  @JsonProperty("assignee_id")
  private Long assigneeId;

  private Set<Long> labels;

  public String getName() {
    return name;
  }

  public Integer getIndex() {
    return index;
  }

  public String getDescription() {
    return description;
  }

  public String getStatusSlug() {
    return statusSlug;
  }

  public Long getAssigneeId() {
    return assigneeId;
  }

  public Set<Long> getLabels() {
    return labels;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setIndex(Integer index) {
    this.index = index;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setStatusSlug(String statusSlug) {
    this.statusSlug = statusSlug;
  }

  public void setAssigneeId(Long assigneeId) {
    this.assigneeId = assigneeId;
  }

  public void setLabels(Set<Long> labels) {
    this.labels = labels;
  }
}
