package org.hrd.finalprojectmuseum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.entity.File;
import org.hrd.finalprojectmuseum.model.enums.BucketType;
import org.hrd.finalprojectmuseum.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("api/v1/file")
@RequiredArgsConstructor
public class FilesController {
    private final FileService fileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<File>> uploadFile(
            @RequestParam("file") @Valid MultipartFile file,
            @RequestParam("bucketType") BucketType bucketType
    ) throws Exception{

        String fileName = fileService.uploadFile(file, bucketType);

        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/file/view/"+fileName)
                .toUriString();

        File fileDto = File.builder()
                .fileName(fileName)
                .fileUrl(fileUrl)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .build();
        ApiResponse<File> response = ApiResponse.<File>builder()
                .success(true)
                .message("Upload file successfully")
                .status(HttpStatus.CREATED)
                .payload(fileDto)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<?> viewFileByFileName(
            @PathVariable String fileName
    ) throws Exception{

        Resource resource = fileService.viewFileByFileName(fileName);

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;

        if(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            mediaType = MediaType.IMAGE_JPEG;
        }
        else if(fileName.endsWith(".png")) {
            mediaType = MediaType.IMAGE_PNG;
        }
        else if(fileName.endsWith(".gif")) {
            mediaType = MediaType.IMAGE_GIF;
        }
        else if(fileName.endsWith(".mp4")) {
            mediaType = MediaType.valueOf("video/mp4");
        }
        else if(fileName.endsWith(".pdf")) {
            mediaType = MediaType.APPLICATION_PDF;
        }
        else if(fileName.endsWith(".glb")) {
            mediaType = MediaType.parseMediaType("model/gltf-binary");
        }


        return ResponseEntity.status(HttpStatus.OK)
                .contentType(mediaType)
                .body(resource);
    }

    @DeleteMapping("delete/{fileName}")
    public ResponseEntity<ApiResponse<Void>> deleteFile(
            @RequestParam("fileName") String fileName
    ) throws Exception {
        fileService.deleteFile(fileName);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("File has been delete successfully")
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
