package com.schoollab.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.Date;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Accessors(chain = true)
public class TeacherInClassDto {
    private String id;
    private String teacherId;
    private String fullName;
    private String gender;
    private Instant birthDate;
    private String imageUrl;
    private String email;
    private String campusName;
    private String phoneNumber;

    //using when search all teacher in campus
    private String campusId;
    private String className;

    //root admin
    private Boolean isVerify;

    @JsonGetter("birth_date")
    public Object getBirthDate() {
        try {
            return birthDate.getEpochSecond();
        } catch (Exception e) {
            return null;
        }
    }
}
