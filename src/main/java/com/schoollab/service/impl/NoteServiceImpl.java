package com.schoollab.service.impl;

import com.schoollab.common.error.BadRequestException;
import com.schoollab.common.error.NotFoundException;
import com.schoollab.common.error.UnKnownException;
import com.schoollab.controller.request.NoteAddRequest;
import com.schoollab.controller.request.NoteUpdateRequestBody;
import com.schoollab.dto.ExperimentDetailDto;
import com.schoollab.dto.ExperimentDto;
import com.schoollab.dto.NoteDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.NoteMapper;
import com.schoollab.model.Note;
import com.schoollab.repository.NoteRepository;
import com.schoollab.service.ExperimentDetailService;
import com.schoollab.service.ExperimentService;
import com.schoollab.service.NoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.google.common.base.Strings.isNullOrEmpty;

@Service
@Slf4j
public class NoteServiceImpl implements NoteService {
    @Autowired
    NoteRepository noteRepository;

    @Autowired
    NoteMapper noteMapper;

    @Autowired
    ExperimentService experimentService;

    @Autowired
    ExperimentDetailService experimentDetailService;

    @Autowired
    GenericMapper genericMapper;

    @Override
    public NoteDto saveNote(String userId, NoteAddRequest req) {
        Note note = noteRepository.findNoteLessonByUser(userId, req.getLessonId());
        if(note != null){
            throw new BadRequestException("Bạn đã tạo ghi chú cho dự án này rồi");
        }
        Note newNote = new Note()
                .setUserId(userId)
                .setLessonId(isNullOrEmpty(req.getLessonId()) ? null : req.getLessonId())
                .setContent(req.getContent())
                .setCreateAt(Instant.now());
        Note savedNote = noteRepository.save(newNote);
        return genericMapper.mapToTypeNotNullProperty(savedNote, NoteDto.class);
    }

    @Override
    public NoteDto updateNote(String noteId, NoteUpdateRequestBody req) {
        Note saveNote;
        Optional<Note> optNote = noteRepository.findById(noteId);
        if(!optNote.isPresent()){
            throw new NotFoundException("Không tìm thấy ghi chú này");
        }else {
            try {
                optNote.get().setContent(req.getContent());
                optNote.get().setUpdateAt(Instant.now());
                saveNote = noteRepository.save(optNote.get());
                return genericMapper.mapToTypeNotNullProperty(saveNote, NoteDto.class);
            }catch (Exception e){
                throw new UnKnownException("Có lỗi trong quá trình xử lý");
            }
        }
    }

    @Override
    public List<NoteDto> filterNote(String userId, String lessonTitle, Instant createAtFrom, Instant createAtTo, int page, int rowNumber, String orderBy) {
        List<NoteDto> notes = noteMapper.getAll(userId, lessonTitle, createAtFrom, createAtTo, page, rowNumber, orderBy);

        for (NoteDto note: notes) {
            List<ExperimentDto> experiments = experimentService.getExperiments(note.getId());
            for (ExperimentDto experiment: experiments) {
                List<ExperimentDetailDto> experimentDetails = experimentDetailService.getExperimentDetails(experiment.getId());
                experiment.setDetails(experimentDetails);
            }
            note.setExperiments(experiments);
        }
        return notes;
    }

    @Override
    public String deleteNote(String id) {
        try {
            noteRepository.deleteById(id);
            return "Xóa ghi chú thành công";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Xoá thất bại");
        }
    }

    @Override
    public NoteDto getOne(String id) {
        NoteDto note = noteMapper.getOne(id);
        if (note != null){
            List<ExperimentDto> experiments = experimentService.getExperiments(note.getId());
            for (ExperimentDto experiment: experiments) {
                List<ExperimentDetailDto> experimentDetails = experimentDetailService.getExperimentDetails(experiment.getId());
                experiment.setDetails(experimentDetails);
            }
            note.setExperiments(experiments);
        }
        return note;
    }

    @Override
    public NoteDto getOneByLessonId(String userId, String lessonId) {
        return noteMapper.getOneByLessonId(userId, lessonId);
    }

    @Override
    public int countAllNote(String userId, String lessonId, Instant createAtFrom, Instant createAtTo) {
        return noteMapper.countAllNote(userId, lessonId, createAtFrom, createAtTo);
    }
}
