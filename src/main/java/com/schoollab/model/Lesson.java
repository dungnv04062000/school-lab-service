package com.schoollab.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "lessons")
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Lesson {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "title", columnDefinition = "nvarchar", nullable = false)
    private String title;

    @Column(name = "class_id", nullable = false)
    private String classId;

    @Column(name = "subject_id", nullable = false)
    private int subjectId;

    @Column(name = "level_id", nullable = false)
    private int levelId;

    @Column(name = "context", columnDefinition = "nvarchar")
    private String context;

    @Column(name = "goal", columnDefinition = "nvarchar")
    private String goal;

    @Column(name = "stem_knowledge_id")
    private String stemKnowledgeId;

    @Column(name = "evaluation_criteria_id")
    private String evaluationCriteriaId;

    @Column(name = "create_by", nullable = false)
    private String createBy;

    @Column(name = "create_at", nullable = false)
    private Instant createAt;

    @Column(name = "end_at")
    private Instant endAt;

    @Column(name = "update_at")
    private Instant updateAt;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "attachment_url")
    private String attachmentUrl;

}
