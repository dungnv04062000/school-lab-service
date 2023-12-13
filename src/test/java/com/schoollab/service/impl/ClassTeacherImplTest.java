package com.schoollab.service.impl;

import com.schoollab.common.constants.Constants;
import com.schoollab.common.error.UnKnownException;
import com.schoollab.controller.request.ClassTeacherRequest;
import com.schoollab.dto.ClassTeacherDto;
import com.schoollab.dto.TeacherInClassDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.ClassTeacherMapper;
import com.schoollab.model.ClassTeacher;
import com.schoollab.model.Role;
import com.schoollab.model.User;
import com.schoollab.model.UserRole;
import com.schoollab.modelTest.SetUpModelTest;
import com.schoollab.repository.ClassTeacherRepository;
import com.schoollab.repository.UserRepository;
import com.schoollab.repository.UserRoleRepository;
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
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class ClassTeacherImplTest {
    @InjectMocks
    ClassTeacherImpl classTeacherService;
    @Mock
    ClassTeacherRepository classTeacherRepo;
    @Mock
    GenericMapper genericMapper;
    @Mock
    ClassTeacherMapper classTeacherMapper;
    @Mock
    UserRepository userRepository;
    @Mock
    UserRoleRepository userRoleRepository;
    ClassTeacherRequest teacherReq;
    ClassTeacherDto classTeacherDto;
    ClassTeacher classTeacher;
    TeacherInClassDto teacherInClassDto;
    List<TeacherInClassDto> listTeacher = new ArrayList<>();
    User user;
    Role role;
    UserRole userRole;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        teacherReq = SetUpModelTest.classTeacherReq();
        classTeacherDto = SetUpModelTest.setUpClassTeacherDto();
        classTeacher = SetUpModelTest.setUpClassTeacher();
        teacherInClassDto = SetUpModelTest.setUpTeacherInClassDto();
        listTeacher.add(teacherInClassDto);
        user = SetUpModelTest.setUpUser();
        role = SetUpModelTest.setUpRole(2, "teacher");
        userRole = SetUpModelTest.setUpUserRole(role);
    }

    @Test
    void addTeacherToClass_success() {
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(userRoleRepository.findByUserIdAndAndRoleId(Mockito.any(), Mockito.eq(3))).thenReturn(userRole);
        when(classTeacherRepo.findTeacherByClass(Mockito.any(), Mockito.any())).thenReturn(null);
        when(classTeacherRepo.save(Mockito.any())).thenReturn(classTeacher);
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(ClassTeacherDto.class))).thenReturn(classTeacherDto);

        ClassTeacherDto response = classTeacherService.addTeacherToClass(teacherReq);
        assertNotNull(response);
        assertEquals(response, classTeacherDto);
    }

    @Test
    void addTeacherToClass_mappingFail() {
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(userRoleRepository.findByUserIdAndAndRoleId(Mockito.any(), Mockito.eq(3))).thenReturn(userRole);
        when(classTeacherRepo.findTeacherByClass(Mockito.any(), Mockito.any())).thenReturn(null);
        when(classTeacherRepo.save(Mockito.any())).thenReturn(classTeacher);
        classTeacherDto.setClassId("1111");
        ClassTeacherDto model = new ClassTeacherDto();
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(ClassTeacherDto.class))).thenReturn(model);

        ClassTeacherDto response = classTeacherService.addTeacherToClass(teacherReq);
        assertNotNull(response);
        assertNotEquals(response, classTeacherDto);
    }

    @Test
    void addTeacherToClass_NotFoundTeacher() {
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        ClassTeacherDto response = null;
        try {
            response = classTeacherService.addTeacherToClass(teacherReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy giáo viên này");
            assertNull(response);
        }
    }

    @Test
    void addTeacherToClass_IsNotATeacher() {
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(userRoleRepository.findByUserIdAndAndRoleId(Mockito.any(), Mockito.eq(3))).thenReturn(userRole);
        ClassTeacherDto response = null;
        try {
            response = classTeacherService.addTeacherToClass(teacherReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Người này không phải là giáo viên");
            assertNull(response);
        }
    }

    @Test
    void addTeacherToClass_teacherExistsInClass() {
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(userRoleRepository.findByUserIdAndAndRoleId(Mockito.any(), Mockito.eq(3))).thenReturn(userRole);
        when(classTeacherRepo.findTeacherByClass(Mockito.any(), Mockito.any())).thenReturn(classTeacher);
        ClassTeacherDto response = null;
        try {
            response = classTeacherService.addTeacherToClass(teacherReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Giáo viên đã có trong lớp học này");
            assertNull(response);
        }
    }

    @Test
    void filterTeacherInClass_success() {
        when(classTeacherMapper.getAllTeacher(Mockito.any(), Mockito.any())).thenReturn(listTeacher);
        when(genericMapper.mapToListOfTypeNotNullProperty(Mockito.any(), Mockito.eq(TeacherInClassDto.class))).thenReturn(listTeacher);

        List<TeacherInClassDto> response = classTeacherService.filterTeacherInClass("123456", "HE140705");
        assertNotNull(response);
        assertEquals(response, listTeacher);
    }

    @Test
    void filterTeacherInClass_nullResponse() {
        when(classTeacherMapper.getAllTeacher(Mockito.any(), Mockito.any())).thenReturn(null);
        when(genericMapper.mapToListOfTypeNotNullProperty(Mockito.any(), Mockito.eq(TeacherInClassDto.class))).thenReturn(new ArrayList<>());

        List<TeacherInClassDto> response = classTeacherService.filterTeacherInClass("123456", "HE140705");
        assertNotNull(response);
        assertNotEquals(response.size(), listTeacher);
    }

    @Test
    void removeTeacherInClass_notFound() {
        when(classTeacherRepo.findById(Mockito.any())).thenReturn(null);
        String response = null;
        try {
            response = classTeacherService.removeTeacherInClass("HE140705");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy giáo viên");
            assertNull(response);
        }
    }

    @Test
    void removeTeacherInClass_exception() {
        when(classTeacherRepo.findById(Mockito.any())).thenReturn(Optional.ofNullable(classTeacher));
        Exception ex = new UnKnownException("Xóa giáo viên thất bại");
        String response = null;
        try {
            doThrow(ex).when(classTeacherRepo).deleteById(Mockito.eq("HE140705"));
            response = classTeacherService.removeTeacherInClass("HE140705");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Xóa giáo viên thất bại");
            assertNull(response);
        }
    }

    @Test
    void removeTeacherInClass_success() {
        when(classTeacherRepo.findById(Mockito.any())).thenReturn(Optional.ofNullable(classTeacher));
        String response = classTeacherService.removeTeacherInClass("HE140705");
        assertNotNull(response);
        assertEquals(response, "Xóa giáo viên thành công");
    }

    @Test
    void filterTeacherInCampus(){
        when(classTeacherMapper.getAllInCampus(Mockito.eq("FPT"), Mockito.eq("Fall"), Mockito.eq(1), Mockito.eq("10A1"), Mockito.eq("DUNG"), Mockito.eq("MALE"), Mockito.eq(1), Mockito.eq(15))).thenReturn(new ArrayList<>());
        List<TeacherInClassDto> response = classTeacherService.filterTeacherInCampus("FPT", "Fall", 1, "10A1", "DUNG", "MALE", 1, 15);
        assertNotNull(response);
        assertEquals(response.size(), 0);
    }

    @Test
    void getAllTeachers(){
        when(classTeacherMapper.getAllInCampusForRootAdmin(Mockito.eq("FPT"), Mockito.eq(3), Mockito.any(), Mockito.any(), Mockito.eq(1), Mockito.eq(15))).thenReturn(new ArrayList<>());
        List<TeacherInClassDto> response = classTeacherService.getAllTeachers("FPT", "DUNG", "MALE", 1, 15);
        assertNotNull(response);
        assertEquals(response.size(), 0);
    }

    @Test
    void countAllTeachers(){
        when(classTeacherMapper.countAllInCampusForRootAdmin(Mockito.eq("FPT"), Mockito.eq(3), Mockito.any(), Mockito.any())).thenReturn(3);
        int response = classTeacherService.countAllTeachers("FPT", "DUNG", "MALE");
        assertNotNull(response);
        assertEquals(response, 3);
    }

    @Test
    void countAllTeacherInCampus(){
        when(classTeacherMapper.countAllInCampus(Mockito.any(), Mockito.any(), Mockito.eq(1), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(2);
        int response = classTeacherService.countAllTeacherInCampus("FPT", "FALL", 1, "DUNG", "DUNg", "MALE");
        assertNotNull(response);
        assertEquals(response, 2);
    }
}