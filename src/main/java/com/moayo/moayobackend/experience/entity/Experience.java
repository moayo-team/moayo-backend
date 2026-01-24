package com.moayo.moayobackend.experience.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "experience")
public class Experience {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private String organization; // 주최/기관
    private String title; // 활동명
    private String activity; // 참여형태/활동분류
    private String role; // 역할

    @Column(length = 2000)
    private String summary;      // 활동 소개

    private LocalDate startDate;
    private LocalDate endDate;

    private Boolean visible;

    public Experience(Long memberId,
                      String organization,
                      String title,
                      String activity,
                      String role,
                      String summary,
                      LocalDate startDate,
                      LocalDate endDate,
                      Boolean visible) {
        this.memberId = memberId;
        this.organization = organization;
        this.title = title;
        this.activity = activity;
        this.role = role;
        this.summary = summary;
        this.startDate = startDate;
        this.endDate = endDate;
        this.visible = visible;
    }

    public void validateOwner(Long memberId) {
        if (this.memberId == null || !this.memberId.equals(memberId)) {
            throw new IllegalArgumentException("No permission");
        }
    }

    public void applyPatch(String organization,
                           String title,
                           String activity,
                           String role,
                           String summary,
                           LocalDate startDate,
                           LocalDate endDate) {
        if (organization != null)
            this.organization = organization;
        if (title != null)
            this.title = title;
        if (activity != null)
            this.activity = activity;
        if (role != null)
            this.role = role;
        if (summary != null)
            this.summary = summary;
        if (startDate != null)
            this.startDate = startDate;
        if (endDate != null)
            this.endDate = endDate;
    }

    public void changeVisibility(Boolean visible) {
        if (visible != null)
            this.visible = visible;
    }
}
