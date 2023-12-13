package com.schoollab.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "experiments")
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Experiment {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    @Column(name = "title", columnDefinition = "nvarchar")
    private String title;

    @Column(name = "note_id", nullable = false)
    private String noteId;

    @Column(name = "first_label")
    private String firstLabel;

    @Column(name = "second_label")
    private String secondLabel;

    @Column(name = "result_label")
    private String resultLabel;

    @Column(name = "first_measure", columnDefinition = "nvarchar")
    private String firstMeasure;

    @Column(name = "second_measure", columnDefinition = "nvarchar")
    private String secondMeasure;

    @Column(name = "result_measure", columnDefinition = "nvarchar")
    private String resultMeasure;

    @Column(name = "create_by", nullable = false)
    private String createBy;

    @Column(name = "create_at", nullable = false)
    private Instant createAt;
}
