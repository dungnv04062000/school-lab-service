package com.schoollab.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassTeacherDto {
    private String id;

    private String classId;

    private String teacherId;

    private Instant createAt;

    @JsonGetter("create_at")
    public Object getUpdateAt() {
        try {
            return createAt.getEpochSecond();
        } catch (Exception e) {
            return null;
        }
    }
}
