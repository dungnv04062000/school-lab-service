package com.schoollab.service.impl;

import com.schoollab.common.error.BadRequestException;
import com.schoollab.controller.request.SemesterCreateRequestBody;
import com.schoollab.controller.request.SemesterUpdateRequestBody;
import com.schoollab.dto.SemesterDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.SemesterMapper;
import com.schoollab.model.Semester;
import com.schoollab.model.User;
import com.schoollab.repository.SemesterRepository;
import com.schoollab.repository.UserRepository;
import com.schoollab.service.SemesterService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class SemesterServiceImpl implements SemesterService {

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SemesterMapper semesterMapper;

    @Autowired
    GenericMapper genericMapper;

    @Override
    public List<SemesterDto> getSemesters(String userId, String semesterName, Integer year) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(!optionalUser.isPresent()){
            throw new BadRequestException("Không tìm thấy người dùng");
        }
        return semesterMapper.getSemesters(optionalUser.get().getCampusId(), semesterName, year);
    }

    @Override
    public SemesterDto getOne(String semesterId) {
        Optional<Semester> optionalSemester = semesterRepository.findById(semesterId);
        if(!optionalSemester.isPresent()){
            throw new BadRequestException("Không tìm thấy học kỳ này");
        }
        return genericMapper.mapToTypeNotNullProperty(optionalSemester.get(), SemesterDto.class);
    }

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public SemesterDto createSemester(SemesterCreateRequestBody requestBody) {
        Semester _semester = semesterRepository.findByNameAndYear(requestBody.getName(), requestBody.getYear());
        if(_semester != null){
            throw new RuntimeException("Học kỳ này đã tồn tại");
        }

        Semester semester = new Semester()
                .setName(requestBody.getName())
                .setCampusId(requestBody.getCampusId())
                .setYear(requestBody.getYear())
                .setStartAt(Instant.ofEpochSecond(requestBody.getStartAt()))
                .setCreateAt(Instant.now());
        Semester savedSemester = semesterRepository.save(semester);
        return genericMapper.mapToTypeNotNullProperty(savedSemester, SemesterDto.class);
    }

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public SemesterDto updateSemester(String semesterId, SemesterUpdateRequestBody requestBody) {
        Optional<Semester> optionalSemester = semesterRepository.findById(semesterId);
        if(!optionalSemester.isPresent()){
            throw new BadRequestException("Không tìm thấy học kỳ này");
        }
        Semester semester = optionalSemester.get();

        Semester _semester = semesterRepository.findByNameAndYear(requestBody.getName(), requestBody.getYear());
        if(_semester != null && !_semester.getId().equals(semesterId)) {
            throw new RuntimeException("Học kỳ này đã tồn tại");
        }


        if(StringUtils.isNotBlank(requestBody.getName())){
            semester.setName(requestBody.getName().trim());
        }

        if(requestBody.getYear() != null){
            semester.setYear(requestBody.getYear());
        }

        if(requestBody.getStartAt() != null){
            semester.setStartAt(Instant.ofEpochSecond(requestBody.getStartAt()));
        }

        semester.setUpdateAt(Instant.now());
        Semester updatedSemester = semesterRepository.save(semester);
        return genericMapper.mapToTypeNotNullProperty(updatedSemester, SemesterDto.class);
    }

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public void deleteSemester(String semesterId) {
        Optional<Semester> optionalSemester = semesterRepository.findById(semesterId);
        if(!optionalSemester.isPresent()){
            throw new BadRequestException("Không tìm thấy học kỳ này");
        }
        semesterRepository.delete(optionalSemester.get());
    }
}
