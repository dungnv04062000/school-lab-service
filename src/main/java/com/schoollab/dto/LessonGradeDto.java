package com.schoollab.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LessonGradeDto {

    private String studentId;

    private String studentName;

    private Integer experimentCount;

    private String groupId;

    private String groupName;

    private Float hardWorking;

    private Float teamwork;

    private Float skill;

    private Float preparation;

    private Float implementation;

    private Float presentation;

    private Float production;

    private Float total;

    private Float grade;
}
