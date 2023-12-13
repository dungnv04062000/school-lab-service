package com.schoollab.service.impl;

import com.schoollab.common.constants.Constants;
import com.schoollab.common.error.BadRequestException;
import com.schoollab.common.error.NotFoundException;
import com.schoollab.controller.request.ClassCreateRequestBody;
import com.schoollab.controller.request.ClassUpdateRequestBody;
import com.schoollab.dto.ClassDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.ClassMapper;
import com.schoollab.model.Class;
import com.schoollab.model.User;
import com.schoollab.model.UserRole;
import com.schoollab.repository.ClassRepository;
import com.schoollab.repository.UserRepository;
import com.schoollab.repository.UserRoleRepository;
import com.schoollab.service.ClassService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ClassServiceImpl implements ClassService {

    @Autowired
    ClassRepository classRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    GenericMapper genericMapper;

    @Autowired
    ClassMapper classMapper;

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public ClassDto createClass(ClassCreateRequestBody requestBody) {
        if(classRepository.findByNameAndAndSemesterId(requestBody.getName(), requestBody.getSemesterId()) != null){
            throw new BadRequestException("Lớp này đã tồn tại");
        }
        if(requestBody.getFormTeacherId() != null){
            Class objClass = classRepository.
                    findByFormTeacherIdAndSemesterId(requestBody.getFormTeacherId(), requestBody.getSemesterId());
            if(objClass != null){
                throw new BadRequestException("Giáo viên này đã chủ nhiệm 1 lớp khác trong học kỳ này");
            }
        }

        Class newClass = new Class().setName(requestBody.getName())
                .setCampusId(requestBody.getCampusId())
                .setSemesterId(requestBody.getSemesterId())
                .setGradeId(requestBody.getGradeId())
                .setFormTeacherId(requestBody.getFormTeacherId())
                .setCreateAt(Instant.now());

        Class savedClass = classRepository.save(newClass);
        return genericMapper.mapToTypeNotNullProperty(savedClass, ClassDto.class);
    }

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public void deleteClass(String classId) {
        Optional<Class> optClass = classRepository.findById(classId);
        if(!optClass.isPresent()){
            throw new NotFoundException("Không tìm thấy lớp học này!");
        }

        Class objClass = optClass.get();
        classRepository.delete(objClass);
    }

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public ClassDto updateClass(String classId, ClassUpdateRequestBody requestBody) {
        Optional<Class> optClass = classRepository.findById(classId);
        if(!optClass.isPresent()){
            throw new NotFoundException("Không tìm thấy lớp học này");
        }

        if(requestBody.getFormTeacherId() != null){
            Optional<User> optTeacher = userRepository.findById(requestBody.getFormTeacherId());
            if(!optTeacher.isPresent()){
                throw new NotFoundException("Không tìm thấy giáo viên này!");
            }
            UserRole userRole = userRoleRepository.findByUserIdAndAndRoleId(requestBody.getFormTeacherId(), Constants.ROLE_TEACHER_ID);
            if(userRole == null ){
                throw new BadRequestException("Người này không phải là giáo viên");
            }
            Class objClass = classRepository.findByFormTeacherIdAndSemesterId(requestBody.getFormTeacherId(), optClass.get().getSemesterId());
            if(objClass != null && !objClass.getId().equals(classId)){
                throw new BadRequestException("Giáo viên này đã chủ nhiệm 1 lớp khác");
            }
        }

        Class updatingClass = optClass.get();


        if(StringUtils.isNotBlank(requestBody.getName())){
            if(classRepository.findByNameAndAndSemesterId(requestBody.getName(), updatingClass.getSemesterId()) != null
                    && !requestBody.getName().equals(updatingClass.getName())){
                throw new BadRequestException("Lớp này đã tồn tại trong học kỳ này");
            }
            updatingClass.setName(requestBody.getName());
        }
        if(StringUtils.isNotBlank(requestBody.getFormTeacherId())){
            updatingClass.setFormTeacherId(requestBody.getFormTeacherId());
        } else {
            updatingClass.setFormTeacherId(null);
        }
        updatingClass.setGradeId(requestBody.getGradeId());
        updatingClass.setUpdateAt(Instant.now());
        Class updatedClass = classRepository.save(updatingClass);
        return genericMapper.mapToTypeNotNullProperty(updatedClass, ClassDto.class);
    }

    @Override
    public List<ClassDto> getAll(String teacherId, String semesterId, String gradeId, String className, String formTeacher) {
        Optional<User> optUser = userRepository.findById(teacherId);
        if(!optUser.isPresent()){
            throw new NotFoundException("Không tìm thấy giáo viên này");
        }
        return classMapper.getAll(optUser.get().getCampusId(), semesterId, gradeId, teacherId, className, formTeacher);
    }

    @Override
    public List<ClassDto> getAllForAdminSchool(String campusId, String semesterId, String gradeId, String className, String formTeacher) {
        return classMapper.getAll(campusId, semesterId, gradeId, null, className, formTeacher);
    }

    @Override
    public ClassDto getOne(String id) {
        return classMapper.getOne(id);
    }
}
