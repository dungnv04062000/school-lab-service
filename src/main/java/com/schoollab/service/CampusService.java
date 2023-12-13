package com.schoollab.service;

import com.schoollab.controller.request.CampusCreateRequestBody;
import com.schoollab.dto.CampusDto;

import java.util.List;

public interface CampusService {
    CampusDto addNewCampus(CampusCreateRequestBody requestBody);

    CampusDto updateCampusInfo(String campusId, CampusCreateRequestBody requestBody);

    String deleteCampus(String campusId);

    List<CampusDto> filterCampuses(String name);

    List<CampusDto> getAllCampus();

    CampusDto getOneCampus(String campusId);
}
