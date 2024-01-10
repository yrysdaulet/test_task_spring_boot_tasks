package junit.repo;

import junit.models.Priority;
import junit.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCompletedAndPriority(boolean completed, Priority priority);

    List<Task> findByDeadlineBefore(Date deadline);
}
