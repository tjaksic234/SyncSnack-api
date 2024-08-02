package com.example.KavaSpring.services;

import com.amazonaws.services.s3.model.PutObjectResult;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface AmazonS3Service {
    PutObjectResult uploadToS3(String path, String fileName, InputStream file);
    byte[] downloadFromS3(String fileUri) throws IOException;
    void deleteFromS3(String path, String fileName);
    void updateFileInS3(String path, String fileName, InputStream newFile);
    URL generatePresignedUrl(String fileUri);
}
