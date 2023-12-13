package com.schoollab.controller.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserUpdateRequestBody {
        private String firstName;

        private String lastName;

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
}
