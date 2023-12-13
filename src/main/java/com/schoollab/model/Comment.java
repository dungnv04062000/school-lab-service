package com.schoollab.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "comments")
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "content", nullable = false, columnDefinition = "nvarchar")
    private String content;

    @Column(name = "lesson_id", nullable = false)
    private String lessonId;

    @Column(name = "from_id", nullable = false)
    private String fromId;

    @Column(name = "comment_id")
    private String commentId;

    @Column(name = "create_at", nullable = false)
    private Instant createAt;

    @Column(name = "update_at")
    private Instant updateAt;
}
