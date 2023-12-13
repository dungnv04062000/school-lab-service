package com.schoollab.controller.api.v1;

import com.schoollab.common.constants.PageConstant;
import com.schoollab.common.util.TimeUtil;
import com.schoollab.common.util.UserInfoUtil;
import com.schoollab.controller.request.TutorialCreateRequestBody;
import com.schoollab.controller.request.TutorialUpdateRequestBody;
import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.dto.TutorialDto;
import com.schoollab.service.TutorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/v1")
@CrossOrigin("*")
public class TutorialController {
    @Autowired
    TutorialService tutorialService;

    @PostMapping(value = "/tutorials", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<TutorialDto>> addTutorial(@ModelAttribute @Valid TutorialCreateRequestBody requestBody,
                                                                    @RequestParam(value = "campus_id") String campusId,
                                                                    @RequestParam(value = "file", required = false) MultipartFile multipartFile){
//        TutorialDto tutorialDto = tutorialService.addTutorial("GV002", "fschool-hoa-lac", requestBody, multipartFile);
        TutorialDto tutorialDto = tutorialService.addTutorial(UserInfoUtil.getUserID(), campusId, requestBody, multipartFile);

        ResponseBodyDto response = new ResponseBodyDto(tutorialDto, ResponseCodeEnum.R_201, "CREATED");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/tutorials/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<TutorialDto>> updateTutorial(@PathVariable(name = "id") String tutorialId,
                                                                       @RequestParam(value = "file", required = false) MultipartFile multipartFile,
                                                                       @ModelAttribute @Valid TutorialUpdateRequestBody requestBody){
        TutorialDto tutorialDto = tutorialService.updateTutorial(tutorialId, requestBody, multipartFile);

        ResponseBodyDto response = new ResponseBodyDto(tutorialDto, ResponseCodeEnum.R_200, "UPDATED");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/tutorials/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<TutorialDto>> deleteTutorial(@PathVariable(name = "id") String tutorialId){
        String message = tutorialService.deleteTutorial(tutorialId, UserInfoUtil.getUserID());

        ResponseBodyDto response = new ResponseBodyDto(message, ResponseCodeEnum.R_200, "DELETED");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/tutorials/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<TutorialDto>> getOne(@PathVariable(name = "id") String tutorialId){
        TutorialDto tutorialDto = tutorialService.getOne(tutorialId);

        ResponseBodyDto response = new ResponseBodyDto(tutorialDto, ResponseCodeEnum.R_200, "SEARCH");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/tutorials", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<TutorialDto>> searchTutorials(@RequestParam(name = "title", required = false) String title,
                                                                        @RequestParam(name = "campus_id") String campusId,
                                                                        @RequestParam(name = "create_by", required = false) String createBy,
                                                                        @RequestParam(name = "create_at_from", required = false) Long createAtFrom,
                                                                        @RequestParam(name = "create_at_to", required = false) Long createAtTo,
                                                                        @RequestParam(name = "page") Integer page){
        List<TutorialDto> tutorialDtoList = tutorialService.getAll(campusId, createBy, title,
                createAtFrom != null ? TimeUtil.getStartOfThisDay(Instant.ofEpochSecond(createAtFrom)) : null,
                createAtTo != null ? TimeUtil.getEndOfThisDay(Instant.ofEpochSecond(createAtTo)) : null,
                page == null ? 1 : page, PageConstant.TUTORIAL_ROW_NUMBER);

        int countTutorial = tutorialService.countAll(campusId, createBy, title,
                createAtFrom != null ? TimeUtil.getStartOfThisDay(Instant.ofEpochSecond(createAtFrom)) : null,
                createAtTo != null ? TimeUtil.getEndOfThisDay(Instant.ofEpochSecond(createAtTo)) : null);

        ResponseBodyDto response = new ResponseBodyDto(tutorialDtoList, ResponseCodeEnum.R_200, "SEARCH", countTutorial);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
