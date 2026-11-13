
import java.sql.*;
import java.util.Scanner;

public class HostelManagement {

    static final String URL = "jdbc:mysql://localhost:3306/hostel_db";
    static final String USER = "root";
    static final String PASSWORD = "root";

    static Connection con;
    static Scanner sc = new Scanner(System.in);

    // Database Connection
    public static void connect() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Database Connected Successfully!");
    }

    // (a) Insert Student Records
    public static void insertStudents() {

        String sql = "INSERT INTO HostelStudent VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 1; i <= 3; i++) {

                System.out.println("\nEnter Student " + i + " Details:");

                System.out.print("Student ID: ");
                int id = sc.nextInt();
                sc.nextLine();

                System.out.print("Student Name: ");
                String name = sc.nextLine();

                System.out.print("Roll Number: ");
                String roll = sc.nextLine();

                System.out.print("Hostel Name: ");
                String hostel = sc.nextLine();

                System.out.print("Room Number: ");
                String room = sc.nextLine();

                System.out.print("Fee: ");
                double fee = sc.nextDouble();

                ps.setInt(1, id);
                ps.setString(2, name);
                ps.setString(3, roll);
                ps.setString(4, hostel);
                ps.setString(5, room);
                ps.setDouble(6, fee);

                try {
                    ps.executeUpdate();
                    System.out.println("Record Inserted Successfully!");
                } catch (SQLIntegrityConstraintViolationException e) {
                    System.out.println("Duplicate Student ID!");
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // (b) Retrieve and Sort Students
    public static void retrieveStudents() {

        String sql = "SELECT * FROM HostelStudent WHERE hostel_name = ? AND fee > 50000 ORDER BY fee DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            sc.nextLine();

            System.out.print("Enter Hostel Name: ");
            String hostel = sc.nextLine();

            ps.setString(1, hostel);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(
                    rs.getInt("student_id") + " " +
                    rs.getString("student_name") + " " +
                    rs.getString("roll_number") + " " +
                    rs.getString("hostel_name") + " " +
                    rs.getString("room_number") + " " +
                    rs.getDouble("fee")
                );
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // (c) Update Hostel Fee
    public static void updateFee() {

        String sql = "UPDATE HostelStudent SET fee = ? WHERE student_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.print("Enter Student ID: ");
            int id = sc.nextInt();

            System.out.print("Enter New Fee: ");
            double fee = sc.nextDouble();

            ps.setDouble(1, fee);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("Fee Updated Successfully!");
            else
                System.out.println("Student Not Found!");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // (d) Search and Update Room
    public static void updateRoom() {

        String search = "SELECT * FROM HostelStudent WHERE student_id = ?";

        try (PreparedStatement ps = con.prepareStatement(search)) {

            System.out.print("Enter Student ID: ");
            int id = sc.nextInt();

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                System.out.println("Student Found: " +
                    rs.getString("student_name"));

                sc.nextLine();

                System.out.print("Enter New Room Number: ");
                String room = sc.nextLine();

                String update = "UPDATE HostelStudent SET room_number = ? WHERE student_id = ?";

                try (PreparedStatement ups = con.prepareStatement(update)) {

                    ups.setString(1, room);
                    ups.setInt(2, id);
                    ups.executeUpdate();
                }

                System.out.println("Room Updated Successfully!");

                try (PreparedStatement ps2 = con.prepareStatement(search)) {
                    ps2.setInt(1, id);
                    ResultSet rs2 = ps2.executeQuery();

                    if (rs2.next()) {
                        System.out.println("Updated Details:");
                        System.out.println("ID: " + rs2.getInt("student_id"));
                        System.out.println("Name: " + rs2.getString("student_name"));
                        System.out.println("Roll: " + rs2.getString("roll_number"));
                        System.out.println("Hostel: " + rs2.getString("hostel_name"));
                        System.out.println("Room: " + rs2.getString("room_number"));
                        System.out.println("Fee: " + rs2.getDouble("fee"));
                    }
                }

            } else {
                System.out.println("Student Not Found!");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // (e) Aggregate and Delete
    public static void aggregateAndDelete() {

        try {
            String sum = "SELECT SUM(fee) AS total_fee FROM HostelStudent";

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sum)) {

                if (rs.next())
                    System.out.println("Total Fee Collected: " +
                        rs.getDouble("total_fee"));
            }

            System.out.print("Enter Student ID to Delete: ");
            int id = sc.nextInt();

            String delete = "DELETE FROM HostelStudent WHERE student_id = ? AND fee > 0";

            try (PreparedStatement ps = con.prepareStatement(delete)) {

                ps.setInt(1, id);

                int rows = ps.executeUpdate();

                if (rows > 0)
                    System.out.println("Record Deleted Successfully!");
                else
                    System.out.println("Record Not Found or Fee Not Fully Paid!");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Main Method
    public static void main(String[] args) {

        try {
            connect();

            int choice;

            do {
                System.out.println("\n===== KIET HOSTEL MANAGEMENT SYSTEM =====");
                System.out.println("1. Insert Student Records");
                System.out.println("2. Retrieve Students");
                System.out.println("3. Update Hostel Fee");
                System.out.println("4. Search and Update Room");
                System.out.println("5. Total Fee and Delete Student");
                System.out.println("6. Exit");

                System.out.print("Enter Choice: ");
                choice = sc.nextInt();

                switch (choice) {

                    case 1:
                        insertStudents();
                        break;

                    case 2:
                        retrieveStudents();
                        break;

                    case 3:
                        updateFee();
                        break;

                    case 4:
                        updateRoom();
                        break;

                    case 5:
                        aggregateAndDelete();
                        break;

                    case 6:
                        System.out.println("Thank You!");
                        break;

                    default:
                        System.out.println("Invalid Choice!");
                }

            } while (choice != 6);

            con.close();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
