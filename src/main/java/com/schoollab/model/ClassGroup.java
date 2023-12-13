package com.schoollab.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "class_groups")
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ClassGroup {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "name", nullable = false, columnDefinition = "nvarchar")
    private String name;

    @Column(name = "class_id", nullable = false)
    private String classId;

    @Column(name = "lesson_id", nullable = false)
    private String lessonId;

    @Column(name = "create_at", nullable = false)
    private Instant createAt;
}
