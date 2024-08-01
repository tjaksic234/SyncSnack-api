package com.example.KavaSpring.config;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Component
@Slf4j
@Data
public class AmazonS3Config {
    @Value("${S3_ACCESS_KEY}")
    private String accessKey;

    @Value("${S3_SECRET_KEY}")
    private String secretKey;

    @Value("${S3_BUCKET_NAME}")
    private String bucket;

    public AmazonS3 S3client() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_NORTH_1)
                .build();
    }

    public PutObjectResult uploadToS3(String path, String fileName, InputStream file) {
        AmazonS3 s3client = S3client();
        String fullPath = path + "/" + fileName;
        return s3client.putObject(bucket, fullPath, file, new ObjectMetadata());
    }

    public byte[] downloadFromS3(String fileUri) throws IOException {
        AmazonS3 s3client = S3client();
        S3Object s3Object = s3client.getObject(bucket, fileUri);
        S3ObjectInputStream s3is = s3Object.getObjectContent();
        return s3is.readAllBytes();
    }

    public void deleteFromS3(String path, String fileName) {
        AmazonS3 s3client = S3client();
        try {
            s3client.deleteObject(bucket, path + "/" + fileName);
            log.info("File deleted successfully");
        } catch (AmazonServiceException e) {
            log.info("Error occurred while deleting file");
            log.error(e.getMessage());
        }
    }

    public void updateFileInS3(String path, String fileName, InputStream newFile) {
        AmazonS3 s3client = S3client();
        String fullPath = path + "/" + fileName;
        try {
            if (s3client.doesObjectExist(bucket, fullPath)) {
                s3client.deleteObject(bucket, fullPath);
                log.info("Existing file deleted successfully");
            }

            s3client.putObject(bucket, fullPath, newFile, new ObjectMetadata());
            log.info("New file uploaded successfully");
        } catch (AmazonServiceException e) {
            log.error("Error occurred while updating file in S3", e);
            throw new RuntimeException("Failed to update file in S3", e);
        }
    }

}
