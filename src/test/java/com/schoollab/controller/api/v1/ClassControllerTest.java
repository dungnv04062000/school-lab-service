package com.schoollab.controller.api.v1;

import com.schoollab.controller.request.ClassCreateRequestBody;
import com.schoollab.controller.request.ClassUpdateRequestBody;
import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.dto.ClassDto;
import com.schoollab.model.Class;
import com.schoollab.modelTest.SetUpModelTest;
import com.schoollab.repository.ClassRepository;
import com.schoollab.service.ClassService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClassControllerTest {
    @InjectMocks
    ClassController classController;

    @Mock
    ClassService classService;

    Class aClass = new Class();
    ClassDto classDto;

    ClassCreateRequestBody classCreateRequestBody;

    ClassUpdateRequestBody classUpdateBody;

    @Mock
    ClassRepository classRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        classDto = SetUpModelTest.setUpClassDto();
        classCreateRequestBody = SetUpModelTest.clasReq();
    }

    @Test
    void getClassesForAdmin() {
        List<ClassDto> classDtos = new ArrayList<>();
        classDtos.add(classDto);

        ResponseBodyDto response = new ResponseBodyDto<>(classDtos, ResponseCodeEnum.R_200, "OK", classDtos.size());
        when(classService.getAllForAdminSchool(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(classDtos);
        ResponseEntity<ResponseBodyDto<List<ClassDto>>> responseEntity = classController.getClassesForAdmin("1", "Fall 2022", "2","10A1","Trung");

        assertNotNull(response);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
//        assertEquals(responseEntity.getBody().getSize(), listCampus.size());
        assertEquals(responseEntity.getBody(), response);

    }

    @Test
    void getClassesForSubjectTeacher() {
//        List<ClassDto> classDtos = new ArrayList<>();
//        classDtos.add(classDto);
//
//        ResponseBodyDto response = new ResponseBodyDto<>(classDtos, ResponseCodeEnum.R_200, "OK", classDtos.size());
//        when(classService.getAll(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(classDtos);
//
//        ResponseEntity<ResponseBodyDto<List<ClassDto>>> responseEntity = classController.getClassesForSubjectTeacher("1","2","10A1");
//
//        assertNotNull(response);
//        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
////        assertEquals(responseEntity.getBody().getSize(), listCampus.size());
//        assertEquals(responseEntity.getBody(), response);
    }

    @Test
    void getOne() {
        ResponseBodyDto responseBodyDto = new ResponseBodyDto<>(classDto, ResponseCodeEnum.R_200, "OK");
        when(classService.getOne(Mockito.any())).thenReturn(classDto);

        ResponseEntity<ResponseBodyDto<ClassDto>> response = classController.getOne("1");

        assertNotNull(response);
        //kiểm tra status trả về
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        //kiểm tra model trả về của service
        assertEquals(response.getBody().getItem(), classDto);
        //kiểm tra respone tạo được từ controller trả về phía font-end
        assertEquals(response.getBody(), responseBodyDto);
    }

    @Test
    void getOneBody(){
        ResponseBodyDto responseBodyDto = new ResponseBodyDto<>(classDto, ResponseCodeEnum.R_200, "OK");
        when(classService.getOne(Mockito.any())).thenReturn(classDto);

        ResponseEntity<ResponseBodyDto<ClassDto>> response = classController.getOne("1");
        assertEquals(response.getBody().getItem(), classDto);
    }

    @Test
    void createClass() {
        ResponseBodyDto responseBodyDto = new ResponseBodyDto<>(classDto, ResponseCodeEnum.R_201, "CREATED");
        when(classService.createClass(Mockito.any())).thenReturn(classDto);

        ResponseEntity<ResponseBodyDto<ClassDto>> response = classController.createClass(classCreateRequestBody);
        assertNotNull(response);
        //kiểm tra status trả về
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        //kiểm tra model trả về của service
        assertEquals(response.getBody().getItem(), classDto);
        //kiểm tra respone tạo được từ controller trả về phía font-end
        assertEquals(response.getBody(), responseBodyDto);
    }

    @Test
    void updateClass() {
        ResponseBodyDto responseBodyDto = new ResponseBodyDto<>(classDto, ResponseCodeEnum.R_200, "OK");
        when(classService.updateClass(Mockito.any(),Mockito.any())).thenReturn(classDto);
        ResponseEntity<ResponseBodyDto<ClassDto>> response = classController.updateClass("1",classUpdateBody);
        assertNotNull(response);
        //kiểm tra status trả về
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        //kiểm tra model trả về của service
        assertEquals(response.getBody().getItem(), classDto);
        //kiểm tra respone tạo được từ controller trả về phía font-end
        assertEquals(response.getBody(), responseBodyDto);
    }

    @Test
    void deleteClass() {
        ResponseBodyDto responseBodyDto = new ResponseBodyDto<>("DELETED", ResponseCodeEnum.R_200, "DELETED");
        when(classRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(aClass));
        classService.deleteClass("123456");

        ResponseEntity<ResponseBodyDto<ClassDto>> response = classController.deleteClass("1");
        assertNotNull(response);
        //kiểm tra status trả về
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        //kiểm tra model trả về của service
//        assertEquals(response.getBody().getItem(), classDto);
        //kiểm tra respone tạo được từ controller trả về phía font-end
        assertEquals(response.getBody(), responseBodyDto);



    }
}