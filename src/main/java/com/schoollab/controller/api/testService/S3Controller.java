package com.schoollab.controller.api.testService;

import com.schoollab.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/test/s3")
public class S3Controller {

    @Autowired
    S3Service s3Service;

    @PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public String upload(@RequestParam("file")MultipartFile multipartFile){
        return s3Service.saveFile(multipartFile, null, null);
    }

    @PostMapping(value = "/upload/{folder-name}/{file-name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String upload(@RequestParam("file")MultipartFile multipartFile,
                         @PathVariable("folder-name") String folderName,
                         @PathVariable("file-name") String fileName
                         ){
        return s3Service.saveFile(multipartFile, folderName, fileName);
    }

    @GetMapping(value = "/download/{file-name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> download(@PathVariable("file-name") String fileName){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.ALL_VALUE);
        headers.add("Content-Disposition", "attachment; filename=" + fileName);
        byte[] bytes = s3Service.downloadFile(fileName);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(bytes);
    }

    @DeleteMapping(value = "/delete/{file-name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String delete(@PathVariable("file-name") String fileName){
        return s3Service.deleteFile(fileName);
    }

    @GetMapping()
    public List<String> listFiles(){
        return s3Service.listAllFiles();
    }
}
