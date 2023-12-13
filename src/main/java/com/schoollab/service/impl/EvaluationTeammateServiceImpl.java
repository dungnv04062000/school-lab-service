package com.schoollab.service.impl;

import com.schoollab.common.enums.LessonStatusEnum;
import com.schoollab.common.error.BadRequestException;
import com.schoollab.common.error.NotFoundException;
import com.schoollab.controller.request.EvaluationTeammateCreateUpdateRequestBody;
import com.schoollab.dto.EvaluationTeammateDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.EvaluationTeammateMapper;
import com.schoollab.model.EvaluationTeammate;
import com.schoollab.model.Lesson;
import com.schoollab.repository.EvaluationTeammateRepository;
import com.schoollab.repository.LessonRepository;
import com.schoollab.service.EvaluationTeammateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class EvaluationTeammateServiceImpl implements EvaluationTeammateService {

    @Autowired
    EvaluationTeammateRepository evaluationTeammateRepository;

    @Autowired
    LessonRepository lessonRepository;

    @Autowired
    EvaluationTeammateMapper evaluationTeammateMapper;

    @Autowired
    GenericMapper genericMapper;

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public EvaluationTeammateDto evaluateTeammate(String fromId, String toId, String lessonId, Integer hardWorkingPoint, Integer teamworkPoint, Integer skillPoint) {
        EvaluationTeammate alreadyEvaluationTeammate = evaluationTeammateRepository
                .findByFromIdAndToIdAndLessonId(fromId, toId, lessonId);
        Optional<Lesson> optLesson = lessonRepository.findById(lessonId);
        if(!optLesson.isPresent()){
            throw new NotFoundException("Không tìm thấy dự án này");
        }
        Lesson lesson = optLesson.get();
        if(lesson.getStatus().equals(LessonStatusEnum.FINISHED.name())){
            throw new BadRequestException("Dự án này đã kết thúc");
        }

        if(alreadyEvaluationTeammate == null){
            //chưa có đánh giá -> tạo mới
            EvaluationTeammate evaluationTeammate = new EvaluationTeammate()
                    .setFromId(fromId)
                    .setToId(toId)
                    .setLessonId(lessonId)
                    .setHardWorking(hardWorkingPoint)
                    .setTeamwork(teamworkPoint)
                    .setSkill(skillPoint)
                    .setCreateAt(Instant.now());
            EvaluationTeammate savedEvaluationTeammate = evaluationTeammateRepository.save(evaluationTeammate);
            return genericMapper.mapToTypeNotNullProperty(savedEvaluationTeammate, EvaluationTeammateDto.class);
        } else {
            //đã có đánh giá -> update
            alreadyEvaluationTeammate
                    .setHardWorking(hardWorkingPoint)
                    .setTeamwork(teamworkPoint)
                    .setSkill(skillPoint)
                    .setUpdateAt(Instant.now());
            EvaluationTeammate savedEvaluationTeammate = evaluationTeammateRepository.save(alreadyEvaluationTeammate);
            return genericMapper.mapToTypeNotNullProperty(savedEvaluationTeammate, EvaluationTeammateDto.class);
        }
    }

    @Override
    public List<EvaluationTeammateDto> evaluateTeammates(String fromId, EvaluationTeammateCreateUpdateRequestBody requestBody) {
        if(requestBody == null
                || requestBody.getTeammates() == null
                || requestBody.getTeammates().isEmpty()){
            return Collections.emptyList();
        }

        List<EvaluationTeammateDto> result = new ArrayList<>();
        for(EvaluationTeammateCreateUpdateRequestBody.Teammate teammate : requestBody.getTeammates()){
            result.add(evaluateTeammate(fromId, teammate.getToId(),
                    teammate.getLessonId(),
                    teammate.getHardWorking(),
                    teammate.getTeamwork(),
                    teammate.getSkill()));
        }
        return result;
    }

    @Override
    public List<EvaluationTeammateDto> getAllEvaluationTeammates(String lessonId, String userId) {
        return evaluationTeammateMapper.getAllEvaluationTeammates(lessonId, userId);
    }
}
