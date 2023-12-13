package com.schoollab.service.impl;

import com.schoollab.common.constants.Constants;
import com.schoollab.common.error.BadRequestException;
import com.schoollab.common.error.NotFoundException;
import com.schoollab.common.error.UnKnownException;
import com.schoollab.controller.request.CampusCreateRequestBody;
import com.schoollab.dto.CampusDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.model.*;
import com.schoollab.repository.*;
import com.schoollab.service.CampusService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CampusServiceImpl implements CampusService {

    @Autowired
    CampusRepository campusRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    WardRepository wardRepository;

    @Autowired
    DistrictRepository districtRepository;

    @Autowired
    CityRepository cityRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    GenericMapper genericMapper;

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public CampusDto addNewCampus(CampusCreateRequestBody requestBody) {
        Campus save;
        Campus existsCampus = campusRepository.findCorrectCampusByName(requestBody.getName());
        if(!Objects.isNull(existsCampus)){
            throw new BadRequestException("Tên campus này đã tồn tại");
        }
        try {
            City city = cityRepository.findByCode(requestBody.getCityCode());
            if(city == null){
                City newCity = new City()
                        .setCode(requestBody.getCityCode())
                        .setName(requestBody.getCity());
                city = cityRepository.save(newCity);
            }
            District district = districtRepository.findByCode(requestBody.getDistrictCode());
            if(district == null){
                District newDistrict = new District()
                        .setCode(requestBody.getDistrictCode())
                        .setName(requestBody.getDistrict())
                        .setCityId(city.getId());
                district = districtRepository.save(newDistrict);
            }
            Ward ward = wardRepository.findByCode(requestBody.getWardCode());
            if(ward == null){
                Ward newWard = new Ward()
                        .setCode(requestBody.getWardCode())
                        .setName(requestBody.getWard())
                        .setDistrictId(district.getId());
                ward = wardRepository.save(newWard);
            }

            Address address = new Address()
                    .setStreet(requestBody.getStreet())
                    .setWardId(ward.getId())
                    .setCreateAt(Instant.now());
            addressRepository.save(address);

            Campus campus = new Campus()
                    .setName(requestBody.getName())
                    .setAddressId(address.getId())
                    .setCreateAt(Instant.now());

            save = campusRepository.save(campus);
        }catch (Exception e){
            e.printStackTrace();
            throw new UnKnownException("Có lỗi trong quá trình xử lý");
        }
        return genericMapper.mapToTypeNotNullProperty(save, CampusDto.class);
    }

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public CampusDto updateCampusInfo(String campusId, CampusCreateRequestBody requestBody) {
            Optional<Campus> optionalCampus = campusRepository.findById(campusId);
            if(!optionalCampus.isPresent()){
                throw new NotFoundException("Cập nhật thất bại do không tìm thấy thông tin");
            }
            if(requestBody.getAdminId() != null){
                Optional<User> optionalUser = userRepository.findById(requestBody.getAdminId());
                if(!optionalUser.isPresent()){
                    throw new NotFoundException("Không tìm thấy người dùng này");
                }

                if(!optionalUser.get().getCampusId().equals(campusId)){
                    throw new RuntimeException("Người dùng không thuộc campus này");
                }

                //thêm role school admin
                UserRole userRole = userRoleRepository.findByUserIdAndAndRoleId(requestBody.getAdminId(), Constants.ROLE_SCHOOL_ADMIN_ID);
                if(userRole == null){
                    UserRole newRole = new UserRole()
                            .setRoleId(Constants.ROLE_SCHOOL_ADMIN_ID)
                            .setUserId(requestBody.getAdminId());
                    userRoleRepository.save(newRole);
                }
            }
        Campus campus = optionalCampus.get();
        //update info to old campus
        campus.setName(requestBody.getName());

        campus.setAdminId(requestBody.getAdminId());
        campus.setUpdateAt(Instant.now());

            Optional<Address> oldAddress = addressRepository.findById(optionalCampus.get().getAddressId());
            oldAddress.ifPresent(address -> addressRepository.delete(address));
            City city = cityRepository.findByCode(requestBody.getCityCode());
            if(city == null){
                City newCity = new City()
                        .setCode(requestBody.getCityCode())
                        .setName(requestBody.getCity());
                city = cityRepository.save(newCity);
            }
            District district = districtRepository.findByCode(requestBody.getDistrictCode());
            if(district == null){
                District newDistrict = new District()
                        .setCode(requestBody.getDistrictCode())
                        .setName(requestBody.getDistrict())
                        .setCityId(city.getId());
                district = districtRepository.save(newDistrict);
            }
            Ward ward = wardRepository.findByCode(requestBody.getWardCode());
            if(ward == null){
                Ward newWard = new Ward()
                        .setCode(requestBody.getWardCode())
                        .setName(requestBody.getWard())
                        .setDistrictId(district.getId());
                ward = wardRepository.save(newWard);
            }

            Address address = new Address()
                    .setStreet(requestBody.getStreet())
                    .setWardId(ward.getId())
                    .setCreateAt(Instant.now());
            addressRepository.save(address);

            campus.setAddressId(address.getId());

            Campus updateCampus = campusRepository.save(campus);

        return genericMapper.mapToTypeNotNullProperty(updateCampus, CampusDto.class);
    }

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public String deleteCampus(String campusId) {
        Optional<Campus> campus = campusRepository.findById(campusId);
        campus.ifPresent(value -> {
            //xóa role school admin
            UserRole userRole = userRoleRepository.findByUserIdAndAndRoleId(value.getAdminId(), Constants.ROLE_SCHOOL_ADMIN_ID);
            if(userRole != null){
                userRoleRepository.delete(userRole);
            }
            campusRepository.delete(value);
        });

        return "Xóa thành công";
    }

    @Override
    public List<CampusDto> filterCampuses(String name) {
        List<Campus> campuses;
        if(StringUtils.isNotBlank(name)){
            campuses = campusRepository.findCampusByName(name.trim());
        } else {
            campuses = campusRepository.findAll();
        }
        if(campuses == null){
            return null;
        }
        List<CampusDto> listCampusDto = new ArrayList<>();
        for (Campus c: campuses) {
            CampusDto campusDto = new CampusDto();
            BeanUtils.copyProperties(c, campusDto);
            if(campusDto.getAdminId() != null){
                Optional<User> optionalUser = userRepository.findById(campusDto.getAdminId());
                optionalUser.ifPresent(user -> campusDto.setAdminName(user.getFirstName() + " " + user.getLastName()));
            }

            Optional<Address> address = addressRepository.findById(c.getAddressId());
            if(address.isPresent()){
                campusDto.setStreet(address.get().getStreet());
                Optional<Ward> optWard = wardRepository.findById(address.get().getWardId());
                if(optWard.isPresent()){
                    Ward ward = optWard.get();
                    campusDto.setWard(ward.getName());
                    campusDto.setWardCode(ward.getCode());
                    Optional<District> optDistrict = districtRepository.findById(ward.getDistrictId());
                    if(optDistrict.isPresent()){
                        District district = optDistrict.get();
                        campusDto.setDistrictCode(district.getCode());
                        campusDto.setDistrict(district.getName());
                        Optional<City> optCity = cityRepository.findById(district.getCityId());
                        if(optCity.isPresent()){
                            City city = optCity.get();
                            campusDto.setCityCode(city.getCode());
                            campusDto.setCity(city.getName());
                        }
                    }
                }
            }
            listCampusDto.add(campusDto);
        }
        return listCampusDto;
    }

    @Override
    public List<CampusDto> getAllCampus() {
        List<Campus> campuses = campusRepository.findAll();
        return genericMapper.mapToListOfTypeNotNullProperty(campuses, CampusDto.class);
    }

    @Override
    public CampusDto getOneCampus(String campusId) {
        Optional<Campus> optionalCampus = campusRepository.findById(campusId);
        if(!optionalCampus.isPresent()){
            throw new NotFoundException("Không tìm thấy cơ sở này");
        }
        CampusDto campusDto = genericMapper.mapToTypeNotNullProperty(optionalCampus.get(), CampusDto.class);
        if(campusDto.getAdminId() != null){
            Optional<User> optionalUser = userRepository.findById(campusDto.getAdminId());
            optionalUser.ifPresent(user -> campusDto.setAdminName(user.getFirstName() + " " + user.getLastName()));
        }

        Optional<Address> address = addressRepository.findById(optionalCampus.get().getAddressId());
        if(address.isPresent()){
            campusDto.setStreet(address.get().getStreet());
            Optional<Ward> optWard = wardRepository.findById(address.get().getWardId());
            if(optWard.isPresent()){
                Ward ward = optWard.get();
                campusDto.setWard(ward.getName());
                campusDto.setWardCode(ward.getCode());
                Optional<District> optDistrict = districtRepository.findById(ward.getDistrictId());
                if(optDistrict.isPresent()){
                    District district = optDistrict.get();
                    campusDto.setDistrictCode(district.getCode());
                    campusDto.setDistrict(district.getName());
                    Optional<City> optCity = cityRepository.findById(district.getCityId());
                    if(optCity.isPresent()){
                        City city = optCity.get();
                        campusDto.setCityCode(city.getCode());
                        campusDto.setCity(city.getName());
                    }
                }
            }
        }
        return campusDto;
    }
}
