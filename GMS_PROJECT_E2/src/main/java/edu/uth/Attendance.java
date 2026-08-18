package edu.uth;

import java.time.LocalDateTime;

public class Attendance {
    private String attendanceId;
    private String memberUsername;
    private LocalDateTime checkIn;
    private String status;

    public Attendance(String attendanceId, LocalDateTime checkIn, String status) {
        this(attendanceId, null, checkIn, status);
    }

    public Attendance(String attendanceId, String memberUsername, LocalDateTime checkIn, String status) {
        this.attendanceId = attendanceId;
        this.memberUsername = memberUsername;
        this.checkIn = checkIn;
        this.status = status;
    }

    public void record() {
        System.out.println("Attendance recorded: " + attendanceId + " for member " + (memberUsername != null ? memberUsername : "N/A"));
    }

    public String getAttendanceId() {
        return attendanceId;
    }

    public String getMemberUsername() {
        return memberUsername;
    }

    public void setMemberUsername(String memberUsername) {
        this.memberUsername = memberUsername;
    }

    public LocalDateTime getCheckIn() {
        return checkIn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "attendanceId: " + attendanceId + "\n" +
               "memberUsername: " + (memberUsername != null ? memberUsername : "N/A") + "\n" +
               "checkIn: " + checkIn + "\n" +
               "status: " + status;
    }
}
