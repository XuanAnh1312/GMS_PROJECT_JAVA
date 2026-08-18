package edu.uth;

import java.time.LocalDate;

public class WorkoutProgress {
    private String progressId;
    private String memberUsername;
    private LocalDate recordedDate;
    private String metrics;
    private double progressScore;

    public WorkoutProgress(String progressId, LocalDate recordedDate, String metrics, double progressScore) {
        this(progressId, null, recordedDate, metrics, progressScore);
    }

    public WorkoutProgress(String progressId, String memberUsername, LocalDate recordedDate, String metrics, double progressScore) {
        this.progressId = progressId;
        this.memberUsername = memberUsername;
        this.recordedDate = recordedDate;
        this.metrics = metrics;
        this.progressScore = progressScore;
    }

    public void updateProgress() {
        System.out.println("Workout progress " + progressId + " updated.");
    }

    public void updateProgress(String metrics, double progressScore) {
        this.metrics = metrics;
        this.progressScore = progressScore;
        updateProgress();
    }

    public String getProgressId() {
        return progressId;
    }

    public String getMemberUsername() {
        return memberUsername;
    }

    public void setMemberUsername(String memberUsername) {
        this.memberUsername = memberUsername;
    }

    public LocalDate getRecordedDate() {
        return recordedDate;
    }

    public String getMetrics() {
        return metrics;
    }

    public void setMetrics(String metrics) {
        this.metrics = metrics;
    }

    public double getProgressScore() {
        return progressScore;
    }

    public void setProgressScore(double progressScore) {
        this.progressScore = progressScore;
    }

    @Override
    public String toString() {
        return "progressId: " + progressId + "\n" +
               "memberUsername: " + (memberUsername != null ? memberUsername : "N/A") + "\n" +
               "recordedDate: " + recordedDate + "\n" +
               "metrics: " + metrics + "\n" +
               "progressScore: " + progressScore;
    }
}
