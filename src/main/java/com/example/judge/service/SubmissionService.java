package com.example.judge.service;

import com.example.judge.model.Submission;
import com.example.judge.model.SubmissionStatus;
import com.example.judge.repository.SubmissionRepository;
import org.springframework.stereotype.Service;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionProcessorService processorService;

    public SubmissionService(SubmissionRepository submissionRepository, SubmissionProcessorService processorService) {
        this.submissionRepository = submissionRepository;
        this.processorService = processorService;
    }

    public Submission createSubmission(Submission submission) {
        submission.setStatus(SubmissionStatus.PENDING);
        Submission saved = submissionRepository.save(submission);
        // async process
        processorService.processSubmission(saved.getId());
        return saved;
    }
}
