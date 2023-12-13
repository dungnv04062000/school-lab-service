package com.schoollab.service;


import com.schoollab.controller.request.ExperimentCreateRequestBody;
import com.schoollab.controller.request.ExperimentDetailCreateRequestBody;
import com.schoollab.controller.request.ExperimentDetailUpdateRequestBody;
import com.schoollab.dto.ExperimentDetailDto;

import java.util.List;

public interface ExperimentDetailService {

    ExperimentDetailDto createExperimentDetail(ExperimentDetailCreateRequestBody requestBody);

    ExperimentDetailDto updateExperimentDetail(String experimentDetailId, ExperimentDetailUpdateRequestBody requestBody);

    List<ExperimentDetailDto> getExperimentDetails(String experimentId);
}
