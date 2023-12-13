package com.schoollab.service;

import com.schoollab.dto.ClassGroupDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ClassGroupService {

    List<ClassGroupDto> getAllGroups(String classId, String lessonId);

    ClassGroupDto getOwnerGroups(String lessonId, String studentId);

    List<ClassGroupDto> randomGroup(String classId, String lessonId, int numberOfGroup, Boolean isOverride);

    List<ClassGroupDto> createGroupByOrder(String classId, String lessonId, int numberOfGroup, Boolean isOverride);

    List<ClassGroupDto> createGroupByTemplate(String classId, String lessonId, MultipartFile file);

    String createGroupTemplate(String classId);

    int deleteGroup(String classId, String lessonId);
}
