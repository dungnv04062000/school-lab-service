package com.schoollab.service;

import com.schoollab.controller.request.EvaluationTeammateCreateUpdateRequestBody;
import com.schoollab.dto.EvaluationTeammateDto;

import java.util.List;

public interface EvaluationTeammateService {
    EvaluationTeammateDto evaluateTeammate(String fromId,
                                           String toId,
                                           String lessonId,
                                           Integer hardWorkingPoint,
                                           Integer teamworkPoint,
                                           Integer skillPoint);

    List<EvaluationTeammateDto> evaluateTeammates(String fromId,
                                                  EvaluationTeammateCreateUpdateRequestBody requestBody);

    List<EvaluationTeammateDto> getAllEvaluationTeammates(String lessonId, String userId);
}
