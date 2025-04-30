package model.entity;


import model.entity.Role;
import model.entity.User;
import model.entity.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Class UserSearchService cung cấp các chức năng tìm kiếm nâng cao người dùng trong hệ thống y tế.
 * Cho phép tìm kiếm người dùng (bác sĩ và bệnh nhân) theo nhiều tiêu chí khác nhau.
 */
public class UserSearchService {
    private final UserService userService;

    /**
     * Khởi tạo UserSearchService với UserService đã có
     *
     * @param userService Dịch vụ quản lý người dùng
     */
    public UserSearchService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Tìm kiếm người dùng theo mã (ID/username)
     *
     * @param userId Mã người dùng cần tìm
     * @return User đối tượng người dùng, hoặc null nếu không tìm thấy
     */
    public User searchUserById(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return null;
        }

        return userService.getUserByUsername(userId);
    }

    /**
     * Tìm kiếm bác sĩ theo mã
     *
     * @param doctorId Mã bác sĩ cần tìm
     * @return User đối tượng bác sĩ, hoặc null nếu không tìm thấy hoặc không phải bác sĩ
     */
    public User searchDoctorById(String doctorId) {
        User user = searchUserById(doctorId);
        if (user != null && user.getRole() == Role.DOCTOR) {
            return user;
        }
        return null;
    }

    /**
     * Tìm kiếm bệnh nhân theo mã
     *
     * @param patientId Mã bệnh nhân cần tìm
     * @return User đối tượng bệnh nhân, hoặc null nếu không tìm thấy hoặc không phải bệnh nhân
     */
    public User searchPatientById(String patientId) {
        User user = searchUserById(patientId);
        if (user != null && user.getRole() == Role.PATIENT) {
            return user;
        }
        return null;
    }

    /**
     * Tìm kiếm người dùng theo nhiều tiêu chí
     *
     * @param searchTerm Từ khóa tìm kiếm (có thể là tên, email, hoặc một phần thông tin)
     * @param role Vai trò người dùng cần tìm (null nếu tìm tất cả vai trò)
     * @return Danh sách người dùng thỏa mãn điều kiện tìm kiếm
     */
    public List<User> searchUsers(String searchTerm, Role role) {
        List<User> users = userService.getAllUsers();

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            if (role == null) {
                return users;
            }
            return users.stream()
                    .filter(user -> user.getRole() == role)
                    .collect(Collectors.toList());
        }

        String term = searchTerm.toLowerCase().trim();

        Predicate<User> searchFilter = user ->
                user.getUsername().toLowerCase().contains(term) ||
                        user.getFullName().toLowerCase().contains(term) ||
                        (user.getEmail() != null && user.getEmail().toLowerCase().contains(term)) ||
                        (user.getPhone() != null && user.getPhone().toLowerCase().contains(term));

        if (role != null) {
            searchFilter = searchFilter.and(user -> user.getRole() == role);
        }

        return users.stream()
                .filter(searchFilter)
                .collect(Collectors.toList());
    }

    /**
     * Tìm kiếm bác sĩ theo từ khóa
     *
     * @param searchTerm Từ khóa tìm kiếm
     * @return Danh sách bác sĩ thỏa mãn điều kiện tìm kiếm
     */
    public List<User> searchDoctors(String searchTerm) {
        return searchUsers(searchTerm, Role.DOCTOR);
    }

    /**
     * Tìm kiếm bệnh nhân theo từ khóa
     *
     * @param searchTerm Từ khóa tìm kiếm
     * @return Danh sách bệnh nhân thỏa mãn điều kiện tìm kiếm
     */
    public List<User> searchPatients(String searchTerm) {
        return searchUsers(searchTerm, Role.PATIENT);
    }

    /**
     * Tìm kiếm nâng cao với nhiều tiêu chí phức tạp
     *
     * @param searchCriteria Các tiêu chí tìm kiếm được áp dụng
     * @return Danh sách người dùng thỏa mãn điều kiện tìm kiếm
     */
    public List<User> advancedSearch(SearchCriteria searchCriteria) {
        List<User> allUsers = userService.getAllUsers();

        if (searchCriteria == null) {
            return allUsers;
        }

        return allUsers.stream()
                .filter(user -> matchesSearchCriteria(user, searchCriteria))
                .collect(Collectors.toList());
    }

    /**
     * Kiểm tra người dùng có thỏa mãn các tiêu chí tìm kiếm không
     *
     * @param user Người dùng cần kiểm tra
     * @param criteria Tiêu chí tìm kiếm
     * @return true nếu người dùng thỏa mãn tiêu chí, false nếu không
     */
    private boolean matchesSearchCriteria(User user, SearchCriteria criteria) {
        // Lọc theo vai trò
        if (criteria.getRole() != null && user.getRole() != criteria.getRole()) {
            return false;
        }

        // Lọc theo từ khóa cho username
        if (criteria.getUsernameKeyword() != null && !criteria.getUsernameKeyword().isEmpty()) {
            String keyword = criteria.getUsernameKeyword().toLowerCase();
            if (!user.getUsername().toLowerCase().contains(keyword)) {
                return false;
            }
        }

        // Lọc theo từ khóa cho họ tên
        if (criteria.getNameKeyword() != null && !criteria.getNameKeyword().isEmpty()) {
            String keyword = criteria.getNameKeyword().toLowerCase();
            if (!user.getFullName().toLowerCase().contains(keyword)) {
                return false;
            }
        }

        // Lọc theo từ khóa cho email
        if (criteria.getEmailKeyword() != null && !criteria.getEmailKeyword().isEmpty()) {
            String keyword = criteria.getEmailKeyword().toLowerCase();
            if (user.getEmail() == null || !user.getEmail().toLowerCase().contains(keyword)) {
                return false;
            }
        }

        // Lọc theo từ khóa cho số điện thoại
        if (criteria.getPhoneKeyword() != null && !criteria.getPhoneKeyword().isEmpty()) {
            String keyword = criteria.getPhoneKeyword().toLowerCase();
            if (user.getPhone() == null || !user.getPhone().toLowerCase().contains(keyword)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Lớp nội bộ chứa các tiêu chí tìm kiếm nâng cao
     */
    public static class SearchCriteria {
        private Role role;
        private String usernameKeyword;
        private String nameKeyword;
        private String emailKeyword;
        private String phoneKeyword;

        public SearchCriteria() {
            // Khởi tạo mặc định
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public String getUsernameKeyword() {
            return usernameKeyword;
        }

        public void setUsernameKeyword(String usernameKeyword) {
            this.usernameKeyword = usernameKeyword;
        }

        public String getNameKeyword() {
            return nameKeyword;
        }

        public void setNameKeyword(String nameKeyword) {
            this.nameKeyword = nameKeyword;
        }

        public String getEmailKeyword() {
            return emailKeyword;
        }

        public void setEmailKeyword(String emailKeyword) {
            this.emailKeyword = emailKeyword;
        }

        public String getPhoneKeyword() {
            return phoneKeyword;
        }

        public void setPhoneKeyword(String phoneKeyword) {
            this.phoneKeyword = phoneKeyword;
        }
    }
}
