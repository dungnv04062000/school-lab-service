package com.schoollab.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "evaluations")
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Evaluation {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "from_id", nullable = false)
    private String fromId;

    @Column(name = "to_id", nullable = false)
    private String toId;

    @Column(name = "lesson_id", nullable = false)
    private String lessonId;

    @Column(name = "grade", nullable = false)
    private Float grade;

    @Column(name = "create_at", nullable = false)
    private Instant createAt;

    @Column(name = "update_at")
    private Instant updateAt;

}
