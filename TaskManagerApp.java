import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class TaskManagerApp {

    private static final String FILE_NAME = "tasks.txt";
    private static ArrayList<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        loadTasksFromFile();

        while (true) {
            System.out.println("\n===== TASK MANAGER =====");
            System.out.println();
            System.out.println("1. Add Task");
            System.out.println("2. Remove Task");
            System.out.println("3. List Tasks");
            System.out.println("4. Mark Task Completed/Pending");
            System.out.println("5. Edit Task");
            System.out.println("6. Exit");
            System.out.println();
            System.out.print("Enter your choice: ");

            int choice = safeIntInput(sc);

            switch (choice) {
                case 1: addTask(sc); break;
                case 2: removeTask(sc); break;
                case 3: listTasks(); break;
                case 4: toggleTaskCompletion(sc); break;
                case 5: editTask(sc); break;
                case 6:
                    saveTasksToFile();
                    System.out.println("Tasks saved. Exiting...");
                    return;
                default:
                    System.out.println("Invalid option! Try again.");
            }
        }
    }

    // Helper to safely read integers
    private static int safeIntInput(Scanner sc) {
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Add Task
    private static void addTask(Scanner sc) {
        System.out.print("Enter task description: ");
        String desc = sc.nextLine().trim();

        if (desc.isEmpty()) {
            System.out.println("Task cannot be empty!");
            return;
        }

        tasks.add(new Task(desc, false));
        System.out.println("Task added successfully.");
    }

    // Remove Task
    private static void removeTask(Scanner sc) {
        listTasks();
        if (tasks.isEmpty()) return;

        System.out.print("Enter the task number to remove: ");
        int index = safeIntInput(sc);

        if (index < 1 || index > tasks.size()) {
            System.out.println("Invalid task number!");
            return;
        }

        tasks.remove(index - 1);
        System.out.println("Task removed successfully.");
    }

    // List Tasks
    private static void listTasks() {
        if (tasks.isEmpty()) {
            System.out.println("\nNo tasks available!");
            return;
        }

        System.out.println("\n----- Your Tasks -----");
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            String status = t.isCompleted ? "[✓]" : "[ ]";
            System.out.println((i + 1) + ". " + status + " " + t.description);
        }
    }

    // MULTI-SELECT toggle method
    private static void toggleTaskCompletion(Scanner sc) {
        listTasks();
        if (tasks.isEmpty()) return;

        System.out.print("Enter task numbers to toggle (e.g., 1, 3, 4): ");
        String input = sc.nextLine().trim();

        if (input.isEmpty()) {
            System.out.println("No input provided!");
            return;
        }

        String[] parts = input.split("[, ]+");

        boolean anyValid = false;

        for (String p : parts) {
            try {
                int index = Integer.parseInt(p);

                if (index < 1 || index > tasks.size()) {
                    System.out.println("Invalid task number: " + p);
                    continue;
                }

                Task t = tasks.get(index - 1);
                t.isCompleted = !t.isCompleted;

                System.out.println("Toggled task " + index + ": " + t.description);
                anyValid = true;

            } catch (NumberFormatException e) {
                System.out.println("Invalid input: " + p);
            }
        }

        if (!anyValid) {
            System.out.println("No valid task numbers entered.");
        }
    }

    // Edit Task
    private static void editTask(Scanner sc) {
        listTasks();
        if (tasks.isEmpty()) return;

        System.out.print("Enter task number to edit: ");
        int index = safeIntInput(sc);

        if (index < 1 || index > tasks.size()) {
            System.out.println("Invalid task number!");
            return;
        }

        Task t = tasks.get(index - 1);

        System.out.print("Enter new description: ");
        String newDesc = sc.nextLine().trim();

        if (newDesc.isEmpty()) {
            System.out.println("Description cannot be empty.");
            return;
        }

        t.description = newDesc;
        System.out.println("Task updated successfully.");
    }

    // File I/O
    private static void loadTasksFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":::");
                if (parts.length == 2) {
                    String desc = parts[0];
                    boolean isCompleted = Boolean.parseBoolean(parts[1]);
                    tasks.add(new Task(desc, isCompleted));
                }
            }
            System.out.println("Tasks loaded from file.");
        } catch (IOException e) {
            System.out.println("Error loading tasks: " + e.getMessage());
        }
    }

    private static void saveTasksToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Task t : tasks) {
                bw.write(t.description + ":::" + t.isCompleted);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }
}

class Task {
    String description;
    boolean isCompleted;

    Task(String description, boolean isCompleted) {
        this.description = description;
        this.isCompleted = isCompleted;
    }
}