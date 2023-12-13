package com.schoollab.service.impl;

import com.schoollab.common.constants.Constants;
import com.schoollab.common.error.BadRequestException;
import com.schoollab.common.error.NotFoundException;
import com.schoollab.common.error.UnKnownException;
import com.schoollab.controller.request.CampusCreateRequestBody;
import com.schoollab.dto.CampusDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.model.*;
import com.schoollab.modelTest.SetUpModelTest;
import com.schoollab.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CampusServiceImplTest {
    @InjectMocks
    CampusServiceImpl campusService;
    @Mock
    CampusRepository campusRepository;
    @Mock
    AddressRepository addressRepository;
    @Mock
    WardRepository wardRepository;
    @Mock
    DistrictRepository districtRepository;
    @Mock
    CityRepository cityRepository;
    @Mock
    GenericMapper genericMapper;
    @Mock
    UserRoleRepository userRoleRepository;
    @Mock
    UserRepository userRepository;
    CampusCreateRequestBody campusCreateRequestBody;
    Campus campus;
    CampusDto campusDto;
    City city;
    Ward ward;
    Address address;
    District district;
    UserRole userRole;
    Role role;
    User user;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        campusCreateRequestBody = SetUpModelTest.campusReq();
        campus = SetUpModelTest.setUpCampus();
        campusDto = SetUpModelTest.setUpCampusDto();
        address = SetUpModelTest.setUpAddress();
        ward = SetUpModelTest.setUpWard();
        district = SetUpModelTest.setUpDistrict();
        city = SetUpModelTest.setUpCity();
        role = SetUpModelTest.setUpRole(1, "admin");
        userRole = SetUpModelTest.setUpUserRole(role);
        user = SetUpModelTest.setUpUser();
    }

    private void mockReturn(){
        when(campusRepository.findCampusByName(Mockito.anyString())).thenReturn(null);
        when(cityRepository.findByCode(Mockito.anyString())).thenReturn(city);
        when(districtRepository.findByCode(Mockito.anyString())).thenReturn(district);
        when(wardRepository.findByCode(Mockito.anyString())).thenReturn(ward);
        when(addressRepository.save(Mockito.any())).thenReturn(address);
        when(campusRepository.save(Mockito.any())).thenReturn(campus);
        when(genericMapper.mapToTypeNotNullProperty(Mockito.eq(campus), Mockito.eq(CampusDto.class))).thenReturn(campusDto);
    }

    private void mockReturnNull(){
        when(campusRepository.findCampusByName(Mockito.anyString())).thenReturn(null);
        when(cityRepository.findByCode(Mockito.anyString())).thenReturn(null);
        when(cityRepository.save(Mockito.any())).thenReturn(city);
        when(districtRepository.findByCode(Mockito.anyString())).thenReturn(null);
        when(districtRepository.save(Mockito.any())).thenReturn(district);
        when(wardRepository.findByCode(Mockito.any())).thenReturn(null);
        when(wardRepository.save(Mockito.any())).thenReturn(ward);
        when(addressRepository.save(Mockito.any())).thenReturn(address);
        when(campusRepository.save(Mockito.any())).thenReturn(campus);
        when(genericMapper.mapToTypeNotNullProperty(Mockito.eq(campus), Mockito.eq(CampusDto.class))).thenReturn(campusDto);
    }

    @Test
    void addNewCampus_nameExists() {
        CampusDto response = null;
        Exception ex = new BadRequestException("Tên campus đã tồn tại.");
        when(campusRepository.findCorrectCampusByName(Mockito.eq("FPT Hòa Lạc Campus"))).thenThrow(ex);
        try {
            response = campusService.addNewCampus(campusCreateRequestBody);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void addNewCampus_exception() {
        Exception ex = new UnKnownException("Có lỗi trong quá trình xử lý");
        when(campusRepository.findCampusByName(Mockito.anyString())).thenReturn(null);
        CampusDto response = null;
        try {
            when(cityRepository.findByCode(Mockito.anyString())).thenThrow(ex);
            response = campusService.addNewCampus(campusCreateRequestBody);
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void addNewCampus_success(){
        mockReturn();

        CampusDto response = campusService.addNewCampus(campusCreateRequestBody);

        assertNotNull(response);
        assertEquals(response, campusDto);
    }

    @Test
    void addNewCampus_mappingFail(){
        mockReturn();
        when(genericMapper.mapToTypeNotNullProperty(Mockito.eq(campus), Mockito.eq(CampusDto.class))).thenReturn(null);

        CampusDto response = campusService.addNewCampus(campusCreateRequestBody);

        assertNull(response);
    }

    @Test
    void addNewCampus_saveFail(){
        Exception ex = new UnKnownException("Có lỗi trong quá trình xử lý");
        mockReturn();
        CampusDto response = null;
        try {
            doThrow(ex).when(addressRepository.save(Mockito.any()));
            response = campusService.addNewCampus(campusCreateRequestBody);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void addNewCampus_allNull(){
        mockReturnNull();

        CampusDto response = campusService.addNewCampus(campusCreateRequestBody);

        assertNotNull(response);
        assertEquals(response, campusDto);
    }

    @Test
    void updateCampusInfo_notFoundCampus() {
        Exception ex = new NotFoundException("Cập nhật thất bại do không tìm thấy thông tin!");
        when(campusRepository.findById(Mockito.any())).thenReturn(null);
        CampusDto response = null;
        try{
            response = campusService.updateCampusInfo("123456", campusCreateRequestBody);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void updateCampusInfo_exception() {
        Exception ex = new UnKnownException("Có lỗi trong quá trình xử lý");
        when(campusRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(campus));
        CampusDto response = null;
        try{
            doThrow(ex).when(addressRepository.findById(Mockito.any()));
            response = campusService.updateCampusInfo("123456", campusCreateRequestBody);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void updateCampusInfo_success(){
        mockReturn();
        when(campusRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(campus));
        when(addressRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(address));

        CampusDto response = campusService.updateCampusInfo("123456", campusCreateRequestBody);

        assertNotNull(response);
        assertEquals(response, campusDto);
    }

    @Test
    void updateCampusInfo_addressNotNull_success(){
        mockReturnNull();
        when(campusRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(campus));
        when(addressRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(address));

        CampusDto response = campusService.updateCampusInfo("123456", campusCreateRequestBody);

        assertNotNull(response);
        assertEquals(response, campusDto);
    }

    @Test
    void updateCampusInfo_NotFound(){
        when(campusRepository.findById(Mockito.eq("123456"))).thenReturn(Optional.ofNullable(null));
        CampusDto response = null;
        try {
            response = campusService.updateCampusInfo("123456", campusCreateRequestBody);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Cập nhật thất bại do không tìm thấy thông tin");
            assertNull(response);
        }
    }

    @Test
    void updateCampusInfo_NotEqualsCampus(){
        campusCreateRequestBody.setAdminId("HE140705");
        when(campusRepository.findById(Mockito.eq("1234"))).thenReturn(Optional.ofNullable(campus));
        when(userRepository.findById(Mockito.eq("HE140705"))).thenReturn(Optional.ofNullable(null));
        CampusDto response = null;
        try {
            response = campusService.updateCampusInfo("1234", campusCreateRequestBody);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy người dùng này");
            assertNull(response);
        }
    }

    @Test
    void updateCampusInfo_NotFoundUser(){
        campusCreateRequestBody.setAdminId("HE140705");
        when(campusRepository.findById(Mockito.eq("123456"))).thenReturn(Optional.ofNullable(campus));
        when(userRepository.findById(Mockito.eq("HE140705"))).thenReturn(Optional.ofNullable(user));
        CampusDto response = null;
        try {
            response = campusService.updateCampusInfo("123456", campusCreateRequestBody);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Người dùng không thuộc campus này");
            assertNull(response);
        }
    }

    @Test
    void updateCampusInfo_UserRoleNull(){
        mockReturnNull();
        campusCreateRequestBody.setAdminId("HE140705");
        when(campusRepository.findById(Mockito.eq("FPT"))).thenReturn(Optional.ofNullable(campus));
        when(userRepository.findById(Mockito.eq("HE140705"))).thenReturn(Optional.ofNullable(user));
        when(userRoleRepository.findByUserIdAndAndRoleId(Mockito.any(), Mockito.eq(Constants.ROLE_SCHOOL_ADMIN_ID))).thenReturn(null);
        CampusDto response = campusService.updateCampusInfo("FPT", campusCreateRequestBody);
        assertNotNull(response);
        assertEquals(response, campusDto);
    }
    @Test
    void updateCampusInfo_addressNull_success(){
        mockReturnNull();
        when(addressRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        campusCreateRequestBody.setAdminId("HE140705");
        when(campusRepository.findById(Mockito.eq("FPT"))).thenReturn(Optional.ofNullable(campus));
        when(userRepository.findById(Mockito.eq("HE140705"))).thenReturn(Optional.ofNullable(user));
        when(userRoleRepository.findByUserIdAndAndRoleId(Mockito.any(), Mockito.eq(Constants.ROLE_SCHOOL_ADMIN_ID))).thenReturn(userRole);

        CampusDto response = campusService.updateCampusInfo("FPT", campusCreateRequestBody);

        assertNotNull(response);
        assertEquals(response, campusDto);
    }

    @Test
    void deleteCampus_success() {
        when(campusRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(campus));
        when(userRoleRepository.findByUserIdAndAndRoleId(Mockito.any(), Mockito.eq(Constants.ROLE_SCHOOL_ADMIN_ID))).thenReturn(userRole);

        String response = campusService.deleteCampus("123456");

        assertNotNull(response);
        assertEquals(response, "Xóa thành công");
    }

    @Test
    void filterCampuses_notFound() {
        when(campusRepository.findCampusByName(Mockito.any())).thenReturn(null);

        List<CampusDto> response = campusService.filterCampuses("FPT");

        assertNull(response);
    }

    @Test
    void filterCampuses_exception(){
        List<CampusDto> response = null;
        try {
            when(campusRepository.findCampusByName(Mockito.any())).thenThrow(Exception.class);
            response = campusService.filterCampuses("FPT");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void filterCampuses_success() {
        List<Campus> list = new ArrayList<>();
        list.add(campus);
        when(campusRepository.findCampusByName(Mockito.any())).thenReturn(list);
        when(addressRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(address));
        when(wardRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(ward));
        when(districtRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(district));
        when(cityRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(city));
        List<CampusDto> listOut = new ArrayList<>();
        listOut.add(campusDto);
        when(genericMapper.mapToListOfTypeNotNullProperty(Mockito.any(), Mockito.eq(CampusDto.class))).thenReturn(listOut);

        List<CampusDto> response = campusService.filterCampuses("FPT");

        assertNotNull(response);
        assertEquals(listOut, response);
    }

    @Test
    void getAllCampus() {
        List<Campus> list = new ArrayList<>();
        list.add(campus);
        when(campusRepository.findAll()).thenReturn(list);

        List<CampusDto> listOut = new ArrayList<>();
        listOut.add(campusDto);
        when(genericMapper.mapToListOfTypeNotNullProperty(Mockito.any(), Mockito.eq(CampusDto.class))).thenReturn(listOut);

        List<CampusDto> response = campusService.getAllCampus();

        assertNotNull(response);
        assertEquals(response, listOut);
    }

    @Test
    void getAllCampus_exception(){
        List<CampusDto> response = null;
        try {
            when(campusRepository.findAll()).thenThrow(Exception.class);
            response = campusService.getAllCampus();
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void getOneCampus_NotFound(){
        when(campusRepository.findById(Mockito.eq("FPT"))).thenReturn(Optional.ofNullable(null));
        CampusDto response = null;
        try {
            response = campusService.getOneCampus("FPT");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
            assertEquals(e.getMessage(), "Không tìm thấy cơ sở này");
        }
    }

    @Test
    void getOneCampus_AddressNull(){
        when(campusRepository.findById(Mockito.eq("FPT"))).thenReturn(Optional.ofNullable(campus));
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(CampusDto.class))).thenReturn(campusDto);
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(addressRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        CampusDto response  = campusService.getOneCampus("FPT");
        assertEquals(response, campusDto);
        assertNotNull(response);
    }

    @Test
    void getOneCampus_WardNull(){
        when(campusRepository.findById(Mockito.eq("FPT"))).thenReturn(Optional.ofNullable(campus));
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(CampusDto.class))).thenReturn(campusDto);
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(addressRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(address));
        when(wardRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        CampusDto response  = campusService.getOneCampus("FPT");
        assertEquals(response, campusDto);
        assertNotNull(response);
    }

    @Test
    void getOneCampus_DistrictNull(){
        when(campusRepository.findById(Mockito.eq("FPT"))).thenReturn(Optional.ofNullable(campus));
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(CampusDto.class))).thenReturn(campusDto);
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(addressRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(address));
        when(wardRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(ward));
        when(districtRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        CampusDto response  = campusService.getOneCampus("FPT");
        assertEquals(response, campusDto);
        assertNotNull(response);
    }

    @Test
    void getOneCampus_CityNull(){
        when(campusRepository.findById(Mockito.eq("FPT"))).thenReturn(Optional.ofNullable(campus));
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(CampusDto.class))).thenReturn(campusDto);
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(addressRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(address));
        when(wardRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(ward));
        when(districtRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(district));
        when(cityRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        CampusDto response  = campusService.getOneCampus("FPT");
        assertEquals(response, campusDto);
        assertNotNull(response);
    }

    @Test
    void getOneCampus_success(){
        when(campusRepository.findById(Mockito.eq("FPT"))).thenReturn(Optional.ofNullable(campus));
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(CampusDto.class))).thenReturn(campusDto);
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(addressRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(address));
        when(wardRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(ward));
        when(districtRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(district));
        when(cityRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(city));
        CampusDto response  = campusService.getOneCampus("FPT");
        assertEquals(response, campusDto);
        assertNotNull(response);
    }
}