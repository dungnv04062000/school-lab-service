package com.schoollab.service;

import com.schoollab.controller.request.NoteAddRequest;
import com.schoollab.controller.request.NoteUpdateRequestBody;
import com.schoollab.dto.NoteDto;

import java.time.Instant;
import java.util.List;

public interface NoteService {
    NoteDto saveNote(String userId, NoteAddRequest noteAddRequest);

    NoteDto updateNote(String noteId, NoteUpdateRequestBody req);

    List<NoteDto> filterNote(String userId, String lessonId, Instant createAtFrom, Instant createAtTo, int page, int rowNumber, String orderBy);

    String deleteNote(String id);

    NoteDto getOne(String id);

    NoteDto getOneByLessonId(String userId, String lessonId);

    int countAllNote(String userId, String lessonId, Instant createAtFrom, Instant createAtTo);
}
