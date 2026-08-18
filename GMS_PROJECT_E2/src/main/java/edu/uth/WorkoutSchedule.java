package edu.uth;

import java.time.LocalDate;

public class WorkoutSchedule {
    private String scheduleId;
    private String description;
    private LocalDate scheduleDate;
    private String status;

    public WorkoutSchedule(String scheduleId, String description, LocalDate scheduleDate, String status) {
        this.scheduleId = scheduleId;
        this.description = description;
        this.scheduleDate = scheduleDate;
        this.status = status;
    }

    public void create() {
        System.out.println("Workout Schedule created: " + scheduleId);
    }

    public void update() {
        System.out.println("Workout Schedule updated: " + scheduleId);
    }

    public void update(String description) {
        this.description = description;
        update();
    }

    public void assignToMember() {
        System.out.println("Schedule " + scheduleId + " assigned to member.");
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "scheduleId: " + scheduleId + "\n" +
               "description: " + description + "\n" +
               "scheduleDate: " + scheduleDate + "\n" +
               "status: " + status;
    }
}
