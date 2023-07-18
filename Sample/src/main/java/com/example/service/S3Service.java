package com.example.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class S3Service {

    @Autowired
    private AmazonS3 amazonS3;
    private Logger logger = LoggerFactory.getLogger(S3Service.class);

    //create bucket
    public ResponseEntity createBucket(String bucketName) {
        if (amazonS3.doesBucketExist(bucketName)) {
            logger.info("bucket name is already used");
            return null;
        }
        amazonS3.createBucket(bucketName);
        return null;
    }

    //get list of bucket
    public List<Bucket> getbucketList() {
        return amazonS3.listBuckets();
    }

    //delete bucket
    public ResponseEntity deleteBucket(String bucketName) {
        try {
            amazonS3.deleteBucket(bucketName);
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            return null;
        }
        return null;
    }

   // List all objects name
    public List<S3ObjectSummary> listObjectsInBucket(String bucketName){
        ObjectListing objectListing = amazonS3.listObjects(bucketName);
        return objectListing.getObjectSummaries();
    }

    //downlaod the file

  /*  public ResponseEntity downloadFile(StorageInfo storageInfo) throws Exception {
        HttpHeaders header = new HttpHeaders();
        createConnectionForS3();
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        ResponseEntity<Resource> fileResource = null;
        if (checkIfObjectExist(storageInfo)) {
            String filePath = storageInfo.getFilePath().get(0);
            S3Object s3object = s3Client.getObject(storageInfo.getBucketName(), filePath);
            InputStream inputStream = s3object.getObjectContent();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1048576];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
            fileResource = ResponseEntity.ok()
                    .headers(header)
                    .contentLength(outputStream.size())
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(resource);
        } else
            fileResource = ResponseEntity.notFound().build();
        return fileResource;
    }*/
}
