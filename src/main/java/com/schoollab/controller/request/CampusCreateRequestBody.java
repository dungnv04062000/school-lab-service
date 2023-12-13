package com.schoollab.controller.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CampusCreateRequestBody {
    @NotNull(message = "Tên campus không được để trống")
    private String name;

    private String adminId;

    private String street;

    private String wardCode;

    private String ward;

    private String districtCode;

    private String district;

    private String cityCode;

    private String city;
}
