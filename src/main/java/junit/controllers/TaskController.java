package junit.controllers;

import junit.models.Priority;
import junit.models.Task;
import junit.models.TaskFile;
import junit.service.TaskFileService;
import junit.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskFileService taskFileService;

    @Autowired
    public TaskController(TaskService taskService, TaskFileService taskFileService) {
        this.taskService = taskService;
        this.taskFileService = taskFileService;
    }


    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        Task savedTask = taskService.createTask(task);
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long taskId) {
        Optional<Task> taskOptional = taskService.getTaskById(taskId);
        return taskOptional.map(task -> new ResponseEntity<>(task, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(@PathVariable Long taskId,  @RequestBody Task updatedTask) {
        Optional<Task> existingTaskOptional = taskService.getTaskById(taskId);

        if (existingTaskOptional.isPresent()) {
            Task existingTask = existingTaskOptional.get();
            existingTask.setCompleted(updatedTask.isCompleted());
            existingTask.setDescription(updatedTask.getDescription());
            existingTask.setPriority(updatedTask.getPriority());
            existingTask.setDeadline(updatedTask.getDeadline());

            Task savedTask = taskService.updateTask(existingTask);
            return new ResponseEntity<>(savedTask, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable Long taskId) {
        if (taskService.existsTaskById(taskId)) {
            taskService.deleteTask(taskId);
            return new ResponseEntity<>("Task deleted",HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/sort-by-priority")
    public ResponseEntity<List<Task>> getTasksSortedByPriority() {
        List<Task> tasks = taskService.getTasksSortedByPriority();
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Task>> getTaskByStatusAndPriority(
            @RequestParam(name = "completed", required = false) Boolean completed,
            @RequestParam(name = "priority", required = false) Priority priority
            )
    {
        if (completed==null && priority==null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(taskService.getTasksByCompletedAndPriority(completed,priority),HttpStatus.BAD_REQUEST);

    }
    @PostMapping("/{taskId}/set-deadline")
    public ResponseEntity<Task> setTaskDeadline(
            @PathVariable Long taskId,
            @RequestBody Task updatedTask) {
        Task taskWithDeadline = taskService.setTaskDeadline(taskId, updatedTask);
        if (taskWithDeadline != null) {
            return new ResponseEntity<>(taskWithDeadline, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{taskId}/get-deadline")
    public ResponseEntity<Date> getTaskDeadline(@PathVariable Long taskId) {
        Date deadline = taskService.getTaskDeadline(taskId);
        if (deadline != null) {
            return new ResponseEntity<>(deadline, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/tasks-with-deadline-before")
    public ResponseEntity<List<Task>> getTasksWithDeadlineBefore(@RequestBody Task taskWithDeadline) {
        List<Task> tasks = taskService.getTasksWithDeadlineBefore(taskWithDeadline);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }


    @PostMapping("/{taskId}/attach-file")
    public ResponseEntity<Task> attachFileToTask(
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file) throws IOException {
        Task updatedTask = taskFileService.attachFileToTask(taskId, file);
        return new ResponseEntity<>(updatedTask, HttpStatus.OK);
    }

    @GetMapping("/{taskId}/download-file/{fileId}")
    public ResponseEntity<byte[]> downloadTaskFile(
            @PathVariable Long taskId,
            @PathVariable Long fileId) {
        TaskFile taskFile = taskFileService.getTaskFile(taskId, fileId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", taskFile.getFileName());

        return new ResponseEntity<>(taskFile.getData(), headers, HttpStatus.OK);
    }

}
