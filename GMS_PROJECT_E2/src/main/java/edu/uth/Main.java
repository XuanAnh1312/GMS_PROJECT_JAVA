package edu.uth;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {
    // Data stores
    private static List<User> users = new ArrayList<>();
    private static List<Member> members = new ArrayList<>();
    private static List<Trainer> trainers = new ArrayList<>();
    private static List<Admin> admins = new ArrayList<>();
    private static List<Facility> facilities = new ArrayList<>();
    private static List<SubscriptionPlan> plans = new ArrayList<>();
    private static List<Subscription> subscriptions = new ArrayList<>();
    private static List<WorkoutSchedule> schedules = new ArrayList<>();
    private static List<Attendance> attendances = new ArrayList<>();

    private static final ReportExporter reportExporter = new ReportExporter();

    public static void main(String[] args) {
        boolean loaded = DataManager.loadAllData(users, members, trainers, admins, facilities, plans, subscriptions, schedules, attendances);
        if (!loaded || users.isEmpty()) {
            seedData();
            saveData();
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            IO.println("\n========================================");
            IO.println("           GYM MANAGEMENT SYSTEM        ");
            IO.println("========================================");
            IO.println("1. LOG IN");
            IO.println("0. EXIT");
            IO.print("Select option: ");
            String opt = scanner.nextLine().trim();

            if (opt.equals("1")) {
                handleLogin(scanner);
            } else if (opt.equals("0")) {
                IO.println("Exiting Gym Management System. Goodbye!");
                break;
            } else {
                IO.println("Invalid selection. Please try again.");
            }
        }

        scanner.close();
    }

    private static void saveData() {
        DataManager.saveAllData(users, members, trainers, admins, facilities, plans, subscriptions, schedules, attendances);
    }

    private static void handleLogin(Scanner scanner) {
        IO.println("\n--- USER LOGIN ---");
        IO.print("Username: ");
        String username = scanner.nextLine().trim();
        IO.print("Password: ");
        String password = scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            IO.println("Operation failed. Reason: Username and password cannot be empty.");
            return;
        }

        User logged = authenticate(username, password);
        if (logged == null) {
            IO.println("Operation failed. Reason: Incorrect username or password.");
            return;
        }

        logged.login();
        IO.println("Login successful! Welcome, " + logged.getUsername() + " [" + logged.getRole().getRoleName() + "]");

        if (logged instanceof Admin) {
            adminMenu((Admin) logged, scanner);
        } else if (logged instanceof Trainer) {
            trainerMenu((Trainer) logged, scanner);
        } else if (logged instanceof Member) {
            memberMenu((Member) logged, scanner);
        }
    }

    private static void handleRegister(Scanner scanner) {
        IO.println("\n--- ADD NEW MEMBER (ADMIN ACTION) ---");
        IO.print("Enter desired Username: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) {
            IO.println("Operation failed. Reason: Username cannot be empty.");
            return;
        }
        if (findUserByUsername(username) != null) {
            IO.println("Operation failed. Reason: Username already taken.");
            return;
        }

        IO.print("Enter Password: ");
        String password = scanner.nextLine().trim();
        if (password.isEmpty()) {
            IO.println("Operation failed. Reason: Password cannot be empty.");
            return;
        }

        IO.print("Enter Email: ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty() || !email.contains("@")) {
            IO.println("Operation failed. Reason: Invalid email address.");
            return;
        }

        IO.print("Enter Phone Number: ");
        String phone = scanner.nextLine().trim();

        IO.print("Enter Date of Birth (YYYY-MM-DD): ");
        String dobStr = scanner.nextLine().trim();
        LocalDate dob = LocalDate.of(2000, 1, 1);
        if (!dobStr.isEmpty()) {
            try {
                dob = LocalDate.parse(dobStr);
            } catch (DateTimeParseException e) {
                IO.println("Invalid date format. Defaulting to 2000-01-01.");
            }
        }

        String userId = "M" + (members.size() + 101);
        String memId = "MEM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Member newMember = new Member(userId, username, password, email, phone, dob, memId);
        newMember.register();

        members.add(newMember);
        users.add(newMember);

        // Assign plan option
        if (!plans.isEmpty()) {
            IO.println("\nAvailable Subscription Plans:");
            for (int i = 0; i < plans.size(); i++) {
                SubscriptionPlan p = plans.get(i);
                IO.println((i + 1) + ". " + p.getPlanName() + " - $" + String.format("%.2f", p.getPrice()) + " / " + p.getDurationMonths() + " month(s)");
            }
            IO.print("Choose a plan number (or 0 to skip subscription for now): ");
            String pChoice = scanner.nextLine().trim();
            try {
                int pIdx = Integer.parseInt(pChoice) - 1;
                if (pIdx >= 0 && pIdx < plans.size()) {
                    SubscriptionPlan chosenPlan = plans.get(pIdx);
                    Subscription sub = new Subscription("SUB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                            LocalDate.now(), LocalDate.now().plusMonths(chosenPlan.getDurationMonths()), "active", chosenPlan);
                    sub.setAmountPaid(chosenPlan.getPrice());
                    newMember.setSubscription(sub);
                    subscriptions.add(sub);
                    IO.println("Subscription created: " + chosenPlan.getPlanName() + " attached to " + username);
                }
            } catch (NumberFormatException ignored) {}
        }

        saveData();
        IO.println("Registration completed successfully! You can now log in.");
    }

    private static User authenticate(String username, String password) {
        for (User u : users) {
            if (u.login(username, password)) {
                return u;
            }
        }
        return null;
    }

    // ==========================================
    // ADMIN MENU
    // ==========================================
    private static void adminMenu(Admin admin, Scanner scanner) {
        while (true) {
            IO.println("\n===== ADMIN MENU =====");
            IO.println("1. Manage Facilities");
            IO.println("2. Manage Trainers");
            IO.println("3. Manage Subscription Plans");
            IO.println("4. Manage Member Profiles");
            IO.println("5. Generate & Export Reports (Revenue & Attendance)");
            IO.println("0. Logout");
            IO.print("Select option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    manageFacilitiesMenu(admin, scanner);
                    break;
                case "2":
                    manageTrainersMenu(admin, scanner);
                    break;
                case "3":
                    managePlansMenu(admin, scanner);
                    break;
                case "4":
                    manageMembersMenu(admin, scanner);
                    break;
                case "5":
                    adminReportsMenu(admin, scanner);
                    break;
                case "0":
                    admin.logout();
                    return;
                default:
                    IO.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void manageFacilitiesMenu(Admin admin, Scanner scanner) {
        while (true) {
            IO.println("\n--- Manage Facilities ---");
            IO.println("1. View Facilities");
            IO.println("2. Add Facility");
            IO.println("3. Update Facility");
            IO.println("4. Delete Facility");
            IO.println("0. Back");
            IO.print("Select: ");
            String subChoice = scanner.nextLine().trim();

            switch (subChoice) {
                case "1":
                    admin.manageFacility(facilities);
                    break;
                case "2":
                    IO.print("Facility ID: ");
                    String fid = scanner.nextLine().trim();
                    if (fid.isEmpty()) { IO.println("Operation failed. Reason: Facility ID cannot be empty."); break; }
                    if (findFacilityById(fid) != null) { IO.println("Operation failed. Reason: Facility ID already exists."); break; }
                    IO.print("Facility Name: ");
                    String fname = scanner.nextLine().trim();
                    IO.print("Facility Status (e.g. Active, Maintenance): ");
                    String fstatus = scanner.nextLine().trim();
                    if (fstatus.isEmpty()) fstatus = "Active";

                    Facility nf = new Facility(fid, fname, fstatus);
                    facilities.add(nf);
                    saveData();
                    IO.println("Facility added successfully: " + nf);
                    break;
                case "3":
                    admin.manageFacility(facilities);
                    IO.print("Enter Facility ID to update: ");
                    String ufid = scanner.nextLine().trim();
                    Facility uf = findFacilityById(ufid);
                    if (uf == null) {
                        IO.println("Operation failed. Reason: Facility not found.");
                        break;
                    }
                    IO.print("New Name (leave blank to keep '" + uf.getName() + "'): ");
                    String nname = scanner.nextLine().trim();
                    if (nname.isEmpty()) nname = uf.getName();

                    IO.print("New Status (leave blank to keep '" + uf.getStatus() + "'): ");
                    String nstat = scanner.nextLine().trim();
                    if (nstat.isEmpty()) nstat = uf.getStatus();

                    uf.update(nname, nstat);
                    saveData();
                    IO.println("Facility updated successfully: " + uf);
                    break;
                case "4":
                    admin.manageFacility(facilities);
                    IO.print("Enter Facility ID to delete: ");
                    String dfid = scanner.nextLine().trim();
                    Facility df = findFacilityById(dfid);
                    if (df == null) {
                        IO.println("Operation failed. Reason: Facility not found.");
                        break;
                    }
                    facilities.remove(df);
                    saveData();
                    IO.println("Facility deleted successfully.");
                    break;
                case "0":
                    return;
                default:
                    IO.println("Invalid selection.");
            }
        }
    }

    private static void manageTrainersMenu(Admin admin, Scanner scanner) {
        while (true) {
            IO.println("\n--- Manage Trainers ---");
            IO.println("1. View Trainers");
            IO.println("2. Add Trainer");
            IO.println("3. Update Trainer");
            IO.println("4. Delete Trainer");
            IO.println("0. Back");
            IO.print("Select: ");
            String subChoice = scanner.nextLine().trim();

            switch (subChoice) {
                case "1":
                    admin.manageTrainer(trainers);
                    break;
                case "2":
                    IO.print("Trainer ID: ");
                    String tid = scanner.nextLine().trim();
                    if (tid.isEmpty()) { IO.println("Operation failed. Reason: Trainer ID cannot be empty."); break; }
                    if (findUserById(tid) != null) { IO.println("Operation failed. Reason: User ID already exists."); break; }

                    IO.print("Username: ");
                    String uname = scanner.nextLine().trim();
                    if (findUserByUsername(uname) != null) { IO.println("Operation failed. Reason: Username already exists."); break; }

                    IO.print("Password: ");
                    String pass = scanner.nextLine().trim();
                    IO.print("Email: ");
                    String email = scanner.nextLine().trim();
                    IO.print("Specialization (e.g. Strength, Yoga, Cardio): ");
                    String spec = scanner.nextLine().trim();
                    if (spec.isEmpty()) spec = "General Fitness";

                    Trainer nt = new Trainer(tid, uname, pass, email, spec);
                    trainers.add(nt);
                    users.add(nt);
                    saveData();
                    IO.println("Trainer added successfully: " + nt.getUsername());
                    break;
                case "3":
                    admin.manageTrainer(trainers);
                    IO.print("Enter Trainer ID to update: ");
                    String utid = scanner.nextLine().trim();
                    Trainer ut = findTrainerById(utid);
                    if (ut == null) {
                        IO.println("Operation failed. Reason: Trainer not found.");
                        break;
                    }
                    IO.print("New Email (leave blank to keep '" + ut.getEmail() + "'): ");
                    String nemail = scanner.nextLine().trim();
                    if (!nemail.isEmpty()) ut.setEmail(nemail);

                    IO.print("New Password (leave blank to keep current): ");
                    String npass = scanner.nextLine().trim();
                    if (!npass.isEmpty()) ut.setPassword(npass);

                    IO.print("New Specialization (leave blank to keep '" + ut.getSpecialization() + "'): ");
                    String nspec = scanner.nextLine().trim();
                    if (!nspec.isEmpty()) ut.setSpecialization(nspec);

                    saveData();
                    IO.println("Trainer updated successfully: " + ut.getUsername());
                    break;
                case "4":
                    admin.manageTrainer(trainers);
                    IO.print("Enter Trainer ID to delete: ");
                    String dtid = scanner.nextLine().trim();
                    Trainer dt = findTrainerById(dtid);
                    if (dt == null) {
                        IO.println("Operation failed. Reason: Trainer not found.");
                        break;
                    }
                    trainers.remove(dt);
                    users.remove(dt);
                    saveData();
                    IO.println("Trainer deleted successfully.");
                    break;
                case "0":
                    return;
                default:
                    IO.println("Invalid selection.");
            }
        }
    }

    private static void managePlansMenu(Admin admin, Scanner scanner) {
        while (true) {
            IO.println("\n--- Manage Subscription Plans ---");
            IO.println("1. View Plans");
            IO.println("2. Add Plan");
            IO.println("3. Update Plan");
            IO.println("4. Delete Plan");
            IO.println("0. Back");
            IO.print("Select: ");
            String subChoice = scanner.nextLine().trim();

            switch (subChoice) {
                case "1":
                    admin.managePlan(plans);
                    break;
                case "2":
                    IO.print("Plan ID: ");
                    String pid = scanner.nextLine().trim();
                    if (pid.isEmpty()) { IO.println("Operation failed. Reason: Plan ID cannot be empty."); break; }
                    if (findPlanById(pid) != null) { IO.println("Operation failed. Reason: Plan ID already exists."); break; }

                    IO.print("Plan Name: ");
                    String pname = scanner.nextLine().trim();
                    IO.print("Price ($): ");
                    double price;
                    try {
                        price = Double.parseDouble(scanner.nextLine().trim());
                    } catch (NumberFormatException e) {
                        IO.println("Operation failed. Reason: Invalid price number.");
                        break;
                    }
                    IO.print("Duration (months): ");
                    int duration;
                    try {
                        duration = Integer.parseInt(scanner.nextLine().trim());
                    } catch (NumberFormatException e) {
                        IO.println("Operation failed. Reason: Invalid duration months.");
                        break;
                    }

                    SubscriptionPlan np = new SubscriptionPlan(pid, pname, price, duration);
                    plans.add(np);
                    saveData();
                    IO.println("Subscription plan added successfully: " + np);
                    break;
                case "3":
                    admin.managePlan(plans);
                    IO.print("Enter Plan ID to update: ");
                    String upid = scanner.nextLine().trim();
                    SubscriptionPlan up = findPlanById(upid);
                    if (up == null) {
                        IO.println("Operation failed. Reason: Plan not found.");
                        break;
                    }
                    IO.print("New Plan Name (leave blank to keep '" + up.getPlanName() + "'): ");
                    String npname = scanner.nextLine().trim();
                    if (npname.isEmpty()) npname = up.getPlanName();

                    IO.print("New Price (leave blank to keep $" + up.getPrice() + "): ");
                    String nprStr = scanner.nextLine().trim();
                    double nprice = up.getPrice();
                    if (!nprStr.isEmpty()) {
                        try { nprice = Double.parseDouble(nprStr); } catch (NumberFormatException ignored) {}
                    }

                    IO.print("New Duration Months (leave blank to keep " + up.getDurationMonths() + "): ");
                    String ndurStr = scanner.nextLine().trim();
                    int ndur = up.getDurationMonths();
                    if (!ndurStr.isEmpty()) {
                        try { ndur = Integer.parseInt(ndurStr); } catch (NumberFormatException ignored) {}
                    }

                    up.updatePlan(npname, nprice, ndur);
                    saveData();
                    IO.println("Subscription plan updated successfully: " + up);
                    break;
                case "4":
                    admin.managePlan(plans);
                    IO.print("Enter Plan ID to delete: ");
                    String dpid = scanner.nextLine().trim();
                    SubscriptionPlan dp = findPlanById(dpid);
                    if (dp == null) {
                        IO.println("Operation failed. Reason: Plan not found.");
                        break;
                    }
                    plans.remove(dp);
                    saveData();
                    IO.println("Subscription plan deleted successfully.");
                    break;
                case "0":
                    return;
                default:
                    IO.println("Invalid selection.");
            }
        }
    }

    private static void manageMembersMenu(Admin admin, Scanner scanner) {
        while (true) {
            IO.println("\n--- Manage Member Profiles ---");
            IO.println("1. View All Members");
            IO.println("2. Add Member");
            IO.println("3. Update Member Profile");
            IO.println("4. Delete Member");
            IO.println("0. Back");
            IO.print("Select: ");
            String subChoice = scanner.nextLine().trim();

            switch (subChoice) {
                case "1":
                    IO.println("\n--- Members List ---");
                    for (Member m : members) {
                        IO.println("ID: " + m.getUserId() + " | Username: " + m.getUsername() + " | Email: " + m.getEmail() +
                                " | Phone: " + m.getPhoneNumber() + " | DOB: " + m.getDateOfBirth() + " | MembershipID: " + m.getMembershipId());
                        if (m.getSubscription() != null) {
                            IO.println("   Subscription: " + m.getSubscription().getStatus() + " until " + m.getSubscription().getEndDate());
                        } else {
                            IO.println("   Subscription: None");
                        }
                    }
                    break;
                case "2":
                    admin.addMember();
                    handleRegister(scanner); // Reuses validation and data saving
                    break;
                case "3":
                    admin.updateMember();
                    IO.print("Enter Member Username or ID: ");
                    String mid = scanner.nextLine().trim();
                    Member um = findMemberByIdOrUsername(mid);
                    if (um == null) {
                        IO.println("Operation failed. Reason: Member not found.");
                        break;
                    }
                    IO.print("New Email (leave blank to keep '" + um.getEmail() + "'): ");
                    String nemail = scanner.nextLine().trim();
                    if (!nemail.isEmpty()) admin.updateMemberProfile(um, nemail);

                    IO.print("New Phone Number (leave blank to keep '" + um.getPhoneNumber() + "'): ");
                    String nphone = scanner.nextLine().trim();
                    if (!nphone.isEmpty()) um.setPhoneNumber(nphone);

                    saveData();
                    IO.println("Member profile updated successfully: " + um.getUsername());
                    break;
                case "4":
                    admin.deleteMember();
                    IO.print("Enter Member Username or ID to delete: ");
                    String dmid = scanner.nextLine().trim();
                    Member dm = findMemberByIdOrUsername(dmid);
                    if (dm == null) {
                        IO.println("Operation failed. Reason: Member not found.");
                        break;
                    }
                    admin.deleteMember(dm, members);
                    users.remove(dm);
                    saveData();
                    IO.println("Member deleted successfully.");
                    break;
                case "0":
                    return;
                default:
                    IO.println("Invalid selection.");
            }
        }
    }

    private static void adminReportsMenu(Admin admin, Scanner scanner) {
        while (true) {
            IO.println("\n--- Reports & Analytics ---");
            IO.println("1. View Revenue Report");
            IO.println("2. View Attendance Report");
            IO.println("3. Export Revenue Report to CSV");
            IO.println("4. Export Attendance Report to CSV");
            IO.println("5. View Top Performing Members");
            IO.println("0. Back");
            IO.print("Select: ");
            String subChoice = scanner.nextLine().trim();

            switch (subChoice) {
                case "1":
                    admin.generateRevenueReport(subscriptions);
                    break;
                case "2":
                    admin.generateAttendanceReport(attendances);
                    break;
                case "3":
                    ReportExporter.exportRevenueReport(subscriptions, "revenue_report.csv");
                    break;
                case "4":
                    ReportExporter.exportAttendanceReport(attendances, members, "attendance_report.csv");
                    break;
                case "5":
                    showTopPerformingMembers();
                    break;
                case "0":
                    return;
                default:
                    IO.println("Invalid selection.");
            }
        }
    }

    private static void showTopPerformingMembers() {
        IO.println("\n========== TOP PERFORMING MEMBERS ==========");
        if (members.isEmpty()) {
            IO.println("No members registered.");
            return;
        }
        List<Member> sorted = new ArrayList<>(members);
        sorted.sort((m1, m2) -> {
            double avg1 = m1.getProgressRecords().stream().mapToDouble(WorkoutProgress::getProgressScore).average().orElse(0.0);
            double avg2 = m2.getProgressRecords().stream().mapToDouble(WorkoutProgress::getProgressScore).average().orElse(0.0);
            return Double.compare(avg2, avg1);
        });

        int rank = 1;
        for (Member m : sorted) {
            double avgScore = m.getProgressRecords().stream().mapToDouble(WorkoutProgress::getProgressScore).average().orElse(0.0);
            IO.printf("Rank %d: %s | Avg Progress Score: %.2f | Attendance: %.2f%%\n",
                    rank++, m.getUsername(), avgScore, m.calculateAttendancePercentage());
        }
        IO.println("============================================\n");
    }

    // ==========================================
    // TRAINER MENU
    // ==========================================
    private static void trainerMenu(Trainer trainer, Scanner scanner) {
        while (true) {
            IO.println("\n===== TRAINER MENU =====");
            IO.println("1. Manage Workout Schedules to Members (Assign / Update / View)");
            IO.println("2. Track Member Attendance & Progress");
            IO.println("3. Generate & Export Reports (Progress & Attendance)");
            IO.println("0. Logout");
            IO.print("Select option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    trainerSchedulesSubMenu(trainer, scanner);
                    break;
                case "2":
                    trainerTrackSubMenu(trainer, scanner);
                    break;
                case "3":
                    trainerReportsSubMenu(trainer, scanner);
                    break;
                case "0":
                    trainer.logout();
                    return;
                default:
                    IO.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void trainerSchedulesSubMenu(Trainer trainer, Scanner scanner) {
        while (true) {
            IO.println("\n--- Manage Workout Schedules ---");
            IO.println("1. Assign Workout Schedule to Member");
            IO.println("2. Update Member Schedule");
            IO.println("3. View Member Workout Schedules");
            IO.println("0. Back");
            IO.print("Select: ");
            String subChoice = scanner.nextLine().trim();

            switch (subChoice) {
                case "1":
                    trainer.assignSchedule();
                    IO.print("Enter Member Username or ID: ");
                    String mname = scanner.nextLine().trim();
                    Member targetMember = findMemberByIdOrUsername(mname);
                    if (targetMember == null) {
                        IO.println("Operation failed. Reason: Member not found.");
                        break;
                    }
                    IO.print("Enter Schedule Description (e.g. Upper Body Workout): ");
                    String desc = scanner.nextLine().trim();
                    if (desc.isEmpty()) desc = "General Workout Schedule";

                    String schId = "SCH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                    WorkoutSchedule ws = new WorkoutSchedule(schId, desc, LocalDate.now(), "active");
                    ws.create();
                    schedules.add(ws);

                    trainer.assignSchedule(ws, targetMember);
                    saveData();
                    IO.println("Schedule assigned successfully!");
                    break;
                case "2":
                    trainer.updateSchedule();
                    IO.print("Enter Member Username or ID: ");
                    String mnameUp = scanner.nextLine().trim();
                    Member memberUp = findMemberByIdOrUsername(mnameUp);
                    if (memberUp == null || memberUp.getSchedules().isEmpty()) {
                        IO.println("Operation failed. Reason: Member not found or has no assigned schedules.");
                        break;
                    }
                    IO.println("Schedules for " + memberUp.getUsername() + ":");
                    for (int i = 0; i < memberUp.getSchedules().size(); i++) {
                        IO.println((i + 1) + ". " + memberUp.getSchedules().get(i));
                    }
                    IO.print("Select schedule number to update: ");
                    try {
                        int sIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                        if (sIdx >= 0 && sIdx < memberUp.getSchedules().size()) {
                            WorkoutSchedule targetSch = memberUp.getSchedules().get(sIdx);
                            IO.print("Enter new description: ");
                            String newDesc = scanner.nextLine().trim();
                            trainer.updateSchedule(targetSch, newDesc);
                            saveData();
                            IO.println("Schedule updated successfully!");
                        } else {
                            IO.println("Operation failed. Reason: Invalid schedule choice.");
                        }
                    } catch (NumberFormatException e) {
                        IO.println("Operation failed. Reason: Invalid index format.");
                    }
                    break;
                case "3":
                    trainer.viewSchedule();
                    IO.print("Enter Member Username or ID: ");
                    String mnameV = scanner.nextLine().trim();
                    Member memberV = findMemberByIdOrUsername(mnameV);
                    if (memberV == null) {
                        IO.println("Operation failed. Reason: Member not found.");
                        break;
                    }
                    trainer.viewSchedule(memberV);
                    break;
                case "0":
                    return;
                default:
                    IO.println("Invalid selection.");
            }
        }
    }

    private static void trainerTrackSubMenu(Trainer trainer, Scanner scanner) {
        while (true) {
            IO.println("\n--- Track Member Attendance & Progress ---");
            IO.println("1. Record Attendance for Member");
            IO.println("2. Record Workout Progress for Member");
            IO.println("0. Back");
            IO.print("Select: ");
            String subChoice = scanner.nextLine().trim();

            switch (subChoice) {
                case "1":
                    trainer.trackAttendance();
                    IO.print("Enter Member Username or ID: ");
                    String maName = scanner.nextLine().trim();
                    Member attMember = findMemberByIdOrUsername(maName);
                    if (attMember == null) {
                        IO.println("Operation failed. Reason: Member not found.");
                        break;
                    }
                    IO.print("Attendance status (present / absent): ");
                    String status = scanner.nextLine().trim().toLowerCase();
                    if (!status.equals("present") && !status.equals("absent")) status = "present";

                    String attId = "AT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                    Attendance at = new Attendance(attId, attMember.getUsername(), LocalDateTime.now(), status);
                    attendances.add(at);
                    trainer.trackAttendance(attMember, at);
                    saveData();
                    IO.println("Attendance recorded successfully for " + attMember.getUsername());
                    break;
                case "2":
                    trainer.trackProgress();
                    IO.print("Enter Member Username or ID: ");
                    String mpName = scanner.nextLine().trim();
                    Member prgMember = findMemberByIdOrUsername(mpName);
                    if (prgMember == null) {
                        IO.println("Operation failed. Reason: Member not found.");
                        break;
                    }
                    IO.print("Enter progress metrics/notes (e.g. Bench 80kg, 10 reps): ");
                    String metrics = scanner.nextLine().trim();
                    IO.print("Enter progress score (0-100): ");
                    double score;
                    try {
                        score = Double.parseDouble(scanner.nextLine().trim());
                    } catch (NumberFormatException e) {
                        IO.println("Operation failed. Reason: Invalid progress score number.");
                        break;
                    }

                    String prgId = "PRG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                    WorkoutProgress wp = new WorkoutProgress(prgId, prgMember.getUsername(), LocalDate.now(), metrics, score);
                    trainer.trackProgress(wp, prgMember);
                    saveData();
                    IO.println("Workout progress recorded successfully!");
                    break;
                case "0":
                    return;
                default:
                    IO.println("Invalid selection.");
            }
        }
    }

    private static void trainerReportsSubMenu(Trainer trainer, Scanner scanner) {
        while (true) {
            IO.println("\n--- Reports & Export ---");
            IO.println("1. Generate Member Progress Report");
            IO.println("2. Export Attendance Report to CSV");
            IO.println("3. Export Progress Report to CSV");
            IO.println("0. Back");
            IO.print("Select: ");
            String subChoice = scanner.nextLine().trim();

            switch (subChoice) {
                case "1":
                    trainer.generateProgressReport();
                    IO.print("Enter Member Username or ID: ");
                    String mname = scanner.nextLine().trim();
                    Member target = findMemberByIdOrUsername(mname);
                    if (target == null) {
                        IO.println("Operation failed. Reason: Member not found.");
                        break;
                    }
                    trainer.generateProgressReport(target);
                    break;
                case "2":
                    ReportExporter.exportAttendanceReport(attendances, members, "attendance_report.csv");
                    break;
                case "3":
                    ReportExporter.exportProgressReport(members, "progress_report.csv");
                    break;
                case "0":
                    return;
                default:
                    IO.println("Invalid selection.");
            }
        }
    }

    // ==========================================
    // MEMBER MENU
    // ==========================================
    private static void memberMenu(Member member, Scanner scanner) {
        while (true) {
            IO.println("\n===== MEMBER MENU =====");
            IO.println("1. View Workout Schedules");
            IO.println("2. Update Workout Progress");
            IO.println("3. View Reports (Attendance & Practice Progress)");
            IO.println("4. Manage Subscriptions (Renew & Track Status)");
            IO.println("0. Logout");
            IO.print("Select option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    member.viewSchedule();
                    break;
                case "2":
                    member.updateProgress();
                    IO.println("Submit self workout update:");
                    IO.print("Enter workout activity / notes: ");
                    String notes = scanner.nextLine().trim();
                    IO.print("Self rating / score (0-100): ");
                    double score = 75.0;
                    try {
                        score = Double.parseDouble(scanner.nextLine().trim());
                    } catch (NumberFormatException ignored) {}

                    String prgId = "PRG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                    WorkoutProgress wp = new WorkoutProgress(prgId, member.getUsername(), LocalDate.now(), notes, score);
                    member.getProgressRecords().add(wp);
                    saveData();
                    IO.println("Progress updated successfully!");
                    break;
                case "3":
                    member.viewReports();
                    break;
                case "4":
                    memberSubscriptionSubMenu(member, scanner);
                    break;
                case "0":
                    member.logout();
                    return;
                default:
                    IO.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void memberSubscriptionSubMenu(Member member, Scanner scanner) {
        while (true) {
            IO.println("\n--- Manage Subscriptions ---");
            IO.println("1. View Subscription Status");
            IO.println("2. Renew Subscription");
            IO.println("0. Back");
            IO.print("Select: ");
            String subChoice = scanner.nextLine().trim();

            switch (subChoice) {
                case "1":
                    member.viewSubscriptionStatus();
                    break;
                case "2":
                    if (member.getSubscription() == null) {
                        if (plans.isEmpty()) {
                            IO.println("Operation failed. Reason: No subscription plans available in gym system.");
                            break;
                        }
                        IO.println("No existing subscription found. Choose a plan to subscribe:");
                        for (int i = 0; i < plans.size(); i++) {
                            SubscriptionPlan p = plans.get(i);
                            IO.println((i + 1) + ". " + p.getPlanName() + " - $" + String.format("%.2f", p.getPrice()) + " (" + p.getDurationMonths() + " months)");
                        }
                        IO.print("Select plan number: ");
                        try {
                            int pIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                            if (pIdx >= 0 && pIdx < plans.size()) {
                                SubscriptionPlan chosen = plans.get(pIdx);
                                Subscription newSub = new Subscription("SUB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                                        LocalDate.now(), LocalDate.now().plusMonths(chosen.getDurationMonths()), "active", chosen);
                                newSub.setAmountPaid(chosen.getPrice());
                                member.setSubscription(newSub);
                                subscriptions.add(newSub);
                                saveData();
                                IO.println("Subscribed successfully to " + chosen.getPlanName());
                            } else {
                                IO.println("Operation failed. Reason: Invalid plan choice.");
                            }
                        } catch (NumberFormatException e) {
                            IO.println("Operation failed. Reason: Invalid format.");
                        }
                    } else {
                        member.viewSubscriptionStatus();
                        IO.print("Are you sure you want to renew your subscription? (y/n): ");
                        String confirm = scanner.nextLine().trim();
                        if (confirm.equalsIgnoreCase("y")) {
                            member.renewSubscription();
                            saveData();
                        } else {
                            IO.println("Renewal cancelled.");
                        }
                    }
                    break;
                case "0":
                    return;
                default:
                    IO.println("Invalid selection.");
            }
        }
    }

    // ==========================================
    // HELPER LOOKUP METHODS
    // ==========================================
    public static List<User> getUsers() { return users; }
    public static List<Member> getMembers() { return members; }
    public static List<Trainer> getTrainers() { return trainers; }
    public static List<Admin> getAdmins() { return admins; }
    public static List<Facility> getFacilities() { return facilities; }
    public static List<SubscriptionPlan> getPlans() { return plans; }
    public static List<Subscription> getSubscriptions() { return subscriptions; }
    public static List<WorkoutSchedule> getSchedules() { return schedules; }
    public static List<Attendance> getAttendances() { return attendances; }

    private static User findUserByUsername(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) return u;
        }
        return null;
    }

    private static User findUserById(String id) {
        for (User u : users) {
            if (u.getUserId().equalsIgnoreCase(id)) return u;
        }
        return null;
    }

    private static Member findMemberByIdOrUsername(String idOrUsername) {
        for (Member m : members) {
            if (m.getUserId().equalsIgnoreCase(idOrUsername) || m.getUsername().equalsIgnoreCase(idOrUsername)) {
                return m;
            }
        }
        return null;
    }

    private static Trainer findTrainerById(String id) {
        for (Trainer t : trainers) {
            if (t.getUserId().equalsIgnoreCase(id) || t.getUsername().equalsIgnoreCase(id)) return t;
        }
        return null;
    }

    private static Facility findFacilityById(String id) {
        for (Facility f : facilities) {
            if (f.getFacilityId().equalsIgnoreCase(id)) return f;
        }
        return null;
    }

    private static SubscriptionPlan findPlanById(String id) {
        for (SubscriptionPlan p : plans) {
            if (p.getPlanId().equalsIgnoreCase(id)) return p;
        }
        return null;
    }

    private static void seedData() {
        Admin admin = new Admin("A1", "admin", "adminpass", "admin@example.com", "super");
        admins.add(admin);
        users.add(admin);

        Trainer trainer = new Trainer("T1", "trainer1", "tpass", "trainer1@example.com", "Strength & Fitness");
        trainers.add(trainer);
        users.add(trainer);

        Member member1 = new Member("M1", "member1", "mpass", "member1@example.com", "0901234567", LocalDate.of(1995, 5, 20), "MEM-1001");
        members.add(member1);
        users.add(member1);

        Member member2 = new Member("M2", "member2", "mpass", "member2@example.com", "0987654321", LocalDate.of(1998, 8, 15), "MEM-1002");
        members.add(member2);
        users.add(member2);

        Facility f1 = new Facility("F1", "Cardio Zone & Treadmills", "Active");
        Facility f2 = new Facility("F2", "Free Weight & Squat Racks", "Active");
        facilities.add(f1);
        facilities.add(f2);

        SubscriptionPlan plan1 = new SubscriptionPlan("P1", "Basic Monthly", 50.0, 1);
        SubscriptionPlan plan2 = new SubscriptionPlan("P2", "VIP Annual", 500.0, 12);
        plans.add(plan1);
        plans.add(plan2);

        Subscription sub1 = new Subscription("SUB1", LocalDate.now().minusDays(10), LocalDate.now().plusMonths(1), "active", plan1);
        sub1.setAmountPaid(plan1.getPrice());
        subscriptions.add(sub1);
        member1.setSubscription(sub1);

        WorkoutSchedule ws1 = new WorkoutSchedule("SCH1", "Full Body Conditioning & Core", LocalDate.now(), "active");
        schedules.add(ws1);
        trainer.assignSchedule(ws1, member1);

        Attendance at1 = new Attendance("AT1", member1.getUsername(), LocalDateTime.now().minusDays(1), "present");
        attendances.add(at1);
        trainer.trackAttendance(member1, at1);

        WorkoutProgress wp1 = new WorkoutProgress("PR1", member1.getUsername(), LocalDate.now(), "Squat 80kg 4x10, Bench 60kg", 85.0);
        trainer.trackProgress(wp1, member1);
    }
}
