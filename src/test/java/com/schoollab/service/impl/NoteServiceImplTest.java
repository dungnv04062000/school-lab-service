package com.schoollab.service.impl;

import com.schoollab.common.constants.Constants;
import com.schoollab.common.error.BadRequestException;
import com.schoollab.common.error.UnKnownException;
import com.schoollab.controller.request.NoteAddRequest;
import com.schoollab.controller.request.NoteUpdateRequestBody;
import com.schoollab.dto.ExperimentDetailDto;
import com.schoollab.dto.ExperimentDto;
import com.schoollab.dto.NoteDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.NoteMapper;
import com.schoollab.model.Note;
import com.schoollab.modelTest.SetUpModelTest;
import com.schoollab.repository.ExperimentRepository;
import com.schoollab.repository.NoteRepository;
import com.schoollab.service.ExperimentDetailService;
import com.schoollab.service.ExperimentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NoteServiceImplTest {
    @InjectMocks
    NoteServiceImpl noteService;
    @Mock
    NoteRepository noteRepository;
    @Mock
    GenericMapper genericMapper;
    @Mock
    NoteMapper noteMapper;
    @MockBean
    NoteAddRequest noteAddRequest;
    @MockBean
    Note note;
    @Mock
    ExperimentService experimentService;
    @Mock
    ExperimentDetailService experimentDetailService;
    @MockBean
    NoteDto noteDto;
    @Mock
    ExperimentRepository experimentRepository;
    ExperimentDto experimentDto;
    NoteUpdateRequestBody noteUpdateReq;
    ExperimentDetailDto experimentDetailDto;
    List<ExperimentDto> experimentDtos = new ArrayList<>();
    List<ExperimentDetailDto> experimentDetailDtos = new ArrayList<>();
    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        noteAddRequest = SetUpModelTest.noteReq();
        note = SetUpModelTest.setUpNote();
        noteDto = SetUpModelTest.setUpNoteDto();
        noteUpdateReq = SetUpModelTest.noteUpdateReq();
        experimentDto = SetUpModelTest.setUpExperimentDto();
        experimentDetailDto = SetUpModelTest.setUpExperimentDetailDto();
        experimentDtos.add(experimentDto);
        experimentDetailDtos.add(experimentDetailDto);
    }

    private void mockReturn(){
        Mockito.when(noteRepository.save(Mockito.any())).thenReturn(note);
        Mockito.when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(NoteDto.class))).thenReturn(noteDto);
    }

    @Test
    void saveNote_success() {
        Mockito.when(noteRepository.findNoteLessonByUser(Mockito.any(), Mockito.any())).thenReturn(null);
        mockReturn();
        NoteDto response = noteService.saveNote("HE140705", noteAddRequest);

        assertNotEquals(null, response);
        assertEquals(noteDto, response);
    }

    @Test
    void saveNote_ExistsNote() {
        when(noteRepository.findNoteLessonByUser(Mockito.eq("HE140705"), Mockito.any())).thenReturn(note);
        NoteDto response = null;
        try {
            response = noteService.saveNote("HE140705", noteAddRequest);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Bạn đã tạo ghi chú cho dự án này rồi");
        }
    }

    @Test
    void updateNote_success(){
        Mockito.when(noteRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(note));
        mockReturn();
        NoteDto response = noteService.updateNote("HE140705", noteUpdateReq);

        assertNotEquals(null, response);
        assertEquals(noteDto, response);
    }

    @Test
    void updateNote_notFound(){
        Mockito.when(noteRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        NoteDto response = null;
        try {
            response = noteService.updateNote("HE140705", noteUpdateReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy ghi chú này");
            assertEquals(null, response);
        }

    }

    @Test
    void updateNote_exception(){
        Exception ex = new UnKnownException("Có lỗi trong quá trình xử lý");
        Mockito.when(noteRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(note));
        NoteDto response = null;
        try {
            when(noteRepository.save(Mockito.any())).thenThrow(ex);
            response = noteService.updateNote("HE140705", noteUpdateReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), ex.getMessage());
            assertEquals(null, response);
        }

    }

    @Test
    void saveNote_mappingFail(){
        NoteDto noteMappingFail = new NoteDto();
        BeanUtils.copyProperties(noteDto, noteMappingFail);
        noteMappingFail.setContent("Mapping Fail!");
        Mockito.when(noteRepository.findNoteLessonByUser(Mockito.any(), Mockito.any())).thenReturn(null);
        mockReturn();
        NoteDto response = noteService.saveNote("HE140705", noteAddRequest);

        assertNotEquals(null, response);
        assertEquals(noteMappingFail.getUserId(), response.getUserId());
        assertNotEquals(noteMappingFail, response);
    }

    @Test
    void saveNote_exception(){
        Exception e = new UnKnownException("Có lỗi trong quá trình xử lý");

        Mockito.when(noteRepository.findNoteLessonByUser(Mockito.any(), Mockito.any())).thenReturn(null);
        Mockito.when(noteRepository.save(Mockito.any())).thenThrow(e);
        NoteDto response = null;
        try {
            response = noteService.saveNote("HE140705", noteAddRequest);
        }catch (Exception ex){
            assertTrue(ex instanceof Exception);
            assertEquals(null, response);
        }
    }

    @Test
    void getOne_success_tc1() {
        Mockito.when(noteMapper.getOne(Mockito.any())).thenReturn(noteDto);
        when(experimentService.getExperiments(Mockito.any())).thenReturn(new ArrayList<>());
        NoteDto response = noteService.getOne("1234");
        assertNotNull(response);
        assertEquals(noteDto, response);
    }

    @Test
    void getOne_success_tc2() {
        Mockito.when(noteMapper.getOne(Mockito.any())).thenReturn(noteDto);
        when(experimentService.getExperiments(Mockito.any())).thenReturn(experimentDtos);
        when(experimentDetailService.getExperimentDetails(Mockito.any())).thenReturn(experimentDetailDtos);
        NoteDto response = noteService.getOne("1234");
        assertNotNull(response);
        assertEquals(noteDto, response);
    }

    @Test
    void getOne_success_responseNull() {
        Mockito.when(noteMapper.getOne(Mockito.any())).thenReturn(null);
        NoteDto response = noteService.getOne("1234");

        assertNull(response);
    }

    @Test
    void getOneByLessonId_success() {
        Mockito.when(noteMapper.getOneByLessonId(Mockito.eq("HE140705"), Mockito.eq("1234"))).thenReturn(noteDto);

        NoteDto response = noteService.getOneByLessonId("HE140705","1234");
        assertNotNull(response);
        assertEquals(noteDto, response);
    }

    @Test
    void getOneByLessonId_success_responseNull() {
        Mockito.when(noteMapper.getOneByLessonId(Mockito.eq("HE140705"), Mockito.eq("1234"))).thenReturn(null);
        NoteDto response = noteService.getOneByLessonId("HE140705","1234");

        assertNull(response);
    }

    @Test
    void filterNote_success_tc1() {
        List<NoteDto> list = new ArrayList<>();
        list.add(noteDto);
        Mockito.when(noteMapper.getAll(Mockito.eq("HE140705"), Mockito.eq("1234"), Mockito.eq(Instant.ofEpochSecond(123)),
                Mockito.eq(Instant.ofEpochSecond(123)),Mockito.eq(1), Mockito.eq(15), Mockito.eq("ASC"))).thenReturn(list);
        when(experimentService.getExperiments(Mockito.any())).thenReturn(new ArrayList<>());

        List<NoteDto> response = noteService.filterNote("HE140705","1234", Instant.ofEpochSecond(123), Instant.ofEpochSecond(123), 1, 15, "ASC");
        assertNotNull(response);
        assertEquals(list.size(), response.size());
    }

    @Test
    void filterNote_success_tc2() {
        List<NoteDto> list = new ArrayList<>();
        list.add(noteDto);
        Mockito.when(noteMapper.getAll(Mockito.eq("HE140705"), Mockito.eq("1234"), Mockito.eq(Instant.ofEpochSecond(123)),
                Mockito.eq(Instant.ofEpochSecond(123)),Mockito.eq(1), Mockito.eq(15), Mockito.eq("ASC"))).thenReturn(list);
        when(experimentService.getExperiments(Mockito.any())).thenReturn(new ArrayList<>());
        when(experimentService.getExperiments(Mockito.any())).thenReturn(experimentDtos);
        when(experimentDetailService.getExperimentDetails(Mockito.any())).thenReturn(experimentDetailDtos);
        List<NoteDto> response = noteService.filterNote("HE140705","1234", Instant.ofEpochSecond(123), Instant.ofEpochSecond(123), 1, 15, "ASC");
        assertNotNull(response);
        assertEquals(list.size(), response.size());
    }

    @Test
    void filterNote_success_responseNull() {
        List<NoteDto> list = new ArrayList<>();
        Mockito.when(noteMapper.getAll(Mockito.eq("HE140705"), Mockito.eq("1234"), Mockito.eq(Instant.now()),
                Mockito.eq(Instant.now()),Mockito.eq(1), Mockito.eq(15), Mockito.eq("ASC"))).thenReturn(list);

        List<NoteDto> response = noteService.filterNote("HE140705","1234", Instant.now(), Instant.now(), 1, 15, "ASC");

        assertTrue(response.isEmpty());
    }

    @Test
    void deleteNote_success() {
        doNothing().when(noteRepository).deleteById(Mockito.eq("123456789"));
        String response = noteService.deleteNote("123456789");

        assertEquals("Xóa ghi chú thành công", response);
    }

    @Test
    void deleteNote_exception() {
        Exception e = new BadRequestException("Xoá thất bại");
        doThrow(e).when(noteRepository).deleteById(Mockito.eq("123456789"));
        String response = null;
        try{
            response = noteService.deleteNote("123456789");
        }catch (Exception ex){
            assertTrue(ex instanceof Exception);
        }
        assertEquals(null, response);
    }

    @Test
    void countAllNote(){
        when(noteMapper.countAllNote(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
        int response = noteService.countAllNote("HE140705", "1234", Instant.now(), Instant.now());
        assertNotNull(response);
        assertEquals(response, 1);
    }
}