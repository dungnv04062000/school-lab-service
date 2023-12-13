package com.schoollab.service;

import com.schoollab.controller.request.TutorialCreateRequestBody;
import com.schoollab.controller.request.TutorialUpdateRequestBody;
import com.schoollab.dto.TutorialDto;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

public interface TutorialService {
    TutorialDto addTutorial(String createBy, String campusId, TutorialCreateRequestBody req, MultipartFile multipartFile);

    TutorialDto updateTutorial(String id, TutorialUpdateRequestBody requestBody, MultipartFile multipartFile);

    String deleteTutorial(String id, String userId);

    TutorialDto getOne(String id);
    List<TutorialDto> getAll(String campusId, String createBy, String title, Instant createAtFrom, Instant createAtTo, int page, int size);

    int countAll(String campusId, String createBy, String title, Instant createAtFrom, Instant createAtTo);
}
