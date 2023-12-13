package com.schoollab.service;

import com.schoollab.controller.request.SendMailRequestBody;
import com.schoollab.dto.SendMailResponseDto;

import java.util.Map;

public interface SendMailService {
    SendMailResponseDto sendMail(SendMailRequestBody req, Map<String, Object> model);
}
