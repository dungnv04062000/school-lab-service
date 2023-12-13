package com.schoollab.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.schoollab.common.constants.S3Constant;
import com.schoollab.common.error.BadRequestException;
import com.schoollab.common.error.NotFoundException;
import com.schoollab.common.util.GoogleIdTokenUtil;
import com.schoollab.controller.request.EmailRegisterRequestBody;
import com.schoollab.controller.request.SendMailRequestBody;
import com.schoollab.controller.request.UserRegisterRequestBody;
import com.schoollab.controller.request.UserUpdateRequestBody;
import com.schoollab.dto.SendMailResponseDto;
import com.schoollab.dto.UserDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.model.*;
import com.schoollab.repository.*;
import com.schoollab.service.S3Service;
import com.schoollab.service.SendMailService;
import com.schoollab.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    WardRepository wardRepository;

    @Autowired
    DistrictRepository districtRepository;

    @Autowired
    CityRepository cityRepository;

    @Autowired
    CampusRepository campusRepository;

    @Autowired
    GenericMapper genericMapper;
    @Autowired
    SendMailService sendMailService;
    @Autowired
    S3Service s3Service;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public UserDto registerByGmail(EmailRegisterRequestBody requestBody) {
        log.info("Register by email");
        GoogleIdToken.Payload payload = GoogleIdTokenUtil.verifingToken(requestBody.getIdToken());
        if (payload == null) {
            throw new BadRequestException("IdToken không hợp lệ");
        }
        String userId = payload.getSubject();
        String email = payload.getEmail();
        String username = getUsernameFromEmail(email);

        if (userRepository.findById(userId).isPresent()
                || userRepository.findByEmail(email) != null
                || accountRepository.findByUsername(username) != null) {
            throw new BadRequestException("Tài khoản này đã tồn tại");
        }

        City city = cityRepository.findByCode(requestBody.getCityCode());
        if (city == null) {
            City newCity = new City()
                    .setCode(requestBody.getCityCode())
                    .setName(requestBody.getCity());
            city = cityRepository.save(newCity);
        }
        District district = districtRepository.findByCode(requestBody.getDistrictCode());
        if (district == null) {
            District newDistrict = new District()
                    .setCode(requestBody.getDistrictCode())
                    .setName(requestBody.getDistrict())
                    .setCityId(city.getId());
            district = districtRepository.save(newDistrict);
        }
        Ward ward = wardRepository.findByCode(requestBody.getWardCode());
        if (ward == null) {
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

        String salt = generateSalt();
        String hashedPassword = hasingPassword(requestBody.getPassword(), salt);

        User user = new User()
                .setId(requestBody.getUserId().trim().toUpperCase())
                .setCampusId(requestBody.getCampusId())
                .setFirstName(requestBody.getFirstName().trim())
                .setLastName(requestBody.getLastName().trim())
                .setEmail(email.trim())
                .setGender(requestBody.getGender())
                .setImageUrl(S3Constant.S3_FOLDER_AVATAR + "/"
                        + (requestBody.getGender().equals("MALE")
                        ? S3Constant.S3_DEFAULT_AVATAR_MALE_FILENAME
                        : S3Constant.S3_DEFAULT_AVATAR_FEMALE_FILENAME)) // xét ảnh mặc định theo giới tính khi đăng ký
                .setAddressId(address.getId())
                .setPhoneNumber(requestBody.getPhoneNumber())
                .setRegisterAt(Instant.now());
        if (requestBody.getBirthDate() != null) {
            user.setBirthDate(requestBody.getBirthDate());
        }
        User savedUser = userRepository.save(user);

        Account account = new Account().setUsername(username.trim())
                .setPassword(hashedPassword)
                .setSalt(salt)
                .setUserId(user.getId())
                .setIsVerify(true);
        accountRepository.save(account);

        Role role = roleRepository.findByName(requestBody.getRole());
        UserRole userRole = new UserRole()
                .setUserId(savedUser.getId())
                .setRoleId(role.getId());
        userRoleRepository.save(userRole);

        return genericMapper.mapToTypeNotNullProperty(savedUser, UserDto.class);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public UserDto register(UserRegisterRequestBody requestBody) {
        if (accountRepository.findByUsername(requestBody.getUsername()) != null) {
            throw new BadRequestException("Tài khoản này đã tồn tại");
        }
        if (userRepository.findByEmail(requestBody.getEmail()) != null) {
            throw new BadRequestException("Email này đã tồn tại");
        }
        if (userRepository.findById(requestBody.getUserId()).isPresent()) {
            throw new BadRequestException("Mã số này đã tồn tại");
        }

        City city = cityRepository.findByCode(requestBody.getCityCode());
        if (city == null) {
            City newCity = new City()
                    .setCode(requestBody.getCityCode())
                    .setName(requestBody.getCity());
            city = cityRepository.save(newCity);
        }
        District district = districtRepository.findByCode(requestBody.getDistrictCode());
        if (district == null) {
            District newDistrict = new District()
                    .setCode(requestBody.getDistrictCode())
                    .setName(requestBody.getDistrict())
                    .setCityId(city.getId());
            district = districtRepository.save(newDistrict);
        }
        Ward ward = wardRepository.findByCode(requestBody.getWardCode());
        if (ward == null) {
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

        User user = new User()
                .setId(requestBody.getUserId().trim().toUpperCase())
                .setCampusId(requestBody.getCampusId())
                .setFirstName(requestBody.getFirstName().trim())
                .setLastName(requestBody.getLastName().trim())
                .setEmail(requestBody.getEmail().trim())
                .setGender(requestBody.getGender())
                .setImageUrl(S3Constant.S3_FOLDER_AVATAR + "/"
                        + (requestBody.getGender().equals("MALE")
                        ? S3Constant.S3_DEFAULT_AVATAR_MALE_FILENAME
                        : S3Constant.S3_DEFAULT_AVATAR_FEMALE_FILENAME)) // xét ảnh mặc định theo giới tính khi đăng ký
                .setAddressId(address.getId())
                .setPhoneNumber(requestBody.getPhoneNumber())
                .setRegisterAt(Instant.now());
        if (requestBody.getBirthDate() != null) {
            user.setBirthDate(requestBody.getBirthDate());
        }
        User savedUser = userRepository.save(user);

        String salt = generateSalt();
        String hashedPassword = hasingPassword(requestBody.getPassword(), salt);

        Account account = new Account().setUsername(requestBody.getUsername())
                .setPassword(hashedPassword)
                .setSalt(salt)
                .setUserId(savedUser.getId())
                .setIsVerify(false);

        accountRepository.save(account);

        Role role = roleRepository.findByName(requestBody.getRole());

        UserRole userRole = new UserRole()
                .setUserId(savedUser.getId())
                .setRoleId(role.getId());

        userRoleRepository.save(userRole);

        Map<String, Object> model = new HashMap<>();
        model.put("name", requestBody.getFirstName() + " " + requestBody.getLastName());
        model.put("userId", requestBody.getUserId());
        SendMailRequestBody sendMailRequestBody = new SendMailRequestBody()
                .setSubject("Confirm Email - SchoolLab")
                .setTo(user.getEmail())
                .setType("verify_account");
        SendMailResponseDto message = sendMailService.sendMail(sendMailRequestBody, model);

        return genericMapper.mapToTypeNotNullProperty(savedUser, UserDto.class);
    }

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public UserDto updateUser(String userId, UserUpdateRequestBody requestBody) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            throw new NotFoundException("Không tìm thấy thông tin người dùng này");
        }

        User user = optionalUser.get();
        user.setFirstName(requestBody.getFirstName());
        user.setLastName(requestBody.getLastName());
        user.setGender(requestBody.getGender());
        user.setPhoneNumber(requestBody.getPhoneNumber());
        user.setUpdateAt(Instant.now());
        if (requestBody.getBirthDate() != null) {
            user.setBirthDate(requestBody.getBirthDate());
        }

        City city = cityRepository.findByCode(requestBody.getCityCode());
        if (city == null) {
            City newCity = new City()
                    .setCode(requestBody.getCityCode())
                    .setName(requestBody.getCity());
            city = cityRepository.save(newCity);
        }
        District district = districtRepository.findByCode(requestBody.getDistrictCode());
        if (district == null) {
            District newDistrict = new District()
                    .setCode(requestBody.getDistrictCode())
                    .setName(requestBody.getDistrict())
                    .setCityId(city.getId());
            district = districtRepository.save(newDistrict);
        }
        Ward ward = wardRepository.findByCode(requestBody.getWardCode());
        if (ward == null) {
            Ward newWard = new Ward()
                    .setCode(requestBody.getWardCode())
                    .setName(requestBody.getWard())
                    .setDistrictId(district.getId());
            ward = wardRepository.save(newWard);
        }

        String addressId = optionalUser.get().getAddressId();
        Optional<Address> optAddress = addressId != null ? addressRepository.findById(addressId) : Optional.empty();
        Address newAddress;
        if (optAddress.isPresent()) {
            newAddress = optAddress.get();
            newAddress.setUpdateAt(Instant.now());
        } else {
            newAddress = new Address().setCreateAt(Instant.now());
        }

        newAddress.setStreet(requestBody.getStreet());
        newAddress.setWardId(ward.getId());
        addressRepository.save(newAddress);
        user.setAddressId(newAddress.getId());

        User updatedUser = userRepository.save(user);
        UserDto userDto = genericMapper.mapToTypeNotNullProperty(updatedUser, UserDto.class);
        userDto.setCityCode(city.getCode());
        userDto.setCity(city.getName());
        userDto.setDistrict(district.getName());
        userDto.setDistrictCode(district.getCode());
        userDto.setWard(ward.getName());
        userDto.setWardCode(ward.getCode());
        userDto.setStreet(newAddress.getStreet());

        return userDto;
    }

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public UserDto changeAvatar(String userId, MultipartFile multipartFile) {
        Optional<User> optUser = userRepository.findById(userId);
        if (!optUser.isPresent()) {
            throw new NotFoundException("Không tìm thấy người dùng này!");
        }

        User user = optUser.get();
        String avatarFileName = user.getId() + "_avatar";
        //xóa ảnh cũ nếu ko phải ảnh mặc mịnh và ko null
        if (user.getImageUrl() != null
                && !user.getImageUrl().equals("avatar/" + S3Constant.S3_DEFAULT_AVATAR_MALE_FILENAME)
                && !user.getImageUrl().equals("avatar/" + S3Constant.S3_DEFAULT_AVATAR_FEMALE_FILENAME)
        ) {
            s3Service.deleteFile(user.getImageUrl());
        }
        //lưu ảnh mới
        String fileName = s3Service.saveFile(multipartFile, S3Constant.S3_FOLDER_AVATAR, avatarFileName);
        user.setImageUrl(fileName);
        userRepository.save(user);
        return genericMapper.mapToTypeNotNullProperty(user, UserDto.class);
    }

    @Override
    public UserDto setDefaultAvatar(String userId) {
        Optional<User> optUser = userRepository.findById(userId);
        if (!optUser.isPresent()) {
            throw new NotFoundException("Không tìm thấy người dùng này!");
        }

        User user = optUser.get();
        //xóa ảnh cũ
        if (user.getImageUrl() != null) {
            s3Service.deleteFile(user.getImageUrl());
        }
        //lưu ảnh mới
        String avatarName = "avatar/" + (user.getGender().equals("MALE") ? S3Constant.S3_DEFAULT_AVATAR_MALE_FILENAME : S3Constant.S3_DEFAULT_AVATAR_FEMALE_FILENAME);
        user.setImageUrl(avatarName);
        userRepository.save(user);
        return genericMapper.mapToTypeNotNullProperty(user, UserDto.class);
    }

    @Override
    public List<UserDto> getUserByCampusId(String campusId) {
        List<User> users = userRepository.findAllByCampusId(campusId);
        return genericMapper.mapToListOfTypeNotNullProperty(users, UserDto.class);
    }

    @Override
    public UserDto getUser(String userId) {
        Optional<User> optUser = userRepository.findById(userId);
        if (!optUser.isPresent()) {
            throw new NotFoundException("Không tìm thấy người dùng này");
        }
        User user = optUser.get();
        UserDto userDto = genericMapper.mapToTypeNotNullProperty(user, UserDto.class);
        List<UserRole> userRoles = userRoleRepository.findAllByUserId(userId);
        List<String> roles = new ArrayList<>();
        userRoles.forEach(ur -> {
            Optional<Role> optRole = roleRepository.findById(ur.getRoleId());
            optRole.ifPresent(role -> roles.add(role.getName()));
        });
        userDto.setRoles(roles);
        if (user.getAddressId() != null) {
            Optional<Address> address = addressRepository.findById(user.getAddressId());
            if (address.isPresent()) {
                userDto.setStreet(address.get().getStreet());
                Optional<Ward> optWard = wardRepository.findById(address.get().getWardId());
                if (optWard.isPresent()) {
                    Ward ward = optWard.get();
                    userDto.setWardCode(ward.getCode());
                    userDto.setWard(ward.getName());
                    Optional<District> optDistrict = districtRepository.findById(ward.getDistrictId());
                    if (optDistrict.isPresent()) {
                        District district = optDistrict.get();
                        userDto.setDistrictCode(district.getCode());
                        userDto.setDistrict(district.getName());
                        Optional<City> optCity = cityRepository.findById(district.getCityId());
                        if (optCity.isPresent()) {
                            City city = optCity.get();
                            userDto.setCityCode(city.getCode());
                            userDto.setCity(city.getName());
                        }
                    }
                }
            }
        }

        Optional<Campus> optionalCampus = campusRepository.findById(user.getCampusId());
        optionalCampus.ifPresent(campus -> userDto.setCampusName(campus.getName()));

        return userDto;
    }

    private String getUsernameFromEmail(String email) {
        return email.split("@")[0];
    }

    //encrypt password
    @Override
    public String hasingPassword(String password, String salt) {
        return passwordEncoder.encode(password + salt);
    }

    //generate salt to hasing password
    @Override
    public String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[10];
        random.nextBytes(salt);
        return salt.toString();
    }
}
