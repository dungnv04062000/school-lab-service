package com.schoollab.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import java.time.Instant;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Accessors(chain = true)
public class ClassDto {

    private String id;

    private String name;

    private String campusId;

    private String campusName;

    private String semesterId;

    private String semesterName;

    private Integer year;

    private String formTeacherId;

    private String formTeacherName;

    private int gradeId;

    private String gradeName;

    private int studentCount;

    private Instant createAt;

    private Instant updateAt;

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
