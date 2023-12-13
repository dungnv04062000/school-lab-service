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
public class TutorialDto {
    private String id;
    private String title;
    private String description;
    private String attachmentUrl;
    private String createBy;
    private String campusId;
    private String createByFullname;
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
