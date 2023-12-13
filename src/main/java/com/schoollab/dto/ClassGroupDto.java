package com.schoollab.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassGroupDto {
    private String id;

    private String name;

    private String classId;

    private String lessonId;

    private List<GroupMemberDto> members;

    private Instant createAt;

    @JsonGetter("create_at")
    public Object getCreateAt() {
        try {
            return createAt.getEpochSecond();
        } catch (Exception e) {
            return null;
        }
    }
}
