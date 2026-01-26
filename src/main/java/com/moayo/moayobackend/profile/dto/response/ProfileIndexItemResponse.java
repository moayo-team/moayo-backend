package com.moayo.moayobackend.profile.dto.response;

public record ProfileIndexItemResponse(
        Long id,
        Long profileId,
        String indexKey,
        String indexValue,
        String itemType,
        String textValue,
        String linkUrl,
        String fileUrl,
        String fileName,
        String fileType,
        Long fileSize
) {}
