package com.schoollab.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "submissions")
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Submission {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "from_id", nullable = false)
    private String fromId;

    @Column(name = "lesson_id", nullable = false)
    private String lessonId;

    @Column(name = "content", columnDefinition = "nvarchar", nullable = false)
    private String content;

    @Column(name = "attachment_url", columnDefinition = "nvarchar")
    private String attachmentUrl;

    @Column(name = "create_at", nullable = false)
    private Instant createAt;

    @Column(name = "update_at")
    private Instant updateAt;

    @Column(name = "late_time")
    private Integer lateTime;

    @Column(name = "status", columnDefinition = "nvarchar", nullable = false)
    private String status;

}
