package model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserSearchService {
    private final UserService userService;

    public UserSearchService(UserService userService) {
        this.userService = userService;
    }

    public static class SearchCriteria {
        private Role role;
        private String usernameKeyword;
        private String nameKeyword;
        private String emailKeyword;
        private String phoneKeyword;

        public void setRole(Role role) { this.role = role; }
        public void setUsernameKeyword(String usernameKeyword) { this.usernameKeyword = usernameKeyword; }
        public void setNameKeyword(String nameKeyword) { this.nameKeyword = nameKeyword; }
        public void setEmailKeyword(String emailKeyword) { this.emailKeyword = emailKeyword; }
        public void setPhoneKeyword(String phoneKeyword) { this.phoneKeyword = phoneKeyword; }

        public Role getRole() { return role; }
        public String getUsernameKeyword() { return usernameKeyword; }
        public String getNameKeyword() { return nameKeyword; }
        public String getEmailKeyword() { return emailKeyword; }
        public String getPhoneKeyword() { return phoneKeyword; }
    }

    /**
     * Search users by term and role
     */
    public List<User> searchUsers(String searchTerm, Role role) {
        List<User> users = userService.getAllUsers();
        return users.stream()
                .filter(user -> role == null || user.getRole() == role)
                .filter(user -> searchTerm == null || searchTerm.isEmpty() ||
                        user.getUserId().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        user.getUsername().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        user.getFullName().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Search user by ID
     */
    public User searchUserById(String id) {
        return userService.getUserById(id);
    }

    /**
     * Advanced search with criteria
     */
    public List<User> advancedSearch(SearchCriteria criteria) {
        List<User> users = userService.getAllUsers();
        return users.stream()
                .filter(user -> criteria.getRole() == null || user.getRole() == criteria.getRole())
                .filter(user -> criteria.getUsernameKeyword() == null || user.getUsername().toLowerCase().contains(criteria.getUsernameKeyword().toLowerCase()))
                .filter(user -> criteria.getNameKeyword() == null || user.getFullName().toLowerCase().contains(criteria.getNameKeyword().toLowerCase()))
                .filter(user -> criteria.getEmailKeyword() == null || (user.getEmail() != null && user.getEmail().toLowerCase().contains(criteria.getEmailKeyword().toLowerCase())))
                .filter(user -> criteria.getPhoneKeyword() == null || (user.getPhoneNumber() != null && user.getPhoneNumber().toLowerCase().contains(criteria.getPhoneKeyword().toLowerCase())))
                .collect(Collectors.toList());
    }
}