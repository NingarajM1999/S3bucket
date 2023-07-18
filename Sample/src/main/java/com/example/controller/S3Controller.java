package com.example.controller;

import com.example.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class S3Controller {

    @Autowired
    S3Service s3Service;

    @PostMapping("/createBucket")
    public ResponseEntity createBucket(@RequestParam String bucketName){
        return s3Service.createBucket(bucketName);
    }
    @GetMapping("/getListOfBucket")
    public ResponseEntity<List<String>>getListOfBucket(){
        return (ResponseEntity<List<String>>) s3Service.getbucketList();
    }
    @DeleteMapping("/deleteBucket")
    public ResponseEntity deleteBucket(String buckName){
        return s3Service.deleteBucket(buckName);
    }

}
