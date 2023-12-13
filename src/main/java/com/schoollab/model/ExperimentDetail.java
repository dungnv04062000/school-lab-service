package com.schoollab.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "experiment_detail")
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ExperimentDetail {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    @Column(name = "experiment_id", nullable = false)
    private String experimentId;

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    @Column(name = "first_input")
    private Float firstInput;

    @Column(name = "second_input")
    private Float secondInput;

    @Column(name = "result")
    private Float result;

    @Column(name = "create_at", nullable = false)
    private Instant createAt;

    @Column(name = "update_at")
    private Instant updateAt;
}
