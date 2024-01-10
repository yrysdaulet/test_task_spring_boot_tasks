package junit.repo;
import junit.models.TaskFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskFileRepository extends JpaRepository<TaskFile, Long> {

}
