package com.schoollab.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import java.time.Instant;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Accessors(chain = true)
public class LessonDto {

    private String id;

    private String title;

    private String classId;

    private String semesterId;

    private String semesterName;

    private String year;

    private int levelId;

    private String levelName;

    private int subjectId;

    private String className;

    private String subjectName;

    private String context;

    private String goal;

    private String stemKnowledgeId;

    private String science;

    private String technology;

    private String engineering;

    private String mathematics;

    private String evaluationCriteriaId;

    private Float preparation;

    private Float implementation;

    private Float presentation;

    private Float production;

    private String createBy;

    private String createByFullName;

    private Instant createAt;

    private Instant endAt;

    private Instant updateAt;

    private String status;

    private String attachmentUrl;

    private Integer countComment;

    @JsonGetter("create_at")
    public Object getCreateAt() {
        try {
            return createAt.getEpochSecond();
        } catch (Exception e) {
            return null;
        }
    }

    @JsonGetter("end_at")
    public Object getEndAt() {
        try {
            return endAt.getEpochSecond();
        } catch (Exception e) {
            return null;
        }
    }

    @JsonGetter("update_at")
    public Object getUpdateAt() {
        try {
            return updateAt.getEpochSecond();
        } catch (Exception e) {
            return null;
        }
    }
}
