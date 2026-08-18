package edu.uth;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportExporter {

    public static void exportRevenueReport(List<Subscription> subscriptions, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("SubscriptionID,StartDate,EndDate,Status,PlanName,AmountPaid");
            double totalRevenue = 0;
            for (Subscription sub : subscriptions) {
                writer.printf("%s,%s,%s,%s,%s,%.2f\n",
                        sub.getSubscriptionId(),
                        sub.getStartDate(),
                        sub.getEndDate(),
                        sub.getStatus(),
                        sub.getPlan() != null ? sub.getPlan().getPlanName() : "N/A",
                        sub.getAmountPaid());
                if ("active".equalsIgnoreCase(sub.getStatus())) {
                    totalRevenue += sub.getAmountPaid();
                }
            }
            writer.printf("TOTAL_REVENUE,,,,,\n");
            writer.printf("Active Total,,,,,%.2f\n", totalRevenue);
            System.out.println("Revenue report successfully exported to " + filename);
        } catch (IOException e) {
            System.err.println("Error exporting revenue report: " + e.getMessage());
        }
    }

    public static void exportAttendanceReport(List<Attendance> attendances, List<Member> members, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("AttendanceID,MemberUsername,CheckInTime,Status");
            for (Attendance att : attendances) {
                writer.printf("%s,%s,%s,%s\n",
                        att.getAttendanceId(),
                        att.getMemberUsername() != null ? att.getMemberUsername() : "Unknown",
                        att.getCheckIn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        att.getStatus());
            }
            System.out.println("Attendance report successfully exported to " + filename);
        } catch (IOException e) {
            System.err.println("Error exporting attendance report: " + e.getMessage());
        }
    }

    public static void exportProgressReport(List<Member> members, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("MemberUsername,ProgressID,RecordedDate,Metrics,ProgressScore");
            for (Member m : members) {
                for (WorkoutProgress p : m.getProgressRecords()) {
                    writer.printf("%s,%s,%s,%s,%.2f\n",
                            m.getUsername(),
                            p.getProgressId(),
                            p.getRecordedDate(),
                            p.getMetrics(),
                            p.getProgressScore());
                }
            }
            System.out.println("Progress report successfully exported to " + filename);
        } catch (IOException e) {
            System.err.println("Error exporting progress report: " + e.getMessage());
        }
    }

    // Default overloaded methods matching Class Diagram zero-arg or simple signatures
    public void exportRevenueReport() {
        exportRevenueReport(Main.getSubscriptions(), "revenue_report.csv");
    }

    public void exportAttendanceReport() {
        exportAttendanceReport(Main.getAttendances(), Main.getMembers(), "attendance_report.csv");
    }

    public void exportProgressReport() {
        exportProgressReport(Main.getMembers(), "progress_report.csv");
    }
}
