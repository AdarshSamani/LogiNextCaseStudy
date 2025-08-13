import java.util.*;
import java.sql.*;

class Order {
    int id;
    int orderTime;
    int travelTime;

    public Order(int id, int orderTime, int travelTime) {
        this.id = id;
        this.orderTime = orderTime;
        this.travelTime = travelTime;
    }
}

class Driver {
    int id;
    int availableAt;

    public Driver(int id) {
        this.id = id;
        this.availableAt = 0;
    }
}

public class LogiNextAssignment {

    static final String JDBC_URL = "jdbc:mysql://localhost:3306/loginext";
    static final String JDBC_USER = "root";
    static final String JDBC_PASSWORD = "adarsh";

    static List<Order> orders = new ArrayList<>();

    static PriorityQueue<Driver> driverQueue = new PriorityQueue<>(
            (a, b) -> a.availableAt - b.availableAt);

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int N = sc.nextInt();
        int M = sc.nextInt();

        for (int i = 0; i < N; i++) {
            int O = sc.nextInt();
            int T = sc.nextInt();
            orders.add(new Order(i + 1, O, T));
        }

        for (int i = 0; i < M; i++) {
            driverQueue.offer(new Driver(i + 1));
        }

        for (Order order : orders) {
            Driver driver = driverQueue.poll();

            if (driver != null && driver.availableAt <= order.orderTime) {
                driver.availableAt = order.orderTime + order.travelTime;
                System.out.println("C" + order.id + " - D" + driver.id);
                saveToDatabase(order.id, driver.id, "Assigned");
                driverQueue.offer(driver);
            } else {
                System.out.println("C" + order.id + " - No Food :-(");
                saveToDatabase(order.id, -1, "Failed");

                if (driver != null) {
                    driverQueue.offer(driver);
                }
            }
        }

        sc.close();
    }

    public static void saveToDatabase(int orderId, int driverId, String status) {
        try (
                Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO orders (order_id, driver_id, status) VALUES (?, ?, ?)")) {
            ps.setInt(1, orderId);
            ps.setInt(2, driverId);
            ps.setString(3, status);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}