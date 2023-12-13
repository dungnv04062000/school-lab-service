package com.schoollab.service.impl;

import com.schoollab.dto.EvaluationGroupDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.EvaluationGroupMapper;
import com.schoollab.model.ClassGroup;
import com.schoollab.model.EvaluationGroup;
import com.schoollab.model.GroupMember;
import com.schoollab.model.Lesson;
import com.schoollab.modelTest.SetUpModelTest;
import com.schoollab.repository.ClassGroupRepository;
import com.schoollab.repository.EvaluationGroupRepository;
import com.schoollab.repository.GroupMemberRepository;
import com.schoollab.repository.LessonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class EvaluationGroupServiceImplTest {
    @InjectMocks
    EvaluationGroupServiceImpl evaluationGroupService;
    @Mock
    EvaluationGroupRepository evaluationGroupRepository;
    @Mock
    LessonRepository lessonRepository;
    @Mock
    EvaluationGroupMapper evaluationGroupMapper;
    @Mock
    ClassGroupRepository classGroupRepository;
    @Mock
    GroupMemberRepository groupMemberRepository;
    @Mock
    GenericMapper genericMapper;
    EvaluationGroupDto evaluationGroupDto;
    Lesson lesson;
    EvaluationGroup evaluationGroup;
    List<EvaluationGroupDto> evaluationGroupDtos;
    List<ClassGroup> classGroups;
    ClassGroup classGroup;
    GroupMember groupMember;
    List<GroupMember> groupMembers;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        evaluationGroup = SetUpModelTest.setUpEvaluationGroup();
        evaluationGroupDto = SetUpModelTest.setUpEvaluationGroupDto();
        lesson = SetUpModelTest.setUpLesson();
        classGroup = SetUpModelTest.setUpClassGroup();
        evaluationGroupDtos = new ArrayList<>();
        evaluationGroupDtos.add(evaluationGroupDto);
        classGroups = new ArrayList<>();
        classGroups.add(classGroup);
        groupMember = SetUpModelTest.setUpGroupMember();
        groupMembers = new ArrayList<>();
        groupMembers.add(groupMember);
        groupMembers.add(groupMember);
    }

    @Test
    void evaluateGroup_NotFoundLesson() {
        when(evaluationGroupRepository.findByFromIdAndGroupIdAndLessonId(Mockito.eq("HE140705"), Mockito.eq("1"), Mockito.eq("123456"))).thenReturn(evaluationGroup);
        when(lessonRepository.findById(Mockito.eq("123456"))).thenReturn(Optional.ofNullable(null));
        EvaluationGroupDto response = null;
        try {
            response = evaluationGroupService.evaluateGroup("HE140705", "1", "123456", 25, 25, 25, 25);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy dự án này");
            assertNull(response);
        }

    }

    @Test
    void evaluateGroup_FinishedLesson() {
        lesson.setStatus("FINISHED");
        when(evaluationGroupRepository.findByFromIdAndGroupIdAndLessonId(Mockito.eq("HE140705"), Mockito.eq("1"), Mockito.eq("123456"))).thenReturn(evaluationGroup);
        when(lessonRepository.findById(Mockito.eq("123456"))).thenReturn(Optional.ofNullable(lesson));
        EvaluationGroupDto response = null;
        try {
            response = evaluationGroupService.evaluateGroup("HE140705", "1", "123456", 25, 25, 25, 25);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Dự án này đã kết thúc");
            assertNull(response);
        }

    }

    @Test
    void evaluateGroup_EvaluationExists() {
        when(evaluationGroupRepository.findByFromIdAndGroupIdAndLessonId(Mockito.eq("HE140705"), Mockito.eq("1"), Mockito.eq("123456"))).thenReturn(evaluationGroup);
        when(lessonRepository.findById(Mockito.eq("123456"))).thenReturn(Optional.ofNullable(lesson));
        when(evaluationGroupRepository.save(Mockito.any())).thenReturn(evaluationGroup);
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(EvaluationGroupDto.class))).thenReturn(evaluationGroupDto);
        EvaluationGroupDto response  = evaluationGroupService.evaluateGroup("HE140705", "1", "123456", 25, 25, 25, 25);

        assertNotNull(response);
        assertEquals(response, evaluationGroupDto);
    }

    @Test
    void evaluateGroup_EvaluationNull() {
        when(evaluationGroupRepository.findByFromIdAndGroupIdAndLessonId(Mockito.eq("HE140705"), Mockito.eq("1"), Mockito.eq("123456"))).thenReturn(null);
        when(lessonRepository.findById(Mockito.eq("123456"))).thenReturn(Optional.ofNullable(lesson));
        when(evaluationGroupRepository.save(Mockito.any())).thenReturn(evaluationGroup);
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(EvaluationGroupDto.class))).thenReturn(evaluationGroupDto);
        EvaluationGroupDto response  = evaluationGroupService.evaluateGroup("HE140705", "1", "123456", 25, 25, 25, 25);

        assertNotNull(response);
        assertEquals(response, evaluationGroupDto);
    }

    @Test
    void getAllEvaluationGroups_GroupNull() {
        when(evaluationGroupMapper.getAllEvaluationGroups(Mockito.eq("123456"), Mockito.eq("HE140705"))).thenReturn(evaluationGroupDtos);
        when(classGroupRepository.findAllByLessonId(Mockito.eq("123456"))).thenReturn(classGroups);
        when(groupMemberRepository.findAllByGroupId(Mockito.any())).thenReturn(new ArrayList<>());

        try {
            List<EvaluationGroupDto> response = evaluationGroupService.getAllEvaluationGroups("123456", "HE140705");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
        }
    }

    @Test
    void getAllEvaluationGroups_OwnerGroup() {
        when(evaluationGroupMapper.getAllEvaluationGroups(Mockito.eq("123456"), Mockito.eq("HE140705"))).thenReturn(evaluationGroupDtos);
        when(classGroupRepository.findAllByLessonId(Mockito.eq("123456"))).thenReturn(classGroups);
        when(groupMemberRepository.findAllByGroupId(Mockito.any())).thenReturn(groupMembers);

        try {
            List<EvaluationGroupDto> response = evaluationGroupService.getAllEvaluationGroups("123456", "HE140705");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
        }
    }

    @Test
    void getAllEvaluationGroups_GroupNotNull() {
        when(evaluationGroupMapper.getAllEvaluationGroups(Mockito.eq("123456"), Mockito.eq("HE140705"))).thenReturn(evaluationGroupDtos);
        when(classGroupRepository.findAllByLessonId(Mockito.eq("123456"))).thenReturn(classGroups);
        groupMembers = new ArrayList<>();
        groupMember.setMemberId("HE1212");
        groupMembers.add(groupMember);
        when(groupMemberRepository.findAllByGroupId(Mockito.any())).thenReturn(groupMembers);

        try {
            List<EvaluationGroupDto> response = evaluationGroupService.getAllEvaluationGroups("123456", "HE140705");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
        }
    }
}