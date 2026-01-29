package com.moayo.moayobackend.profile.entity;

import com.moayo.moayobackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 - 프로필 화면의 "기본정보 추가(+)" 항목
*/
@Getter
@NoArgsConstructor
@Entity
@Table(name = "profile_index_items",
        indexes = {
                @Index(name = "idx_profile_index_items_profile_id", columnList = "profile_id")
        })
public class ProfileIndexItem extends BaseEntity {

    public enum ItemType { text, link, file }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="profile_id", nullable = false)
    private Long profileId;

    @Column(name="index_key", nullable = false, length = 20)
    private String indexKey;

    @Column(name="index_value", nullable = false, length = 200)
    private String indexValue;

    @Enumerated(EnumType.STRING)
    @Column(name="item_type", nullable = false, length = 10)
    private ItemType itemType;

    @Column(name="link_url", length = 500)
    private String linkUrl;

    public ProfileIndexItem(Long profileId, String indexKey, String indexValue, ItemType itemType, String linkUrl) {
        this.profileId = profileId;
        this.indexKey = indexKey;
        this.indexValue = indexValue;
        this.itemType = itemType;
        this.linkUrl = linkUrl;
    }

    public void update(String indexKey, String indexValue, ItemType itemType, String linkUrl) {
        if (indexKey != null) this.indexKey = indexKey;
        if (indexValue != null) this.indexValue = indexValue;
        if (itemType != null) this.itemType = itemType;
        if (linkUrl != null) this.linkUrl = linkUrl;
    }
}
