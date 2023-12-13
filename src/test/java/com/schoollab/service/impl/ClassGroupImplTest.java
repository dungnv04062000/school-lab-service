package com.schoollab.service.impl;

import com.schoollab.common.error.BadRequestException;
import com.schoollab.dto.ClassDto;
import com.schoollab.dto.ClassGroupDto;
import com.schoollab.dto.GroupMemberDto;
import com.schoollab.dto.StudentInClassDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.ClassGroupMapper;
import com.schoollab.mapper.ClassStudentMapper;
import com.schoollab.model.ClassGroup;
import com.schoollab.model.ClassStudent;
import com.schoollab.model.GroupMember;
import com.schoollab.model.User;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class ClassGroupImplTest {
    @InjectMocks
    ClassGroupImpl classGroupService;
    @Mock
    ClassGroupRepository classGroupRepository;
    @Mock
    GroupMemberRepository groupMemberRepository;
    @Mock
    ClassStudentRepository classStudentRepository;
    @Mock
    EvaluationRepository evaluationRepository;
    @Mock
    EvaluationTeammateRepository evaluationTeammateRepository;
    @Mock
    EvaluationGroupRepository evaluationGroupRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ClassStudentMapper classStudentMapper;
    @Mock
    ClassGroupMapper classGroupMapper;
    @Mock
    S3Service s3Service;
    @Mock
    GenericMapper genericMapper;
    @Mock
    MultipartFile multipartFile;
    ClassGroup classGroup;
    ClassGroupDto classGroupDto;
    StudentInClassDto studentInClassDto;
    User user;
    GroupMember groupMember;
    GroupMemberDto groupMemberDto;
    ClassStudent classStudent;
    List<ClassGroup> classGroups;
    List<ClassGroupDto> classGroupDtos;
    List<GroupMember> groupMembers;
    List<GroupMemberDto> groupMemberDtos;
    List<ClassStudent> classStudents;
    List<StudentInClassDto> studentInClassDtos;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        classGroupDto = SetUpModelTest.setUpClassGroupDto();
        classGroup = SetUpModelTest.setUpClassGroup();
        user = SetUpModelTest.setUpUser();
        groupMember = SetUpModelTest.setUpGroupMember();
        groupMemberDto = SetUpModelTest.setUpGroupMemberDto();
        classGroups = new ArrayList<>();
        classGroups.add(classGroup);
        classGroupDtos = new ArrayList<>();
        classGroupDtos.add(classGroupDto);
        groupMembers = new ArrayList<>();
        groupMembers.add(groupMember);
        groupMemberDtos = new ArrayList<>();
        groupMemberDtos.add(groupMemberDto);
        classStudent = SetUpModelTest.setUpClassStudent();
        classStudents = new ArrayList<>();
        classStudents.add(classStudent);
        studentInClassDto = SetUpModelTest.setUpStudentInClassDto();
        studentInClassDtos = new ArrayList<>();
        studentInClassDtos.add(studentInClassDto);
    }

    @Test
    void getAllGroups() {
        when(classGroupRepository.findAllByClassIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(classGroups);
        when(genericMapper.mapToListOfTypeNotNullProperty(Mockito.any(), Mockito.eq(ClassGroupDto.class))).thenReturn(classGroupDtos);
        when(groupMemberRepository.findAllByGroupId(Mockito.any())).thenReturn(groupMembers);
        when(genericMapper.mapToListOfTypeNotNullProperty(Mockito.any(), Mockito.eq(GroupMemberDto.class))).thenReturn(groupMemberDtos);
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));

        List<ClassGroupDto> response = classGroupService.getAllGroups("10A1", "123");
        assertNotNull(response);
        assertEquals(response.size(), classGroupDtos.size());
        assertEquals(response, classGroupDtos);
    }

    @Test
    void getOwnerGroups_notFoundGroup() {
        when(classGroupMapper.getOwnerGroup(Mockito.eq("abc123"), Mockito.eq("HE140705"))).thenReturn(null);
        ClassGroupDto response = null;
        try {
            response = classGroupService.getOwnerGroups("abc123", "HE140705");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Nhóm không tồn tại");
            assertNull(response);
        }
    }

    @Test
    void getOwnerGroups_success() {
        when(classGroupMapper.getOwnerGroup(Mockito.eq("abc123"), Mockito.eq("HE140705"))).thenReturn(classGroupDto);
        when(genericMapper.mapToListOfTypeNotNullProperty(Mockito.any(), Mockito.eq(GroupMemberDto.class))).thenReturn(groupMemberDtos);
        when(groupMemberRepository.findAllByGroupId(Mockito.any())).thenReturn(groupMembers);
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        ClassGroupDto response = classGroupService.getOwnerGroups("abc123", "HE140705");
        assertNotNull(response);
        assertEquals(response, classGroupDto);
    }

    @Test
    void randomGroup_ExistsExperiment() {
        when(classGroupRepository.findAllByClassIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(classGroups);
        List<ClassGroupDto> response = null;
        try {
            response = classGroupService.randomGroup("10A1", "123456", 4, false);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Dự án này đã được chia lớp");
            assertNull(response);
        }
    }

    @Test
    void randomGroup_Exception() {
        Exception ex = new BadRequestException("");
        when(classGroupRepository.findAllByClassIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(classGroups);
        when(groupMemberRepository.findAllByGroupId(Mockito.any())).thenThrow(ex);
        List<ClassGroupDto> response = null;
        try {
            response = classGroupService.randomGroup("10A1", "123456", 4, true);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Có lỗi xảy ra");
            assertNull(response);
        }
    }

    @Test
    void randomGroup_NotExistsStudentInClass() {
        when(classGroupRepository.findAllByClassIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(classGroups);
        List<ClassGroupDto> response = null;
        try {
            response = classGroupService.randomGroup("10A1", "123456", 4, true);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Lớp này chưa có học sinh nào để xếp nhóm");
            assertNull(response);
        }
    }

    @Test
    void randomGroup_BadRequestGroup() {
        when(classGroupRepository.findAllByClassIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(classGroups);
        when(classStudentRepository.findAllByClassId("10A1")).thenReturn(classStudents);
        List<ClassGroupDto> response = null;
        try {
            response = classGroupService.randomGroup("10A1", "123456", 2, true);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Số lượng học sinh không đủ để chia thành 2 nhóm");
            assertNull(response);
        }
    }

    @Test
    void randomGroup_RemoveException() {
        Exception ex = new BadRequestException("");
        when(classGroupRepository.findAllByClassIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(classGroups);
        when(classStudentRepository.findAllByClassId("10A1")).thenReturn(classStudents);
        doThrow(ex).when(evaluationRepository).deleteAllByLessonId(Mockito.any());
        List<ClassGroupDto> response = null;
        try {
            response = classGroupService.randomGroup("10A1", "123456", 1, true);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void randomGroup_success() {
        when(classGroupRepository.findAllByClassIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(classGroups);
        when(classStudentRepository.findAllByClassId("10A1")).thenReturn(classStudents);
        when(classGroupRepository.save(Mockito.any())).thenReturn(classGroup);
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(ClassGroupDto.class))).thenReturn(classGroupDto);
        List<ClassGroupDto> response = classGroupService.randomGroup("10A1", "123456", 1, true);

        assertNotNull(response);
        assertEquals(response, classGroupDtos);
    }

    @Test
    void randomGroup_success2() {
        when(classGroupRepository.findAllByClassIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(classGroups);
        classStudents.add(classStudent);
        classStudents.add(classStudent);
        when(classStudentRepository.findAllByClassId("10A1")).thenReturn(classStudents);
        when(classGroupRepository.save(Mockito.any())).thenReturn(classGroup);
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(ClassGroupDto.class))).thenReturn(classGroupDto);
        List<ClassGroupDto> response = classGroupService.randomGroup("10A1", "123456", 2, true);

        assertNotNull(response);
        assertEquals(response.size(), 2);
    }

    @Test
    void createGroupByOrder_ExistsExperiment() {
        when(classGroupRepository.findAllByClassIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(classGroups);
        List<ClassGroupDto> response = null;
        try {
            response = classGroupService.createGroupByOrder("10A1", "123456", 4, false);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Lớp này đã được chia lớp");
            assertNull(response);
        }
    }

    @Test
    void createGroupByOrder_Exception() {
        Exception ex = new BadRequestException("");
        when(classGroupRepository.findAllByClassIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(classGroups);
        when(groupMemberRepository.findAllByGroupId(Mockito.any())).thenThrow(ex);
        List<ClassGroupDto> response = null;
        try {
            response = classGroupService.createGroupByOrder("10A1", "123456", 4, true);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Có lỗi xảy ra");
            assertNull(response);
        }
    }

    @Test
    void createGroupByOrder_NotExistsStudentInClass() {
        when(classGroupRepository.findAllByClassIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(classGroups);
        List<ClassGroupDto> response = null;
        try {
            response = classGroupService.createGroupByOrder("10A1", "123456", 4, true);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Lớp này chưa có học sinh nào để xếp nhóm");
            assertNull(response);
        }
    }

    @Test
    void createGroupByOrder_BadRequestGroup() {
        when(classGroupRepository.findAllByClassIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(classGroups);
        when(classStudentRepository.findAllByClassId("10A1")).thenReturn(classStudents);
        List<ClassGroupDto> response = null;
        try {
            response = classGroupService.createGroupByOrder("10A1", "123456", 2, true);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Số lượng học sinh không đủ để chia thành 2 nhóm");
            assertNull(response);
        }
    }

    @Test
    void createGroupByOrder_RemoveException() {
        Exception ex = new BadRequestException("");
        when(classGroupRepository.findAllByClassIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(classGroups);
        when(classStudentRepository.findAllByClassId("10A1")).thenReturn(classStudents);
        doThrow(ex).when(evaluationRepository).deleteAllByLessonId(Mockito.any());
        List<ClassGroupDto> response = null;
        try {
            response = classGroupService.createGroupByOrder("10A1", "123456", 1, true);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void createGroupByOrder_success() {
        when(classGroupRepository.findAllByClassIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(classGroups);
        when(classStudentRepository.findAllByClassId("10A1")).thenReturn(classStudents);
        when(classGroupRepository.save(Mockito.any())).thenReturn(classGroup);
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(ClassGroupDto.class))).thenReturn(classGroupDto);
        List<ClassGroupDto> response = classGroupService.createGroupByOrder("10A1", "123456", 1, true);

        assertNotNull(response);
        assertEquals(response, classGroupDtos);
    }

    @Test
    void rcreateGroupByOrder_success2() {
        when(classGroupRepository.findAllByClassIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(classGroups);
        classStudents.add(classStudent);
        classStudents.add(classStudent);
        when(classStudentRepository.findAllByClassId("10A1")).thenReturn(classStudents);
        when(classGroupRepository.save(Mockito.any())).thenReturn(classGroup);
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(ClassGroupDto.class))).thenReturn(classGroupDto);
        List<ClassGroupDto> response = classGroupService.createGroupByOrder("10A1", "123456", 2, true);

        assertNotNull(response);
        assertEquals(response.size(), 2);
    }

    @Test
    void createGroupByTemplate_Exception() {
        Exception ex = new BadRequestException("");
        when(classGroupRepository.findAllByClassIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(classGroups);
        when(groupMemberRepository.findAllByGroupId(Mockito.any())).thenThrow(ex);
        List<ClassGroupDto> response = null;
        try {
            response = classGroupService.createGroupByTemplate("10A1", "123456", multipartFile);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Có lỗi xảy ra");
            assertNull(response);
        }
    }

    @Test
    void createGroupByTemplate_RemoveException() {
        Exception ex = new BadRequestException("");
        when(classGroupRepository.findAllByClassIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(classGroups);
        when(classStudentRepository.findAllByClassId("10A1")).thenReturn(classStudents);
        doThrow(ex).when(evaluationRepository).deleteAllByLessonId(Mockito.any());
        List<ClassGroupDto> response = null;
        try {
            response = classGroupService.createGroupByTemplate("10A1", "123456", multipartFile);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void createGroupByTemplate_NullFile() {
        when(classGroupRepository.findAllByClassIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(classGroups);
        when(classStudentRepository.findAllByClassId("10A1")).thenReturn(classStudents);
        List<ClassGroupDto> response = null;
        try {
            response = classGroupService.createGroupByTemplate("10A1", "123456", null);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy file");
            assertNull(response);
        }
    }

    @Test
    void createGroupByTemplate_ReadFileException() {
        when(classGroupRepository.findAllByClassIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(classGroups);
        when(classStudentRepository.findAllByClassId("10A1")).thenReturn(classStudents);
        List<ClassGroupDto> response = null;
        try {
            response = classGroupService.createGroupByTemplate("10A1", "123456", multipartFile);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void createGroupTemplate() {
        when(classStudentMapper.getAll(Mockito.any(), Mockito.eq(null), Mockito.eq(null))).thenReturn(studentInClassDtos);
        when(s3Service.saveFile((File) Mockito.any(), Mockito.eq("templates/groups"), Mockito.any())).thenReturn("/templates/groups/10A1");
        String response = classGroupService.createGroupTemplate("10A1");
        assertEquals(response, "/templates/groups/10A1");
        assertNotNull(response);
    }

    @Test
    void deleteGroup_Exception() {
        Exception ex = new BadRequestException("");
        when(classGroupRepository.findAllByClassIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(classGroups);
        when(groupMemberRepository.findAllByGroupId(Mockito.any())).thenThrow(ex);
        int response =  classGroupService.deleteGroup("10A1", "123456");
        assertNotNull(response);
        assertEquals(response, 0);
    }

    @Test
    void deleteGroup_Success() {
        when(classGroupRepository.findAllByClassIdAndLessonId(Mockito.any(), Mockito.any())).thenReturn(classGroups);
        int response =  classGroupService.deleteGroup("10A1", "123456");
        assertNotNull(response);
        assertEquals(response, 1);
    }
}