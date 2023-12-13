package com.schoollab.service.impl;

import com.schoollab.dto.SubmissionDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.SubmissionMapper;
import com.schoollab.model.Lesson;
import com.schoollab.model.Submission;
import com.schoollab.modelTest.SetUpModelTest;
import com.schoollab.repository.LessonRepository;
import com.schoollab.repository.SubmissionRepository;
import com.schoollab.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SubmissionServiceImplTest {
    @InjectMocks
    SubmissionServiceImpl submissionService;
    @Mock
    SubmissionRepository submissionRepository;
    @Mock
    GenericMapper genericMapper;
    @Mock
    SubmissionMapper submissionMapper;
    @Mock
    S3Service s3Service;
    @Mock
    LessonRepository lessonRepository;
    @Mock
    MultipartFile multipartFile;
    Submission submission;
    SubmissionDto submissionDto;
    Lesson lesson;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        submissionDto = SetUpModelTest.setUpSubmissionDto();
        submission = SetUpModelTest.setUpSubmission();
        lesson = SetUpModelTest.setUpLesson();
    }

    @Test
    void createSubmission_NotFoundSubmission() {
        when(lessonRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(lesson));
        when(submissionRepository.findAllByFromIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(submission);
        SubmissionDto response = null;
        try {
            response = submissionService.createSubmission("HE140705", "1234", "Nop bai hoa hoc", multipartFile);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
            assertEquals(e.getMessage(), "Không tìm thấy bài nộp này");
        }
    }

    @Test
    void createSubmission_NotFoundLesson() {
        when(lessonRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        SubmissionDto response = null;
        try {
            response = submissionService.createSubmission("HE140705", "1234", "Nop bai hoa hoc", multipartFile);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
            assertEquals(e.getMessage(), "Không tìm thấy dự án này");
        }
    }

    @Test
    void createSubmission_success() {
        when(lessonRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(lesson));
        when(submissionRepository.findAllByFromIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(null);
        when(submissionRepository.save(Mockito.any())).thenReturn(submission);
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(SubmissionDto.class))).thenReturn(submissionDto);
        SubmissionDto response = submissionService.createSubmission("HE140705", "1234", "Nop bai hoa hoc", multipartFile);
        assertNotNull(response);
        assertEquals(response, submissionDto);
    }

    @Test
    void editSubmission_notFoundSubmission() {
        when(submissionRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        SubmissionDto response = null;
        try {
            response = submissionService.editSubmission("HE140705", "nop lai bai nay", multipartFile);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
            assertEquals(e.getMessage(), "Không tìm thấy bài nộp này");
        }
    }

    @Test
    void editSubmission_notFoundLesson() {
        when(submissionRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(submission));
        when(lessonRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        SubmissionDto response = null;
        try {
            response = submissionService.editSubmission("HE140705", "nop lai bai nay", multipartFile);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
            assertEquals(e.getMessage(), "Không tìm thấy dự án này");
        }
    }

    @Test
    void editSubmission_success() {
        when(submissionRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(submission));
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(SubmissionDto.class))).thenReturn(submissionDto);
        when(lessonRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(lesson));
        SubmissionDto response = submissionService.editSubmission("HE140705", "nop lai bai nay", multipartFile);
        assertNotNull(response);
        assertEquals(response, submissionDto);
    }

    @Test
    void getSubmissionByFromIdAndLessonId_notFound() {
        when(submissionRepository.findAllByFromIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(null);
        SubmissionDto response = null;
        try {
            response = submissionService.getSubmissionByFromIdAndLessonId("HE140705", "1234");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
            assertEquals(e.getMessage(), "Bạn chưa nộp bài cho dự án này");
        }
    }

    @Test
    void getSubmissionByFromIdAndLessonId_success() {
        when(submissionRepository.findAllByFromIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(submission);
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(SubmissionDto.class))).thenReturn(submissionDto);
        SubmissionDto response = submissionService.getSubmissionByFromIdAndLessonId("HE140705", "1234");
        assertNotNull(response);
        assertEquals(response, submissionDto);
    }

    @Test
    void getOne_nullResponse_success() {
        when(submissionMapper.getOne(Mockito.any())).thenReturn(null);
        SubmissionDto response = submissionService.getOne("1234");
        assertNull(response);
    }

    @Test
    void getOne_success() {
        when(submissionMapper.getOne(Mockito.any())).thenReturn(submissionDto);
        SubmissionDto response = submissionService.getOne("1234");
        assertNotNull(response);
        assertEquals(response, submissionDto);
    }

    @Test
    void getAll_nullResponse_success() {
        List<SubmissionDto> list = new ArrayList<>();
        when(submissionMapper.getAll(Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.eq(1), Mockito.eq(15))).thenReturn(list);
        List<SubmissionDto> response = submissionService.getAll("He140705", "1234", "Fall 2022", "Hoc hoc", "Dung", Instant.now(), Instant.now(), "ASC", 1, 15);
        assertEquals(response.size(), list.size());
    }

    @Test
    void getAll_success() {
        List<SubmissionDto> list = new ArrayList<>();
        list.add(submissionDto);
        when(submissionMapper.getAll(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.eq(1), Mockito.eq(15))).thenReturn(list);
        List<SubmissionDto> response = submissionService.getAll("He140705", "1234", "Fall 2022", "Hoc hoc", "Dung", Instant.now(), Instant.now(), "ASC", 1, 15);
        assertNotNull(response);
        assertEquals(response.size(), list.size());
        assertEquals(response, list);
    }

    @Test
    void countAll_success() {
        when(submissionMapper.countAll(Mockito.any(),Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
        int response = submissionService.countAll("He140705","1234", "Fall 2022", "Hoc hoc", "Dung", Instant.now(), Instant.now());
        assertEquals(response, 1);
    }

    @Test
    void getOwnerSubmissions_success(){
        List<SubmissionDto> submissionDtos = new ArrayList<>();
        submissionDtos.add(submissionDto);
        when(submissionMapper.getOwnerSubmissions(Mockito.eq("HE140705"), Mockito.eq("Fall2022"), Mockito.eq("HOA HOC"), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.eq(1), Mockito.eq(15))).thenReturn(submissionDtos);
        List<SubmissionDto> response = submissionService.getOwnerSubmissions("HE140705", "Fall2022", "HOA HOC", Instant.now(), Instant.now(), "ASC", 1, 15);
        assertNotNull(response);
        assertEquals(response, submissionDtos);
        assertEquals(response.size(), submissionDtos.size());
    }

    @Test
    void countOwnerSubmissions_success(){
        List<SubmissionDto> submissionDtos = new ArrayList<>();
        submissionDtos.add(submissionDto);
        when(submissionMapper.countOwnerSubmissions(Mockito.eq("HE140705"), Mockito.eq("Fall2022"), Mockito.eq("HOA HOC"), Mockito.any(), Mockito.any())).thenReturn(3);
        int response = submissionService.countOwnerSubmissions("HE140705", "Fall2022", "HOA HOC", Instant.now(), Instant.now());
        assertNotNull(response);
        assertEquals(response, 3);
    }
}