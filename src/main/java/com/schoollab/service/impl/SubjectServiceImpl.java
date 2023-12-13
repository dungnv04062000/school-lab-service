package com.schoollab.service.impl;

import com.schoollab.common.error.BadRequestException;
import com.schoollab.common.error.NotFoundException;
import com.schoollab.common.error.UnKnownException;
import com.schoollab.controller.request.SubjectAddRequest;
import com.schoollab.dto.SubjectDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.SubjectMapper;
import com.schoollab.model.Subject;
import com.schoollab.repository.SubjectRepository;
import com.schoollab.service.SubjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SubjectServiceImpl implements SubjectService {
    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    GenericMapper genericMapper;

    @Autowired
    SubjectMapper subjectMapper;

    @Override
    public SubjectDto saveSubject(SubjectAddRequest req) {
        //kiểm tra tên môn học có bị trùng hay chưa
        Optional<Subject> subjectExistsName = subjectRepository.findSubjectByName(req.getName());
        if(subjectExistsName.isPresent()) throw new BadRequestException("Tên môn học đã có sẵn");
        Subject saveSubject = new Subject()
                .setName(req.getName());
        Subject save = subjectRepository.save(saveSubject);
        return genericMapper.mapToTypeNotNullProperty(save, SubjectDto.class);
    }

    @Override
    public SubjectDto getOneSubject(int subjectId) {
        SubjectDto subject = subjectMapper.getOneSubject(subjectId);
        return genericMapper.mapToTypeNotNullProperty(subject, SubjectDto.class);
    }

    @Override
    public List<SubjectDto> getAllSubject() {
        List<Subject> subjects = subjectRepository.findAll();
        return genericMapper.mapToListOfTypeNotNullProperty(subjects, SubjectDto.class);
    }

    @Override
    public List<SubjectDto> filterSubject(String userId, String subjectName) {
        List<SubjectDto> listSubject = subjectMapper.getAllSubject(userId, subjectName);
        return genericMapper.mapToListOfTypeNotNullProperty(listSubject, SubjectDto.class);
    }

    @Override
    public String deleteSubject(int subjectId) {
        Optional<Subject> subject = subjectRepository.findById(subjectId);
        if(!subject.isPresent()) throw new NotFoundException("Không tìm thấy môn học");
        try {
            subjectRepository.deleteById(subjectId);
        }catch (Exception e){
            e.printStackTrace();
            throw new UnKnownException("Xóa thất bại");
        }
        return "Xóa thành công";
    }
}
