package junit.models;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Data //[getters, setters, equals,hashcode,tostring]
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
public class TaskFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    private String fileName;

    @Lob
    private byte[] data;



}
