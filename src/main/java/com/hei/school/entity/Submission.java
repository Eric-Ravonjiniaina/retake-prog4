package com.hei.school.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Submission {

  @Id private UUID id;

  @Column(nullable = false)
  private String email;

  @Column(name = "thumbnail_key")
  private String thumbnailKey;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;
}
