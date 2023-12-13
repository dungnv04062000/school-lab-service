package com.schoollab.controller.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EvaluationTeammateCreateUpdateRequestBody {
    List<Teammate> teammates;

    @Data
    @NoArgsConstructor
    @Accessors(chain = true)
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Teammate{
        private String toId;

        @NotNull(message = "Dự án không được để trống")
        private String lessonId;

        @NotNull(message = "Điểm chăm chỉ - hard working không được để trống")
        private Integer hardWorking;

        @NotNull(message = "Điểm làm việc nhóm - teamwork không được để trống")
        private Integer teamwork;

        @NotNull(message = "Điểm kĩ năng - skill không được để trống")
        private Integer skill;
    }

}
