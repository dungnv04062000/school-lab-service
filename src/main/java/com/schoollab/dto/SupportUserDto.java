package com.schoollab.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.schoollab.common.error.BadRequestException;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Accessors(chain = true)
public class SupportUserDto {
    private String id;
    private String fromId;
    private String name;
    private String phoneNumber;
    private String email;
    private String title;
    private String content;
    private String response;
    private String priority;
    private Instant createAt;
    private Instant updateAt;
    private String status;

    @JsonGetter("create_at")
    public Object getCreatAt() {
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
