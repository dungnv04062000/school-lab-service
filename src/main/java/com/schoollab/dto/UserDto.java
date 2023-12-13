package com.schoollab.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {

    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private String imageUrl;

    private String street;

    private String wardCode;

    private String ward;

    private String districtCode;

    private String district;

    private String cityCode;

    private String city;

    private String gender;

    private Date birthDate;

    private String campusId;

    private String campusName;

    private String phoneNumber;

    private String addressId;

    private List<String> roles;

    private Instant registerAt;

    private Instant updateAt;

    @JsonGetter("register_at")
    public Object getRegisterAt() {
        try {
            return registerAt.getEpochSecond();
        } catch (Exception e) {
            return null;
        }
    }

    @JsonGetter("update_at")
    public Object getUpdateAt() {
        try {
            return updateAt.getEpochSecond();
        } catch (Exception e) {
            return null;
        }
    }
}
