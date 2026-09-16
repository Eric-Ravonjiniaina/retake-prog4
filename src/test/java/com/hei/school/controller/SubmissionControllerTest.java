package com.hei.school.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.hei.school.mapper.SubmissionMapper;
import com.hei.school.service.SubmissionService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class SubmissionControllerTest {

  @Mock private SubmissionService submissionService;
  @Mock private SubmissionMapper submissionMapper;

  @InjectMocks private SubmissionController controller;

  @Test
  void createSubmission_returns_mapped_dto() {
    var file = new MockMultipartFile("file", "test.png", "image/png", new byte[] {1});
    var entity =
        com.hei.school.entity.Submission.builder()
            .id(UUID.randomUUID())
            .email("toky@example.com")
            .thumbnailKey(null)
            .createdAt(Instant.now())
            .build();
    var dto =
        com.hei.school.dto.Submission.builder()
            .id(entity.getId())
            .email(entity.getEmail())
            .thumbnailKey(null)
            .createdAt(entity.getCreatedAt())
            .build();

    when(submissionService.create(any(), anyString())).thenReturn(entity);
    when(submissionMapper.toDto(entity)).thenReturn(dto);

    var result = controller.createSubmission(file, "toky@example.com");

    assertThat(result.getThumbnailKey()).isNull();
    assertThat(result.getId()).isEqualTo(entity.getId());
  }

  @Test
  void listSubmissions_returns_mapped_list() {
    var entity =
        com.hei.school.entity.Submission.builder()
            .id(UUID.randomUUID())
            .email("toky@example.com")
            .thumbnailKey("thumbnails/x.png")
            .createdAt(Instant.now())
            .build();
    var dto =
        com.hei.school.dto.Submission.builder()
            .id(entity.getId())
            .email(entity.getEmail())
            .thumbnailKey(entity.getThumbnailKey())
            .createdAt(entity.getCreatedAt())
            .build();

    when(submissionService.findAll()).thenReturn(List.of(entity));
    when(submissionMapper.toDto(entity)).thenReturn(dto);

    var result = controller.listSubmissions();

    assertThat(result).containsExactly(dto);
  }
}
