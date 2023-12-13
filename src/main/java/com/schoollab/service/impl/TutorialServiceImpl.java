package com.schoollab.service.impl;

import com.schoollab.common.error.BadRequestException;
import com.schoollab.common.error.NotFoundException;
import com.schoollab.common.error.UnKnownException;
import com.schoollab.controller.request.TutorialCreateRequestBody;
import com.schoollab.controller.request.TutorialUpdateRequestBody;
import com.schoollab.dto.TutorialDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.TutorialMapper;
import com.schoollab.model.Campus;
import com.schoollab.model.Tutorial;
import com.schoollab.model.User;
import com.schoollab.repository.CampusRepository;
import com.schoollab.repository.TutorialRepository;
import com.schoollab.repository.UserRepository;
import com.schoollab.service.S3Service;
import com.schoollab.service.TutorialService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class TutorialServiceImpl implements TutorialService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    CampusRepository campusRepository;
    @Autowired
    TutorialRepository tutorialRepository;
    @Autowired
    S3Service s3Service;
    @Autowired
    GenericMapper genericMapper;
    @Autowired
    TutorialMapper tutorialMapper;


    @Override
    public TutorialDto addTutorial(String createBy, String campusId, TutorialCreateRequestBody req, MultipartFile multipartFile) {
        Optional<User> optUser = userRepository.findById(createBy);
        if(!optUser.isPresent()){
            throw new NotFoundException("Không tìm thấy người dùng này");
        }
        Optional<Campus> optCampus = campusRepository.findById(campusId);
        if(!optCampus.isPresent()){
            throw new NotFoundException("Không tìm thấy cơ sở này");
        }
        Tutorial tutorial = tutorialRepository.findByTitle(req.getTitle());
        if (tutorial != null){
            throw new BadRequestException("Tên đề mục đã có sẵn, vui lòng tạo tên đề mục khác");
        }
        if(multipartFile == null || multipartFile.isEmpty()){
            throw new RuntimeException("Không tìm thấy tệp nào");
        }
        Tutorial savedTutorial = null;

        try {
            Tutorial newTutorial = new Tutorial()
                    .setTitle(req.getTitle().trim())
                    .setCampusId(campusId)
                    .setCreateBy(createBy)
                    .setDescription(req.getDescription().trim())
                    .setCreateAt(Instant.now());

            savedTutorial = tutorialRepository.save(newTutorial);
            String downloadUrl = s3Service.saveFile(multipartFile, "tutorials/" + savedTutorial.getId(), null);
            savedTutorial.setAttachmentUrl(downloadUrl);
            tutorialRepository.save(savedTutorial);
        }catch (Exception e){
            e.printStackTrace();
            throw new UnKnownException("Có lỗi trong quá trình xử lý");
        }

        return genericMapper.mapToTypeNotNullProperty(savedTutorial, TutorialDto.class);
    }

    @Override
    public TutorialDto updateTutorial(String id, TutorialUpdateRequestBody requestBody, MultipartFile multipartFile) {
        Optional<Tutorial> optTutorial = tutorialRepository.findById(id);
        if(!optTutorial.isPresent()){
            throw new NotFoundException("Không tìm thấy thông tin");
        }
        Tutorial tutorial = optTutorial.get();
        if(StringUtils.isNotBlank(requestBody.getTitle().trim())){
            tutorial.setTitle(requestBody.getTitle().trim());
        }
        if(StringUtils.isNotBlank(requestBody.getDescription().trim())){
            tutorial.setDescription(requestBody.getDescription().trim());
        }
        if(multipartFile != null && !multipartFile.isEmpty()){
            if(optTutorial.get().getAttachmentUrl() != null){
                s3Service.deleteFile(optTutorial.get().getAttachmentUrl());
            }
            String downloadUrl = s3Service.saveFile(multipartFile, "tutorials/" + optTutorial.get().getId(), null);
            optTutorial.get().setAttachmentUrl(downloadUrl);
        }
        optTutorial.get().setUpdateAt(Instant.now());

        Tutorial savedTutorial = tutorialRepository.save(optTutorial.get());
        return genericMapper.mapToTypeNotNullProperty(savedTutorial, TutorialDto.class);
    }

    @Override
    public String deleteTutorial(String id, String userId) {
        Optional<Tutorial> optTutorial = tutorialRepository.findById(id);
        if(!optTutorial.isPresent()){
            throw new NotFoundException("Không tìm thấy dữ liệu");
        }
        if(!userId.equals(optTutorial.get().getCreateBy())){
            throw new BadRequestException("Bạn không có quyền xóa");
        }
        tutorialRepository.deleteById(optTutorial.get().getId());
        return "Xóa thành công";
    }

    @Override
    public TutorialDto getOne(String id) {
        Optional<Tutorial> optTutorial = tutorialRepository.findById(id);
        if(!optTutorial.isPresent()){
            return null;
        }
        return genericMapper.mapToTypeNotNullProperty(optTutorial, TutorialDto.class);
    }

    @Override
    public List<TutorialDto> getAll(String campusId, String createBy, String title, Instant createAtFrom, Instant createAtTo, int page, int size) {
        return tutorialMapper.getAll(campusId, createBy, title, createAtFrom, createAtTo, page, size);
    }

    @Override
    public int countAll(String campusId, String createBy, String title, Instant createAtFrom, Instant createAtTo) {
        return tutorialMapper.countAll(campusId, createBy, title, createAtFrom, createAtTo);
    }

}
