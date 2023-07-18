package com.example.controller;

import com.example.service.S3BucketStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
public class S3ClientController {
    @Autowired
    private S3BucketStorageService s3BucketStorageService;

    @PostMapping("file/upload")
    public ResponseEntity<String> uploadFile(@RequestParam String fileName, @RequestParam MultipartFile file) throws IOException {
        return new ResponseEntity<>(s3BucketStorageService.uploadFile(fileName, file), HttpStatus.OK);
    }

    @GetMapping("file/{fileName}")
    public ResponseEntity<byte[]> getFile(@RequestParam String fileName) throws IOException {
        InputStream fileContent = s3BucketStorageService.getFile(fileName);

        byte[] byteArray = new byte[fileContent.available()];
        fileContent.read(byteArray);
        fileContent.close();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(byteArray.length);
        headers.setContentDispositionFormData("attachment", fileName);
        return new ResponseEntity<>(byteArray, headers, HttpStatus.OK);
    }

    @DeleteMapping("/file")
    public ResponseEntity<String> deleteFile(@RequestParam String fileName) {
        try {
            s3BucketStorageService.deleteFile(fileName);
            return new ResponseEntity<>("File deleted: " + fileName, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete file: " + fileName, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<String>> getAllFiles() {
        List<String> fileNames = s3BucketStorageService.getAllFiles();
        return new ResponseEntity<>(fileNames, HttpStatus.OK);
    }

    @GetMapping("/downloadFile")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String fileName) {
        ByteArrayOutputStream byteArrayOutputStream = s3BucketStorageService.downloadFile(fileName);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        byte[] fileContent = byteArrayOutputStream.toByteArray();
        return new ResponseEntity<>(fileContent,headers,HttpStatus.OK);
    }

//    private MediaType contentType(String filename) {
//        String[] fileArrSplit = filename.split("\\.");
//        String fileExtension = fileArrSplit[fileArrSplit.length - 1];
//        switch (fileExtension) {
//            case "txt":
//                return MediaType.TEXT_PLAIN;
//            case "png":
//                return MediaType.IMAGE_PNG;
//            case "jpg":
//                return MediaType.IMAGE_JPEG;
//            default:
//                return MediaType.APPLICATION_OCTET_STREAM;
//        }
//    }

}


