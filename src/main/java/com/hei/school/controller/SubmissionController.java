package com.hei.school.controller;

import com.hei.school.dto.Submission;
import com.hei.school.mapper.SubmissionMapper;
import com.hei.school.service.SubmissionService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class SubmissionController {

  private final SubmissionService submissionService;
  private final SubmissionMapper submissionMapper;

  @PostMapping(path = "/submissions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Submission createSubmission(
      @RequestParam("file") MultipartFile file, @RequestParam("email") String email) {
    return submissionMapper.toDto(submissionService.create(file, email));
  }

  @GetMapping("/submissions")
  public List<Submission> listSubmissions() {
    return submissionService.findAll().stream()
        .map(submissionMapper::toDto)
        .collect(Collectors.toUnmodifiableList());
  }
}
