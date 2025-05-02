package model.entity;


import model.entity.Role;
import model.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class UserService cung cấp các chức năng quản lý người dùng trong hệ thống y tế.
 * Bao gồm các chức năng thêm, sửa, xóa người dùng (bác sĩ và bệnh nhân).
 */
public class UserService {
    // Sử dụng Map để lưu trữ người dùng, với username là key
    private final Map<String, User> userMap = new HashMap<>();

    /**
     * Thêm người dùng mới vào hệ thống
     *
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @param fullName Họ tên đầy đủ
     * @param email Email
     * @param phone Số điện thoại
     * @param role Vai trò (DOCTOR hoặc PATIENT)
     * @param additionalInfo Thông tin bổ sung (ghi chú cho bác sĩ hoặc thông tin bệnh tình cho bệnh nhân)
     * @return User đối tượng người dùng đã được thêm
     * @throws IllegalArgumentException nếu tên đăng nhập đã tồn tại hoặc thông tin không hợp lệ
     */
    public User addUser(String username, String password, String fullName,
                        String email, String phone, Role role, String additionalInfo) {
        // Kiểm tra thông tin đầu vào
        validateUserInput(username, password, fullName, role);

        // Kiểm tra username đã tồn tại chưa
        if (userMap.containsKey(username)) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại: " + username);
        }

        // Tạo người dùng mới
        User user = new User(username, password, fullName, email, phone, role);

        // Thêm thông tin bổ sung tùy theo vai trò
        if (role == Role.DOCTOR && additionalInfo != null && !additionalInfo.isEmpty()) {
            user.setNote(additionalInfo);
        } else if (role == Role.PATIENT && additionalInfo != null && !additionalInfo.isEmpty()) {
            user.setIllnessInfo(additionalInfo);
        }

        // Lưu người dùng vào map
        userMap.put(username, user);

        return user;
    }

    /**
     * Thêm bác sĩ mới vào hệ thống
     *
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @param fullName Họ tên đầy đủ
     * @param email Email
     * @param phone Số điện thoại
     * @param note Ghi chú cho bác sĩ
     * @return User đối tượng bác sĩ đã được thêm
     * @throws IllegalArgumentException nếu tên đăng nhập đã tồn tại hoặc thông tin không hợp lệ
     */
    public User addDoctor(String username, String password, String fullName,
                          String email, String phone, String note) {
        return addUser(username, password, fullName, email, phone, Role.DOCTOR, note);
    }

    /**
     * Thêm bệnh nhân mới vào hệ thống
     *
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @param fullName Họ tên đầy đủ
     * @param email Email
     * @param phone Số điện thoại
     * @param illnessInfo Thông tin bệnh tình
     * @return User đối tượng bệnh nhân đã được thêm
     * @throws IllegalArgumentException nếu tên đăng nhập đã tồn tại hoặc thông tin không hợp lệ
     */
    public User addPatient(String username, String password, String fullName,
                           String email, String phone, String illnessInfo) {
        return addUser(username, password, fullName, email, phone, Role.PATIENT, illnessInfo);
    }

    /**
     * Cập nhật thông tin người dùng
     *
     * @param username Tên đăng nhập của người dùng cần cập nhật
     * @param password Mật khẩu mới (null nếu không thay đổi)
     * @param fullName Họ tên mới (null nếu không thay đổi)
     * @param email Email mới (null nếu không thay đổi)
     * @param phone Số điện thoại mới (null nếu không thay đổi)
     * @param additionalInfo Thông tin bổ sung mới (null nếu không thay đổi)
     * @return User đối tượng người dùng đã cập nhật
     * @throws IllegalArgumentException nếu không tìm thấy người dùng
     */
    public User updateUser(String username, String password, String fullName,
                           String email, String phone, String additionalInfo) {
        // Kiểm tra username có tồn tại không
        User user = getUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Không tìm thấy người dùng: " + username);
        }

        // Cập nhật thông tin
        if (password != null && !password.isEmpty()) {
            user.setPassword(password);
        }

        if (fullName != null && !fullName.isEmpty()) {
            user.setFullName(fullName);
        }

        if (email != null) {
            user.setEmail(email);
        }

        if (phone != null) {
            user.setPhone(phone);
        }

        if (additionalInfo != null) {
            if (user.getRole() == Role.DOCTOR) {
                user.setNote(additionalInfo);
            } else if (user.getRole() == Role.PATIENT) {
                user.setIllnessInfo(additionalInfo);
            }
        }

        return user;
    }

    /**
     * Xóa người dùng khỏi hệ thống
     *
     * @param username Tên đăng nhập của người dùng cần xóa
     * @return User đối tượng người dùng đã xóa
     * @throws IllegalArgumentException nếu không tìm thấy người dùng
     */
    public User deleteUser(String username) {
        User user = userMap.remove(username);
        if (user == null) {
            throw new IllegalArgumentException("Không tìm thấy người dùng: " + username);
        }
        return user;
    }

    /**
     * Lấy danh sách tất cả người dùng
     *
     * @return Danh sách tất cả người dùng
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    /**
     * Lấy danh sách tất cả bác sĩ
     *
     * @return Danh sách tất cả bác sĩ
     */
    public List<User> getAllDoctors() {
        return userMap.values().stream()
                .filter(user -> user.getRole() == Role.DOCTOR)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách tất cả bệnh nhân
     *
     * @return Danh sách tất cả bệnh nhân
     */
    public List<User> getAllPatients() {
        return userMap.values().stream()
                .filter(user -> user.getRole() == Role.PATIENT)
                .collect(Collectors.toList());
    }

    /**
     * Tìm kiếm người dùng theo tên đăng nhập
     *
     * @param username Tên đăng nhập cần tìm
     * @return User đối tượng người dùng (null nếu không tìm thấy)
     */
    public User getUserByUsername(String username) {
        return userMap.get(username);
    }

    /**
     * Tìm kiếm người dùng theo họ tên (tìm kiếm mờ)
     *
     * @param name Phần họ tên cần tìm
     * @return Danh sách người dùng có họ tên chứa chuỗi cần tìm
     */
    public List<User> searchUsersByName(String name) {
        if (name == null || name.isEmpty()) {
            return new ArrayList<>();
        }

        String searchTerm = name.toLowerCase();
        return userMap.values().stream()
                .filter(user -> user.getFullName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    /**
     * Kiểm tra thông tin đăng nhập
     *
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @return User đối tượng người dùng nếu đăng nhập thành công, null nếu thất bại
     */
    public User authenticate(String username, String password) {
        User user = getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    /**
     * Đếm số lượng người dùng theo vai trò
     *
     * @param role Vai trò cần đếm
     * @return Số lượng người dùng có vai trò tương ứng
     */
    public int countUsersByRole(Role role) {
        return (int) userMap.values().stream()
                .filter(user -> user.getRole() == role)
                .count();
    }

    /**
     * Kiểm tra và xác thực thông tin người dùng
     *
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @param fullName Họ tên
     * @param role Vai trò
     * @throws IllegalArgumentException nếu thông tin không hợp lệ
     */
    private void validateUserInput(String username, String password, String fullName, Role role) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        }

        if (username.length() < 3) {
            throw new IllegalArgumentException("Tên đăng nhập phải có ít nhất 3 ký tự");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }

        if (password.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự");
        }

        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống");
        }

        if (role == null) {
            throw new IllegalArgumentException("Vai trò không được để trống");
        }
    }

    /**
     * Thêm nhiều người dùng cùng lúc
     *
     * @param users Danh sách người dùng cần thêm
     * @return Số lượng người dùng đã thêm thành công
     */
    public int addMultipleUsers(List<User> users) {
        int count = 0;
        for (User user : users) {
            try {
                if (!userMap.containsKey(user.getUsername())) {
                    userMap.put(user.getUsername(), user);
                    count++;
                }
            } catch (Exception e) {
                // Bỏ qua người dùng gây lỗi và tiếp tục
            }
        }
        return count;
    }

    /**
     * Xóa tất cả người dùng
     */
    public void clearAllUsers() {
        userMap.clear();
    }
}
