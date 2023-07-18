package com.example.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3BucketStorageService {
    @Autowired
    private AmazonS3 amazonS3;
    private Logger logger = LoggerFactory.getLogger(S3BucketStorageService.class);
    @Value("${application.bucket.name}")
    private String bucketName;

    public String uploadFile(String keyName, MultipartFile multipartFile) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        amazonS3.putObject(keyName, bucketName, multipartFile.getInputStream(), objectMetadata);
        return "File uploaded:" + keyName;
    }
    // or
    public String saveFile(MultipartFile multipartFile){
        String originalFilename = multipartFile.getOriginalFilename();
        try {
            File file = convertMultiPartToFile(multipartFile);
            PutObjectResult putObjectResult = amazonS3.putObject(bucketName, originalFilename, file);
            return putObjectResult.getContentMd5();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public File convertMultiPartToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        FileOutputStream stream = new FileOutputStream(file);
        stream.write(multipartFile.getBytes());
        stream.close();
        return file;
    }

    public InputStream getFile(String file) throws IOException {
        S3Object s3Object = amazonS3.getObject(bucketName, file);
        return s3Object.getObjectContent();
    }

    public String deleteFile(String keyName) {
        amazonS3.deleteObject(bucketName, keyName);
        return "File is deleted";
    }

    public List<String> getAllFiles() {
        List<String> fileNames = new ArrayList<>();
        ListObjectsV2Result result = amazonS3.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        for (S3ObjectSummary objectSummary : objects) {
            fileNames.add(objectSummary.getKey());
        }
        return fileNames;
    }
    //or
    public List<String> listOfFile(){
        ListObjectsV2Result listObjectsV2Result = amazonS3.listObjectsV2(bucketName);
        return listObjectsV2Result.getObjectSummaries().stream().map(S3ObjectSummary::getKey).collect(Collectors.toList());
    }
    public ByteArrayOutputStream downloadFile(String keyName){
       try {
           S3Object object = amazonS3.getObject(new GetObjectRequest(bucketName, keyName));
           S3ObjectInputStream objectContent = object.getObjectContent();
           ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
           int len;
           byte[] bytes = new byte[1024];
           while ((len=objectContent.read(bytes))!=-1) {
               byteArrayOutputStream.write(bytes, 0, len);
           }
           return byteArrayOutputStream;
       }catch (Exception exception){
           logger.error("Exception:"+exception.getMessage());
       }
       return null;
    }
    //or
    public byte[] downloadfile(String fileName){
        S3Object object = amazonS3.getObject(bucketName, fileName);
        S3ObjectInputStream objectContent = object.getObjectContent();
        try {
           return IOUtils.toByteArray(objectContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


