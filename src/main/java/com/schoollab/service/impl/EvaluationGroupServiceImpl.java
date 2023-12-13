package com.schoollab.service.impl;

import com.schoollab.common.enums.LessonStatusEnum;
import com.schoollab.common.error.BadRequestException;
import com.schoollab.common.error.NotFoundException;
import com.schoollab.dto.EvaluationGroupDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.EvaluationGroupMapper;
import com.schoollab.model.ClassGroup;
import com.schoollab.model.EvaluationGroup;
import com.schoollab.model.GroupMember;
import com.schoollab.model.Lesson;
import com.schoollab.repository.ClassGroupRepository;
import com.schoollab.repository.EvaluationGroupRepository;
import com.schoollab.repository.GroupMemberRepository;
import com.schoollab.repository.LessonRepository;
import com.schoollab.service.EvaluationGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class EvaluationGroupServiceImpl implements EvaluationGroupService {

    @Autowired
    EvaluationGroupRepository evaluationGroupRepository;

    @Autowired
    LessonRepository lessonRepository;

    @Autowired
    EvaluationGroupMapper evaluationGroupMapper;

    @Autowired
    ClassGroupRepository classGroupRepository;

    @Autowired
    GroupMemberRepository groupMemberRepository;

    @Autowired
    GenericMapper genericMapper;

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public EvaluationGroupDto evaluateGroup(String fromId,
                                            String groupId,
                                            String lessonId,
                                            Integer preparationPoint,
                                            Integer implementationPoint,
                                            Integer presentationPoint,
                                            Integer productionPoint) {
        EvaluationGroup alreadyEvaluationGroup = evaluationGroupRepository
                .findByFromIdAndGroupIdAndLessonId(fromId, groupId, lessonId);
        Optional<Lesson> optLesson = lessonRepository.findById(lessonId);
        if(!optLesson.isPresent()){
            throw new NotFoundException("Không tìm thấy dự án này");
        }
        Lesson lesson = optLesson.get();
        if(lesson.getStatus().equals(LessonStatusEnum.FINISHED.name())){
            throw new BadRequestException("Dự án này đã kết thúc");
        }

        if(alreadyEvaluationGroup == null){
            //chưa có đánh giá -> tạo mới
            EvaluationGroup evaluationGroup = new EvaluationGroup()
                    .setFromId(fromId)
                    .setGroupId(groupId)
                    .setLessonId(lessonId)
                    .setPreparation(preparationPoint)
                    .setImplementation(implementationPoint)
                    .setPresentation(presentationPoint)
                    .setProduction(productionPoint)
                    .setCreateAt(Instant.now());
            EvaluationGroup savedEvaluationGroup = evaluationGroupRepository.save(evaluationGroup);
            return genericMapper.mapToTypeNotNullProperty(savedEvaluationGroup, EvaluationGroupDto.class);
        } else {
            //đã có đánh giá -> update
            alreadyEvaluationGroup
                    .setPreparation(preparationPoint)
                    .setImplementation(implementationPoint)
                    .setPresentation(presentationPoint)
                    .setProduction(productionPoint)
                    .setUpdateAt(Instant.now());
            EvaluationGroup savedEvaluationGroup = evaluationGroupRepository.save(alreadyEvaluationGroup);
            return genericMapper.mapToTypeNotNullProperty(savedEvaluationGroup, EvaluationGroupDto.class);
        }
    }

    @Override
    public List<EvaluationGroupDto> getAllEvaluationGroups(String lessonId, String fromId) {
        List<EvaluationGroupDto> evaluateGroups = evaluationGroupMapper.getAllEvaluationGroups(lessonId, fromId);
        List<ClassGroup> classGroups = classGroupRepository.findAllByLessonId(lessonId);

        for (ClassGroup classGroup : classGroups){
            boolean isMyGroup = false;
            List<GroupMember> groupMembers = groupMemberRepository.findAllByGroupId(classGroup.getId());
            for(GroupMember member : groupMembers) {
                if(member.getMemberId().equals(fromId)){
                    isMyGroup = true;
                    break;
                }
            }
            if(isMyGroup) continue;
            boolean isEvaluated = false;
            for(EvaluationGroupDto evaluationGroup: evaluateGroups){
                if(evaluationGroup.getGroupId().equals(classGroup.getId())){
                    isEvaluated = true;
                    break;
                }
            }
            if(!isEvaluated){
                EvaluationGroupDto evaluationGroupDto = new EvaluationGroupDto()
                        .setFromId(fromId)
                        .setGroupId(classGroup.getId())
                        .setGroupName(classGroup.getName())
                        .setLessonId(classGroup.getLessonId())
                        .setPreparation(0)
                        .setImplementation(0)
                        .setPresentation(0)
                        .setProduction(0);
                evaluateGroups.add(evaluationGroupDto);
            }
        }

        Collections.sort(evaluateGroups, Comparator.comparing(EvaluationGroupDto::getGroupName));
        return evaluateGroups;
    }
}
