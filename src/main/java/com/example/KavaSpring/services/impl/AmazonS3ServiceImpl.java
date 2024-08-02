package com.example.KavaSpring.services.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.example.KavaSpring.services.AmazonS3Service;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

@Service
@AllArgsConstructor
@Slf4j
public class AmazonS3ServiceImpl implements AmazonS3Service {

    private final AmazonS3 s3Client;

    private final String bucket;

    @Override
    public PutObjectResult uploadToS3(String path, String fileName, InputStream file) {
        String fullPath = path + "/" + fileName;
        return s3Client.putObject(bucket, fullPath, file, new ObjectMetadata());
    }

    @Override
    public byte[] downloadFromS3(String fileUri) throws IOException {
        S3Object s3Object = s3Client.getObject(bucket, fileUri);
        S3ObjectInputStream s3is = s3Object.getObjectContent();
        return s3is.readAllBytes();
    }

    @Override
    public void deleteFromS3(String path, String fileName) {
        try {
            s3Client.deleteObject(bucket, path + "/" + fileName);
            log.info("File deleted successfully");
        } catch (AmazonServiceException e) {
            log.info("Error occurred while deleting file");
            log.error(e.getMessage());
        }
    }

    @Override
    public void updateFileInS3(String path, String fileName, InputStream newFile) {
        String fullPath = path + "/" + fileName;
        try {
            if (s3Client.doesObjectExist(bucket, fullPath)) {
                s3Client.deleteObject(bucket, fullPath);
                log.info("Existing file deleted successfully");
            }

            s3Client.putObject(bucket, fullPath, newFile, new ObjectMetadata());
            log.info("New file uploaded successfully");
        } catch (AmazonServiceException e) {
            log.error("Error occurred while updating file in S3", e);
            throw new RuntimeException("Failed to update file in S3", e);
        }
    }

    @Override
    public URL generatePresignedUrl(String fileUri) {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, fileUri)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);

        return s3Client.generatePresignedUrl(generatePresignedUrlRequest);
    }
}
