package com.schoollab.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "class_teacher")
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ClassTeacher {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @NotNull
    @Column(name = "class_id")
    private String classId;

    @NotNull
    @Column(name = "teacher_id")
    private String teacherId;

    @NotNull
    @Column(name = "create_at")
    private Instant createAt;
}
