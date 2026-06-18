Student Record Management System 
This README describes the system architecture, file layouts, and execution steps for the Student Record Management System implemented in StudentRecordManager.java.

1. System Design Architecture
This system relies on three parallel file storage strategies implemented to comply with standard enterprise data formats and academic requirements.
Model Layer (Student): Represents individual entities, holding values for ID, Name, Department, and GPA. Implements Serializable for stream compatibility.
Storage Controller Layer: Decoupled methods handle reading/writing of data. You can switch between active storage engine types mid-execution.
Buffered Stream Backup Engine: A background binary utility that replicates active student records to a backup location (data/students_backup.bak) using fast, intermediate RAM buffers.

2. Supported Storage Modes & Formats
A. Plain-Text Storage (data/students.txt)
Writer Used: PrintWriter (using custom delimiter commas ,).
Reader Used: Scanner to dynamically parse tokens.

B. Binary Storage (data/students.bin)
Writer Used: DataOutputStream writing exact types (writeUTF() and writeDouble()).
Reader Used: DataInputStream executing structured read operations in FIFO order.
Characteristics: Compact size, not human-readable directly inside a text editor.

C. Serialization Engine (data/students.ser)
Writer Used: ObjectOutputStream which dumps the complete state of the List<Student> object directly to disk.
Reader Used: ObjectInputStream.
Characteristics: Maintains strict object identity and structures across application lifecycles.

3. Compilation and Execution Instructions
Step 1: Compile the source file
Open your command terminal and compile using the standard Java Compiler:
javac StudentRecordManager.java
Step 2: Run the Application
Execute the compiled bytecode with:
java StudentRecordManager

4. Sample Test Cases
Test Case 1: Populating a Student
Launch the application.
Select Option 1 (Add Student).
Fill in the requested credentials:
ID: S001
Name: Abebe Bikila
Department: Physics
GPA: 3.92

Choose Option 5 (Display All Students) to view your input on-screen.
Test Case 2: Switching Engines & Verifying Separation
Go to Option 6 (Switch Storage Engine) and choose 2 (Binary Files).
Try running Option 5. Note that the file is blank because we changed databases.
Add a new record (S002, Bereket Tesfaye, Finance, 3.20) and save.
Select Option 6 and switch back to 1 (Plain-Text Files).
Choose Option 5. You will see Abebe Bikila is preserved safe in the original file space.
Test Case 3: Performance Metrics Report
Select Option 7 (Generate Analytical Report).
The program outputs a formatted terminal report:
        STUDENT PERFORMANCE ANALYTICS        
Total Enrolled Students : 1
Average Cohort GPA      : 3.92
Highest GPA Performance : 3.92 (ID: S001, Name: Abebe Bikila)
Lowest GPA Performance  : 3.92 (ID: S001, Name: Abebe Bikila)

Test Case 4: Backup Check
Select Option 9 (Backup Student Database).
Run Option 8 (Diagnose Files). Check the metadata diagnostics for data/students_backup.bak to observe the generated payload size and modified date.
