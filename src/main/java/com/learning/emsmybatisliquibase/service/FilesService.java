package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FilesService {

    SuccessResponseDto colleagueOnboard(MultipartFile file) throws IOException, MessagingException;

    void managerAccess(MultipartFile file) throws IOException;

    void updateManagerId(MultipartFile file) throws IOException;

    void departmentPermission(MultipartFile file) throws IOException;
}
