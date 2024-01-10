package junit.service;

import junit.models.Priority;
import junit.models.Task;
import junit.repo.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;


    @InjectMocks
    private TaskService taskService;

    private Task simpleTask;
    private List<Task> tasksWithCompletedAndPriority;
    private List<Task> simpleTasks;

    @BeforeEach
    public void init(){
        simpleTask =Task.builder().id(1L).title("Title 1").build();
        simpleTasks = Arrays.asList(
                Task.builder().id(1L).title("Title 1").build(),
                Task.builder().id(2L).title("Title 2").build(),
                Task.builder().id(3L).title("Title 3").build()
        );
        tasksWithCompletedAndPriority = Arrays.asList(
                Task.builder().id(1L).title("Title 1").priority(Priority.HIGH).build(),
                Task.builder().id(2L).title("Title 2").priority(Priority.MEDIUM).build(),
                Task.builder().id(3L).title("Title 3").priority(Priority.LOW).build()
        );

    }


    @Test
    public void testCreateTask() {
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task savedTask = invocation.getArgument(0);
            savedTask.setId(1L);
            return savedTask;
        });
        Task createdTask = taskService.createTask(simpleTask);

        assertNotNull(createdTask);
        assertFalse(createdTask.isCompleted());
        assertNotNull(createdTask.getCreatedDate());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    public void testGetAllTasks() {

        List<Task> tasksExample = simpleTasks;
        System.out.println(tasksExample);
        when(taskRepository.findAll()).thenReturn(tasksExample);

        List<Task> tasks = taskService.getAllTasks();

        assertNotNull(tasks);
        assertEquals(3, tasks.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateTask() {
        Task taskToUpdate =simpleTask;
        when(taskRepository.save(any(Task.class))).thenReturn(taskToUpdate);

        Task updatedTask = taskService.updateTask(taskToUpdate);

        assertNotNull(updatedTask);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    public void testDeleteTask() {
        doNothing().when(taskRepository).deleteById(anyLong());

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testExistsTaskById() {
        when(taskRepository.existsById(anyLong())).thenReturn(true);

        assertTrue(taskService.existsTaskById(1L));

        verify(taskRepository, times(1)).existsById(1L);
    }

    @Test
    public void testGetTasksSortedByPriority() {
        List<Task> tasks = tasksWithCompletedAndPriority;

        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> sortedTasks = taskService.getTasksSortedByPriority();

        assertNotNull(sortedTasks);
        assertEquals(3, sortedTasks.size());
        assertEquals(Priority.LOW, sortedTasks.get(0).getPriority());
        assertEquals(Priority.MEDIUM, sortedTasks.get(1).getPriority());
        assertEquals(Priority.HIGH, sortedTasks.get(2).getPriority());

        verify(taskRepository, times(1)).findAll();
    }

    @Test
    public void testGetTasksByCompletedAndPriority() {
         List<Task> tasks = tasksWithCompletedAndPriority;

        when(taskRepository.findByCompletedAndPriority(true, Priority.HIGH)).thenReturn(Collections.singletonList(tasks.get(0)));
        List<Task> filteredTasks = taskService.getTasksByCompletedAndPriority(true, Priority.HIGH);

        assertNotNull(filteredTasks);
        assertEquals(1, filteredTasks.size());
        assertEquals(Priority.HIGH, filteredTasks.get(0).getPriority());

        verify(taskRepository, times(1)).findByCompletedAndPriority(true, Priority.HIGH);
    }

    @Test
    public void testSetTaskDeadline() {
        Task existingTask = simpleTask;
        Date deadline = new Date();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task updatedTask = taskService.setTaskDeadline(1L, deadline);

        assertNotNull(updatedTask);
        assertEquals(deadline, updatedTask.getDeadline());

        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    public void testGetTaskDeadline() {
        Task existingTask = simpleTask;
        existingTask.setDeadline(new Date());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

        Date deadline = taskService.getTaskDeadline(1L);

        assertNotNull(deadline);
        assertEquals(existingTask.getDeadline(), deadline);

        verify(taskRepository, times(1)).findById(1L);
    }


}
