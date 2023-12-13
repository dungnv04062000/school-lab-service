package com.schoollab.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface S3Service {

    String saveFile(MultipartFile file, String folderName, String expectedFileName);

    String saveFile(File file, String folderName, String expectedFileName);

    byte[] downloadFile(String fileName);

    String deleteFile(String fileName);

    List<String> listAllFiles();
}
