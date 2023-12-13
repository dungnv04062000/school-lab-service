package com.schoollab.service;

import com.schoollab.controller.request.ExperimentCreateRequestBody;
import com.schoollab.controller.request.ExperimentUpdateRequestBody;
import com.schoollab.dto.ExperimentDto;

import java.util.List;

public interface ExperimentService {
    ExperimentDto createExperiment(String createBy, ExperimentCreateRequestBody requestBody);

    ExperimentDto updateExperiment(String experimentId, ExperimentUpdateRequestBody requestBody);

    List<ExperimentDto> getExperiments(String noteId);

    List<ExperimentDto> getExperiments(String userId, String lessonId);
}
