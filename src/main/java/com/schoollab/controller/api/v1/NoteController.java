package com.schoollab.controller.api.v1;

import com.schoollab.common.constants.PageConstant;
import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.common.util.TimeUtil;
import com.schoollab.common.util.UserInfoUtil;
import com.schoollab.controller.request.NoteAddRequest;
import com.schoollab.controller.request.NoteUpdateRequestBody;
import com.schoollab.dto.NoteDto;
import com.schoollab.mapper.NoteMapper;
import com.schoollab.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/v1")
@CrossOrigin("*")
public class NoteController {
    @Autowired
    NoteService noteService;
    @Autowired
    NoteMapper noteMapper;

    @PostMapping(value = "/notes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<NoteDto>> saveNote(@RequestBody @Valid NoteAddRequest requestBody){
        NoteDto noteDto = noteService.saveNote(UserInfoUtil.getUserID(), requestBody);

        ResponseBodyDto response = new ResponseBodyDto(noteDto, ResponseCodeEnum.R_201, "CREATED");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/notes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<NoteDto>> updateNote(@PathVariable(name = "id") String noteId,
                                                               @RequestBody NoteUpdateRequestBody requestBody){
        NoteDto noteDto = noteService.updateNote(noteId, requestBody);

        ResponseBodyDto response = new ResponseBodyDto(noteDto, ResponseCodeEnum.R_200, "UPDATE");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/notes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<NoteDto>> getOne(@PathVariable(name = "id") String id){
        NoteDto noteDto = noteService.getOne(id);

        ResponseBodyDto response = new ResponseBodyDto(noteDto, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/notes/lesson/{lesson-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<NoteDto>> getOneByLessonId(@PathVariable(name = "lesson-id") String lessonId){
        NoteDto noteDto = noteService.getOneByLessonId(UserInfoUtil.getUserID(), lessonId);

        ResponseBodyDto response = new ResponseBodyDto(noteDto, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/notes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<List<NoteDto>>> getAllNote(@RequestParam(name = "lesson_title", required = false) String lessonTitle,
                                                               @RequestParam(name = "create_at_from", required = false) Long createAtFrom,
                                                               @RequestParam(name = "create_at_to", required = false) Long createAtTo,
                                                               @RequestParam(name = "order_by") String orderBy,
                                                               @RequestParam(name = "page") Integer page){
        List<NoteDto> noteList = noteService.filterNote(UserInfoUtil.getUserID(), lessonTitle,
                createAtFrom != null ? TimeUtil.getStartOfThisDay(Instant.ofEpochSecond(createAtFrom)) : null,
                createAtTo != null ? TimeUtil.getEndOfThisDay(Instant.ofEpochSecond(createAtTo)) : null,
                page == null ? 1 : page, PageConstant.NOTE_ROW_NUMBER, orderBy);
        int countNote = noteService.countAllNote(UserInfoUtil.getUserID(), lessonTitle,
                createAtFrom != null ? TimeUtil.getStartOfThisDay(Instant.ofEpochSecond(createAtFrom)) : null,
                createAtTo != null ? TimeUtil.getEndOfThisDay(Instant.ofEpochSecond(createAtTo)) : null);
        ResponseBodyDto response = new ResponseBodyDto(noteList, ResponseCodeEnum.R_200, "SEARCH", countNote);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/notes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<NoteDto>> deleteNote(@PathVariable(name = "id") String noteId){
        String message = noteService.deleteNote(noteId);

        ResponseBodyDto response = new ResponseBodyDto(message, ResponseCodeEnum.R_200, "DELETE");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
