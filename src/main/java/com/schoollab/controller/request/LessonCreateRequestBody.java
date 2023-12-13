package com.schoollab.controller.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LessonCreateRequestBody {

    @NotNull(message = "Tiêu đề không được để trống")
    private String title;

    @NotNull(message = "Mã lớp không được để trống")
    private String classId;

    private String context;

    private String goal;

    @NotNull(message = "Mã môn không được để trống")
    private int subjectId;

    @NotNull(message = "Mức độ không được để trống")
    private int levelId;

    private String endAt;

    //kiến thức STEM
    private String science;

    private String technology;

    private String engineering;

    private String mathematics;

    //tiêu chí đánh giá - chấm điểm
    private float preparation;

    private float implementation;

    private float presentation;

    private float production;

}
