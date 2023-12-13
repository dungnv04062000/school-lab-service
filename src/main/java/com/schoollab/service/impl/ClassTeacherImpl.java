package com.schoollab.service.impl;

import com.schoollab.common.constants.Constants;
import com.schoollab.common.error.BadRequestException;
import com.schoollab.common.error.NotFoundException;
import com.schoollab.common.error.UnKnownException;
import com.schoollab.controller.request.ClassTeacherRequest;
import com.schoollab.dto.ClassTeacherDto;
import com.schoollab.dto.StudentInClassDto;
import com.schoollab.dto.TeacherInClassDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.ClassTeacherMapper;
import com.schoollab.model.ClassTeacher;
import com.schoollab.model.User;
import com.schoollab.model.UserRole;
import com.schoollab.repository.ClassTeacherRepository;
import com.schoollab.repository.UserRepository;
import com.schoollab.repository.UserRoleRepository;
import com.schoollab.service.ClassTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.Subject;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ClassTeacherImpl implements ClassTeacherService {
    @Autowired
    ClassTeacherRepository classTeacherRepo;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    GenericMapper genericMapper;
    @Autowired
    ClassTeacherMapper classTeacherMapper;

    @Override
    public ClassTeacherDto addTeacherToClass(ClassTeacherRequest req) {
        Optional<User> optionalUser = userRepository.findById(req.getTeacherId());
        if(!optionalUser.isPresent()) {
            throw new NotFoundException("Không tìm thấy giáo viên này");
        }

        UserRole userRole = userRoleRepository.findByUserIdAndAndRoleId(req.getTeacherId(), Constants.ROLE_TEACHER_ID);
        if(userRole == null ){
            throw new BadRequestException("Người này không phải là giáo viên");
        }
        ClassTeacher existClassTeacher = classTeacherRepo.findTeacherByClass(req.getClassId(), req.getTeacherId());
        if(!Objects.isNull(existClassTeacher)) throw new BadRequestException("Giáo viên đã có trong lớp học này");
        ClassTeacher classTeacher = new ClassTeacher()
                .setClassId(req.getClassId())
                .setTeacherId(req.getTeacherId())
                .setCreateAt(Instant.now());
        ClassTeacher saveData = classTeacherRepo.save(classTeacher);
        return genericMapper.mapToTypeNotNullProperty(saveData, ClassTeacherDto.class);
    }

    @Override
    public List<TeacherInClassDto> filterTeacherInClass(String classId, String teacher) {
        List<TeacherInClassDto> listTeacher = classTeacherMapper.getAllTeacher(classId, teacher);
        return genericMapper.mapToListOfTypeNotNullProperty(listTeacher, TeacherInClassDto.class);
    }

    @Override
    public String removeTeacherInClass(String id) {
        Optional<ClassTeacher> dataExists = classTeacherRepo.findById(id);
        if(Objects.isNull(dataExists)) throw new NotFoundException("Không tìm thấy giáo viên");
        try {
            classTeacherRepo.deleteById(dataExists.get().getId());
        }catch (Exception e){
            throw new UnKnownException("Xóa giáo viên thất bại");
        }
        return "Xóa giáo viên thành công";
    }

    @Override
    public List<TeacherInClassDto> filterTeacherInCampus(String campusId, String semesterId, Integer roleId, String className, String student, String gender, Integer page, Integer rowNumber) {
        return classTeacherMapper.getAllInCampus(campusId, semesterId, roleId, className, student, gender, page, rowNumber);
    }

    @Override
    public List<TeacherInClassDto> getAllTeachers(String campusId, String teacher, String gender, Integer page, Integer rowNumber) {
        return classTeacherMapper.getAllInCampusForRootAdmin(campusId, Constants.ROLE_TEACHER_ID, teacher, gender, page, rowNumber);
    }

    @Override
    public Integer countAllTeachers(String campusId, String teacher, String gender) {
        return classTeacherMapper.countAllInCampusForRootAdmin(campusId, Constants.ROLE_TEACHER_ID, teacher, gender);
    }

    @Override
    public int countAllTeacherInCampus(String campusId, String semesterId, Integer roleId, String className, String student, String gender) {
        return classTeacherMapper.countAllInCampus(campusId, semesterId, roleId, className, student, gender);
    }

}
