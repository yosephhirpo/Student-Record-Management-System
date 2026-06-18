import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private String studentId;
    private String name;
    private String department;
    private double gpa;

    public Student(String studentId, String name, String department, double gpa) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.gpa = gpa;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    @Override
    public String toString() {
        return String.format("ID: %-10s  Name: %-20s  Dept: %-15s  GPA: %.2f",
                studentId, name, department, gpa);
    }

    public String toTextRecord() {
        return studentId + "," + name + "," + department + "," + gpa;
    }

    public static Student fromTextRecord(String recordLine) {
        String[] parts = recordLine.split(",");
        if (parts.length == 4) {
            String id = parts[0].trim();
            String name = parts[1].trim();
            String dept = parts[2].trim();
            double gpa = Double.parseDouble(parts[3].trim());
            return new Student(id, name, dept, gpa);
        }
        throw new IllegalArgumentException("Invalid text record layout.");
    }
}

public class StudentRecordManager {

    private static final String DATA_DIR = "data";
    private static final String TEXT_FILE = DATA_DIR + "/students.txt";
    private static final String BINARY_FILE = DATA_DIR + "/students.bin";
    private static final String SERIAL_FILE = DATA_DIR + "/students.ser";
    private static final String BACKUP_FILE = DATA_DIR + "/students_backup.bak";

    private static final Scanner scanner = new Scanner(System.in);

    private static int currentStorageStrategy = 1;

    public static void main(String[] args) {
        try {
            initializeEnvironment();
            runMenuLoop();
        } catch (Exception e) {
            System.err.println("Error during initialization: " + e.getMessage());
        }
    }

    private static void initializeEnvironment() throws IOException {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                System.out.println("Created data directory: " + dir.getAbsolutePath());
            }
        }

        ensureFileExists(new File(TEXT_FILE));
        ensureFileExists(new File(BINARY_FILE));
        ensureFileExists(new File(SERIAL_FILE));
    }

    private static void ensureFileExists(File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    private static void runMenuLoop() {
        while (true) {
            System.out.println("       STUDENT RECORD MANAGEMENT SYSTEM       ");
            System.out.println("1. Add Student");
            System.out.println("2. Search Student by ID");
            System.out.println("3. Update Student Information");
            System.out.println("4. Delete Student");
            System.out.println("5. Display All Students");
            System.out.println("6. Switch Storage Engine");
            System.out.println("7. Generate Analytical Report");
            System.out.println("8. Diagnose Files");
            System.out.println("9. Backup Student Database");
            System.out.println("0. Exit System");
            System.out.print("Please select an option: ");

            String input = scanner.nextLine().trim();
            if (input.equals("0")) {
                System.out.println("Exiting system. Goodbye!");
                break;
            }

            try {
                switch (input) {
                    case "1":
                        addStudent();
                        break;
                    case "2":
                        searchStudent();
                        break;
                    case "3":
                        updateStudent();
                        break;
                    case "4":
                        deleteStudent();
                        break;
                    case "5":
                        displayAllStudents();
                        break;
                    case "6":
                        switchStorageEngine();
                        break;
                    case "7":
                        generateReport();
                        break;
                    case "8":
                        diagnoseFiles();
                        break;
                    case "9":
                        backupDatabase();
                        break;
                    default:
                        System.out.println("Invalid selection. Please enter 0-9.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred while executing command: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static String getStorageModeName() {
        switch (currentStorageStrategy) {
            case 1: return "Plain-Text Files (Scanner, PrintWriter)";
            case 2: return "Binary Files (DataInputStream, DataOutputStream)";
            case 3: return "Object Serialization (ObjectInputStream, ObjectOutputStream)";
            default: return "Unknown";
        }
    }

    private static void addStudent() throws Exception {
        System.out.println("\n    Add New Student     ");
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) {
            System.out.println(" Student ID cannot be empty.");
            return;
        }

        List<Student> students = loadAllStudents();
        for (Student s : students) {
            if (s.getStudentId().equalsIgnoreCase(id)) {
                System.out.println("A student with ID " + id + " already exists!");
                return;
            }
        }

        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Department: ");
        String dept = scanner.nextLine().trim();
        System.out.print("Enter GPA (0.00 - 4.00): ");
        double gpa;
        try {
            gpa = Double.parseDouble(scanner.nextLine().trim());
            if (gpa < 0.0 || gpa > 4.0) {
                System.out.println("GPA must be between 0.0 and 4.0.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid GPA value.");
            return;
        }

        Student student = new Student(id, name, dept, gpa);
        students.add(student);
        saveAllStudents(students);
        System.out.println("Success: Student record registered successfully!");
    }

    private static void searchStudent() throws Exception {
        System.out.println("\n Search Student ");
        System.out.print("Enter Student ID to find: ");
        String id = scanner.nextLine().trim();

        List<Student> students = loadAllStudents();
        Optional<Student> matched = students.stream()
                .filter(s -> s.getStudentId().equalsIgnoreCase(id))
                .findFirst();

        if (matched.isPresent()) {
            System.out.println("\nRecord Found:");
            System.out.println(matched.get());
        } else {
            System.out.println("No student found with ID: " + id);
        }
    }

    private static void updateStudent() throws Exception {
        System.out.println("\n Update Student Information ");
        System.out.print("Enter Student ID to update: ");
        String id = scanner.nextLine().trim();

        List<Student> students = loadAllStudents();
        int targetIndex = -1;
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId().equalsIgnoreCase(id)) {
                targetIndex = i;
                break;
            }
        }

        if (targetIndex == -1) {
            System.out.println("No student found with ID: " + id);
            return;
        }

        Student existing = students.get(targetIndex);
        System.out.println("Current Record: " + existing);

        System.out.print("Enter New Name (or press Enter to keep '" + existing.getName() + "'): ");
        String newName = scanner.nextLine().trim();
        if (!newName.isEmpty()) {
            existing.setName(newName);
        }

        System.out.print("Enter New Department (or press Enter to keep '" + existing.getDepartment() + "'): ");
        String newDept = scanner.nextLine().trim();
        if (!newDept.isEmpty()) {
            existing.setDepartment(newDept);
        }

        System.out.print("Enter New GPA (or press Enter to keep '" + existing.getGpa() + "'): ");
        String gpaStr = scanner.nextLine().trim();
        if (!gpaStr.isEmpty()) {
            try {
                double newGpa = Double.parseDouble(gpaStr);
                if (newGpa < 0.0 || newGpa > 4.0) {
                    System.out.println("GPA must be between 0.0 and 4.0. No update applied to GPA.");
                } else {
                    existing.setGpa(newGpa);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid GPA value. No update applied to GPA.");
            }
        }

        saveAllStudents(students);
        System.out.println("Success: Student records updated!");
    }

    private static void deleteStudent() throws Exception {
        System.out.println("\n Delete Student Record ");
        System.out.print("Enter Student ID to delete: ");
        String id = scanner.nextLine().trim();

        List<Student> students = loadAllStudents();
        boolean removed = students.removeIf(s -> s.getStudentId().equalsIgnoreCase(id));

        if (removed) {
            saveAllStudents(students);
            System.out.println("Success: Student record has been purged from system database.");
        } else {
            System.out.println("No student found with ID: " + id);
        }
    }

    private static void displayAllStudents() throws Exception {
        System.out.println("\n Displaying All Student Records ");
        List<Student> students = loadAllStudents();

        if (students.isEmpty()) {
            System.out.println("The database is currently empty under the " + getStorageModeName() + " engine.");
            return;
        }

        System.out.printf("Total records: %d\n", students.size());
        for (Student s : students) {
            System.out.println(s);
        }
    }

    private static void switchStorageEngine() {
        System.out.println("\n Switch Storage Engine ");
        System.out.println("1. Plain-Text Files (students.txt)");
        System.out.println("2. Binary Files (students.bin)");
        System.out.println("3. Object Serialization (students.ser)");
        System.out.print("Select active engine (1-3): ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                currentStorageStrategy = 1;
                break;
            case "2":
                currentStorageStrategy = 2;
                break;
            case "3":
                currentStorageStrategy = 3;
                break;
            default:
                System.out.println("Invalid choice. Keeping active engine.");
                return;
        }
        System.out.println("Active Storage Engine switched to: " + getStorageModeName());
    }

    private static List<Student> loadAllStudents() throws Exception {
        switch (currentStorageStrategy) {
            case 1:
                return loadFromTextFile();
            case 2:
                return loadFromBinaryFile();
            case 3:
                return loadFromSerialFile();
            default:
                return new ArrayList<>();
        }
    }

    private static void saveAllStudents(List<Student> students) throws Exception {
        switch (currentStorageStrategy) {
            case 1:
                saveToTextFile(students);
                break;
            case 2:
                saveToBinaryFile(students);
                break;
            case 3:
                saveToSerialFile(students);
                break;
        }
    }

    private static List<Student> loadFromTextFile() throws IOException {
        List<Student> list = new ArrayList<>();
        File file = new File(TEXT_FILE);
        if (!file.exists() || file.length() == 0) return list;

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (!line.trim().isEmpty()) {
                    try {
                        list.add(Student.fromTextRecord(line));
                    } catch (Exception ex) {
                    }
                }
            }
        }
        return list;
    }

    private static void saveToTextFile(List<Student> students) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TEXT_FILE))) {
            for (Student s : students) {
                writer.println(s.toTextRecord());
            }
        }
    }

    private static List<Student> loadFromBinaryFile() throws IOException {
        List<Student> list = new ArrayList<>();
        File file = new File(BINARY_FILE);
        if (!file.exists() || file.length() == 0) return list;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            while (dis.available() > 0) {
                String id = dis.readUTF();
                String name = dis.readUTF();
                String dept = dis.readUTF();
                double gpa = dis.readDouble();
                list.add(new Student(id, name, dept, gpa));
            }
        } catch (EOFException ignored) {
        }
        return list;
    }

    private static void saveToBinaryFile(List<Student> students) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(BINARY_FILE))) {
            for (Student s : students) {
                dos.writeUTF(s.getStudentId());
                dos.writeUTF(s.getName());
                dos.writeUTF(s.getDepartment());
                dos.writeDouble(s.getGpa());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Student> loadFromSerialFile() throws Exception {
        File file = new File(SERIAL_FILE);
        if (!file.exists() || file.length() == 0) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Student>) ois.readObject();
        } catch (EOFException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private static void saveToSerialFile(List<Student> students) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SERIAL_FILE))) {
            oos.writeObject(students);
        }
    }

    private static void generateReport() throws Exception {
        System.out.println("        STUDENT PERFORMANCE ANALYTICS        ");
        List<Student> students = loadAllStudents();

        if (students.isEmpty()) {
            System.out.println("No students registered in the current database context.");
            System.out.println("Please register students first to extract statistical metrics.");
            return;
        }

        int totalStudents = students.size();
        double sumGpa = 0;
        double maxGpa = Double.MIN_VALUE;
        double minGpa = Double.MAX_VALUE;

        Student topStudent = null;
        Student strugglingStudent = null;

        for (Student s : students) {
            double gpa = s.getGpa();
            sumGpa += gpa;

            if (gpa > maxGpa) {
                maxGpa = gpa;
                topStudent = s;
            }
            if (gpa < minGpa) {
                minGpa = gpa;
                strugglingStudent = s;
            }
        }

        double averageGpa = sumGpa / totalStudents;

        System.out.printf("Total Enrolled Students : %d\n", totalStudents);
        System.out.printf("Average GPA      : %.2f\n", averageGpa);
        if (topStudent != null) {
            System.out.printf("Highest GPA Performance : %.2f (ID: %s, Name: %s)\n",
                    maxGpa, topStudent.getStudentId(), topStudent.getName());
        }
        if (strugglingStudent != null) {
            System.out.printf("Lowest GPA Performance  : %.2f (ID: %s, Name: %s)\n",
                    minGpa, strugglingStudent.getStudentId(), strugglingStudent.getName());
        }
    }

    private static void diagnoseFiles() {
        System.out.println("\n Diagnostics: Internal File System Properties ");
        displayProperties("Text File Engine Database", TEXT_FILE);
        displayProperties("Binary File Engine Database", BINARY_FILE);
        displayProperties("Serialization Engine Database", SERIAL_FILE);
        displayProperties("Backup File Space", BACKUP_FILE);
    }

    private static void displayProperties(String description, String path) {
        File f = new File(path);
        System.out.println("\nTarget Resource: " + description);
        System.out.println("-");
        if (f.exists()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println("File Name         : " + f.getName());
            System.out.println("Absolute Path     : " + f.getAbsolutePath());
            System.out.println("Readable Space Size: " + f.length() + " bytes");
            System.out.println("Last Modified Date: " + sdf.format(f.lastModified()));
        } else {
            System.out.println("Status            : File not instantiated yet.");
        }
    }

    private static void backupDatabase() {
        System.out.println("\n Initiating Buffered Stream Backup ");
        String sourceFilePath;

        switch (currentStorageStrategy) {
            case 2:
                sourceFilePath = BINARY_FILE;
                break;
            case 3:
                sourceFilePath = SERIAL_FILE;
                break;
            default:
                sourceFilePath = TEXT_FILE;
                break;
        }

        File sourceFile = new File(sourceFilePath);
        if (!sourceFile.exists() || sourceFile.length() == 0) {
            System.out.println("Warning: The source database is empty. Add data before backing up.");
            return;
        }

        File destFile = new File(BACKUP_FILE);

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile))) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.flush();
            System.out.println("Success: Database backup written smoothly using Buffered IO streams.");
            System.out.println("    Source: " + sourceFile.getName());
            System.out.println("    Backup Destination: " + destFile.getAbsolutePath());
            System.out.println("    File size backed up: " + destFile.length() + " bytes");
        } catch (IOException e) {
            System.err.println("Error occurred during streaming backup action: " + e.getMessage());
        }
    }
}