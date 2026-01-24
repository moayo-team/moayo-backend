package com.moayo.moayobackend.post.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    PLANNING("기획"),
    MARKETING("마케팅"),
    DESIGN("디자인"),
    DEVELOPMENT("개발"),
    ART_SPORTS("예체능"),
    LITERATURE("문학"),
    OTHERS("기타");

    private final String label;
}