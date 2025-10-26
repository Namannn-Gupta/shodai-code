package com.example.judge.model;

import jakarta.persistence.*;

@Entity
public class TestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String input;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String expectedOutput;

    @ManyToOne
    private Problem problem;

    public TestCase() {}
    public TestCase(String input, String expectedOutput) { this.input = input; this.expectedOutput = expectedOutput; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
    public String getExpectedOutput() { return expectedOutput; }
    public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }
    public Problem getProblem() { return problem; }
    public void setProblem(Problem problem) { this.problem = problem; }
}
