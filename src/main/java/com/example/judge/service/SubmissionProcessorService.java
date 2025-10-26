package com.example.judge.service;

import com.example.judge.model.Submission;
import com.example.judge.model.SubmissionStatus;
import com.example.judge.repository.SubmissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SubmissionProcessorService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionProcessorService.class);

    private final SubmissionRepository submissionRepository;
    private final CodeJudgeService codeJudgeService;

    public SubmissionProcessorService(SubmissionRepository submissionRepository, CodeJudgeService codeJudgeService) {
        this.submissionRepository = submissionRepository;
        this.codeJudgeService = codeJudgeService;
    }

    @Async
    public void processSubmission(Long submissionId) {
        Submission s = submissionRepository.findById(submissionId).orElse(null);
        if (s == null) return;
        s.setStatus(SubmissionStatus.RUNNING);
        submissionRepository.save(s);

        log.info("Processing submission {}", submissionId);
        codeJudgeService.runSubmission(submissionId);
    }
}
