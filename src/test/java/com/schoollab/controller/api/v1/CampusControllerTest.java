package com.schoollab.controller.api.v1;

import com.schoollab.common.error.BadRequestException;
import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.controller.request.CampusCreateRequestBody;
import com.schoollab.dto.CampusDto;
import com.schoollab.modelTest.SetUpModelTest;
import com.schoollab.service.CampusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CampusControllerTest {
    @InjectMocks
    CampusController campusController;
    @Mock
    CampusService campusService;
    CampusDto campusDto;
    CampusCreateRequestBody campusCreateRequestBody;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        campusDto = SetUpModelTest.setUpCampusDto();
        campusCreateRequestBody = SetUpModelTest.campusReq();
    }

    @Test
    void addNewCampus() {
        //set up response trả về từ service cái campusDto
        ResponseBodyDto responseBodyDto = new ResponseBodyDto<>(campusDto, ResponseCodeEnum.R_201, "CREATED");
        //Mặc định giá trị trả về của service
        when(campusService.addNewCampus(Mockito.any())).thenReturn(campusDto);

        //gọi đến controller cần test với những input đầu vào đúng yêu cầu
        ResponseEntity<ResponseBodyDto<CampusDto>> response = campusController.addNewCampus(campusCreateRequestBody);

        //thực hiện kiểm tra các response trả về
        //kiểm tra null
        assertNotNull(response);
        //kiểm tra status trả về
//        assertEquals(response.getStatusCode(), HttpStatus.OK);
        //kiểm tra model trả về của service
        assertEquals(response.getBody().getItem(), campusDto);
        //kiểm tra respone tạo được từ controller trả về phía font-end
        assertEquals(response.getBody(), responseBodyDto);
    }

    @Test
    void addNewCampus_exception() {
        //tạo exception khi throw trong service
        Exception e = new BadRequestException("Tên campus đã tồn tại.");
        //mặc định trả về exception đã tạo khi service bị throw
        when(campusService.addNewCampus(Mockito.any())).thenThrow(e);
        ResponseEntity<ResponseBodyDto<CampusDto>> response = null;
        try {
            //chạy controller
            response = campusController.addNewCampus(campusCreateRequestBody);
        }catch (Exception ex){
            //kiểm tra đúng exception khi bị throw bởi service
            assertTrue(ex instanceof Exception);
            //kiểm tra message trả về từ service
            assertEquals(ex.getMessage(), e.getMessage());
            //exception nên bị null thì ktra null
            assertNull(response);
        }

    }

    @Test
    void updateCampus() {

        ResponseBodyDto responseBodyDto = new ResponseBodyDto<>(campusDto, ResponseCodeEnum.R_200, "UPDATE");
        //Mặc định giá trị trả về của service
        when(campusService.updateCampusInfo(Mockito.any(),Mockito.any())).thenReturn(campusDto);
        //gọi đến controller cần test với những input đầu vào đúng yêu cầu
        ResponseEntity<ResponseBodyDto<CampusDto>> response = campusController.updateCampus("1",campusCreateRequestBody);

        //thực hiện kiểm tra các response trả về
        //kiểm tra null
        assertNotNull(response);
        //kiểm tra status trả về
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        //kiểm tra model trả về của service
        assertEquals(response.getBody().getItem(), campusDto);
        //kiểm tra respone tạo được từ controller trả về phía font-end
        assertEquals(response.getBody(), responseBodyDto);
    }

    @Test
    void deleteCampus() {

        ResponseBodyDto responseBodyDto = new ResponseBodyDto<>("1", ResponseCodeEnum.R_200, "DELETE");
        //Mặc định giá trị trả về của service
        when(campusService.deleteCampus(Mockito.any())).thenReturn("1");

        //gọi đến controller cần test với những input đầu vào đúng yêu cầu
        ResponseEntity<ResponseBodyDto<CampusDto>> response = campusController.deleteCampus("1");
        //thực hiện kiểm tra các response trả về
        //kiểm tra null
        assertNotNull(response);
        //kiểm tra status trả về
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        //kiểm tra model trả về của service
//        assertEquals(response.getBody().getItem(), campusDto);
        //kiểm tra respone tạo được từ controller trả về phía font-end
        assertEquals(response.getBody(), responseBodyDto);
    }

    @Test
    void filterCampus() {
        List<CampusDto> listCampus = new ArrayList<>();
        listCampus.add(campusDto);

        ResponseBodyDto response = new ResponseBodyDto<>(listCampus, ResponseCodeEnum.R_200, "OK", listCampus.size());
        when(campusService.filterCampuses(Mockito.any())).thenReturn(listCampus);

        ResponseEntity<ResponseBodyDto<CampusDto>> responseEntity = campusController.filterCampus("FPT Hòa Lạc Campus");

        assertNotNull(response);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
//        assertEquals(responseEntity.getBody().getSize(), listCampus.size());
        assertEquals(responseEntity.getBody(), response);
    }

    @Test
    void getCampuses() {
        List<CampusDto> listCampus = new ArrayList<>();
        listCampus.add(campusDto);

        ResponseBodyDto response = new ResponseBodyDto<>(listCampus, ResponseCodeEnum.R_200, "OK", listCampus.size());
        when(campusService.getAllCampus()).thenReturn(listCampus);

        ResponseEntity<ResponseBodyDto<CampusDto>> responseEntity = campusController.getCampuses();

        assertNotNull(response);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

        assertEquals(responseEntity.getBody(), response);
    }
}