package com.schoollab.service.impl;

import com.schoollab.common.error.UnKnownException;
import com.schoollab.controller.request.SubjectAddRequest;
import com.schoollab.dto.SubjectDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.SubjectMapper;
import com.schoollab.model.Subject;
import com.schoollab.modelTest.SetUpModelTest;
import com.schoollab.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class SubjectServiceImplTest {
    @InjectMocks
    SubjectServiceImpl subjectService;
    @Mock
    SubjectRepository subjectRepository;
    @Mock
    GenericMapper genericMapper;
    @Mock
    SubjectMapper subjectMapper;
    SubjectAddRequest subjectAddReq;
    SubjectDto subjectDto;
    Subject subject;
    List<Subject> list = new ArrayList<>();
    List<SubjectDto> listDto = new ArrayList<>();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        subjectAddReq = SetUpModelTest.subjectReq();
        subject = SetUpModelTest.setUpSubject();
        subjectDto = SetUpModelTest.setUpSubjectDto();
        list.add(subject);
        listDto.add(subjectDto);
    }

    @Test
    void saveSubject_subjectExists() {
        when(subjectRepository.findSubjectByName(Mockito.any())).thenReturn(Optional.ofNullable(subject));
        SubjectDto response = null;
        try {
            response = subjectService.saveSubject(subjectAddReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Tên môn học đã có sẵn");
            assertNull(response);
        }
    }

    @Test
    void saveSubject_success(){
        when(subjectRepository.findSubjectByName(Mockito.any())).thenReturn(Optional.ofNullable(null));
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(SubjectDto.class))).thenReturn(subjectDto);

        SubjectDto response = subjectService.saveSubject(subjectAddReq);
        assertNotNull(response);
        assertEquals(response, subjectDto);
    }

    @Test
    void getOneSubject_success() {
        when(subjectMapper.getOneSubject(Mockito.eq(1))).thenReturn(subjectDto);
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(SubjectDto.class))).thenReturn(subjectDto);

        SubjectDto response = subjectService.getOneSubject(1);
        assertNotNull(response);
        assertEquals(response, subjectDto);
    }

    @Test
    void getOneSubject_nullResponse_success() {
        when(subjectMapper.getOneSubject(Mockito.eq(1))).thenReturn(null);
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(SubjectDto.class))).thenReturn(null);

        SubjectDto response = subjectService.getOneSubject(1);
        assertNull(response);
    }

    @Test
    void getAllSubject_success() {
        when(subjectRepository.findAll()).thenReturn(list);
        when(genericMapper.mapToListOfTypeNotNullProperty(Mockito.any(), Mockito.eq(SubjectDto.class))).thenReturn(listDto);

        List<SubjectDto> response = subjectService.getAllSubject();
        assertNotNull(response);
        assertEquals(response, listDto);
        assertEquals(response.size(), listDto.size());
    }

    @Test
    void getAllSubject_nullResponse_success() {
        when(subjectRepository.findAll()).thenReturn(null);
        when(genericMapper.mapToListOfTypeNotNullProperty(Mockito.any(), Mockito.eq(SubjectDto.class))).thenReturn(null);

        List<SubjectDto> response = subjectService.getAllSubject();
        assertNull(response);
    }

    @Test
    void filterSubject_success() {
        when(subjectMapper.getAllSubject(Mockito.any(), Mockito.any())).thenReturn(listDto);
        when(genericMapper.mapToListOfTypeNotNullProperty(Mockito.any(), Mockito.eq(SubjectDto.class))).thenReturn(listDto);

        List<SubjectDto> response = subjectService.filterSubject("He140705", "Khoa học");
        assertNotNull(response);
        assertEquals(response, listDto);
        assertEquals(response.size(), listDto.size());
    }

    @Test
    void filterSubject_nullResponse_success() {
        when(subjectMapper.getAllSubject(Mockito.any(), Mockito.any())).thenReturn(null);
        when(genericMapper.mapToListOfTypeNotNullProperty(Mockito.any(), Mockito.eq(SubjectDto.class))).thenReturn(null);

        List<SubjectDto> response = subjectService.getAllSubject();
        assertNull(response);
    }

    @Test
    void deleteSubject_notFound() {
        when(subjectRepository.findById(Mockito.eq(1))).thenReturn(Optional.ofNullable(null));
        String response = null;
        try {
            response = subjectService.deleteSubject(1);
        }catch (Exception e){
            assertNull(response);
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy môn học");
        }
    }

    @Test
    void deleteSubject_exception() {
        Exception ex = new UnKnownException("Xóa thất bại");
        when(subjectRepository.findById(Mockito.eq(1))).thenReturn(Optional.ofNullable(subject));
        doThrow(ex).when(subjectRepository).deleteById(Mockito.eq(1));
        String response = null;
        try {
            response = subjectService.deleteSubject(1);
        }catch (Exception e){
            assertNull(response);
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), ex.getMessage());
        }
    }

    @Test
    void deleteSubject_success(){
        when(subjectRepository.findById(Mockito.eq(1))).thenReturn(Optional.ofNullable(subject));

        String response = subjectService.deleteSubject(1);
        assertNotNull(response);
        assertEquals(response, "Xóa thành công");
    }
}