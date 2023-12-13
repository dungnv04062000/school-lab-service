package com.schoollab.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "evaluation_groups")
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class EvaluationGroup {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "from_id", nullable = false)
    private String fromId;

    @Column(name = "group_id", nullable = false)
    private String groupId;

    @Column(name = "lesson_id", nullable = false)
    private String lessonId;

    @Column(name = "preparation", nullable = false)
    private Integer preparation;

    @Column(name = "implementation", nullable = false)
    private Integer implementation;

    @Column(name = "presentation", nullable = false)
    private Integer presentation;

    @Column(name = "production", nullable = false)
    private Integer production;

    @Column(name = "create_at", nullable = false)
    private Instant createAt;

    @Column(name = "update_at")
    private Instant updateAt;

}
