package com.schoollab.service.impl;

import com.schoollab.common.constants.Constants;
import com.schoollab.common.error.BadRequestException;
import com.schoollab.common.error.UnKnownException;
import com.schoollab.controller.request.ClassManyStudentRequest;
import com.schoollab.controller.request.ClassStudentAddRequestBody;
import com.schoollab.dto.ClassStudentDto;
import com.schoollab.dto.StudentInClassDto;
import com.schoollab.dto.UserDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.ClassStudentMapper;
import com.schoollab.model.Class;
import com.schoollab.model.ClassStudent;
import com.schoollab.model.User;
import com.schoollab.modelTest.SetUpModelTest;
import com.schoollab.repository.ClassRepository;
import com.schoollab.repository.ClassStudentRepository;
import com.schoollab.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClassStudentImplTest {
    @InjectMocks
    ClassStudentImpl classStudentService;
    @Mock
    ClassStudentRepository classStudentRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ClassRepository classRepository;
    @Mock
    ClassStudentMapper classStudentMapper;
    @Mock
    GenericMapper genericMapper;
    @Mock
    MultipartFile multipartFile;
    ClassStudentAddRequestBody singleReq;
    ClassManyStudentRequest multiReq;
    Class aClass;
    User user;
    UserDto userDto;
    ClassStudent classStudent;
    StudentInClassDto studentInClassDto;
    ClassStudentDto classStudentDto;
    List<StudentInClassDto> listStudent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        singleReq = SetUpModelTest.singleStudentReq();
        multiReq = new ClassManyStudentRequest();
        multiReq.setClassId("1234");
        multiReq.setIsOverride("NO");
        multiReq.setFile(multipartFile);
        classStudent = SetUpModelTest.setUpClassStudent();
        classStudentDto = SetUpModelTest.setUpClassStudentDto();
        user = SetUpModelTest.setUpUser();
        userDto = SetUpModelTest.setUpUserDto();
        aClass = SetUpModelTest.setUpClass();
        studentInClassDto = SetUpModelTest.setUpStudentInClassDto();
        listStudent = SetUpModelTest.setUpListStudentInClass();
    }

    @Test
    void saveStudentToClass_notFoundClass() {
        when(classRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        ClassStudentDto response = null;
        try {
            response = classStudentService.saveStudentToClass(singleReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy lớp học này");
            assertNull(response);
        }
    }

    @Test
    void saveStudentToClass_notFoundStudent() {
        when(classRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(aClass));
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        ClassStudentDto response = null;
        try {
            response = classStudentService.saveStudentToClass(singleReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy học sinh này");
            assertNull(response);
        }
    }

    @Test
    void saveStudentToClass_studentExistInClass() {
        when(classRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(aClass));
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(classStudentRepository.findClassStudentByStudentId(Mockito.any())).thenReturn(classStudent);
        ClassStudentDto response = null;
        try {
            response = classStudentService.saveStudentToClass(singleReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Học sinh đã có trong lớp");
            assertNull(response);
        }
    }

    @Test
    void saveStudentToClass_studentExistOtherClass() {
        when(classRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(aClass));
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        classStudent.setClassId("123");
        when(classStudentRepository.findClassStudentByStudentId(Mockito.any())).thenReturn(classStudent);
        ClassStudentDto response = null;
        try {
            response = classStudentService.saveStudentToClass(singleReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Học sinh đang ở trong lớp học khác");
            assertNull(response);
        }
    }

    @Test
    void saveStudentToClass_studentExistInOtherClass() {
        when(classRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(aClass));
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(classStudentRepository.findByStudentIdAndSemesterId(Mockito.any(), Mockito.any())).thenReturn(classStudent);
        classStudent.setClassId("123");
        when(classStudentRepository.findClassStudentByStudentId(Mockito.any())).thenReturn(classStudent);
        ClassStudentDto response = null;
        try {
            response = classStudentService.saveStudentToClass(singleReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Học sinh đang ở trong lớp học khác");
            assertNull(response);
        }
    }

    @Test
    void saveStudentToClass_studentExistInThisClass() {
        when(classRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(aClass));
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(classStudentRepository.findByStudentIdAndSemesterId(Mockito.any(), Mockito.any())).thenReturn(classStudent);
        classStudent.setClassId("123456");
        when(classStudentRepository.findClassStudentByStudentId(Mockito.any())).thenReturn(classStudent);
        ClassStudentDto response = null;
        try {
            response = classStudentService.saveStudentToClass(singleReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Học sinh đã có trong lớp này");
            assertNull(response);
        }
    }

    @Test
    void saveStudentToClass_exception() {
        when(classRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(aClass));
        ClassStudentDto response = null;
        Exception ex = new UnKnownException("Có lỗi trong quá trình xử lý");
        try {
            when(userRepository.findById(Mockito.any())).thenThrow(ex);
            response = classStudentService.saveStudentToClass(singleReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Có lỗi trong quá trình xử lý");
            assertNull(response);
        }
    }

    @Test
    void saveStudentToClass_success() {
        when(classRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(aClass));
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(classStudentRepository.findClassStudentByStudentId(Mockito.any())).thenReturn(null);
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(ClassStudentDto.class))).thenReturn(classStudentDto);

        ClassStudentDto response = classStudentService.saveStudentToClass(singleReq);
        assertNotNull(response);
        assertEquals(response, classStudentDto);
    }

    @Test
    void saveManyStudentToClass_NotFoundClass(){
        when(classRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        List<ClassStudentDto> response = null;
        try {
            response = classStudentService.saveManyStudentToClass(multiReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy lớp học này");
        }
    }

    @Test
    void saveManyStudentToClass_FileNull(){
        when(classRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(aClass));
        List<ClassStudentDto> response = null;
        try {
            multiReq.setFile(null);
            response = classStudentService.saveManyStudentToClass(multiReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
        }
    }

    @Test
    void saveManyStudentToClass_FileException(){
        when(classRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(aClass));
        List<ClassStudentDto> response = null;
        try {
            response = classStudentService.saveManyStudentToClass(multiReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
        }
    }

//    @Test
//    void saveManyStudentToClass_reqStudentIdNull() {
//        List<ClassStudentDto> list = new ArrayList<>();
//        ClassManyStudentRequest.StudentRequest st = new ClassManyStudentRequest.StudentRequest();
//        st.setStudentId("");
//        List<ClassManyStudentRequest.StudentRequest> listReq = new ArrayList<>();
//        listReq.add(st);
//        ClassManyStudentRequest req = new ClassManyStudentRequest();
//        req.setClassId("123456");
//        req.setData(listReq);
//        when(genericMapper.mapToListOfTypeNotNullProperty(Mockito.any(), Mockito.eq(ClassStudentDto.class))).thenReturn(list);
//        List<ClassStudentDto> response = classStudentService.saveManyStudentToClass(req);
//
//        assertNotNull(response);
//        assertEquals(response.size(), list.size());
//    }

    @Test
    void searchAllStudent_success() {
        List<UserDto> listUser = new ArrayList<>();
        listUser.add(userDto);
        List<ClassStudent> listClassStudent = new ArrayList<>();
        listClassStudent.add(classStudent);
        when(classStudentRepository.findAllByClassId(Mockito.any())).thenReturn(listClassStudent);
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(genericMapper.mapToListOfTypeNotNullProperty(Mockito.any(), Mockito.eq(UserDto.class))).thenReturn(listUser);

        List<StudentInClassDto> response = classStudentService.searchAllStudent("123456", "123456", "123456", 1, 10);
        assertNotNull(response);
    }

    @Test
    void searchAllStudent_userNull() {
        List<UserDto> listUser = new ArrayList<>();
        listUser.add(userDto);
        List<ClassStudent> listClassStudent = new ArrayList<>();
        listClassStudent.add(classStudent);
        when(classStudentRepository.findAllByClassId(Mockito.any())).thenReturn(listClassStudent);
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        when(genericMapper.mapToListOfTypeNotNullProperty(Mockito.any(), Mockito.eq(UserDto.class))).thenReturn(listUser);

        List<StudentInClassDto> response = classStudentService.searchAllStudent("123456", "123456", "123456", 1, 10);
        assertNotNull(response);
    }

    @Test
    void filterStudent_success() {
        when(genericMapper.mapToListOfTypeNotNullProperty(Mockito.any(), Mockito.eq(StudentInClassDto.class))).thenReturn(listStudent);

        List<StudentInClassDto> response = classStudentService.filterStudent("123456", "Dung", "Male");
        assertNotNull(response);
        assertEquals(response.size(), listStudent.size());
    }

    @Test
    void filterStudent_mappingException() {
        List<StudentInClassDto> response = null;
        try {
            when(genericMapper.mapToListOfTypeNotNullProperty(Mockito.any(), Mockito.eq(StudentInClassDto.class))).thenThrow(Exception.class);
            response = classStudentService.filterStudent("123456", "Dung", "Male");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void countAllStudent(){
        when(classStudentMapper.countAllByCampusForRootAdmin(Mockito.any(), Mockito.eq(4), Mockito.any(), Mockito.any())).thenReturn(2);
        int response = classStudentService.countAllStudent("FPT", "DUng", "MALE");

        assertNotNull(response);
        assertEquals(response, 2);
    }

    @Test
    void filterStudentInCampus(){
        when(classStudentMapper.getAllInCampus(Mockito.any(), Mockito.any(), Mockito.eq(1), Mockito.eq(1), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.eq(1), Mockito.eq(15))).thenReturn(new ArrayList<>());
        List<StudentInClassDto> response = classStudentService.filterStudentInCampus("FPT", "FAll", 1, 1, "10A1", "Dung", "MALE", 1, 15);
        assertNotNull(response);
        assertEquals(response.size(), 0);
    }

    @Test
    void countAllStudentInCampus(){
        when(classStudentMapper.countAllInCampus(Mockito.any(), Mockito.any(), Mockito.eq(1), Mockito.eq(1), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(2);
        int response = classStudentService.countAllStudentInCampus("FPT", "FALL", 1, 1, "Duxng", "Dung", "MALE");
    }

    @Test
    void removeStudent_Exception(){
        Exception ex =new BadRequestException("");
        doThrow(ex).when(classStudentRepository).deleteById(Mockito.any());
        try {
            classStudentService.removeStudent("HE140705");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
        }
    }

    @Test
    void removeStudent_Success(){
        doNothing().when(classStudentRepository).deleteById(Mockito.any());
        classStudentService.removeStudent("HE140705");
    }
}