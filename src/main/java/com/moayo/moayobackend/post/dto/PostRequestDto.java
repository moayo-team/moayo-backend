package com.moayo.moayobackend.post.dto;

import com.moayo.moayobackend.post.entity.Category;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PostRequestDto {

    @NotNull(message = "제목은 필수입니다.")
    @Size(max = 50, message = "제목은 50자 이내여야 합니다.")
    private String title;

    @NotNull(message = "본문은 필수입니다.")
    @Size(max = 500, message = "본문은 500자 이내여야 합니다.")
    private String content;

    @NotNull(message = "카테고리는 필수입니다.")
    private Category category;

    private String role; // 모집 직군 (e.g. 프론트엔드, 디자이너)

    private Integer totalCount; // 모집 인원

    private LocalDate deadline; // 마감일
}
