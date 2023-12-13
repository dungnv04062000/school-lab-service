package com.schoollab.controller.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EvaluationCreateUpdateRequestBody {

    @NotNull(message = "Mã nhóm không được để trống")
    private String groupId;

    @NotNull(message = "Dự án không được để trống")
    private String lessonId;

    @NotNull(message = "Điểm không được để trống")
    private Float grade;
}
