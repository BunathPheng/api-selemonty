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
import java.util.*;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final MinioClient minioClient;

    private final List<String> bucketNames = new ArrayList<>(Arrays.asList("logo", "image", "artifact"));

    @Override
    public String uploadFile(MultipartFile file, BucketType bucketType) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        validateFileType(file, bucketType);

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

    private void validateFileType(MultipartFile file, BucketType bucketType) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }

        String fileExtension = getFileExtension(originalFileName).toLowerCase();
        String contentType = file.getContentType();

        switch (bucketType) {
            case LOGO:
            case IMAGE:
                validateImageFile(fileExtension, contentType, bucketType);
                break;
            case ARTIFACT3D:
                validate3DArtifactFile(fileExtension, contentType);
                break;
            default:
                throw new IllegalArgumentException("Unsupported bucket type: " + bucketType);
        }
    }

    private void validateImageFile(String fileExtension, String contentType, BucketType bucketType) {
        Set<String> allowedExtensions = Set.of("jpg", "jpeg", "png", "gif", "bmp", "webp", "svg");

        Set<String> allowedMimeTypes = Set.of(
                "image/jpeg",
                "image/jpg",
                "image/png",
                "image/gif",
                "image/bmp",
                "image/webp",
                "image/svg+xml"
        );

        if (!allowedExtensions.contains(fileExtension)) {
            throw new IllegalArgumentException(
                    String.format("Invalid file extension for %s. Allowed extensions: %s. Received: %s",
                            bucketType.name().toLowerCase(),
                            String.join(", ", allowedExtensions),
                            fileExtension)
            );
        }

        if (contentType != null && !allowedMimeTypes.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                    String.format("Invalid content type for %s. Allowed types: %s. Received: %s",
                            bucketType.name().toLowerCase(),
                            String.join(", ", allowedMimeTypes),
                            contentType)
            );
        }
    }

    private void validate3DArtifactFile(String fileExtension, String contentType) {
        if (!"glb".equals(fileExtension)) {
            throw new IllegalArgumentException(
                    String.format("Invalid file extension for 3D artifact. Only .glb files are allowed. Received: %s",
                            fileExtension)
            );
        }

        Set<String> allowedMimeTypes = Set.of(
                "application/octet-stream",
                "model/gltf-binary",
                "model/gltf+json"
        );

        if (contentType != null && !allowedMimeTypes.contains(contentType.toLowerCase())) {
            System.out.println("Warning: Unexpected MIME type for .glb file: " + contentType);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "";
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return ""; // No extension found
        }

        return fileName.substring(lastDotIndex + 1);
    }

    private void validateFileTypeAdvanced(MultipartFile file, BucketType bucketType) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }

        validateFileSize(file, bucketType);

        String fileExtension = getFileExtension(originalFileName).toLowerCase();
        String contentType = file.getContentType();

        switch (bucketType) {
            case LOGO:
                validateLogoFile(fileExtension, contentType, file.getSize());
                break;
            case IMAGE:
                validateImageFileAdvanced(fileExtension, contentType, file.getSize());
                break;
            case ARTIFACT3D:
                validate3DArtifactFileAdvanced(fileExtension, contentType, file.getSize());
                break;
            default:
                throw new IllegalArgumentException("Unsupported bucket type: " + bucketType);
        }
    }

    private void validateFileSize(MultipartFile file, BucketType bucketType) {
        long maxSize;
        switch (bucketType) {
            case LOGO:
                maxSize = 5 * 1024 * 1024; // 5MB for logos
                break;
            case IMAGE:
                maxSize = 10 * 1024 * 1024; // 10MB for images
                break;
            case ARTIFACT3D:
                maxSize = 100 * 1024 * 1024; // 100MB for 3D artifacts
                break;
            default:
                maxSize = 10 * 1024 * 1024; // Default 10MB
        }

        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException(
                    String.format("File size exceeds limit for %s. Maximum allowed: %d MB, Received: %.2f MB",
                            bucketType.name().toLowerCase(),
                            maxSize / (1024 * 1024),
                            file.getSize() / (1024.0 * 1024.0))
            );
        }
    }

    private void validateLogoFile(String fileExtension, String contentType, long fileSize) {
        Set<String> allowedExtensions = Set.of("jpg", "jpeg", "png", "svg");

        if (!allowedExtensions.contains(fileExtension)) {
            throw new IllegalArgumentException(
                    String.format("Invalid file extension for logo. Allowed extensions: %s. Received: %s",
                            String.join(", ", allowedExtensions),
                            fileExtension)
            );
        }
    }

    private void validateImageFileAdvanced(String fileExtension, String contentType, long fileSize) {
        Set<String> allowedExtensions = Set.of("jpg", "jpeg", "png", "gif", "bmp", "webp");

        if (!allowedExtensions.contains(fileExtension)) {
            throw new IllegalArgumentException(
                    String.format("Invalid file extension for image. Allowed extensions: %s. Received: %s",
                            String.join(", ", allowedExtensions),
                            fileExtension)
            );
        }
    }

    private void validate3DArtifactFileAdvanced(String fileExtension, String contentType, long fileSize) {
        if (!"glb".equals(fileExtension)) {
            throw new IllegalArgumentException(
                    String.format("Invalid file extension for 3D artifact. Only .glb files are allowed. Received: %s",
                            fileExtension)
            );
        }
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
