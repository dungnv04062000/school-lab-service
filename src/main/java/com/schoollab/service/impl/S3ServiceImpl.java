package com.schoollab.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.schoollab.common.error.BadRequestException;
import com.schoollab.common.util.FileUtil;
import com.schoollab.service.S3Service;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3ServiceImpl implements S3Service {

    @Autowired
    private AmazonS3 s3;

    @Value("${bucketName}")
    private String bucketName;

    @Override
    public String saveFile(MultipartFile multipartFile, String folderName, String expectedFileName) {
        if(multipartFile == null || multipartFile.isEmpty()){
            throw new BadRequestException("Không tìm thấy file nào");
        }
        try {
            File file = FileUtil.convertMultipartFileToFile(multipartFile);

            StringBuilder fileName = new StringBuilder();
            if(StringUtils.isNotBlank(folderName)){
                fileName.append(folderName).append("/");
            }

            if(StringUtils.isNotBlank(expectedFileName)){
                String fileExtension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
                fileName.append(expectedFileName).append(".").append(fileExtension);
            } else {
                fileName.append(multipartFile.getOriginalFilename());
            }

            PutObjectResult putObjectResult = s3.putObject(bucketName, fileName.toString(), file);
            file.delete();
            return fileName.toString();
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public String saveFile(File file, String folderName, String expectedFileName) {
        if(file == null || !file.exists()){
            throw new BadRequestException("Không tìm thấy file nào");
        }
        try {
            StringBuilder fileName = new StringBuilder();
            if(StringUtils.isNotBlank(folderName)){
                fileName.append(folderName).append("/");
            }

            if(StringUtils.isNotBlank(expectedFileName)){
                fileName.append(expectedFileName);
            } else {
                fileName.append(file.getName());
            }

            PutObjectResult putObjectResult = s3.putObject(bucketName, fileName.toString(), file);
            return fileName.toString();
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] downloadFile(String fileName) {
        S3Object s3Object = s3.getObject(bucketName, fileName);
        S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
        try {
            return IOUtils.toByteArray(s3ObjectInputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String deleteFile(String fileName) {
        s3.deleteObject(bucketName, fileName);
        return "File Deleted";
    }

    @Override
    public List<String> listAllFiles() {
        ListObjectsV2Result listObjectsV2Result = s3.listObjectsV2(bucketName);
        return listObjectsV2Result.getObjectSummaries().stream().map(S3ObjectSummary::getKey).collect(Collectors.toList());
    }
}
