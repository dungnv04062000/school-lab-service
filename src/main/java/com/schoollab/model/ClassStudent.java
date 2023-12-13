package com.schoollab.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "class_student")
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ClassStudent {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "class_id", nullable = false)
    private String classId;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "create_at", nullable = false)
    private Instant createAt;
}
