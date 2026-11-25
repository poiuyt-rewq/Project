import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** CampusDesk: a simple console-based student issue tracker. */
public class CampusDesk {
    private static final Path DATA_FILE = Paths.get("data", "tickets.txt");
    private static final Scanner SC = new Scanner(System.in);
    private static final List<Ticket> tickets = new ArrayList<>();
    private static int nextId = 1;


    enum Status { OPEN, IN_PROGRESS, RESOLVED }
    enum Priority { LOW, MEDIUM, HIGH }


    static class Ticket {
        int id;
        String createdAt, category, title, description;
        Priority priority;
        Status status;
        Ticket(int id, String createdAt, String category, String title, String description, Priority priority, Status status) {
            this.id=id; this.createdAt=createdAt; this.category=category; this.title=title;
            this.description=description; this.priority=priority; this.status=status;
        }
    }


    public static void main(String[] args) {
        loadTickets();
        System.out.println("================================");
        System.out.println("        CAMPUSDESK              ");
        System.out.println(" Student Issue Tracking System  ");
        System.out.println("================================");
        while (true) {
            System.out.println("\n1. Report an issue\n2. View all issues\n3. Search by ticket ID\n4. Filter by status\n5. Update issue status\n6. Delete a ticket\n7. Exit");
            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1 -> createTicket();
                case 2 -> listTickets(tickets);
                case 3 -> searchTicket();
                case 4 -> filterTickets();
                case 5 -> updateStatus();
                case 6 -> deleteTicket();
                case 7 -> { saveTickets(); System.out.println("Your records are saved. Goodbye!"); return; }
                default -> System.out.println("Please enter a number from 1 to 7.");
            }
        }
    }


    private static void createTicket() {
        System.out.println("\n--- Report an issue ---");
        String category = readNonEmpty("Category (Wi-Fi/Classroom/Library/Hostel/Other): ");
        String title = readNonEmpty("Short title: ");
        String description = readNonEmpty("Description: ");
        Priority priority = choosePriority();
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        Ticket t = new Ticket(nextId++, time, category, title, description, priority, Status.OPEN);
        tickets.add(t);
        saveTickets();
        System.out.println("Issue reported successfully. Ticket ID: " + t.id);
    }

    private static void listTickets(List<Ticket> list) {
        if (list.isEmpty()) { System.out.println("No tickets found."); return; }
        System.out.printf("\n%-5s %-17s %-13s %-9s %-12s %s%n", "ID", "Created", "Category", "Priority", "Status", "Title");
        System.out.println("----------------------------------------------------------------------------");
        for (Ticket t : list) {
            System.out.printf("%-5d %-17s %-13s %-9s %-12s %s%n", t.id, t.createdAt, shorten(t.category,13), t.priority, t.status, t.title);
        }
    }

    private static void searchTicket() {
        int id = readInt("Enter ticket ID: ");
        Ticket t = findById(id);
        if (t == null) { System.out.println("Ticket not found."); return; }
        printDetails(t);
    }

    private static void filterTickets() {
        Status status = chooseStatus();
        List<Ticket> matches = new ArrayList<>();
        for (Ticket t : tickets) if (t.status == status) matches.add(t);
        listTickets(matches);
    }

    private static void updateStatus() {
        int id = readInt("Ticket ID to update: ");
        Ticket t = findById(id);
        if (t == null) { System.out.println("Ticket not found."); return; }
        System.out.println("Current status: " + t.status);
        t.status = chooseStatus();
        saveTickets();
        System.out.println("Ticket status updated.");
    }

    private static void deleteTicket() {
        int id = readInt("Ticket ID to delete: ");
        Ticket t = findById(id);
        if (t == null) { System.out.println("Ticket not found."); return; }
        String confirm = readNonEmpty("Delete ticket #" + id + "? (y/n): ");
        if (confirm.equalsIgnoreCase("y")) {
            tickets.remove(t); saveTickets(); System.out.println("Ticket deleted.");
        } else System.out.println("Deletion cancelled.");
    }

    private static Ticket findById(int id) {
        for (Ticket t : tickets) if (t.id == id) return t;
        return null;
    }

    private static void printDetails(Ticket t) {
        System.out.println("\nTicket #" + t.id + " | " + t.status);
        System.out.println("Created: " + t.createdAt + "\nCategory: " + t.category + "\nPriority: " + t.priority);
        System.out.println("Title: " + t.title + "\nDescription: " + t.description);
    }

    private static Priority choosePriority() {
        while (true) {
            System.out.println("Priority: 1. Low  2. Medium  3. High");
            int n = readInt("Choose priority: ");
            if (n == 1) return Priority.LOW;
            if (n == 2) return Priority.MEDIUM;
            if (n == 3) return Priority.HIGH;
            System.out.println("Invalid choice.");
        }
    }


    private static Status chooseStatus() {
        while (true) {
            System.out.println("Status: 1. Open  2. In Progress  3. Resolved");
            int n = readInt("Choose status: ");
            if (n == 1) return Status.OPEN;
            if (n == 2) return Status.IN_PROGRESS;
            if (n == 3) return Status.RESOLVED;
            System.out.println("Invalid choice.");
        }
    }


    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SC.nextLine().trim();
            try { return Integer.parseInt(value); }
            catch (NumberFormatException e) { System.out.println("Please enter a valid number."); }
        }
    }

    private static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = SC.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("This field cannot be empty.");
        }
    }

    private static String shorten(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }


    // Base64-encode text fields so delimiters and newlines do not corrupt the save file.
    private static String encode(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }
    private static String decode(String s) {
        return new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8);
    }

    private static void saveTickets() {
        try {
            Files.createDirectories(DATA_FILE.getParent());
            try (BufferedWriter out = Files.newBufferedWriter(DATA_FILE, StandardCharsets.UTF_8)) {
                for (Ticket t : tickets) {
                    out.write(t.id + "\t" + encode(t.createdAt) + "\t" + encode(t.category) + "\t" + encode(t.title) + "\t" + encode(t.description) + "\t" + t.priority + "\t" + t.status);
                    out.newLine();
                }
            }
        } catch (IOException e) { System.out.println("Could not save data: " + e.getMessage()); }
    }

    private static void loadTickets() {
        if (!Files.exists(DATA_FILE)) return;
        try (BufferedReader in = Files.newBufferedReader(DATA_FILE, StandardCharsets.UTF_8)) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] p = line.split("\\t", -1);
                if (p.length != 7) continue;
                try {
                    int id = Integer.parseInt(p[0]);
                    Ticket t = new Ticket(id, decode(p[1]), decode(p[2]), decode(p[3]), decode(p[4]), Priority.valueOf(p[5]), Status.valueOf(p[6]));
                    tickets.add(t); nextId = Math.max(nextId, id + 1);
                } catch (RuntimeException ignored) { /* Skip malformed record rather than crash. */ }
            }
        } catch (IOException e) { System.out.println("Could not load saved data: " + e.getMessage()); }
    }
}
