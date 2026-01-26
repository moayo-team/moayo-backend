package com.moayo.moayobackend.profile.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "profile_index_items")
public class ProfileIndexItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="profile_id", nullable = false)
    private Long profileId;

    @Column(name="index_key", nullable = false, length = 50)
    private String indexKey;

    @Column(name="index_value", nullable = false, length = 255)
    private String indexValue;

    // file/link/text
    @Column(name="item_type", nullable = false, length = 20)
    private String itemType;

    @Column(name="text_value", columnDefinition = "text")
    private String textValue;

    @Column(name="link_url", length = 255)
    private String linkUrl;

    @Column(name="file_url", length = 255)
    private String fileUrl;

    @Column(name="file_name", length = 255)
    private String fileName;

    @Column(name="file_type", length = 50)
    private String fileType;

    @Column(name="file_size")
    private Long fileSize;

    public static ProfileIndexItem create(
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
    ) {
        ProfileIndexItem i = new ProfileIndexItem();
        i.profileId = profileId;
        i.indexKey = indexKey;
        i.indexValue = indexValue;
        i.itemType = itemType;
        i.textValue = textValue;
        i.linkUrl = linkUrl;
        i.fileUrl = fileUrl;
        i.fileName = fileName;
        i.fileType = fileType;
        i.fileSize = fileSize;
        return i;
    }

    // PATCH: null이면 기존값 유지
    public void update(
            String indexKey,
            String indexValue,
            String itemType,
            String textValue,
            String linkUrl,
            String fileUrl,
            String fileName,
            String fileType,
            Long fileSize
    ) {
        if (indexKey != null) this.indexKey = indexKey;
        if (indexValue != null) this.indexValue = indexValue;
        if (itemType != null) this.itemType = itemType;

        if (textValue != null) this.textValue = textValue;
        if (linkUrl != null) this.linkUrl = linkUrl;

        if (fileUrl != null) this.fileUrl = fileUrl;
        if (fileName != null) this.fileName = fileName;
        if (fileType != null) this.fileType = fileType;
        if (fileSize != null) this.fileSize = fileSize;
    }
}
