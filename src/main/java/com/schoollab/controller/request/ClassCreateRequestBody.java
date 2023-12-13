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
public class ClassCreateRequestBody {

    @NotNull(message = "Tên lớp không được để trống")
    private String name;

    @NotNull(message = "Campus không được để trống")
    private String campusId;

    @NotNull(message = "Học kỳ không được để trống")
    private String semesterId;

    @NotNull(message = "Khối không được để trống")
    private int gradeId;

    private String formTeacherId;
}
