package com.hei.school.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;

import com.hei.school.endpoint.event.EventProducer;
import com.hei.school.endpoint.event.model.ThumbnailGenerationRequested;
import com.hei.school.file.bucket.BucketComponent;
import com.hei.school.repository.SubmissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

  @Mock private SubmissionRepository repository;
  @Mock private BucketComponent bucketComponent;
  @Mock private EventProducer<ThumbnailGenerationRequested> eventProducer;

  @InjectMocks private SubmissionService service;

  @Test
  void create_returns_submission_with_null_thumbnail_and_fires_event() {
    var file = new MockMultipartFile("file", "test.png", "image/png", new byte[] {1, 2, 3});

    var submission = service.create(file, "toky@mail.hei.school@example.com");

    assertThat(submission.getThumbnailKey()).isNull();
    verify(repository).save(submission);
    verify(eventProducer).accept(anyList());
  }

  @Test
  void create_throws_when_email_is_blank() {
    var file = new MockMultipartFile("file", "test.png", "image/png", new byte[] {1});

    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalArgumentException.class, () -> service.create(file, "  "));
  }
}
