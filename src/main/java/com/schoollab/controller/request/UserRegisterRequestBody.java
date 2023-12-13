package com.schoollab.controller.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserRegisterRequestBody {

    @NotNull(message = "Username không được để trống")
    private String username;

    @NotNull(message = "Password không được để trống")
    private String password;

    @NotNull(message = "UserId không được để trống")
    private String userId;

    @NotNull(message = "CampusId không được để trống")
    private String campusId;

    private String firstName;

    private String lastName;

    @NotNull(message = "Email không được để trống")
    private String email;

    private String gender;

    private String phoneNumber;

    private Date birthDate;

    private String street;

    private String wardCode;

    private String ward;

    private String districtCode;

    private String district;

    private String cityCode;

    private String city;

    @NotNull(message = "Role không được để trống")
    private String role;

}
