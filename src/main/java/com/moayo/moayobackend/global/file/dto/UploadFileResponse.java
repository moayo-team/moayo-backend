package com.moayo.moayobackend.global.file.dto;

public record UploadFileResponse(
        Long fileId,
        String fileName,
        String contentType,
        Long size
) {}
