package com.hei.school.service.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hei.school.endpoint.event.model.ThumbnailGenerationRequested;
import com.hei.school.entity.Submission;
import com.hei.school.file.bucket.BucketComponent;
import com.hei.school.mail.Email;
import com.hei.school.mail.Mailer;
import com.hei.school.repository.SubmissionRepository;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ThumbnailGenerationRequestedServiceTest {

  @Mock private BucketComponent bucketComponent;
  @Mock private SubmissionRepository repository;
  @Mock private Mailer mailer;

  @InjectMocks private ThumbnailGenerationRequestedService worker;

  private UUID submissionId;
  private ThumbnailGenerationRequested event;
  private Submission submission;

  @BeforeEach
  void setUp() throws Exception {
    submissionId = UUID.randomUUID();
    event =
        ThumbnailGenerationRequested.builder()
            .submissionId(submissionId)
            .originalObjectKey("originals/" + submissionId + ".png")
            .recipientEmail("toky@example.com")
            .build();
    submission =
        Submission.builder()
            .id(submissionId)
            .email("toky@example.com")
            .thumbnailKey(null)
            .createdAt(Instant.now())
            .build();

    File source = File.createTempFile("source-", ".png");
    ImageIO.write(new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB), "png", source);

    when(repository.findById(submissionId)).thenReturn(Optional.of(submission));
    when(bucketComponent.download(anyString())).thenReturn(source);
    when(bucketComponent.presign(anyString(), any(Duration.class)))
        .thenReturn(new URL("https://example.com/thumb.png"));
  }

  @Test
  void accept_updates_thumbnail_key_and_sends_email() {
    worker.accept(event);

    assertThat(submission.getThumbnailKey()).isEqualTo("thumbnails/" + submissionId + ".png");
    verify(repository).save(submission);
    verify(mailer).accept(any(Email.class));
  }
}
