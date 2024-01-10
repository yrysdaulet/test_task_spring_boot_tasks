package junit.service;

import junit.models.Task;
import junit.models.TaskFile;
import junit.repo.TaskFileRepository;
import junit.repo.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static junit.framework.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskFileServiceTest {

    @Mock
    private TaskFileRepository taskFileRepository;
    @Mock
    private TaskRepository taskRepository;
    @InjectMocks
    private TaskFileService taskFileService;


    @Test
    public void attachFileToTask() throws IOException {
        Task task1 = Task.builder().id(1L).title("Title 1").build();
        Long taskId =1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task1));
        Optional<Task> taskOptional = taskRepository.findById(taskId);


        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test-file.txt",
                    "text/plain",
                    "Hello, World!".getBytes());
            TaskFile taskFile = new TaskFile();
            taskFile.setTask(task);
            taskFile.setFileName(file.getOriginalFilename());
            taskFile.setData(file.getBytes());
            when(taskFileRepository.save(any(TaskFile.class))).thenReturn(taskFile);
            taskFileRepository.save(taskFile);

        }
        verify(taskRepository).findById(eq(taskId));
        verify(taskFileRepository).save(any(TaskFile.class));
    }

    @Test
    public void getTaskFile_ReturnsTaskFile_WhenTaskFileExistsForTaskIdAndFileId() {
        // Arrange
        Long taskId = 1L;
        Long fileId = 2L;
        Task task = Task.builder().id(1L).title("Title 1").build();
        TaskFile taskFile =  TaskFile.builder().task(task).fileName("test-file.txt").data("content".getBytes()).build();

        when(taskFileRepository.findById(fileId)).thenReturn(Optional.of(taskFile));

        TaskFile result = taskFileService.getTaskFile(taskId, fileId);

        assertNotNull(result);
        assertEquals(taskFile, result);
        verify(taskFileRepository).findById(eq(fileId));
    }

    @Test
    public void getTaskFile_ReturnsNull_WhenTaskFileDoesNotExistForTaskIdAndFileId() {
        Long taskId = 1L;
        Long fileId = 2L;

        when(taskFileRepository.findById(fileId)).thenReturn(Optional.empty());

        TaskFile result = taskFileService.getTaskFile(taskId, fileId);

        assertNull(result);
        verify(taskFileRepository).findById(eq(fileId));
    }

    @Test
    public void getTaskFile_ReturnsNull_WhenTaskFileBelongsToDifferentTask() {
        // Arrange
        Long taskId = 1L;
        Long fileId = 2L;
        Long differentTaskId = 3L;

        Task task = Task.builder().id(taskId).title("Title 1").build();
        TaskFile taskFile =  TaskFile.builder().task(task).fileName("test-file.txt").data("content".getBytes()).build();
        taskFile.setId(fileId);

        when(taskFileRepository.findById(fileId)).thenReturn(Optional.of(taskFile));

        TaskFile result = taskFileService.getTaskFile(differentTaskId, fileId);

        assertNull(result);
        verify(taskFileRepository).findById(eq(fileId));
    }
}
