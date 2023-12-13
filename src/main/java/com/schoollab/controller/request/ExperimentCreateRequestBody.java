package com.schoollab.controller.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ExperimentCreateRequestBody {

    private String title;

    @NotNull(message = "NoteId không được để trống")
    private String noteId;

    private String firstLabel;

    private String secondLabel;

    private String resultLabel;

    @NotNull(message = "Đơn vị đo (1) không được để trống")
    private String firstMeasure;

    @NotNull(message = "Đơn vị đo (2) không được để trống")
    private String secondMeasure;

    @NotNull(message = "Đơn vị đo (KQ) không được để trống")
    private String resultMeasure;
}
