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
public class ExperimentDetailUpdateRequestBody {
    @NotNull(message = "Dữ liệu 1 không được để trống")
    private Float firstInput;

    @NotNull(message = "Dữ liệu 2 không được để trống")
    private Float secondInput;

    @NotNull(message = "Kết quả không được để trống")
    private Float result;
}
