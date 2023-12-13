package com.schoollab.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "lesson_histories")
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class LessonHistory {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "lesson_id", nullable = false)
    private String lessonId;

    @Column(name = "context", columnDefinition = "nvarchar")
    private String context;

    @Column(name = "goal", columnDefinition = "nvarchar")
    private String goal;

    @Column(name = "attachment_url")
    private String attachmentUrl;

    @Column(name = "create_at", nullable = false)
    private Instant createAt;
}
