package junit.service;

import junit.models.Priority;
import junit.models.Task;
import junit.models.TaskFile;
import junit.repo.TaskFileRepository;
import junit.repo.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskFileRepository taskFileRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, TaskFileRepository taskFileRepository) {
        this.taskRepository = taskRepository;
        this.taskFileRepository = taskFileRepository;
    }

    public Task createTask(Task task) {
        if (task.getCreatedDate() == null) {
            task.setCreatedDate(new Date());
        }
        if (!task.isCompleted()) {
            task.setCompleted(false);
        }
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }

    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    public boolean existsTaskById(Long taskId) {
        return taskRepository.existsById(taskId);
    }

    public List<Task> getTasksSortedByPriority(){

        return taskRepository.findAll().stream().sorted((Comparator.comparing(Task::getPriority))).collect(Collectors.toList());
    }
    public List<Task> getTasksByCompletedAndPriority(Boolean completed, Priority priority){
        if(completed!=null&&priority!=null){
        return taskRepository.findByCompletedAndPriority(completed, priority);
        } else if (completed!=null) {
        return taskRepository.findByCompleted(completed);
        }
        return taskRepository.findByPriority(priority);
    }
    public Task setTaskDeadline(Long taskId, Task updateTask) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setDeadline(updateTask.getDeadline());
            return taskRepository.save(task);
        }
        return null;
    }

    public Date getTaskDeadline(Long taskId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        return taskOptional.map(Task::getDeadline).orElse(null);
    }

    public List<Task> getTasksWithDeadlineBefore(Task taskWithDeadline) {
        return taskRepository.findByDeadlineBefore(taskWithDeadline.getDeadline());
    }

}
