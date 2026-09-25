package hexlet.code.app.specification;

import hexlet.code.app.model.Task;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecification {

  public static Specification<Task> titleContains(String titleCont) {
    return (root, query, criteriaBuilder) -> {
      if (titleCont == null || titleCont.isBlank()) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.like(
          criteriaBuilder.lower(root.get("name")), "%" + titleCont.toLowerCase() + "%");
    };
  }

  public static Specification<Task> hasAssignee(Long assigneeId) {
    return (root, query, criteriaBuilder) -> {
      if (assigneeId == null) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.equal(root.get("assignee").get("id"), assigneeId);
    };
  }

  public static Specification<Task> hasStatus(String status) {
    return (root, query, criteriaBuilder) -> {
      if (status == null || status.isBlank()) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.equal(root.get("taskStatus").get("slug"), status);
    };
  }

  public static Specification<Task> hasLabel(Long labelId) {
    return (root, query, criteriaBuilder) -> {
      if (labelId == null) {
        return criteriaBuilder.conjunction();
      }

      var labels = root.join("labels");

      query.distinct(true);

      return criteriaBuilder.equal(labels.get("id"), labelId);
    };
  }
}
