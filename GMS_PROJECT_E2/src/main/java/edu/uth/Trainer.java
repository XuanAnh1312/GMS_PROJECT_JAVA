package edu.uth;

public class Trainer extends User {
    private String specialization;

    public Trainer(String userId, String username, String password, String email, String specialization) {
        super(userId, username, password, email, Role.TRAINER);
        this.specialization = specialization;
    }

    public void assignSchedule() {
        System.out.println("Trainer " + getUsername() + " is assigning a schedule.");
    }

    public void assignSchedule(WorkoutSchedule schedule, Member member) {
        if (schedule != null && member != null) {
            if (!member.getSchedules().contains(schedule)) {
                member.getSchedules().add(schedule);
            }
            schedule.assignToMember();
            System.out.println("Trainer " + getUsername() + " assigned schedule (" + schedule.getScheduleId() + ") to member " + member.getUsername());
        }
    }

    public void updateSchedule() {
        System.out.println("Trainer " + getUsername() + " is updating a workout schedule.");
    }

    public void updateSchedule(WorkoutSchedule schedule, String description) {
        if (schedule != null) {
            schedule.update(description);
            System.out.println("Trainer " + getUsername() + " updated schedule " + schedule.getScheduleId() + " description to: " + description);
        }
    }

    public void viewSchedule() {
        System.out.println("Trainer " + getUsername() + " is viewing workout schedules.");
    }

    public void viewSchedule(Member member) {
        if (member != null) {
            System.out.println("Schedules for member " + member.getUsername() + ":");
            if (member.getSchedules().isEmpty()) {
                System.out.println("  No schedules assigned.");
            } else {
                for (WorkoutSchedule s : member.getSchedules()) {
                    System.out.println("---------------------------");
                    System.out.println(s);
                    System.out.println("---------------------------");
                }
            }
        }
    }

    public void trackAttendance() {
        System.out.println("Trainer " + getUsername() + " is tracking member attendance.");
    }

    public void trackAttendance(Member member, Attendance attendance) {
        if (member != null && attendance != null) {
            attendance.setMemberUsername(member.getUsername());
            member.getAttendances().add(attendance);
            attendance.record();
            System.out.println("Trainer " + getUsername() + " recorded attendance for " + member.getUsername());
        }
    }

    public void trackProgress() {
        System.out.println("Trainer " + getUsername() + " is tracking member progress.");
    }

    public void trackProgress(WorkoutProgress progress, Member member) {
        if (member != null && progress != null) {
            progress.setMemberUsername(member.getUsername());
            member.getProgressRecords().add(progress);
            progress.updateProgress();
            System.out.println("Trainer " + getUsername() + " recorded progress for " + member.getUsername() + ":\n" + progress);
        }
    }

    public void generateProgressReport() {
        System.out.println("Trainer " + getUsername() + " is generating an overall progress report.");
    }

    public void generateProgressReport(Member member) {
        if (member != null) {
            member.viewReports();
        }
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String toString() {
        return super.toString() + "\n" +
               "specialization: " + specialization;
    }
}
