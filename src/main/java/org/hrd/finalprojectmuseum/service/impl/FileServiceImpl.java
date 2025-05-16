package org.hrd.finalprojectmuseum.service.impl;

import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.enums.BucketType;
import org.hrd.finalprojectmuseum.service.FileService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final MinioClient minioClient;

    private final List<String> bucketNames = new ArrayList<>(Arrays.asList("logo", "image", "artifact"));

    @Override
    public String uploadFile(MultipartFile file, BucketType bucketType) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        String bucketName = "";
        if(bucketType == BucketType.LOGO){
            bucketName = bucketNames.getFirst();
        }else if(bucketType == BucketType.IMAGE){
            bucketName = bucketNames.get(1);
        } else if (bucketType == BucketType.ARTIFACT3D) {
            bucketName = bucketNames.get(2);
        }

        for (String bucket : bucketNames){
            BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder()
                    .bucket(bucket)
                    .build();
            boolean isBucketExist = minioClient.bucketExists(bucketExistsArgs);
            if (!isBucketExist) {
                MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
                        .bucket(bucket)
                        .build();
                minioClient.makeBucket(makeBucketArgs);
            }
        }

        String originalFileName = file.getOriginalFilename();
        String newFileName = UUID.randomUUID() + "." + StringUtils.getFilename(originalFileName);

        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(newFileName)
                .contentType(file.getContentType())
                .stream(file.getInputStream(), file.getSize(), -1)
                .build();

        ObjectWriteResponse response = minioClient.putObject(putObjectArgs);
        return response.object();
    }

    @Override
    public Resource viewFileByFileName(String fileName) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        for (String bucket : bucketNames) {
            try {
                // Check if object exists or not
                minioClient.statObject(
                        StatObjectArgs.builder()
                                .bucket(bucket)
                                .object(fileName)
                                .build()
                );
                InputStream result = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucket)
                                .object(fileName)
                                .build()
                );

                return new InputStreamResource(result);

            } catch (ErrorResponseException e) {
                if (!"NoSuchKey".equals(e.errorResponse().code())) {
                    throw e;
                }
            }
        }
        throw new AppNotFoundException("File not found in any bucket: " + fileName);
    }


    @Override
    public void deleteFile(String fileName) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        for (String bucket : bucketNames) {
            try {
                // Check if object exists or not
                minioClient.statObject(
                        StatObjectArgs.builder()
                                .bucket(bucket)
                                .object(fileName)
                                .build()
                );
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(bucket)
                        .object(fileName)
                        .build());
                return;

            } catch (ErrorResponseException e) {
                if (!"NoSuchKey".equals(e.errorResponse().code())) {
                    throw e;
                }
            }
        }
        throw new AppNotFoundException("File not found in any bucket: " + fileName);
    }
}
