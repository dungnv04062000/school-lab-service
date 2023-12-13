package com.schoollab.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "classes")
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Class {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "name", nullable = false, columnDefinition = "nvarchar")
    private String name;

    @Column(name = "form_teacher_id", unique = true)
    private String formTeacherId;

    @Column(name = "create_at", nullable = false)
    private Instant createAt;

    @Column(name = "campus_id", nullable = false)
    private String campusId;

    @Column(name = "semester_id", nullable = false)
    private String semesterId;

    @Column(name = "grade_id", nullable = false)
    private int gradeId;

    @Column(name = "update_at")
    private Instant updateAt;
}
