package com.moayo.moayobackend.profile.dto.request;

public record ProfileIndexItemCreateRequest(
        String indexKey,
        String indexValue,
        String itemType,   // file/link/text

        String textValue,
        String linkUrl,

        String fileUrl,
        String fileName,
        String fileType,
        Long fileSize
) {}
