package com.example.judge.service;

import com.example.judge.model.Submission;
import com.example.judge.model.TestCase;
import com.example.judge.model.Problem;
import com.example.judge.model.SubmissionStatus;
import com.example.judge.repository.ProblemRepository;
import com.example.judge.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CodeJudgeService {

    private final SubmissionRepository submissionRepository;
    private final ProblemRepository problemRepository;

    @Value("${judge.docker.image:judge-java:latest}")
    private String judgeImage;

    public CodeJudgeService(SubmissionRepository submissionRepository, ProblemRepository problemRepository) {
        this.submissionRepository = submissionRepository;
        this.problemRepository = problemRepository;
    }

    public void runSubmission(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId).orElse(null);
        if (submission == null) return;

        Problem problem = problemRepository.findById(submission.getProblemId()).orElse(null);
        if (problem == null) {
            submission.setStatus(SubmissionStatus.WRONG_ANSWER);
            submissionRepository.save(submission);
            return;
        }

        Path baseDir = Path.of(System.getProperty("java.io.tmpdir"), "submissions", String.valueOf(submissionId));
        try {
            Files.createDirectories(baseDir);
            Path sourceFile = baseDir.resolve("Main.java");
            Files.writeString(sourceFile, submission.getCode());

            List<TestCase> cases = problem.getTestCases();
            for (TestCase tc : cases) {
                String hostPath = baseDir.toAbsolutePath().toString();
                String[] cmd = new String[]{"docker", "run", "--rm", "--memory=256m", "--stop-timeout", "5", "-v", hostPath + ":/app", judgeImage, "sh", "-c", "javac Main.java 2> compile.err && java Main"};

                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.directory(baseDir.toFile());
                Process p = pb.start();

                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()))) {
                    writer.write(tc.getInput() == null ? "" : tc.getInput());
                    writer.flush();
                } catch (IOException e) {
                    // ignore
                }

                boolean finished = p.waitFor(6, TimeUnit.SECONDS);
                if (!finished) {
                    p.destroyForcibly();
                    submission.setStatus(SubmissionStatus.TIME_LIMIT_EXCEEDED);
                    submission.setOutput("Time limit exceeded");
                    submissionRepository.save(submission);
                    return;
                }

                String stdout = readStream(p.getInputStream());
                String stderr = readStream(p.getErrorStream());

                Path compileErr = baseDir.resolve("compile.err");
                if (Files.exists(compileErr)) {
                    String compileMsg = Files.readString(compileErr);
                    if (compileMsg != null && !compileMsg.isBlank()) {
                        submission.setStatus(SubmissionStatus.COMPILATION_ERROR);
                        submission.setOutput(compileMsg);
                        submissionRepository.save(submission);
                        return;
                    }
                }

                String expected = tc.getExpectedOutput() == null ? "" : tc.getExpectedOutput();
                if (!stdout.strip().equals(expected.strip())) {
                    submission.setStatus(SubmissionStatus.WRONG_ANSWER);
                    submission.setOutput("Expected:\n" + expected + "\nGot:\n" + stdout + "\nStderr:\n" + stderr);
                    submissionRepository.save(submission);
                    return;
                }
            }

            submission.setStatus(SubmissionStatus.ACCEPTED);
            submission.setOutput("All tests passed");
            submissionRepository.save(submission);

        } catch (Exception e) {
            submission.setStatus(SubmissionStatus.COMPILATION_ERROR);
            submission.setOutput("Exception: " + e.getMessage());
            submissionRepository.save(submission);
        } finally {
            try {
                Files.walk(baseDir)
                        .sorted((a, b) -> b.compareTo(a))
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException ignored) {}
        }
    }

    private String readStream(InputStream is) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        }
    }
}
