package com.schoollab.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "notes")
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Note {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    @NotNull
    @Column(name = "user_id")
    private String userId;

    @Column(name = "lesson_id")
    private String lessonId;

    @NotNull
    @Column(name = "content")
    private String content;

    @NotNull
    @Column(name = "create_at")
    private Instant createAt;

    @Column(name = "update_at")
    private Instant updateAt;
}
