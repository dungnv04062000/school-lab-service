package com.schoollab.service.impl;

import com.schoollab.common.constants.S3Constant;
import com.schoollab.common.error.NotFoundException;
import com.schoollab.common.util.GoogleIdTokenUtil;
import com.schoollab.controller.request.EmailRegisterRequestBody;
import com.schoollab.controller.request.UserRegisterRequestBody;
import com.schoollab.controller.request.UserUpdateRequestBody;
import com.schoollab.dto.CampusDto;
import com.schoollab.dto.UserDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.model.*;
import com.schoollab.modelTest.SetUpModelTest;
import com.schoollab.repository.*;
import com.schoollab.service.S3Service;
import com.schoollab.service.SendMailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;
    @Mock
    AccountRepository accountRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    UserRoleRepository userRoleRepository;
    @Mock
    AddressRepository addressRepository;
    @Mock
    WardRepository wardRepository;
    @Mock
    DistrictRepository districtRepository;
    @Mock
    CityRepository cityRepository;
    @Mock
    CampusRepository campusRepository;
    @Mock
    GenericMapper genericMapper;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    SendMailService sendMailService;
    @Mock
    S3Service s3Service;
    @Mock
    MultipartFile multipartFile;
    EmailRegisterRequestBody emailReq;
    UserRegisterRequestBody userReq;
    UserUpdateRequestBody userUpdateReq;
    User user;
    UserDto userDto;
    UserRole userRole;
    Role role;
    City city;
    Ward ward;
    Address address;
    District district;
    Account account;
    Campus campus;
    List<UserRole> listRole;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        account = SetUpModelTest.setUpAccount();
        emailReq = SetUpModelTest.emailRegisterRq();
        userReq = SetUpModelTest.userRegisterReq();
        userUpdateReq = SetUpModelTest.userUpdateReq();
        user = SetUpModelTest.setUpUser();
        userDto = SetUpModelTest.setUpUserDto();
        role = SetUpModelTest.setUpRole(3, "teacher");
        userRole = SetUpModelTest.setUpUserRole(role);
        city = SetUpModelTest.setUpCity();
        ward = SetUpModelTest.setUpWard();
        address = SetUpModelTest.setUpAddress();
        district = SetUpModelTest.setUpDistrict();
        campus = SetUpModelTest.setUpCampus();
        listRole = new ArrayList<>();
        listRole.add(userRole);
    }

//    @Test
//    void registerByGmail_failToken() {
//        when(GoogleIdTokenUtil.verifingToken(Mockito.anyString())).thenReturn(null);
//        UserDto response = null;
//        try {
//            response = userService.registerByGmail(emailReq);
//        }catch (Exception e){
//            assertTrue(e instanceof IllegalArgumentException);
//            assertNull(response);
//        }
//    }

    private void mockReturn(){
        when(accountRepository.findByUsername(Mockito.any())).thenReturn(null);
        when(userRepository.findByEmail(Mockito.any())).thenReturn(null);
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        when(cityRepository.save(Mockito.any())).thenReturn(city);
        when(districtRepository.save(Mockito.any())).thenReturn(district);
        when(wardRepository.save(Mockito.any())).thenReturn(ward);
        when(addressRepository.save(Mockito.any())).thenReturn(address);
        when(userRepository.save(Mockito.any())).thenReturn(user);
        when(roleRepository.findByName(Mockito.any())).thenReturn(role);

        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(UserDto.class))).thenReturn(userDto);
    }

    private void mockReturnNull(){
        when(cityRepository.findByCode(Mockito.anyString())).thenReturn(null);
        when(districtRepository.findByCode(Mockito.anyString())).thenReturn(null);
        when(wardRepository.findByCode(Mockito.any())).thenReturn(null);
    }

    private void mockReturnNotNull(){
        when(cityRepository.findByCode(Mockito.anyString())).thenReturn(city);
        when(districtRepository.findByCode(Mockito.anyString())).thenReturn(district);
        when(wardRepository.findByCode(Mockito.any())).thenReturn(ward);
    }

    private void mockGetUserReturn(){
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(UserDto.class))).thenReturn(userDto);
        when(userRoleRepository.findAllByUserId(Mockito.any())).thenReturn(listRole);
        when(roleRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        when(addressRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(address));
        when(wardRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(ward));
        when(districtRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(district));
        when(cityRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(city));
        when(campusRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(campus));
    }

    @Test
    void register_existsAccount() {
        when(accountRepository.findByUsername(Mockito.any())).thenReturn(account);
        UserDto response = null;
        try {
            response = userService.register(userReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Tài khoản này đã tồn tại");
            assertNull(response);
        }
    }

    @Test
    void register_existsEmail() {
        when(accountRepository.findByUsername(Mockito.any())).thenReturn(null);
        when(userRepository.findByEmail(Mockito.any())).thenReturn(user);
        UserDto response = null;
        try {
            response = userService.register(userReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Email này đã tồn tại");
            assertNull(response);
        }
    }

    @Test
    void register_existsUser() {
        when(accountRepository.findByUsername(Mockito.any())).thenReturn(null);
        when(userRepository.findByEmail(Mockito.any())).thenReturn(null);
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        UserDto response = null;
        try {
            response = userService.register(userReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Mã số này đã tồn tại");
            assertNull(response);
        }
    }

    @Test
    void register_success(){
        mockReturn();
        mockReturnNull();
        UserDto response = userService.register(userReq);

        assertNotNull(response);
        assertEquals(userDto, response);
    }

    @Test
    void register_addressNotNull_success(){
        mockReturn();
        mockReturnNotNull();
        UserDto response = userService.register(userReq);

        assertNotNull(response);
        assertEquals(userDto, response);
    }

    @Test
    void register_cityNull_success(){
        mockReturn();
        mockReturnNotNull();
        when(cityRepository.findByCode(Mockito.anyString())).thenReturn(null);
        UserDto response = userService.register(userReq);

        assertNotNull(response);
        assertEquals(userDto, response);
    }

    @Test
    void register_districtNull_success(){
        mockReturn();
        mockReturnNotNull();
        when(districtRepository.findByCode(Mockito.anyString())).thenReturn(null);
        UserDto response = userService.register(userReq);

        assertNotNull(response);
        assertEquals(userDto, response);
    }

    @Test
    void register_wardNull_success(){
        mockReturn();
        mockReturnNotNull();
        when(wardRepository.findByCode(Mockito.anyString())).thenReturn(null);
        UserDto response = userService.register(userReq);

        assertNotNull(response);
        assertEquals(userDto, response);
    }
    @Test
    void updateUser_notFoundUser() {
        Exception ex = new NotFoundException("Không tìm thấy thông tin người dùng này");
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        UserDto response = null;
        try {
            response = userService.updateUser("HE140705", userUpdateReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), ex.getMessage());
            assertEquals(null, response);
        }
    }

    @Test
    void updateUser_addressNotFoundUser() {
        Exception ex = new NotFoundException("Không có dữ liệu địa chỉ của người dùng.");
        mockReturn();
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(addressRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        UserDto response = null;
        try {
            response = userService.updateUser("HE140705", userUpdateReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), ex.getMessage());
            assertEquals(null, response);
        }
    }

    @Test
    void updateUser_success(){
        mockReturn();
        mockReturnNotNull();
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(addressRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(address));

        UserDto response = userService.updateUser("HE140705", userUpdateReq);
        assertEquals(response, userDto);
        assertNotNull(response);
    }

    @Test
    void updateUser_addressNull_success(){
        mockReturn();
        mockReturnNull();
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(addressRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(address));

        UserDto response = userService.updateUser("HE140705", userUpdateReq);
        assertEquals(response, userDto);
        assertNotNull(response);
    }

    @Test
    void updateUser_cityNull_success(){
        mockReturn();
        mockReturnNotNull();
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(addressRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(address));
        when(cityRepository.findByCode(Mockito.any())).thenReturn(city);

        UserDto response = userService.updateUser("HE140705", userUpdateReq);
        assertEquals(response, userDto);
        assertNotNull(response);
    }

    @Test
    void updateUser_districtNull_success(){
        mockReturn();
        mockReturnNotNull();
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(addressRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(address));
        when(districtRepository.findByCode(Mockito.any())).thenReturn(district);

        UserDto response = userService.updateUser("HE140705", userUpdateReq);
        assertEquals(response, userDto);
        assertNotNull(response);
    }

    @Test
    void updateUser_wardNull_success(){
        mockReturn();
        mockReturnNotNull();
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(addressRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(address));
        when(wardRepository.findByCode(Mockito.any())).thenReturn(ward);

        UserDto response = userService.updateUser("HE140705", userUpdateReq);
        assertEquals(response, userDto);
        assertNotNull(response);
    }

    @Test
    void changeAvatar_notFoundUser() {
        Exception ex = new NotFoundException("Không tìm thấy người dùng này!");
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        UserDto response = null;
        try {
            response = userService.changeAvatar("HE140705", multipartFile);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), ex.getMessage());
            assertEquals(null, response);
        }
    }

    @Test
    void changeAvatar_success() {
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(s3Service.saveFile(Mockito.eq(multipartFile), Mockito.eq(S3Constant.S3_FOLDER_AVATAR), Mockito.any())).thenReturn("http/change/avatar");
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(UserDto.class))).thenReturn(userDto);
        UserDto response = userService.changeAvatar("HE140705", multipartFile);
        assertNotNull(response);
        assertEquals(userDto, response);
    }

    @Test
    void getUser_notFoundUser() {
        Exception ex = new NotFoundException("Không tìm thấy người dùng này");
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        UserDto response = null;
        try {
            response = userService.getUser("HE140705");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), ex.getMessage());
            assertEquals(null, response);
        }
    }

    @Test
    void getUser_addressNull_campusNull_success() {
        mockGetUserReturn();
        when(addressRepository.findById(Mockito.any())).thenReturn(null);
        when(campusRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        when(addressRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        UserDto response = userService.getUser("HE140705");
        assertNotNull(response);
        assertEquals(userDto, response);
    }

    @Test
    void getUser_wardNull_campusNull_success() {
        mockGetUserReturn();
        when(wardRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        when(campusRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        UserDto response = userService.getUser("HE140705");
        assertNotNull(response);
        assertEquals(userDto, response);
    }

    @Test
    void getUser_districtNull_campusNull_success() {
        mockGetUserReturn();
        when(districtRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        when(campusRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        UserDto response = userService.getUser("HE140705");
        assertNotNull(response);
        assertEquals(userDto, response);
    }

    @Test
    void getUser_cityNull_campusNull_success() {
        mockGetUserReturn();
        when(campusRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        UserDto response = userService.getUser("HE140705");
        assertNotNull(response);
        assertEquals(userDto, response);
    }

    @Test
    void getUser_campusNotNull_success() {
        mockGetUserReturn();
        UserDto response = userService.getUser("HE140705");
        assertNotNull(response);
        assertEquals(userDto, response);
    }

    @Test
    void getUser_roleNotNull_success() {
        mockGetUserReturn();
        when(roleRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(role));
        UserDto response = userService.getUser("HE140705");
        assertNotNull(response);
        assertEquals(userDto, response);
    }
    
    @Test
    void setDefaultAvatar_notFoundUser(){
        when(userRepository.findById(Mockito.eq("HE140705"))).thenReturn(Optional.ofNullable(null));
        UserDto response = null;
        try {
            response = userService.setDefaultAvatar("HE140705");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy người dùng này!");
            assertNull(response);
        }
    }

    @Test
    void setDefaultAvatar_success(){
        when(userRepository.findById(Mockito.eq("HE140705"))).thenReturn(Optional.ofNullable(user));
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(UserDto.class))).thenReturn(userDto);
        UserDto response = userService.setDefaultAvatar("HE140705");
        assertNotNull(response);
        assertEquals(userDto, response);
    }

    @Test
    void getUserByCampusId(){
        List<User> list = new ArrayList<>();
        list.add(user);
        List<UserDto> reponseList = new ArrayList<>();
        reponseList.add(userDto);
        when(userRepository.findAllByCampusId(Mockito.eq("THPT FPT - HOA LAC"))).thenReturn(list);
        when(genericMapper.mapToListOfTypeNotNullProperty(Mockito.any(), Mockito.eq(UserDto.class))).thenReturn(reponseList);
        List<UserDto> response = userService.getUserByCampusId("THPT FPT - HOA LAC");
        assertNotNull(response);
        assertEquals(response.size(), reponseList.size());
        assertEquals(reponseList, response);
    }
}