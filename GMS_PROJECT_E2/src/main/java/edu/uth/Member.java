package edu.uth;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Member extends User {
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String membershipId;
    private Subscription subscription;
    private List<WorkoutSchedule> schedules = new ArrayList<>();
    private List<Attendance> attendances = new ArrayList<>();
    private List<WorkoutProgress> progressRecords = new ArrayList<>();

    public Member(String userId, String username, String password, String email, String membershipId) {
        this(userId, username, password, email, "N/A", LocalDate.of(2000, 1, 1), membershipId);
    }

    public Member(String userId, String username, String password, String email, String phoneNumber, LocalDate dateOfBirth, String membershipId) {
        super(userId, username, password, email, Role.MEMBER);
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.membershipId = membershipId;
    }

    // Methods from Class Diagram
    public void register() {
        System.out.println("Member registered successfully: " + getUsername() + " (ID: " + getUserId() + ")");
    }

    public void viewSchedule() {
        System.out.println("\n--- Workout Schedules for " + getUsername() + " ---");
        if (schedules.isEmpty()) {
            System.out.println("No workout schedules assigned yet.");
        } else {
            for (WorkoutSchedule s : schedules) {
                System.out.println("---------------------------");
                System.out.println(s);
                System.out.println("---------------------------");
            }
        }
    }

    public void updateProgress() {
        System.out.println("Updating progress for member: " + getUsername());
    }

    public void viewReports() {
        System.out.println("\n========== PERSONAL REPORT FOR " + getUsername().toUpperCase() + " ==========");
        System.out.println("Attendance Percentage: " + String.format("%.2f%%", calculateAttendancePercentage()));
        System.out.println("Total Attendance Records: " + attendances.size());
        System.out.println("Total Workout Schedules: " + schedules.size());
        System.out.println("Total Progress Entries: " + progressRecords.size());
        if (!progressRecords.isEmpty()) {
            double avgScore = progressRecords.stream().mapToDouble(WorkoutProgress::getProgressScore).average().orElse(0.0);
            System.out.println("Average Progress Score: " + String.format("%.2f", avgScore));
            System.out.println("Latest Progress Record:\n" + progressRecords.get(progressRecords.size() - 1));
        }
        System.out.println("=============================================================\n");
    }

    public void renewSubscription() {
        if (subscription != null) {
            subscription.renew();
            System.out.println("Subscription renewed for member " + getUsername() + ".");
        } else {
            System.out.println("No active subscription to renew for member " + getUsername() + ".");
        }
    }

    public void viewSubscriptionStatus() {
        System.out.println("\n--- Subscription Status for " + getUsername() + " ---");
        if (subscription == null) {
            System.out.println("No subscription assigned.");
        } else {
            System.out.println(subscription);
        }
    }

    public double calculateAttendancePercentage() {
        if (attendances.isEmpty()) return 0.0;
        long present = attendances.stream()
                .filter(a -> "present".equalsIgnoreCase(a.getStatus()))
                .count();
        return present * 100.0 / attendances.size();
    }

    // Getters and Setters
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public List<WorkoutSchedule> getSchedules() {
        return schedules;
    }

    public List<Attendance> getAttendances() {
        return attendances;
    }

    public List<WorkoutProgress> getProgressRecords() {
        return progressRecords;
    }

    @Override
    public String toString() {
        return super.toString() + "\n" +
               "membershipId: " + membershipId + "\n" +
               "phoneNumber: " + phoneNumber + "\n" +
               "dateOfBirth: " + dateOfBirth;
    }
}
