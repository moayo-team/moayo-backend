package com.moayo.moayobackend.experience.dto.request;

public record AttachFileRequest(
        Long fileId,
        String fileName
) {}
