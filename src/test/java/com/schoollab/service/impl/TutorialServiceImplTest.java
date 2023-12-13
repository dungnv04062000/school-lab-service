package com.schoollab.service.impl;

import com.schoollab.common.error.UnKnownException;
import com.schoollab.controller.request.TutorialCreateRequestBody;
import com.schoollab.controller.request.TutorialUpdateRequestBody;
import com.schoollab.dto.TutorialDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.TutorialMapper;
import com.schoollab.model.Campus;
import com.schoollab.model.Tutorial;
import com.schoollab.model.User;
import com.schoollab.modelTest.SetUpModelTest;
import com.schoollab.repository.CampusRepository;
import com.schoollab.repository.TutorialRepository;
import com.schoollab.repository.UserRepository;
import com.schoollab.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class TutorialServiceImplTest {
    @InjectMocks
    TutorialServiceImpl tutorialService;
    @Mock
    UserRepository userRepository;
    @Mock
    CampusRepository campusRepository;
    @Mock
    TutorialRepository tutorialRepository;
    @Mock
    S3Service s3Service;
    @Mock
    GenericMapper genericMapper;
    @Mock
    TutorialMapper tutorialMapper;
    @Mock
    MultipartFile multipartFile;

    Tutorial tutorial;
    TutorialDto tutorialDto;
    User user;
    Campus campus;
    TutorialCreateRequestBody createRequestBody;
    TutorialUpdateRequestBody updateRequestBody;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        createRequestBody = SetUpModelTest.tutorialCreateReq();
        updateRequestBody = SetUpModelTest.tutorialUpdateReq();
        tutorial = SetUpModelTest.setUpTutorial();
        tutorialDto = SetUpModelTest.setUpTutorialDto();
        user = SetUpModelTest.setUpUser();
        campus = SetUpModelTest.setUpCampus();
    }

    @Test
    void addTutorial_notFoundUser() {
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        TutorialDto response = null;
        try {
            response = tutorialService.addTutorial("HE140705", "fschool-hoa-lac", createRequestBody, multipartFile);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy người dùng này");
            assertNull(response);
        }
    }

    @Test
    void addTutorial_notFoundCampus() {
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(campusRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        TutorialDto response = null;
        try {
            response = tutorialService.addTutorial("HE140705", "fschool-hoa-lac", createRequestBody, multipartFile);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy cơ sở này");
            assertNull(response);
        }
    }

    @Test
    void addTutorial_isExistsTitle() {
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(campusRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(campus));
        when(tutorialRepository.findByTitle(Mockito.any())).thenReturn(tutorial);
        TutorialDto response = null;
        try {
            response = tutorialService.addTutorial("HE140705", "fschool-hoa-lac", createRequestBody, multipartFile);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Tên đề mục đã có sẵn, vui lòng tạo tên đề mục khác");
            assertNull(response);
        }
    }

    @Test
    void addTutorial_exception() {
        Exception ex = new UnKnownException("Có lỗi trong quá trình xử lý");
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(campusRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(campus));
        when(tutorialRepository.findByTitle(Mockito.any())).thenReturn(null);
        TutorialDto response = null;
        try {
            when(tutorialRepository.save(Mockito.any())).thenThrow(ex);
            response = tutorialService.addTutorial("HE140705", "fschool-hoa-lac", createRequestBody, multipartFile);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), ex.getMessage());
            assertNull(response);
        }
    }

    @Test
    void addTutorial_success() {
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(campusRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(campus));
        when(tutorialRepository.findByTitle(Mockito.any())).thenReturn(null);
        when(tutorialRepository.save(Mockito.any())).thenReturn(tutorial);
        when(s3Service.saveFile(Mockito.eq(multipartFile), Mockito.any(), Mockito.any())).thenReturn("/tutorials/1");
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(TutorialDto.class))).thenReturn(tutorialDto);

        TutorialDto response = tutorialService.addTutorial("HE140705", "fschool-hoa-lac", createRequestBody, multipartFile);

        assertNotNull(response);
        assertEquals(response, tutorialDto);
    }

    @Test
    void updateTutorial_notFoundTutorial() {
        when(tutorialRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        TutorialDto response = null;
        try {
            response = tutorialService.updateTutorial("1", updateRequestBody, multipartFile);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy thông tin");
            assertNull(response);
        }
    }

    @Test
    void updateTutorial_success() {
        when(tutorialRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(tutorial));
        TutorialDto response = null;
        try {
            response = tutorialService.updateTutorial("1", updateRequestBody, multipartFile);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy thông tin");
            assertNull(response);
        }
    }

    @Test
    void deleteTutorial_success() {
        when(tutorialRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(tutorial));
        doNothing().when(tutorialRepository).deleteById(Mockito.any());

        String response = tutorialService.deleteTutorial("1", "GV002");
        assertNotNull(response);
        assertEquals(response, "Xóa thành công");
    }

    @Test
    void deleteTutorial_deleteFail() {
        when(tutorialRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(tutorial));
        String response = null;
        try {
            response = tutorialService.deleteTutorial("1", "GV002");

        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
            assertEquals(e.getMessage(), "Bạn không có quyền xóa");
        }
    }

    @Test
    void deleteTutorial_notFoundTutorial() {
        when(tutorialRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        String response = null;
        try {
            response = tutorialService.deleteTutorial("1", "GV002");

        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
            assertEquals(e.getMessage(), "Không tìm thấy dữ liệu");
        }
    }

    @Test
    void getAll() {
        List<TutorialDto> list = new ArrayList<>();
        list.add(tutorialDto);
        when(tutorialMapper.getAll(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.eq(1), Mockito.eq(15))).thenReturn(list);

        List<TutorialDto> response = tutorialService.getAll("fs-hoa-lac", "GV002", "Title", Instant.now(), Instant.now(), 1, 15);
        assertNotNull(response);
        assertEquals(response.size(), list.size());
        assertEquals(response, list);
    }

    @Test
    void getAll_nullResponse() {
        List<TutorialDto> list = new ArrayList<>();
        when(tutorialMapper.getAll(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.eq(1), Mockito.eq(15))).thenReturn(list);

        List<TutorialDto> response = tutorialService.getAll("fs-hoa-lac", "GV002", "Title", Instant.now(), Instant.now(), 1, 15);
        assertNotNull(response);
        assertEquals(response.size(), list.size());
    }

    @Test
    void countAll() {
        when(tutorialMapper.countAll(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(3);
        int response = tutorialService.countAll("fs-hoa-lac", "GV002", "Title", Instant.now(), Instant.now());
        assertEquals(response, 3);
        assertNotEquals(response, 2);
        assertNotNull(response);
    }
    @Test
    void getOne_nullResponse(){
        when(tutorialRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));

        TutorialDto response = tutorialService.getOne("1");
        assertNull(response);
    }

    @Test
    void getOne_success(){
        when(tutorialRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(tutorial));
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(TutorialDto.class))).thenReturn(tutorialDto);

        TutorialDto response = tutorialService.getOne("1");
        assertNotNull(response);
        assertEquals(response, tutorialDto);
    }
}