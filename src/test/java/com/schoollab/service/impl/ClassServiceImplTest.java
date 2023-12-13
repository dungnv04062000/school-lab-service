package com.schoollab.service.impl;

import com.schoollab.common.constants.Constants;
import com.schoollab.controller.request.ClassCreateRequestBody;
import com.schoollab.controller.request.ClassUpdateRequestBody;
import com.schoollab.dto.ClassDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.ClassMapper;
import com.schoollab.model.Class;
import com.schoollab.model.Role;
import com.schoollab.model.User;
import com.schoollab.model.UserRole;
import com.schoollab.modelTest.SetUpModelTest;
import com.schoollab.repository.ClassRepository;
import com.schoollab.repository.UserRepository;
import com.schoollab.repository.UserRoleRepository;
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
import static org.mockito.Mockito.when;

class ClassServiceImplTest {
    @InjectMocks
    ClassServiceImpl classService;
    @Mock
    ClassRepository classRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    GenericMapper genericMapper;
    @Mock
    ClassMapper classMapper;
    @Mock
    UserRoleRepository userRoleRepository;
    ClassCreateRequestBody requestBody;
    ClassUpdateRequestBody updateBody;
    Class aClass;
    ClassDto classDto;
    User user;
    UserRole userRole;
    Role role;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        requestBody = SetUpModelTest.clasReq();
        updateBody = SetUpModelTest.clasUpdateReq();
        aClass = SetUpModelTest.setUpClass();
        classDto = SetUpModelTest.setUpClassDto();
        user = SetUpModelTest.setUpUser();
        role = SetUpModelTest.setUpRole(3, "teacher");
        userRole = SetUpModelTest.setUpUserRole(role);
    }

    @Test
    void createClass_teacherIdExists() {
        when(classRepository.findByFormTeacherId(Mockito.any())).thenReturn(aClass);
        ClassDto response = null;
        try {
            response = classService.createClass(requestBody);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void createClass_success(){
        when(classRepository.findByFormTeacherId(Mockito.any())).thenReturn(null);
        when(classRepository.save(Mockito.any())).thenReturn(aClass);
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(ClassDto.class))).thenReturn(classDto);

        ClassDto response = classService.createClass(requestBody);

        assertNotNull(response);
        assertEquals(response, classDto);
    }

    @Test
    void createClass_exception(){
        when(classRepository.findByFormTeacherId(Mockito.any())).thenReturn(null);
        ClassDto response = null;
        try {
            when(classRepository.save(Mockito.any())).thenThrow(Exception.class);
            response = classService.createClass(requestBody);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void deleteClass_success() {
        when(classRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(aClass));
        classService.deleteClass("123456");
    }

    @Test
    void deleteClass_fail() {
        try {
            when(classRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
            classService.deleteClass("123456");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(),"Không tìm thấy lớp học này!");
        }
    }

    @Test
    void updateClass_notFoundClassId() {
        when(classRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        ClassDto response = null;
        try {
            response = classService.updateClass("123456", updateBody);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void updateClass_notFoundTeacher() {
        when(classRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(aClass));
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        ClassDto response = null;
        try {
            response = classService.updateClass("123456", updateBody);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
            assertEquals(e.getMessage(), "Không tìm thấy giáo viên này!");
        }
    }

    @Test
    void updateClass_notFoundUserId() {
        when(classRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(aClass));
        when(userRepository.findById(Mockito.any())).thenReturn(null);
        ClassDto response = null;
        try {
            response = classService.updateClass("123456", updateBody);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void updateClass_existsTeacherInClass(){
        aClass.setFormTeacherId("HE40706");
        when(classRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(aClass));
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(classRepository.findByFormTeacherId(Mockito.any())).thenReturn(aClass);

        ClassDto response = null;
        try {
            response = classService.updateClass("123456", updateBody);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void updateClass_success_tc1(){
        when(classRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(aClass));
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(classRepository.findByFormTeacherIdAndSemesterId(Mockito.any(), Mockito.any())).thenReturn(null);
        when(userRoleRepository.findByUserIdAndAndRoleId(Mockito.any(), Mockito.eq(3))).thenReturn(userRole);
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(ClassDto.class))).thenReturn(classDto);

        ClassDto response = classService.updateClass("123456", updateBody);

        assertNotNull(response);
        assertEquals(response, classDto);
    }

    @Test
    void updateClass_success_tc2(){
        when(classRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(aClass));
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(ClassDto.class))).thenReturn(classDto);
        updateBody.setFormTeacherId(null);
        ClassDto response = classService.updateClass("123456", updateBody);

        assertNotNull(response);
        assertEquals(response, classDto);
    }

    @Test
    void updateClass_NotFoundClass(){
        when(classRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        ClassDto response = null;
        try {
            response = classService.updateClass("HE140705", updateBody);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy lớp học này");
            assertNull(response);
        }
    }

    @Test
    void updateClass_NotFoundTeacher(){
        when(classRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(aClass));
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        ClassDto response = null;
        try {
            response = classService.updateClass("HE140705", updateBody);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy giáo viên này!");
            assertNull(response);
        }
    }

    @Test
    void updateClass_IsNotTeacher(){
        when(classRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(aClass));
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(userRoleRepository.findByUserIdAndAndRoleId(Mockito.any(), Mockito.eq(3))).thenReturn(null);
        ClassDto response = null;
        try {
            response = classService.updateClass("HE140705", updateBody);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Người này không phải là giáo viên");
            assertNull(response);
        }
    }

    @Test
    void updateClass_HasInOtherClass(){
        when(classRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(aClass));
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(userRoleRepository.findByUserIdAndAndRoleId(Mockito.any(), Mockito.eq(3))).thenReturn(userRole);
        when(classRepository.findByFormTeacherIdAndSemesterId(Mockito.eq("HE140705"), Mockito.any())).thenReturn(aClass);
        ClassDto response = null;
        try {
            response = classService.updateClass("HE140705", updateBody);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Giáo viên này đã chủ nhiệm 1 lớp khác");
            assertNull(response);
        }
    }

    @Test
    void getAll_nullResponse_notFoundUserId() {
        List<ClassDto> list = new ArrayList<>();
        when(userRepository.findById(Mockito.eq("HE140705"))).thenReturn(Optional.ofNullable(null));
//        when(classMapper.getAll(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(list);
        List<ClassDto> response = null;
        try {
            response = classService.getAll("He140705", "40288186848b35ec01848b37c7350000", "1234", "Hoc hoc", "HE140705");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy giáo viên này");
//            assertNull(response);
        }
    }

    @Test
    void getAll_nullResponse_success() {
        List<ClassDto> list = new ArrayList<>();
        when(userRepository.findById(Mockito.eq("He140705"))).thenReturn(Optional.ofNullable(user));
        when(classMapper.getAll(Mockito.any(), Mockito.any(), Mockito.any(),Mockito.any() , Mockito.any(), Mockito.any())).thenReturn(list);
        List<ClassDto> response = classService.getAll("He140705", "40288186848b35ec01848b37c7350000","1234", "Hoc hoc", "HE140705");
        assertEquals(response.size(), list.size());
    }

    @Test
    void getAll_success() {
        List<ClassDto> list = new ArrayList<>();
        list.add(classDto);
        when(userRepository.findById(Mockito.eq("He140705"))).thenReturn(Optional.ofNullable(user));
        when(classMapper.getAll(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(list);
        List<ClassDto> response = classService.getAll("He140705","40288186848b35ec01848b37c7350000", "1234", "Hoc hoc", "HE140705");
        assertNotNull(response);
        assertEquals(response.size(), list.size());
        assertEquals(response, list);
    }

    @Test
    void getOne_nullResponse_success() {
        when(classMapper.getOne(Mockito.any())).thenReturn(null);
        ClassDto response = classService.getOne("1234");
        assertNull(response);
    }

    @Test
    void getOne_success() {
        when(classMapper.getOne(Mockito.any())).thenReturn(classDto);
        ClassDto response = classService.getOne("1234");
        assertNotNull(response);
        assertEquals(response, classDto);
    }
}