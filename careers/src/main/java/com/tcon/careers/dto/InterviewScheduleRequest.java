package com.tcon.careers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewScheduleRequest {

    @NotNull(message = "Round number is required")
    private Integer round;

    @NotNull(message = "Scheduled date is required")
    private LocalDateTime scheduledDate;

    @NotBlank(message = "Interviewer is required")
    private String interviewer;

    @NotBlank(message = "Interview type is required")
    private String type; // 'phone', 'video', 'in-person', 'technical'

    private String meetingLink;
    private String location;
}

