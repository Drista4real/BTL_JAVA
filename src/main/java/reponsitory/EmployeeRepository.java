package reponsitory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Utils.JDBCUtil;
import model.Employee;

public class EmployeeRepository {
	
	public static List<Object[]> findEmployee(String charFind) {
        List<Object[]> list = new ArrayList<>();

        try (Connection conn = JDBCUtil.getConnection()) {
            String query = "SELECT " +
                    "    Employee.id,Employee.name, Employee.phoneNumber, Employee.gender, Employee.birthDate, Employee.role " +
                    "FROM Employee " +        
                    "WHERE LOWER(Employee.name) LIKE ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            String keyword = "%" + charFind.toLowerCase() + "%";
            stmt.setString(1, keyword);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
            	int id =rs.getInt("id");
                String name = rs.getString("name");
                String phone = rs.getString("phoneNumber");
                int genderInt = rs.getInt("gender");
                String gender = (genderInt == 1) ? "Nam" : "Nữ";

                int age = 0;
                Date birthDate = rs.getDate("birthDate");
                if (birthDate != null) {
                    age = java.time.Period.between(
                            birthDate.toLocalDate(),
                            java.time.LocalDate.now()
                    ).getYears();
                }

                String role = rs.getString("role");
                list.add(new Object[]{
                        id,
                        name,
                        phone,
                        gender,
                        age,
                        role,
                        null // Cột "Khám"
                });
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
	
	public static Object[] findById(int id) {
	    Object[] employeeData = null;
	    try (Connection conn = JDBCUtil.getConnection()) {
	        String query = "SELECT * FROM Employee WHERE id = ?";
	        PreparedStatement stmt = conn.prepareStatement(query);
	        stmt.setInt(1, id);
	        ResultSet rs = stmt.executeQuery();

	        if (rs.next()) {
	            String name = rs.getString("name");
	            String phone = rs.getString("phoneNumber");
	            Date birthDate = rs.getDate("birthDate");
	            String address = rs.getString("address");
	            String idCard = rs.getString("idCard");
	            double salary = rs.getDouble("salary");
	            String role = rs.getString("role");
	            String username = rs.getString("username");
	            String password = rs.getString("password");
	            int genderInt = rs.getInt("gender");
	            String gender = genderInt == 1 ? "Nam" : "Nữ";

	            employeeData = new Object[]{
	                    id, name, phone,
	                    birthDate != null ? birthDate.toString() : "",
	                    gender, address, idCard,
	                    salary, role, username, password
	            };
	        }

	        rs.close();
	        stmt.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return employeeData;
	}

	public static boolean updateEmployeeById(int id, String name, Date birthDate, String address,
            int gender, String phone, String idCard,
            String username, String password,
            double salary, String role) {
			String sql = "UPDATE Employee SET name=?, birthDate=?, address=?, gender=?, phoneNumber=?, " +
					"idCard=?, username=?, password=?, salary=?, role=? WHERE id=?";
			try (Connection conn = JDBCUtil.getConnection();
					PreparedStatement stmt = conn.prepareStatement(sql)) {

					stmt.setString(1, name);
					stmt.setDate(2, new Date(birthDate.getTime()));
					stmt.setString(3, address);
					stmt.setInt(4, gender);
					stmt.setString(5, phone);
					stmt.setString(6, idCard);
					stmt.setString(7, username);
					stmt.setString(8, password);
					stmt.setDouble(9, salary);
					stmt.setString(10, role);
					stmt.setInt(11, id);

					int rows = stmt.executeUpdate();
					return rows > 0;
		} catch (Exception e) {
		e.printStackTrace();
		return false;
		}
	}

	public static boolean insertEmployee(String name, Date birthDate, String address, int gender,
            String phone, String cccd, String username, String password,
            double salary, int year, String role) {
			String sql = "INSERT INTO Employee (name, birthDate, address, gender, phoneNumber, idCard, username, password, salary, experienceYears, role) " +
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = JDBCUtil.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setString(1, name);
			stmt.setDate(2, birthDate);
			stmt.setString(3, address);
			stmt.setInt(4, gender);
			stmt.setString(5, phone);
			stmt.setString(6, cccd);
			stmt.setString(7, username);
			stmt.setString(8, password);
			stmt.setDouble(9, salary);
			stmt.setInt(10, year);
			stmt.setString(11, role);
			
			return stmt.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean isPhoneExists(String phone) {
	    String query = "SELECT COUNT(*) FROM Employee WHERE phoneNumber = ?";
	    
	    try (Connection conn = JDBCUtil.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(query)) {
	        
	        stmt.setString(1, phone);  // Gán số điện thoại vào câu truy vấn
	        ResultSet rs = stmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getInt(1) > 0;  // Nếu số điện thoại tồn tại trong cơ sở dữ liệu, trả về true
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;  // Nếu không tìm thấy số điện thoại, trả về false
	}

	
	public static boolean isCCCDExists(String idCard) {
	    String query = "SELECT COUNT(*) FROM Employee WHERE idCard = ?";
	    
	    try (Connection conn = JDBCUtil.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(query)) {
	        
	        stmt.setString(1, idCard);  // Gán CCCD vào câu truy vấn
	        ResultSet rs = stmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getInt(1) > 0;  // Nếu CCCD tồn tại trong cơ sở dữ liệu, trả về true
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;  // Nếu không tìm thấy CCCD, trả về false
	}

	public static boolean isUsernameExists(String username) {
	    String query = "SELECT COUNT(*) FROM Employee WHERE username = ?";

	    try (Connection conn = JDBCUtil.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setString(1, username);  // Gán username vào câu truy vấn
	        ResultSet rs = stmt.executeQuery();

	        if (rs.next()) {
	            return rs.getInt(1) > 0;  // Nếu số lượng bản ghi > 0, tức là username đã tồn tại
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;  // Nếu không tìm thấy username, trả về false
	}
	//code của Minh
	public static List<Employee> getEmployees() {
		try {
			Connection con= JDBCUtil.getConnection();
			Statement statement= con.createStatement();
			ResultSet rs= statement.executeQuery("select * from Employee");

			List<Employee> employees= new ArrayList<Employee>();
			while (rs.next()) {
				int id= rs.getInt("id");
				String name= rs.getString("name");
				Date birthday= rs.getDate("birthDate");
				String address= rs.getString("address");
				int gender= rs.getInt("gender");
				String phoneNumber= rs.getString("phoneNumber");
				String idCard= rs.getString("idCard");
				String username= rs.getString("username");
				String password= rs.getString("password");
				double salary= rs.getDouble("salary");
				int experience= rs.getInt("experienceYears");
				String role= rs.getString("role");

				employees.add(new Employee(id,name,birthday,address,gender,phoneNumber,idCard,username,password,salary,experience,role));
			}
			rs.close();
			return employees;
		} catch (IOException e){
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}
}
