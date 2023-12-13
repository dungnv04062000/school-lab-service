package com.schoollab.service;

import com.schoollab.controller.request.SupportUserRequest;
import com.schoollab.dto.SupportUserDto;

import java.time.Instant;
import java.util.List;

public interface SupportUserService {
    SupportUserDto save(SupportUserRequest req);

    SupportUserDto getOne(String supportId);

    SupportUserDto updateStatus(String supportId, String response);

    List<SupportUserDto> filterSupportUser(String priority, String status, Instant fromDate, Instant toDate, String userId, String type, Integer page, Integer rowNumber);

    Integer countSupportUser(String priority, String status, Instant fromDate, Instant toDate, String userId);

}
