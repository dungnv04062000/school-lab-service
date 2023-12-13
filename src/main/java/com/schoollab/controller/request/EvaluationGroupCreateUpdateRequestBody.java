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
public class EvaluationGroupCreateUpdateRequestBody {

    private String groupId;

    @NotNull(message = "Dự án không được để trống")
    private String lessonId;

    @NotNull(message = "Điểm chuẩn bị-lên kế hoạch không được để trống")
    private Integer preparation;

    @NotNull(message = "Điểm thực hiện dự án không được để trống")
    private Integer implementation;

    @NotNull(message = "Điểm thuyết trình không được để trống")
    private Integer presentation;

    @NotNull(message = "Điểm sản phẩm không được để trống")
    private Integer production;
}
