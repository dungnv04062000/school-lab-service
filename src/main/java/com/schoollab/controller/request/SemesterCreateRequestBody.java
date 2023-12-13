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
public class SemesterCreateRequestBody {

    @NotNull(message = "Campus không được để trống")
    private String campusId;

    @NotNull(message = "Tên học kỳ không được để trống")
    private String name;

    @NotNull(message = "Ngày bắt đấu học kỳ không được để trống")
    private Long startAt;

    @NotNull(message = "Năm học không được để trống")
    private Integer year;
}
