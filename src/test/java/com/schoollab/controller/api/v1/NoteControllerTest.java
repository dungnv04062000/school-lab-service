package com.schoollab.controller.api.v1;

import com.schoollab.common.util.UserInfoUtil;
import com.schoollab.controller.request.NoteAddRequest;
import com.schoollab.controller.request.NoteUpdateRequestBody;
import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.dto.NoteDto;
import com.schoollab.modelTest.SetUpModelTest;
import com.schoollab.service.NoteService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class NoteControllerTest {
    @InjectMocks
    NoteController noteController;

    @Mock
    NoteService noteService;
    NoteDto noteDto;

    NoteAddRequest noteAddRequest;

    NoteUpdateRequestBody noteUpdateRequestBody;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        noteDto = SetUpModelTest.setUpNoteDto();
        noteAddRequest = SetUpModelTest.noteReq();
    }

    @Test
    void saveNote() {
//        ResponseBodyDto responseBodyDto = new ResponseBodyDto<>(noteDto, ResponseCodeEnum.R_201, "CREATE");
//        when(noteService.saveNote(Mockito.any(), Mockito.any())).thenReturn(noteDto);
//        ResponseEntity<ResponseBodyDto<NoteDto>> response = noteController.saveNote(noteAddRequest);
//        assertNotNull(response);
//        assertEquals(response.getStatusCode(), HttpStatus.OK);
//        assertEquals(response.getBody().getItem(),noteDto);
//        assertEquals(response.getBody(),responseBodyDto);

    }

    @Test
    void updateNote() {
        ResponseBodyDto responseBodyDto = new ResponseBodyDto(noteDto, ResponseCodeEnum.R_200, "UPDATE");
        when(noteService.updateNote(Mockito.any(), Mockito.any())).thenReturn(noteDto);

        ResponseEntity<ResponseBodyDto<NoteDto>> response = noteController.updateNote("1", noteUpdateRequestBody);
        assertNotNull(response);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getItem(), noteDto);
        assertEquals(response.getBody(), responseBodyDto);

    }

    @Test
    void getOne() {
        ResponseBodyDto responseBodyDto = new ResponseBodyDto(noteDto, ResponseCodeEnum.R_200, "OK");
        when(noteService.getOne(Mockito.any())).thenReturn(noteDto);

        ResponseEntity<ResponseBodyDto<NoteDto>> response = noteController.getOne("1");

        assertNotNull(response);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getItem(), noteDto);
        assertEquals(response.getBody(), responseBodyDto);
    }

    @Test
    void getOneByLessonId() {
//        ResponseBodyDto responseBodyDto = new ResponseBodyDto(noteDto, ResponseCodeEnum.R_200, "OK");
//        when(noteService.getOneByLessonId(Mockito.any(),Mockito.any())).thenReturn(noteDto);
//
//        ResponseEntity<ResponseBodyDto<NoteDto>> response = noteController.getOneByLessonId("1");
//
//        assertNotNull(response);
//
//        assertEquals(response.getStatusCode(), HttpStatus.OK);
////        assertEquals(response.getBody().getItem(),noteDto);
//        assertEquals(response.getBody(),responseBodyDto);

    }

    @Test
    void getAllNote() {
//        List<NoteDto> noteList =new ArrayList<>();
//        noteList.add(noteDto);
//
//        ResponseBodyDto responseBodyDto = new ResponseBodyDto(noteList, ResponseCodeEnum.R_200, "SEARCH", 3);
//        when(noteService.countAllNote(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(3);
//
//        when(noteService.filterNote(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any()))
//                .thenReturn(noteList);
//
//        ResponseEntity<ResponseBodyDto<List<NoteDto>>> response = noteController.getAllNote("HE140705", 3L,4L,"abc",1);
//
//        assertNotNull(response);
//        assertEquals(response.getStatusCode(), HttpStatus.OK);
//        assertEquals(response, 1);
    }

    @Test
    void deleteNote() {
        ResponseBodyDto responseBodyDto = new ResponseBodyDto("1", ResponseCodeEnum.R_200, "DELETE");

        when(noteService.deleteNote(Mockito.any())).thenReturn("1");

        ResponseEntity<ResponseBodyDto<NoteDto>> response = noteController.deleteNote("1");

        assertNotNull(response);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
//        assertEquals(response.getBody().getItem(),noteDto);
        assertEquals(response.getBody(), responseBodyDto);
    }
}