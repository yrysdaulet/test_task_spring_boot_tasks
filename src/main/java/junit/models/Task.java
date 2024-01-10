package junit.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data //[getters, setters, equals,hashcode,tostring]
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotBlank(message = "Description is mandatory")
    private String description;

    private Date createdDate;

    private boolean completed;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Temporal(TemporalType.TIMESTAMP)
    private Date deadline;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<TaskFile> taskFiles = new ArrayList<>();


}
