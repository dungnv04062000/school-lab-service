package com.schoollab.service.impl;

import com.schoollab.common.error.NotFoundException;
import com.schoollab.controller.request.ExperimentCreateRequestBody;
import com.schoollab.controller.request.ExperimentUpdateRequestBody;
import com.schoollab.dto.ExperimentDetailDto;
import com.schoollab.dto.ExperimentDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.model.Experiment;
import com.schoollab.model.Note;
import com.schoollab.repository.ExperimentRepository;
import com.schoollab.repository.NoteRepository;
import com.schoollab.service.ExperimentDetailService;
import com.schoollab.service.ExperimentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ExperimentServiceImpl implements ExperimentService {

    @Autowired
    ExperimentRepository experimentRepository;

    @Autowired
    NoteRepository noteRepository;

    @Autowired
    ExperimentDetailService experimentDetailService;

    @Autowired
    GenericMapper genericMapper;

    @Override
    public ExperimentDto createExperiment(String createBy, ExperimentCreateRequestBody requestBody) {
        Optional<Note> optNote = noteRepository.findById(requestBody.getNoteId());
        if (!optNote.isPresent()) {
            throw new NotFoundException("Bạn phải tạo ghi chú trước");
        }

        Experiment experiment = new Experiment()
                .setTitle(requestBody.getTitle().trim())
                .setNoteId(requestBody.getNoteId())
                .setCreateBy(createBy)
                .setCreateAt(Instant.now())
                .setFirstLabel(StringUtils.isNotBlank(requestBody.getFirstLabel()) ? requestBody.getFirstLabel().trim() : "Đại lượng 1")
                .setSecondLabel(StringUtils.isNotBlank(requestBody.getSecondLabel()) ? requestBody.getSecondLabel().trim() : "Đại lượng 2")
                .setResultLabel(StringUtils.isNotBlank(requestBody.getResultLabel()) ? requestBody.getResultLabel().trim() : "Kết quả")
                .setFirstMeasure(StringUtils.isNotBlank(requestBody.getFirstMeasure()) ? requestBody.getFirstMeasure().trim() : null)
                .setSecondMeasure(StringUtils.isNotBlank(requestBody.getSecondMeasure()) ? requestBody.getSecondMeasure().trim() : null)
                .setResultMeasure(StringUtils.isNotBlank(requestBody.getResultMeasure()) ? requestBody.getResultMeasure().trim() : null);

        Experiment savedExperiment = experimentRepository.save(experiment);
        return genericMapper.mapToTypeNotNullProperty(savedExperiment, ExperimentDto.class);
    }

    @Override
    public ExperimentDto updateExperiment(String experimentId, ExperimentUpdateRequestBody requestBody) {
        Optional<Experiment> optExperiment = experimentRepository.findById(experimentId);
        if (!optExperiment.isPresent()) {
            throw new NotFoundException("Không tìm thấy thí nghiệm này");
        }

        Experiment experiment = optExperiment.get();
        experiment.setTitle(requestBody.getTitle().trim());
        experiment.setFirstLabel(StringUtils.isNotBlank(requestBody.getFirstLabel()) ? requestBody.getFirstLabel().trim() : "Đại lượng 1");
        experiment.setSecondLabel(StringUtils.isNotBlank(requestBody.getSecondLabel()) ? requestBody.getSecondLabel().trim() : "Đại lượng 2");
        experiment.setResultLabel(StringUtils.isNotBlank(requestBody.getResultLabel()) ? requestBody.getResultLabel().trim() : "Kết quả");

        experiment.setFirstMeasure(StringUtils.isNotBlank(requestBody.getFirstMeasure()) ? requestBody.getFirstMeasure().trim() : null);
        experiment.setSecondMeasure(StringUtils.isNotBlank(requestBody.getSecondMeasure()) ? requestBody.getSecondMeasure().trim() : null);
        experiment.setResultMeasure(StringUtils.isNotBlank(requestBody.getResultMeasure()) ? requestBody.getResultMeasure().trim() : null);
        Experiment savedExperiment = experimentRepository.save(experiment);
        return genericMapper.mapToTypeNotNullProperty(savedExperiment, ExperimentDto.class);
    }

    @Override
    public List<ExperimentDto> getExperiments(String noteId) {
        List<Experiment> experiments = experimentRepository.findAllByNoteIdOrderByCreateAtDesc(noteId);
        return genericMapper.mapToListOfTypeNotNullProperty(experiments, ExperimentDto.class);
    }

    //dùng cho giáo viên xem nhật ký thí nghiệm của học sinh
    @Override
    public List<ExperimentDto> getExperiments(String userId, String lessonId) {
        Note note = noteRepository.findByUserIdAndLessonId(userId, lessonId);
        if(note == null){
            throw new RuntimeException("Không tìm thấy ghi chú này");
        }
        List<Experiment> experiments = experimentRepository.findAllByNoteIdOrderByCreateAtDesc(note.getId());

        List<ExperimentDto> experimentDtos = genericMapper.mapToListOfTypeNotNullProperty(experiments, ExperimentDto.class);
        for (ExperimentDto experimentDto : experimentDtos){
            List<ExperimentDetailDto> experimentDetailDtos = experimentDetailService.getExperimentDetails(experimentDto.getId());
            if(!experimentDetailDtos.isEmpty()){
                experimentDto.setDetails(experimentDetailDtos);
            }
        }
        return experimentDtos;
    }
}
