package hexlet.code.app.repository;

import hexlet.code.app.Label;
import hexlet.code.app.Task;
import hexlet.code.app.TaskStatus;
import hexlet.code.app.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskRepository extends
        JpaRepository<Task, Long>,
        JpaSpecificationExecutor<Task> {

    boolean existsByAssignee(User assignee);

    boolean existsByTaskStatus(TaskStatus taskStatus);

    boolean existsByLabelsContaining(Label label);
}