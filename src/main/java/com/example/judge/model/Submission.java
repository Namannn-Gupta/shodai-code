package com.example.judge.model;

import jakarta.persistence.*;

@Entity
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long problemId;
    private Long contestId;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String code;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String output;

    public Submission() {}

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getProblemId() { return problemId; }
    public void setProblemId(Long problemId) { this.problemId = problemId; }
    public Long getContestId() { return contestId; }
    public void setContestId(Long contestId) { this.contestId = contestId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public SubmissionStatus getStatus() { return status; }
    public void setStatus(SubmissionStatus status) { this.status = status; }
    public String getOutput() { return output; }
    public void setOutput(String output) { this.output = output; }
}
