package org.hrd.finalprojectmuseum.model.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class File {
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
}
