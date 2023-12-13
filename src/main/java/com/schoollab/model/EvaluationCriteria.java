package com.schoollab.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "evaluation_criterias")
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class EvaluationCriteria {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "preparation", nullable = false)
    private Float preparation;

    @Column(name = "implementation", nullable = false)
    private Float implementation;

    @Column(name = "presentation", nullable = false)
    private Float presentation;

    @Column(name = "production", nullable = false)
    private Float production;

    @Column(name = "create_at", nullable = false)
    private Instant createAt;

    @Column(name = "update_at")
    private Instant updateAt;

}
