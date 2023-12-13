package com.schoollab.controller.api.v1;

import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.controller.request.CampusCreateRequestBody;
import com.schoollab.dto.CampusDto;
import com.schoollab.service.CampusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class CampusController {

    @Autowired
    CampusService campusService;

    @PostMapping(value = "/campuses", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<CampusDto>> addNewCampus(@RequestBody @Valid CampusCreateRequestBody req){
        CampusDto campusDto = campusService.addNewCampus(req);

        ResponseBodyDto response = new ResponseBodyDto<>(campusDto, ResponseCodeEnum.R_201, "CREATED");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/campuses/{campus-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<CampusDto>> updateCampus(@PathVariable(name = "campus-id") String campusId,
                                                                   @RequestBody @Valid CampusCreateRequestBody req){
        CampusDto campusDto = campusService.updateCampusInfo(campusId, req);

        ResponseBodyDto response = new ResponseBodyDto<>(campusDto, ResponseCodeEnum.R_200, "UPDATE");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/campuses/{campus-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<CampusDto>> deleteCampus(@PathVariable(name = "campus-id") String campusId){
        String message = campusService.deleteCampus(campusId);

        ResponseBodyDto response = new ResponseBodyDto<>(message, ResponseCodeEnum.R_200, "DELETE");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/root-admin/campuses", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<CampusDto>> filterCampus(@RequestParam(name = "name", required = false) String name){
        List<CampusDto> listCampus = campusService.filterCampuses(name);

        ResponseBodyDto response = new ResponseBodyDto<>(listCampus, ResponseCodeEnum.R_200, "OK", listCampus.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/root-admin/campuses/{campus-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<CampusDto>> getOneCampus(@PathVariable("campus-id") String campusId){
        CampusDto campus = campusService.getOneCampus(campusId);

        ResponseBodyDto response = new ResponseBodyDto<>(campus, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/campuses", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<CampusDto>> getCampuses(){
        List<CampusDto> campusDtos = campusService.getAllCampus();

        ResponseBodyDto response = new ResponseBodyDto<>(campusDtos, ResponseCodeEnum.R_200, "OK", campusDtos.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
