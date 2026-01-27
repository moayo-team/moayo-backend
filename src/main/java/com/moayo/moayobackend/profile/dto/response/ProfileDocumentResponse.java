package com.moayo.moayobackend.profile.dto.response;

import com.moayo.moayobackend.profile.entity.ProfileDocument;

/*
 ProfileDocumentResponse
 - 학력첨부(파일) 목록/업로드 응답 DTO
*/
public record ProfileDocumentResponse(
        Long id,
        String fileUrl,
        String fileName,
        String fileType,
        Long fileSize
) {
    public static ProfileDocumentResponse from(ProfileDocument d) {
        return new ProfileDocumentResponse(d.getId(), d.getFileUrl(), d.getFileName(), d.getFileType(), d.getFileSize());
    }
}
