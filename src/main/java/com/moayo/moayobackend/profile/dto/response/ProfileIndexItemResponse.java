package com.moayo.moayobackend.profile.dto.response;

import com.moayo.moayobackend.profile.entity.ProfileIndexItem;

/*
 ProfileIndexItemResponse
 - 기본정보 추가 항목 응답 DTO
*/
public record ProfileIndexItemResponse(
        Long id,
        String indexKey,
        String indexValue,
        String itemType,
        String textValue,
        String linkUrl
) {
    public static ProfileIndexItemResponse from(ProfileIndexItem e) {
        return new ProfileIndexItemResponse(
                e.getId(),
                e.getIndexKey(),
                e.getIndexValue(),
                e.getItemType().name(),
                e.getTextValue(),
                e.getLinkUrl()
        );
    }
}
