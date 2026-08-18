package edu.uth;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataManager {

    private static final String DATA_DIR = "data";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static void ensureDataDir() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static void saveAllData(
            List<User> users,
            List<Member> members,
            List<Trainer> trainers,
            List<Admin> admins,
            List<Facility> facilities,
            List<SubscriptionPlan> plans,
            List<Subscription> subscriptions,
            List<WorkoutSchedule> schedules,
            List<Attendance> attendances) {

        ensureDataDir();
        saveFacilities(facilities);
        savePlans(plans);
        saveUsers(users, members, trainers, admins);
        saveSubscriptions(subscriptions, members);
        saveSchedules(schedules, members);
        saveAttendances(attendances);
        saveProgress(members);
    }

    private static void saveFacilities(List<Facility> facilities) {
        try (PrintWriter w = new PrintWriter(new FileWriter(new File(DATA_DIR, "facilities.csv")))) {
            w.println("facilityId,name,status");
            for (Facility f : facilities) {
                w.printf("%s,%s,%s\n", escapeCsv(f.getFacilityId()), escapeCsv(f.getName()), escapeCsv(f.getStatus()));
            }
        } catch (IOException e) {
            System.err.println("Error saving facilities: " + e.getMessage());
        }
    }

    private static void savePlans(List<SubscriptionPlan> plans) {
        try (PrintWriter w = new PrintWriter(new FileWriter(new File(DATA_DIR, "plans.csv")))) {
            w.println("planId,planName,price,durationMonths");
            for (SubscriptionPlan p : plans) {
                w.printf("%s,%s,%.2f,%d\n", escapeCsv(p.getPlanId()), escapeCsv(p.getPlanName()), p.getPrice(), p.getDurationMonths());
            }
        } catch (IOException e) {
            System.err.println("Error saving plans: " + e.getMessage());
        }
    }

    private static void saveUsers(List<User> users, List<Member> members, List<Trainer> trainers, List<Admin> admins) {
        try (PrintWriter w = new PrintWriter(new FileWriter(new File(DATA_DIR, "users.csv")))) {
            w.println("role,userId,username,password,email,extra1,extra2,extra3");
            for (User u : users) {
                if (u instanceof Admin) {
                    Admin a = (Admin) u;
                    w.printf("ADMIN,%s,%s,%s,%s,%s,,\n",
                            escapeCsv(a.getUserId()), escapeCsv(a.getUsername()), escapeCsv(a.getPassword()), escapeCsv(a.getEmail()), escapeCsv(a.getAdminLevel()));
                } else if (u instanceof Trainer) {
                    Trainer t = (Trainer) u;
                    w.printf("TRAINER,%s,%s,%s,%s,%s,,\n",
                            escapeCsv(t.getUserId()), escapeCsv(t.getUsername()), escapeCsv(t.getPassword()), escapeCsv(t.getEmail()), escapeCsv(t.getSpecialization()));
                } else if (u instanceof Member) {
                    Member m = (Member) u;
                    String phone = m.getPhoneNumber() != null ? m.getPhoneNumber() : "N/A";
                    String dob = m.getDateOfBirth() != null ? m.getDateOfBirth().toString() : "2000-01-01";
                    String memId = m.getMembershipId() != null ? m.getMembershipId() : "M-" + m.getUserId();
                    w.printf("MEMBER,%s,%s,%s,%s,%s,%s,%s\n",
                            escapeCsv(m.getUserId()), escapeCsv(m.getUsername()), escapeCsv(m.getPassword()), escapeCsv(m.getEmail()),
                            escapeCsv(phone), escapeCsv(dob), escapeCsv(memId));
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    private static void saveSubscriptions(List<Subscription> subscriptions, List<Member> members) {
        try (PrintWriter w = new PrintWriter(new FileWriter(new File(DATA_DIR, "subscriptions.csv")))) {
            w.println("subscriptionId,memberUsername,startDate,endDate,status,planId,amountPaid");
            for (Member m : members) {
                Subscription s = m.getSubscription();
                if (s != null) {
                    String planId = s.getPlan() != null ? s.getPlan().getPlanId() : "NONE";
                    w.printf("%s,%s,%s,%s,%s,%s,%.2f\n",
                            escapeCsv(s.getSubscriptionId()),
                            escapeCsv(m.getUsername()),
                            s.getStartDate(),
                            s.getEndDate(),
                            escapeCsv(s.getStatus()),
                            escapeCsv(planId),
                            s.getAmountPaid());
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving subscriptions: " + e.getMessage());
        }
    }

    private static void saveSchedules(List<WorkoutSchedule> schedules, List<Member> members) {
        try (PrintWriter w = new PrintWriter(new FileWriter(new File(DATA_DIR, "schedules.csv")))) {
            w.println("scheduleId,memberUsername,description,scheduleDate,status");
            for (Member m : members) {
                for (WorkoutSchedule s : m.getSchedules()) {
                    w.printf("%s,%s,%s,%s,%s\n",
                            escapeCsv(s.getScheduleId()),
                            escapeCsv(m.getUsername()),
                            escapeCsv(s.getDescription()),
                            s.getScheduleDate(),
                            escapeCsv(s.getStatus()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving schedules: " + e.getMessage());
        }
    }

    private static void saveAttendances(List<Attendance> attendances) {
        try (PrintWriter w = new PrintWriter(new FileWriter(new File(DATA_DIR, "attendances.csv")))) {
            w.println("attendanceId,memberUsername,checkIn,status");
            for (Attendance a : attendances) {
                String checkInStr = a.getCheckIn() != null ? a.getCheckIn().format(DATETIME_FMT) : LocalDateTime.now().format(DATETIME_FMT);
                w.printf("%s,%s,%s,%s\n",
                        escapeCsv(a.getAttendanceId()),
                        escapeCsv(a.getMemberUsername() != null ? a.getMemberUsername() : ""),
                        checkInStr,
                        escapeCsv(a.getStatus()));
            }
        } catch (IOException e) {
            System.err.println("Error saving attendances: " + e.getMessage());
        }
    }

    private static void saveProgress(List<Member> members) {
        try (PrintWriter w = new PrintWriter(new FileWriter(new File(DATA_DIR, "progress.csv")))) {
            w.println("progressId,memberUsername,recordedDate,metrics,progressScore");
            for (Member m : members) {
                for (WorkoutProgress p : m.getProgressRecords()) {
                    w.printf("%s,%s,%s,%s,%.2f\n",
                            escapeCsv(p.getProgressId()),
                            escapeCsv(m.getUsername()),
                            p.getRecordedDate(),
                            escapeCsv(p.getMetrics()),
                            p.getProgressScore());
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving progress: " + e.getMessage());
        }
    }

    public static boolean loadAllData(
            List<User> users,
            List<Member> members,
            List<Trainer> trainers,
            List<Admin> admins,
            List<Facility> facilities,
            List<SubscriptionPlan> plans,
            List<Subscription> subscriptions,
            List<WorkoutSchedule> schedules,
            List<Attendance> attendances) {

        File userFile = new File(DATA_DIR, "users.csv");
        if (!userFile.exists()) {
            return false;
        }

        try {
            // Clear existing
            users.clear();
            members.clear();
            trainers.clear();
            admins.clear();
            facilities.clear();
            plans.clear();
            subscriptions.clear();
            schedules.clear();
            attendances.clear();

            // Load Facilities
            loadFacilities(facilities);
            // Load Plans
            loadPlans(plans);
            // Load Users
            loadUsers(users, members, trainers, admins);

            Map<String, SubscriptionPlan> planMap = new HashMap<>();
            for (SubscriptionPlan p : plans) planMap.put(p.getPlanId(), p);

            Map<String, Member> memberMap = new HashMap<>();
            for (Member m : members) memberMap.put(m.getUsername().toLowerCase(), m);

            // Load Subscriptions
            loadSubscriptions(subscriptions, memberMap, planMap);
            // Load Schedules
            loadSchedules(schedules, memberMap);
            // Load Attendances
            loadAttendances(attendances, memberMap);
            // Load Progress
            loadProgress(memberMap);

            return true;
        } catch (Exception e) {
            System.err.println("Failed to load existing data: " + e.getMessage());
            return false;
        }
    }

    private static void loadFacilities(List<Facility> facilities) {
        File file = new File(DATA_DIR, "facilities.csv");
        if (!file.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line = r.readLine(); // header
            while ((line = r.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = parseCsvLine(line);
                if (parts.length >= 3) {
                    facilities.add(new Facility(parts[0], parts[1], parts[2]));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading facilities: " + e.getMessage());
        }
    }

    private static void loadPlans(List<SubscriptionPlan> plans) {
        File file = new File(DATA_DIR, "plans.csv");
        if (!file.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line = r.readLine();
            while ((line = r.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = parseCsvLine(line);
                if (parts.length >= 4) {
                    plans.add(new SubscriptionPlan(parts[0], parts[1], Double.parseDouble(parts[2]), Integer.parseInt(parts[3])));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading plans: " + e.getMessage());
        }
    }

    private static void loadUsers(List<User> users, List<Member> members, List<Trainer> trainers, List<Admin> admins) {
        File file = new File(DATA_DIR, "users.csv");
        if (!file.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line = r.readLine();
            while ((line = r.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = parseCsvLine(line);
                if (parts.length >= 5) {
                    String role = parts[0];
                    String userId = parts[1];
                    String username = parts[2];
                    String password = parts[3];
                    String email = parts[4];

                    if ("ADMIN".equalsIgnoreCase(role)) {
                        String level = parts.length > 5 ? parts[5] : "super";
                        Admin a = new Admin(userId, username, password, email, level);
                        admins.add(a);
                        users.add(a);
                    } else if ("TRAINER".equalsIgnoreCase(role)) {
                        String spec = parts.length > 5 ? parts[5] : "General";
                        Trainer t = new Trainer(userId, username, password, email, spec);
                        trainers.add(t);
                        users.add(t);
                    } else if ("MEMBER".equalsIgnoreCase(role)) {
                        String phone = parts.length > 5 ? parts[5] : "N/A";
                        LocalDate dob = LocalDate.of(2000, 1, 1);
                        if (parts.length > 6 && !parts[6].isEmpty() && !parts[6].equalsIgnoreCase("N/A")) {
                            try { dob = LocalDate.parse(parts[6]); } catch (Exception ignored) {}
                        }
                        String memId = parts.length > 7 ? parts[7] : "M-" + userId;
                        Member m = new Member(userId, username, password, email, phone, dob, memId);
                        members.add(m);
                        users.add(m);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    private static void loadSubscriptions(List<Subscription> subscriptions, Map<String, Member> memberMap, Map<String, SubscriptionPlan> planMap) {
        File file = new File(DATA_DIR, "subscriptions.csv");
        if (!file.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line = r.readLine();
            while ((line = r.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = parseCsvLine(line);
                if (parts.length >= 7) {
                    String subId = parts[0];
                    String uname = parts[1];
                    LocalDate start = LocalDate.parse(parts[2]);
                    LocalDate end = LocalDate.parse(parts[3]);
                    String status = parts[4];
                    String planId = parts[5];
                    double amt = Double.parseDouble(parts[6]);

                    SubscriptionPlan plan = planMap.get(planId);
                    Subscription sub = new Subscription(subId, start, end, status, plan);
                    sub.setAmountPaid(amt);
                    subscriptions.add(sub);

                    Member m = memberMap.get(uname.toLowerCase());
                    if (m != null) {
                        m.setSubscription(sub);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading subscriptions: " + e.getMessage());
        }
    }

    private static void loadSchedules(List<WorkoutSchedule> schedules, Map<String, Member> memberMap) {
        File file = new File(DATA_DIR, "schedules.csv");
        if (!file.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line = r.readLine();
            while ((line = r.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = parseCsvLine(line);
                if (parts.length >= 5) {
                    String schId = parts[0];
                    String uname = parts[1];
                    String desc = parts[2];
                    LocalDate date = LocalDate.parse(parts[3]);
                    String status = parts[4];

                    WorkoutSchedule s = new WorkoutSchedule(schId, desc, date, status);
                    schedules.add(s);

                    Member m = memberMap.get(uname.toLowerCase());
                    if (m != null) {
                        m.getSchedules().add(s);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading schedules: " + e.getMessage());
        }
    }

    private static void loadAttendances(List<Attendance> attendances, Map<String, Member> memberMap) {
        File file = new File(DATA_DIR, "attendances.csv");
        if (!file.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line = r.readLine();
            while ((line = r.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = parseCsvLine(line);
                if (parts.length >= 4) {
                    String attId = parts[0];
                    String uname = parts[1];
                    LocalDateTime checkIn = LocalDateTime.parse(parts[2], DATETIME_FMT);
                    String status = parts[3];

                    Attendance a = new Attendance(attId, uname, checkIn, status);
                    attendances.add(a);

                    Member m = memberMap.get(uname.toLowerCase());
                    if (m != null) {
                        m.getAttendances().add(a);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading attendances: " + e.getMessage());
        }
    }

    private static void loadProgress(Map<String, Member> memberMap) {
        File file = new File(DATA_DIR, "progress.csv");
        if (!file.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line = r.readLine();
            while ((line = r.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = parseCsvLine(line);
                if (parts.length >= 5) {
                    String prgId = parts[0];
                    String uname = parts[1];
                    LocalDate date = LocalDate.parse(parts[2]);
                    String metrics = parts[3];
                    double score = Double.parseDouble(parts[4]);

                    WorkoutProgress p = new WorkoutProgress(prgId, uname, date, metrics, score);
                    Member m = memberMap.get(uname.toLowerCase());
                    if (m != null) {
                        m.getProgressRecords().add(p);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading progress: " + e.getMessage());
        }
    }

    private static String escapeCsv(String str) {
        if (str == null) return "";
        return str.replace(",", " ");
    }

    private static String[] parseCsvLine(String line) {
        return line.split(",");
    }
}
