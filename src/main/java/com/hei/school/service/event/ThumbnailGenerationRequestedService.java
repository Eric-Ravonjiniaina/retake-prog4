package com.hei.school.service.event;

import com.hei.school.endpoint.event.model.ThumbnailGenerationRequested;
import com.hei.school.file.bucket.BucketComponent;
import com.hei.school.mail.Email;
import com.hei.school.mail.Mailer;
import com.hei.school.repository.SubmissionRepository;
import jakarta.mail.internet.InternetAddress;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ThumbnailGenerationRequestedService implements Consumer<ThumbnailGenerationRequested> {

  private static final int THUMBNAIL_SIZE_PX = 256;
  private static final Duration DOWNLOAD_LINK_TTL = Duration.ofDays(7);

  private final BucketComponent bucketComponent;
  private final SubmissionRepository repository;
  private final Mailer mailer;

  @SneakyThrows
  @Override
  public void accept(ThumbnailGenerationRequested event) {
    log.info("Generating thumbnail for submission {}", event.getSubmissionId());

    File original = bucketComponent.download(event.getOriginalObjectKey());
    File resized = resizeTo256x256(original);

    String thumbnailKey = "thumbnails/" + event.getSubmissionId() + ".png";
    bucketComponent.upload(resized, thumbnailKey);

    var submission =
        repository
            .findById(event.getSubmissionId())
            .orElseThrow(
                () ->
                    new IllegalStateException("Submission not found: " + event.getSubmissionId()));
    submission.setThumbnailKey(thumbnailKey);
    repository.save(submission);

    var downloadUri = bucketComponent.presign(thumbnailKey, DOWNLOAD_LINK_TTL);
    mailer.accept(
        new Email(
            new InternetAddress(event.getRecipientEmail()),
            List.of(),
            List.of(),
            "Your thumbnail is ready",
            "Your 256x256 thumbnail has been generated. Download it here: " + downloadUri,
            List.of()));
  }

  private File resizeTo256x256(File source) throws IOException {
    BufferedImage original = ImageIO.read(source);
    BufferedImage resized =
        new BufferedImage(THUMBNAIL_SIZE_PX, THUMBNAIL_SIZE_PX, BufferedImage.TYPE_INT_ARGB);

    Graphics2D g = resized.createGraphics();
    g.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g.drawImage(original, 0, 0, THUMBNAIL_SIZE_PX, THUMBNAIL_SIZE_PX, null);
    g.dispose();

    File out = File.createTempFile("thumbnail-", ".png");
    ImageIO.write(resized, "png", out);
    return out;
  }
}
