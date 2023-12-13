package com.schoollab.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "semesters")
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Semester {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "campus_id", nullable = false)
    private String campusId;

    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    @Column(name = "create_at", nullable = false)
    private Instant createAt;

    @Column(name = "update_at")
    private Instant updateAt;
}
