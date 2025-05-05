package model.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class databaseConnection {
    private static String DB_DRIVER;
    private static String DB_URL = "jdbc:mysql://localhost:3306/PatientManagement?allowPublicKeyRetrieval=true&useSSL=false";
    private static String DB_USER;
    private static String DB_PASSWORD;
    
    static {
        // Load configuration from database.properties
        Properties props = new Properties();
        try (InputStream input = databaseConnection.class.getClassLoader().getResourceAsStream("main/database.properties")) {
            if (input == null) {
                throw new IOException("Không tìm thấy tệp database.properties");
            }
            props.load(input);
            DB_DRIVER = props.getProperty("driver");

            // Add UTF-8 encoding parameter to connection URL
            DB_URL = props.getProperty("url");
            if (!DB_URL.contains("characterEncoding")) {
                if (DB_URL.contains("?")) {
                    DB_URL += "&characterEncoding=UTF-8";
                } else {
                    DB_URL += "?characterEncoding=UTF-8";
                }
            }

            DB_USER = props.getProperty("username");
            DB_PASSWORD = props.getProperty("password");

            // Load JDBC driver
            Class.forName(DB_DRIVER);
            
            System.out.println("URL kết nối: " + DB_URL);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Lỗi khi tải cấu hình cơ sở dữ liệu: " + e.getMessage());
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}