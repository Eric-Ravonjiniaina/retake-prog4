package com.hei.school.service;

import com.hei.school.endpoint.event.EventProducer;
import com.hei.school.endpoint.event.model.ThumbnailGenerationRequested;
import com.hei.school.entity.Submission;
import com.hei.school.file.bucket.BucketComponent;
import com.hei.school.repository.SubmissionRepository;
import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class SubmissionService {

  private final SubmissionRepository repository;
  private final BucketComponent bucketComponent;
  private final EventProducer<ThumbnailGenerationRequested> eventProducer;

  @SneakyThrows
  public Submission create(MultipartFile file, String email) {
    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("file is required");
    }
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("email is required");
    }

    var id = UUID.randomUUID();
    var originalObjectKey = "originals/" + id + extensionOf(file.getOriginalFilename());

    File tempFile =
        File.createTempFile("submission-" + id, extensionOf(file.getOriginalFilename()));
    try {
      file.transferTo(tempFile);
      bucketComponent.upload(tempFile, originalObjectKey);
    } finally {
      tempFile.delete();
    }

    var submission =
        Submission.builder()
            .id(id)
            .email(email)
            .thumbnailKey(null)
            .createdAt(Instant.now())
            .build();
    repository.save(submission);

    eventProducer.accept(
        List.of(
            ThumbnailGenerationRequested.builder()
                .submissionId(id)
                .originalObjectKey(originalObjectKey)
                .recipientEmail(email)
                .build()));

    return submission;
  }

  public List<Submission> findAll() {
    return repository.findAll();
  }

  private static String extensionOf(String filename) {
    if (filename == null || !filename.contains(".")) {
      return ".bin";
    }
    return filename.substring(filename.lastIndexOf('.'));
  }
}
