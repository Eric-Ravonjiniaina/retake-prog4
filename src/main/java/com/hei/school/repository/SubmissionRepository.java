package com.hei.school.repository;

import com.hei.school.entity.Submission;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {}
