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
public class ClassGroupCreateRequestBody {
    @NotNull(message = "Mã lớp không được để trống")
    private String classId;

    @NotNull(message = "Mã dự án không được để trống")
    private String lessonId;

    @NotNull(message = "Số lượng nhóm không được để trống")
    private Integer numberOfGroup;

    @NotNull(message = "Ghi đè không được để trống")
    private Boolean isOverride;
}
