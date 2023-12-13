package com.schoollab.service.impl;

import com.schoollab.dto.EvaluationDto;
import com.schoollab.dto.GradeExcelRowDto;
import com.schoollab.dto.LessonGradeDto;
import com.schoollab.dto.OwnerGradeStatisticDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.EvaluationMapper;
import com.schoollab.model.*;
import com.schoollab.modelTest.SetUpModelTest;
import com.schoollab.repository.*;
import com.schoollab.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class EvaluationServiceImplTest {
    @InjectMocks
    EvaluationServiceImpl evaluationService;
    @Mock
    EvaluationRepository evaluationRepository;
    @Mock
    EvaluationMapper evaluationMapper;
    @Mock
    EvaluationCriteriaRepository evaluationCriteriaRepository;
    @Mock
    GroupMemberRepository groupMemberRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    LessonRepository lessonRepository;
    @Mock
    ClassRepository classRepository;
    @Mock
    ClassStudentRepository classStudentRepository;
    @Mock
    SemesterRepository semesterRepository;
    @Mock
    S3Service s3Service;
    @Mock
    GenericMapper genericMapper;
    @Mock
    MultipartFile multipartFile;
    EvaluationDto evaluationDto;
    Lesson lesson;
    Evaluation evaluation;
    GradeExcelRowDto gradeExcelRowDto;
    LessonGradeDto lessonGradeDto;
    User user;
    OwnerGradeStatisticDto ownerGradeStatisticDto;
    Semester semester;
    GroupMember groupMember;
    List<GroupMember> groupMembers;
    List<LessonGradeDto> lessonGradeDtos;
    List<OwnerGradeStatisticDto> ownerGradeStatisticDtos;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        evaluation = SetUpModelTest.setUpEvaluation();
        evaluationDto = SetUpModelTest.setUpEvaluationDto();
        gradeExcelRowDto = SetUpModelTest.setUpGradeExcelRowDto();
        lessonGradeDto = SetUpModelTest.setUpLessonGradeDto();
        lesson = SetUpModelTest.setUpLesson();
        user = SetUpModelTest.setUpUser();
        ownerGradeStatisticDto = SetUpModelTest.setUpOwnerGradeStatisticDto();
        semester = SetUpModelTest.setUpSemester();
        groupMember = SetUpModelTest.setUpGroupMember();
        groupMembers = new ArrayList<>();
        groupMembers.add(groupMember);
        groupMembers.add(groupMember);
        lessonGradeDtos = new ArrayList<>();
        lessonGradeDtos.add(lessonGradeDto);
        ownerGradeStatisticDtos = new ArrayList<>();
        ownerGradeStatisticDtos.add(ownerGradeStatisticDto);
    }

    @Test
    void evaluateStudent_NotFoundLesson() {
        when(evaluationRepository.findByFromIdAndToIdAndLessonId(Mockito.eq("HE140705"), Mockito.eq("HE140704"), Mockito.eq("12345"))).thenReturn(evaluation);
        when(lessonRepository.findById(Mockito.eq("12345"))).thenReturn(Optional.ofNullable(null));
        EvaluationDto response = null;
        try {
            response = evaluationService.evaluateStudent("HE140705", "HE140704", "12345", 9.0F);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy dự án này");
            assertNull(response);
        }
    }

    @Test
    void evaluateStudent_EndLesson() {
        lesson.setStatus("FINISHED");
        when(evaluationRepository.findByFromIdAndToIdAndLessonId(Mockito.eq("HE140705"), Mockito.eq("HE140704"), Mockito.eq("12345"))).thenReturn(evaluation);
        when(lessonRepository.findById(Mockito.eq("12345"))).thenReturn(Optional.ofNullable(lesson));
        EvaluationDto response = null;
        try {
            response = evaluationService.evaluateStudent("HE140705", "HE140704", "12345", 9.0F);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Dự án này đã kết thúc");
            assertNull(response);
        }
    }
    @Test
    void evaluateStudent_InvalidGradeMax() {
        when(evaluationRepository.findByFromIdAndToIdAndLessonId(Mockito.eq("HE140705"), Mockito.eq("HE140704"), Mockito.eq("12345"))).thenReturn(evaluation);
        when(lessonRepository.findById(Mockito.eq("12345"))).thenReturn(Optional.ofNullable(lesson));
        EvaluationDto response = null;
        try {
            response = evaluationService.evaluateStudent("HE140705", "HE140704", "12345", 11.0F);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Điểm không hợp lệ");
            assertNull(response);
        }
    }

    @Test
    void evaluateStudent_InvalidGradeMin() {
        when(evaluationRepository.findByFromIdAndToIdAndLessonId(Mockito.eq("HE140705"), Mockito.eq("HE140704"), Mockito.eq("12345"))).thenReturn(evaluation);
        when(lessonRepository.findById(Mockito.eq("12345"))).thenReturn(Optional.ofNullable(lesson));
        EvaluationDto response = null;
        try {
            response = evaluationService.evaluateStudent("HE140705", "HE140704", "12345", -1.0F);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Điểm không hợp lệ");
            assertNull(response);
        }
    }

    @Test
    void evaluateStudent_EvaluationExists() {
        when(evaluationRepository.findByFromIdAndToIdAndLessonId(Mockito.eq("HE140705"), Mockito.eq("HE140704"), Mockito.eq("12345"))).thenReturn(evaluation);
        when(lessonRepository.findById(Mockito.eq("12345"))).thenReturn(Optional.ofNullable(lesson));
        when(evaluationRepository.save(Mockito.any())).thenReturn(evaluation);
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(EvaluationDto.class))).thenReturn(evaluationDto);
        EvaluationDto response = evaluationService.evaluateStudent("HE140705", "HE140704", "12345", 10.0F);
        assertNotNull(response);
        assertEquals(response, evaluationDto);
    }

    @Test
    void evaluateStudent_EvaluationNull() {
        when(evaluationRepository.findByFromIdAndToIdAndLessonId(Mockito.eq("HE140705"), Mockito.eq("HE140704"), Mockito.eq("12345"))).thenReturn(null);
        when(lessonRepository.findById(Mockito.eq("12345"))).thenReturn(Optional.ofNullable(lesson));
        when(evaluationRepository.save(Mockito.any())).thenReturn(evaluation);
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(EvaluationDto.class))).thenReturn(evaluationDto);
        EvaluationDto response = evaluationService.evaluateStudent("HE140705", "HE140704", "12345", 10.0F);
        assertNotNull(response);
        assertEquals(response, evaluationDto);
    }

    @Test
    void evaluateStudentByTemplate_NotFoundLesson() {
        when(lessonRepository.findById(Mockito.eq("12345"))).thenReturn(Optional.ofNullable(null));
        List<EvaluationDto> response = null;
        try {
            response = evaluationService.evaluateStudentByTemplate("HE140705", "12345", multipartFile);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy dự án này");
            assertNull(response);
        }
    }

    @Test
    void evaluateStudentByTemplate_EndLesson() {
        lesson.setStatus("FINISHED");
        when(lessonRepository.findById(Mockito.eq("12345"))).thenReturn(Optional.ofNullable(lesson));
        List<EvaluationDto> response = null;
        try {
            response = evaluationService.evaluateStudentByTemplate("HE140705", "12345", multipartFile);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Dự án này đã kết thúc");
            assertNull(response);
        }
    }

    @Test
    void evaluateStudentByTemplate_FileNull() {
        lesson.setStatus("FINISHED");
        when(lessonRepository.findById(Mockito.eq("12345"))).thenReturn(Optional.ofNullable(lesson));
        List<EvaluationDto> response = null;
        try {
            response = evaluationService.evaluateStudentByTemplate("HE140705", "12345", null);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Dự án này đã kết thúc");
            assertNull(response);
        }
    }

    @Test
    void evaluateStudentByTemplate_Exception() {
        when(lessonRepository.findById(Mockito.eq("12345"))).thenReturn(Optional.ofNullable(lesson));
        List<EvaluationDto> response = null;
        try {
            response = evaluationService.evaluateStudentByTemplate("HE140705", "12345", multipartFile);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void evaluateStudentByTemplate_() {
        when(lessonRepository.findById(Mockito.eq("12345"))).thenReturn(Optional.ofNullable(lesson));
        List<EvaluationDto> response = null;
        try {
            response = evaluationService.evaluateStudentByTemplate("HE140705", "12345", multipartFile);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void evaluateByGroup_NotFoundLesson() {
        when(lessonRepository.findById(Mockito.eq("12345"))).thenReturn(Optional.ofNullable(null));
        List<EvaluationDto> response = null;
        try {
            response = evaluationService.evaluateByGroup("HE140705", "HE140704", "12345", 9.0F);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy dự án này");
            assertNull(response);
        }
    }

    @Test
    void evaluateByGroup_EndLesson() {
        lesson.setStatus("FINISHED");
        when(lessonRepository.findById(Mockito.eq("12345"))).thenReturn(Optional.ofNullable(lesson));
        List<EvaluationDto> response = null;
        try {
            response = evaluationService.evaluateByGroup("HE140705", "HE140704", "12345", 9.0F);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Dự án này đã kết thúc");
            assertNull(response);
        }
    }

    @Test
    void evaluateByGroup_InvalidGradeMax() {
        when(lessonRepository.findById(Mockito.eq("12345"))).thenReturn(Optional.ofNullable(lesson));
        List<EvaluationDto> response = null;
        try {
            response = evaluationService.evaluateByGroup("HE140705", "HE140704", "12345", 11.0F);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Điểm không hợp lệ");
            assertNull(response);
        }
    }

    @Test
    void evaluateByGroup_InvalidGradeMin() {
        when(lessonRepository.findById(Mockito.eq("12345"))).thenReturn(Optional.ofNullable(lesson));
        List<EvaluationDto> response = null;
        try {
            response = evaluationService.evaluateByGroup("HE140705", "HE140704", "12345", -1.0F);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Điểm không hợp lệ");
            assertNull(response);
        }
    }

    @Test
    void evaluateByGroup_SuccessWithEvaluationNotNull() {
        when(lessonRepository.findById(Mockito.eq("12345"))).thenReturn(Optional.ofNullable(lesson));
        when(groupMemberRepository.findAllByGroupId(Mockito.any())).thenReturn(groupMembers);
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(EvaluationDto.class))).thenReturn(evaluationDto);
        List<EvaluationDto> response = evaluationService.evaluateByGroup("HE140705", "HE140704", "12345", 10.0F);
        assertNotNull(response);
        assertEquals(response.size(), 2);
    }

    @Test
    void evaluateByGroup_SuccessWithEvaluationNull() {
        when(lessonRepository.findById(Mockito.eq("12345"))).thenReturn(Optional.ofNullable(lesson));
        when(groupMemberRepository.findAllByGroupId(Mockito.any())).thenReturn(groupMembers);
        List<EvaluationDto> response = evaluationService.evaluateByGroup("HE140705", "HE140704", "12345", 10.0F);
        assertNotNull(response);
        assertEquals(response.size(), 0);
    }

    @Test
    void getEvaluationGradeList_NotFoundLesson() {
        when(lessonRepository.findById(Mockito.eq("12345"))).thenReturn(Optional.ofNullable(null));
        List<LessonGradeDto> response = null;
        try {
            response = evaluationService.getEvaluationGradeList("HE140705");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy dự án này");
            assertNull(response);
        }
    }

    @Test
    void getEvaluationGradeList_Success() {
        when(lessonRepository.findById(Mockito.eq("HE140705"))).thenReturn(Optional.ofNullable(lesson));
        when(evaluationMapper.getAllStudentGrade(Mockito.eq("HE140705"))).thenReturn(lessonGradeDtos);
        List<LessonGradeDto> response = evaluationService.getEvaluationGradeList("HE140705");
        assertNotNull(response);
        assertEquals(response, lessonGradeDtos);
    }

    @Test
    void getEvaluationGradeList_NoData() {
        when(lessonRepository.findById(Mockito.eq("HE140705"))).thenReturn(Optional.ofNullable(lesson));
        when(evaluationMapper.getAllStudentGrade(Mockito.eq("HE140705"))).thenReturn(new ArrayList<>());
        List<LessonGradeDto> response = evaluationService.getEvaluationGradeList("HE140705");
        assertNotNull(response);
        assertEquals(response.size(), 0);
    }

    @Test
    void getOneGrade_NotFoundUser() {
        when(userRepository.findById(Mockito.eq("HE140705"))).thenReturn(Optional.ofNullable(null));
        LessonGradeDto response = null;
        try {
            response = evaluationService.getOneGrade("HE140705", "123456");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy người dùng này");
            assertNull(response);
        }
    }

    @Test
    void getOneGrade_NotFoundLesson() {
        when(userRepository.findById(Mockito.eq("HE140705"))).thenReturn(Optional.ofNullable(user));
        when(lessonRepository.findById(Mockito.eq("123456"))).thenReturn(Optional.ofNullable(null));
        LessonGradeDto response = null;
        try {
            response = evaluationService.getOneGrade("HE140705", "123456");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy dự án này");
            assertNull(response);
        }
    }

    @Test
    void getOneGrade_SuccessIsOwner() {
        when(userRepository.findById(Mockito.eq("HE140705"))).thenReturn(Optional.ofNullable(user));
        when(lessonRepository.findById(Mockito.eq("123456"))).thenReturn(Optional.ofNullable(lesson));
        when(evaluationMapper.getAllStudentGrade(Mockito.eq("123456"))).thenReturn(lessonGradeDtos);
        LessonGradeDto response = evaluationService.getOneGrade("HE140705", "123456");
        assertNotNull(response);
        assertEquals(response, lessonGradeDto);
    }

    @Test
    void getOneGrade_SuccessIsNotOwner() {
        when(userRepository.findById(Mockito.eq("HE140706"))).thenReturn(Optional.ofNullable(user));
        when(lessonRepository.findById(Mockito.eq("123456"))).thenReturn(Optional.ofNullable(lesson));
        when(evaluationMapper.getAllStudentGrade(Mockito.eq("123456"))).thenReturn(lessonGradeDtos);
        LessonGradeDto response = evaluationService.getOneGrade("HE140706", "123456");
        assertNull(response);
    }

    @Test
    void getOwnerGradeStatistic_NotFound() {
        when(semesterRepository.findById(Mockito.eq("Fall22"))).thenReturn(Optional.ofNullable(null));
        List<OwnerGradeStatisticDto> response = null;
        try {
            response = evaluationService.getOwnerGradeStatistic("HE140705", "Fall22","subject");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy dữ liệu về học kỳ này");
            assertNull(response);
        }
    }

    @Test
    void getOwnerGradeStatistic_() {
        when(semesterRepository.findById(Mockito.eq("Fall22"))).thenReturn(Optional.ofNullable(semester));
        when(evaluationMapper.getOwnerGradeStatistic(Mockito.eq("HE140705"), Mockito.eq("Fall22"), Mockito.eq("subject"))).thenReturn(ownerGradeStatisticDtos);
        List<OwnerGradeStatisticDto> response = null;
        try {
            response = evaluationService.getOwnerGradeStatistic("HE140705", "Fall22", "subject");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy dữ liệu về học kỳ này");
            assertNull(response);
        }
    }

    @Test
    void getPreviousDateString() {
    }

    @Test
    void downloadEvaluationTemplate() {
    }

    @Test
    void downloadClassGrade() {
    }
}