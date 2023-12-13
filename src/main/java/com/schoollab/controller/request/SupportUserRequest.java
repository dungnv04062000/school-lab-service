package com.schoollab.controller.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SupportUserRequest {
    private String fromId;

    @NotNull(message = "Tên không được để trống")
    private String name;

    @NotNull(message = "Số điện thoại không được để trống")
    private String phoneNumber;

    @NotNull(message = "Email không được để trống")
    private String email;

    @NotNull(message = "Tiêu đề không được để trống")
    private String title;

    @NotNull(message = "Nội dung không được để trống")
    private String content;

    @NotNull(message = "Mức độ không được để trống")
    private String priority;
}
