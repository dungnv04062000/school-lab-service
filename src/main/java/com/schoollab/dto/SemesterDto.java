package com.schoollab.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.time.Instant;

@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SemesterDto {

    private String id;

    private String name;

    private Integer year;

    private String campusId;

    private Instant startAt;

    private Instant createAt;

    private Instant updateAt;

    @JsonGetter("start_at")
    public Object getStartAt() {
        try {
            return startAt.getEpochSecond();
        } catch (Exception e) {
            return null;
        }
    }

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
