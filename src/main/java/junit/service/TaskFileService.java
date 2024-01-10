package junit.service;

import junit.models.Task;
import junit.models.TaskFile;
import junit.repo.TaskFileRepository;
import junit.repo.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Optional;

@Service
public class TaskFileService {

    private final TaskFileRepository taskFileRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public TaskFileService(TaskFileRepository taskFileRepository, TaskRepository taskRepository) {
        this.taskFileRepository = taskFileRepository;
        this.taskRepository = taskRepository;
    }

    public Task attachFileToTask(Long taskId, MultipartFile file) throws IOException {
        Optional<Task> taskOptional = taskRepository.findById(taskId);

        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();

            TaskFile taskFile = new TaskFile();
            taskFile.setTask(task);
            taskFile.setFileName(file.getOriginalFilename());
            taskFile.setData(file.getBytes());

            taskFileRepository.save(taskFile);

            return task;
        }
        return null;
    }

    public TaskFile getTaskFile(Long taskId, Long fileId) {
        Optional<TaskFile> taskFileOptional = taskFileRepository.findById(fileId);

        if (taskFileOptional.isPresent()) {
            TaskFile taskFile = taskFileOptional.get();
            if (taskFile.getTask().getId().equals(taskId)) {
                return taskFile;
            } else {
               return null;
            }
        } else {
            return null;
        }
    }
}
