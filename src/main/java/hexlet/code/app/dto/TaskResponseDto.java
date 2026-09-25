package hexlet.code.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.Set;

public class TaskResponseDto {

  private Long id;

  private Integer index;

  private LocalDateTime createdAt;

  @JsonProperty("assignee_id")
  private Long assigneeId;

  @JsonProperty("title")
  private String name;

  @JsonProperty("content")
  private String description;

  @JsonProperty("status")
  private String statusSlug;

  private Set<Long> labels;

  public TaskResponseDto(
      Long id,
      Integer index,
      LocalDateTime createdAt,
      Long assigneeId,
      String name,
      String description,
      String statusSlug,
      Set<Long> labels) {

    this.id = id;
    this.index = index;
    this.createdAt = createdAt;
    this.assigneeId = assigneeId;
    this.name = name;
    this.description = description;
    this.statusSlug = statusSlug;
    this.labels = labels;
  }

  public Long getId() {
    return id;
  }

  public Integer getIndex() {
    return index;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public Long getAssigneeId() {
    return assigneeId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getStatusSlug() {
    return statusSlug;
  }

  public Set<Long> getLabels() {
    return labels;
  }
}
