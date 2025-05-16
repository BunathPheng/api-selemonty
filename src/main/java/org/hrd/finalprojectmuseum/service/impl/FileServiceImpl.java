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
    public Resource viewFileByFileName(String fileName, BucketType bucketType) throws ServerException, InsufficientDataException,
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

        //check is file exist in bucket or not
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );

        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                throw new AppNotFoundException("File not found in bucket: " + bucketType.name());
            }
            throw e;
        }

        GetObjectArgs object = GetObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .build();

        InputStream result = minioClient.getObject(object);
        Resource resource = new InputStreamResource(result);

        return resource;
    }

    @Override
    public void deleteFile(String fileName, BucketType bucketType) throws ServerException, InsufficientDataException,
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
        //check if file exist or not
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                throw new AppNotFoundException("File not found in bucket: " + bucketType.name());
            }
            throw e;
        }

        RemoveObjectArgs object = RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .build();

        minioClient.removeObject(object);
    }
}
