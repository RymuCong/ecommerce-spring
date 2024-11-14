package com.arius.ecommerce.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${aws.bucketName}")
    private String bucketName;

    @Autowired
    public S3Service(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadProductFile(MultipartFile file) {
        File localFile = null;
        try {
            String fileName = "products/images/" + file.getOriginalFilename();

            // Convert MultipartFile to File
            localFile = convertMultiPartToFile(file);

            // Create ObjectMetadata and set content type
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());

            // Create a PutObjectRequest with the correct content type and permissions
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, localFile)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            // Upload the file
            s3Client.putObject(putObjectRequest);

            // Delete the local file after successful upload
            if (localFile.exists()) {
                localFile.delete();
            }

            return s3Client.getUrl(bucketName, fileName).toString();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        } finally {
            // Ensure the local file is deleted in case of an exception
            if (localFile != null && localFile.exists()) {
                localFile.delete();
            }
        }
    }

    private File convertMultiPartToFile(MultipartFile file) {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return convFile;
    }

    public ResponseEntity<?> download(String file) {
        InputStream inputStream = null;
        FileOutputStream fos = null;

        try {
            S3Object s3Object = s3Client.getObject(bucketName,"products/images/" + file);
            inputStream = s3Object.getObjectContent();
            byte[] content = IOUtils.toByteArray(inputStream);

            String downloadDir = System.getProperty("user.dir") + "/src/main/resources/static/images";
            File downloadedFile = new File(downloadDir, file);
            new File(downloadDir).mkdirs();

            try {
                fos = new FileOutputStream(downloadedFile);
                fos.write(content);
            } catch (IOException e) {
                System.err.println("Error writing file: " + e.getMessage());
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }

            return ResponseEntity.ok(downloadedFile);
        } catch (Exception e) {
            System.err.println("Error downloading file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error downloading file: " + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    System.err.println("Error closing input stream: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public void deleteProductImage(String imageUrl) {
        try {
            // Decode the URL from the database
            String decodedImageUrl = URLDecoder.decode(imageUrl, StandardCharsets.UTF_8);
            String key = imageUrl.substring(decodedImageUrl.indexOf("products/images/"));

            // Delete the object from S3
            s3Client.deleteObject(bucketName, key);
        } catch (Exception e) {
            System.err.println("Error encoding/decoding URL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}