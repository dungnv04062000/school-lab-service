package com.schoollab.service.impl;

import com.schoollab.common.constants.Constants;
import com.schoollab.common.error.BadRequestException;
import com.schoollab.common.error.NotFoundException;
import com.schoollab.controller.request.SupportUserRequest;
import com.schoollab.dto.SupportUserDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.SupportMapper;
import com.schoollab.model.Support;
import com.schoollab.repository.SupportUserRepository;
import com.schoollab.service.SupportUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SupportServiceImpl implements SupportUserService {
    @Autowired
    SupportUserRepository supportUserRepository;

    @Autowired
    GenericMapper genericMapper;

    @Autowired
    SupportMapper supportMapper;

    @Override
    public SupportUserDto save(SupportUserRequest req) {
        Support newSupport = new Support()
                .setFromId(req.getFromId() == null ? null : req.getFromId())
                .setName(req.getName().trim())
                .setEmail(req.getEmail())
                .setPhoneNumber(req.getPhoneNumber())
                .setTitle(req.getTitle().trim())
                .setContent(req.getContent().trim())
                .setPriority(req.getPriority())
                .setCreateAt(Instant.now())
                .setStatus(Constants.SUPPORT_STATUS_WAITTING);
        supportUserRepository.save(newSupport);
        return genericMapper.mapToTypeNotNullProperty(newSupport, SupportUserDto.class);
    }

    @Override
    public SupportUserDto getOne(String supportId) {
        Optional<Support> optionalSupport = supportUserRepository.findById(supportId);
        if(!optionalSupport.isPresent()){
            throw new NotFoundException("Không tìm thấy yêu cầu này");
        }
        return genericMapper.mapToTypeNotNullProperty(optionalSupport.get(), SupportUserDto.class);
    }

    @Override
    public SupportUserDto updateStatus(String supportId, String response) {
        Optional<Support> optionalSupport = supportUserRepository.findById(supportId);
        if (!optionalSupport.isPresent()) throw new NotFoundException("Không tìm thấy thông tin cần hỗ trợ");
        Support support = optionalSupport.get();
        if(response != null && StringUtils.isNotBlank(response.trim())){
            support.setResponse(response.trim());
        }

        support.setStatus(Constants.SUPPORT_STATUS_DONE);
        support.setUpdateAt(Instant.now());
        Support savedSupport = supportUserRepository.save(support);

        return genericMapper.mapToTypeNotNullProperty(savedSupport, SupportUserDto.class);
    }

    @Override
    public List<SupportUserDto> filterSupportUser(String priority, String status, Instant fromDate, Instant toDate, String userId, String type, Integer page, Integer rowNumber) {
        List<SupportUserDto> list = supportMapper.getAllSupportsRequest(priority, status, fromDate, toDate, userId, type, page, rowNumber);
        return genericMapper.mapToListOfTypeNotNullProperty(list, SupportUserDto.class);
    }

    @Override
    public Integer countSupportUser(String priority, String status, Instant fromDate, Instant toDate, String userId) {
        return supportMapper.countAllSupportsRequest(priority, status, fromDate, toDate, userId);
    }
}
