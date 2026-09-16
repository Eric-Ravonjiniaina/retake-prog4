package com.hei.school.mapper;

import org.springframework.stereotype.Component;

@Component
public class SubmissionMapper {

  public com.hei.school.dto.Submission toDto(com.hei.school.entity.Submission entity) {
    return com.hei.school.dto.Submission.builder()
        .id(entity.getId())
        .email(entity.getEmail())
        .thumbnailKey(entity.getThumbnailKey())
        .createdAt(entity.getCreatedAt())
        .build();
  }
}
