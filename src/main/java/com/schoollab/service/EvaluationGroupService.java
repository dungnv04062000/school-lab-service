package com.schoollab.service;

import com.schoollab.dto.EvaluationGroupDto;

import java.util.List;

public interface EvaluationGroupService {
    EvaluationGroupDto evaluateGroup(String fromId,
                                        String groupId,
                                        String lessonId,
                                        Integer preparationPoint,
                                        Integer implementationPoint,
                                        Integer presentationPoint,
                                     Integer productionPoint);

    List<EvaluationGroupDto> getAllEvaluationGroups(String lessonId, String fromId);
}
