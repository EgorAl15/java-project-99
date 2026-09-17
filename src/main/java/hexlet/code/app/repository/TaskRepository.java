package hexlet.code.app.repository;

import hexlet.code.app.Task;
import hexlet.code.app.TaskStatus;
import hexlet.code.app.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    boolean existsByAssignee(User assignee);

    boolean existsByTaskStatus(TaskStatus taskStatus);
}