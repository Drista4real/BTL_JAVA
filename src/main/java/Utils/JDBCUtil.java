package Utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class JDBCUtil {
	public static Connection getConnection() throws FileNotFoundException, IOException, ClassNotFoundException {
		Connection c= null;
		try {
			Connection connection;
			Properties properties = new Properties();	
			properties.load(new FileInputStream("src\\main\\resources\\database.properties"));
			String url = properties.getProperty("url");
			String username = properties.getProperty("username");
			String password = properties.getProperty("password");
			String driver = properties.getProperty("driver");
			connection = DriverManager.getConnection(url, username, password);
			Class.forName(driver);
			System.out.println("Thanh cong");
			
			c=DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
		
	}
	public static void closeConnection(Connection c ){
		
	}
}
