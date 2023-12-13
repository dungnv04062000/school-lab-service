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
public class SubmissionDto {

    private String id;

    private String fromId;

    private String fromName;

    private String lessonId;

    private String lessonName;

    private String classId;

    private String className;

    private String semesterId;

    private String semesterName;

    private String year;

    private String content;

    private String attachmentUrl;

    private int subjectId;

    private String subjectName;

    private Instant createAt;

    private Instant updateAt;

    private Integer lateTime;

    private String status;

    @JsonGetter("create_at")
    public Object getCreateAt() {
        try {
            return createAt.getEpochSecond();
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
