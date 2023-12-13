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
public class ExperimentUpdateRequestBody {

    private String title;

    private String firstLabel;

    private String secondLabel;

    private String resultLabel;

    private String firstMeasure;

    private String secondMeasure;

    private String resultMeasure;
}
