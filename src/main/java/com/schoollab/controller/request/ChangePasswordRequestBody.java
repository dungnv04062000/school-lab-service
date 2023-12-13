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
public class ChangePasswordRequestBody {
    @NotNull(message = "Mật khẩu hiện tại không được để trống")
    private String currentPassword;

    @NotNull(message = "Mật khẩu mới không được để trống")
    private String newPassword;
}
