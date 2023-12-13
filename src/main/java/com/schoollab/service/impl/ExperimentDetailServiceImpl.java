package com.schoollab.service.impl;

import com.schoollab.common.error.NotFoundException;
import com.schoollab.controller.request.ExperimentDetailCreateRequestBody;
import com.schoollab.controller.request.ExperimentDetailUpdateRequestBody;
import com.schoollab.dto.ExperimentDetailDto;
import com.schoollab.dto.ExperimentDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.model.Experiment;
import com.schoollab.model.ExperimentDetail;
import com.schoollab.model.Note;
import com.schoollab.repository.ExperimentDetailRepository;
import com.schoollab.repository.ExperimentRepository;
import com.schoollab.repository.NoteRepository;
import com.schoollab.service.ExperimentDetailService;
import com.schoollab.service.ExperimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ExperimentDetailServiceImpl implements ExperimentDetailService {

    @Autowired
    ExperimentDetailRepository experimentDetailRepository;

    @Autowired
    ExperimentRepository experimentRepository;

    @Autowired
    GenericMapper genericMapper;

    @Override
    public ExperimentDetailDto createExperimentDetail(ExperimentDetailCreateRequestBody requestBody) {
        Optional<Experiment> optionalExperiment = experimentRepository.findById(requestBody.getExperimentId());
        if(!optionalExperiment.isPresent()){
            throw new NotFoundException("Không tìm thấy thí nghiệm này");
        }

        ExperimentDetail experimentDetail = new ExperimentDetail()
                .setExperimentId(requestBody.getExperimentId())
                .setFirstInput(requestBody.getFirstInput())
                .setSecondInput(requestBody.getSecondInput())
                .setResult(requestBody.getResult())
                .setOrderNumber(requestBody.getOrderNumber())
                .setCreateAt(Instant.now());
        ExperimentDetail savedExperimentDetail = experimentDetailRepository.save(experimentDetail);
        return genericMapper.mapToTypeNotNullProperty(savedExperimentDetail, ExperimentDetailDto.class);
    }

    @Override
    public ExperimentDetailDto updateExperimentDetail(String experimentDetailId, ExperimentDetailUpdateRequestBody requestBody) {
        Optional<ExperimentDetail> optionalExperimentDetail = experimentDetailRepository.findById(experimentDetailId);
        if(!optionalExperimentDetail.isPresent()){
            throw new NotFoundException("Không tìm thấy lần thử nghiệm này");
        }

        ExperimentDetail experimentDetail = optionalExperimentDetail.get();
        experimentDetail.setFirstInput(requestBody.getFirstInput());
        experimentDetail.setSecondInput(requestBody.getSecondInput());
        experimentDetail.setResult(requestBody.getResult());
        experimentDetail.setUpdateAt(Instant.now());
        ExperimentDetail savedExperimentDetail = experimentDetailRepository.save(experimentDetail);
        return genericMapper.mapToTypeNotNullProperty(savedExperimentDetail, ExperimentDetailDto.class);
    }

    @Override
    public List<ExperimentDetailDto> getExperimentDetails(String experimentId) {
        List<ExperimentDetail> experimentDetails = experimentDetailRepository.findAllByExperimentIdOrderByCreateAtAsc(experimentId);
        return genericMapper.mapToListOfTypeNotNullProperty(experimentDetails, ExperimentDetailDto.class);
    }
}
