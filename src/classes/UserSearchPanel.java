package classes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import model.entity.Role;
import model.entity.User;
import model.entity.UserService;
import model.entity.UserSearchService;
import model.entity.UserSearchService.SearchCriteria;

/**
 * Panel tìm kiếm và hiển thị thông tin người dùng trong hệ thống y tế
 */
public class UserSearchPanel extends JPanel {
    private User currentUser;
    private UserService userService;
    private UserSearchService searchService;

    // Components cho tìm kiếm
    private JTextField searchField;
    private JComboBox<String> roleFilter;
    private JRadioButton simpleSearchRadio;
    private JRadioButton advancedSearchRadio;

    // Components cho tìm kiếm nâng cao
    private JTextField usernameField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPanel advancedPanel;

    // Components cho tìm kiếm nhanh theo ID
    private JTextField idSearchField;
    private JButton idSearchButton;

    // Components cho hiển thị kết quả
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JLabel totalResultsLabel;

    // Components cho hiển thị chi tiết
    private JPanel userDetailPanel;
    private JLabel avatarLabel;
    private JLabel idLabel;
    private JLabel nameLabel;
    private JLabel roleLabel;
    private JLabel emailLabel;
    private JLabel phoneLabel;
    private JLabel statusLabel;

    private List<User> currentResults;

    /**
     * Constructor với User hiện tại và UserService
     */
    public UserSearchPanel(User currentUser, UserService userService) {
        this.currentUser = currentUser;
        this.userService = userService;
        this.searchService = new UserSearchService(userService);
        this.currentResults = new ArrayList<>();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        // Tạo giao diện người dùng
        initUI();

        // Load dữ liệu mặc định (tất cả người dùng)
        performSimpleSearch("", null);
    }

    /**
     * Khởi tạo giao diện người dùng
     */
    private void initUI() {
        // Panel chính sử dụng JSplitPane để chia màn hình
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(700);
        mainSplitPane.setResizeWeight(0.7);

        // Panel bên trái: tìm kiếm và kết quả
        JPanel leftPanel = createSearchAndResultPanel();

        // Panel bên phải: hiển thị chi tiết người dùng
        userDetailPanel = createUserDetailPanel();

        mainSplitPane.setLeftComponent(leftPanel);
        mainSplitPane.setRightComponent(userDetailPanel);

        add(mainSplitPane, BorderLayout.CENTER);
    }

    /**
     * Tạo panel tìm kiếm và hiển thị kết quả
     */
    private JPanel createSearchAndResultPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Panel tìm kiếm ở trên
        JPanel searchPanel = createSearchPanel();
        panel.add(searchPanel, BorderLayout.NORTH);

        // Panel kết quả ở dưới
        JPanel resultPanel = createResultPanel();
        panel.add(resultPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Tạo panel tìm kiếm với các tùy chọn
     */
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm người dùng"));

        // Panel tìm kiếm theo ID
        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        idPanel.add(new JLabel("Tìm theo ID:"));
        idSearchField = new JTextField(15);
        idPanel.add(idSearchField);

        idSearchButton = new JButton("Tìm");
        idSearchButton.addActionListener(e -> searchById());
        idPanel.add(idSearchButton);

        JButton clearButton = new JButton("Xóa");
        clearButton.addActionListener(e -> {
            idSearchField.setText("");
            searchField.setText("");
            roleFilter.setSelectedIndex(0);
            clearAdvancedSearch();
            performSimpleSearch("", null);
        });
        idPanel.add(clearButton);

        // Panel tìm kiếm đơn giản
        JPanel simplePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchField = new JTextField(20);

        String[] roleOptions = {"Tất cả", "Bác sĩ", "Bệnh nhân", "Quản trị viên"};
        roleFilter = new JComboBox<>(roleOptions);

        JButton searchButton = new JButton("Tìm kiếm");
        searchButton.addActionListener(e -> {
            if (simpleSearchRadio.isSelected()) {
                performSimpleSearch(searchField.getText(), getSelectedRole());
            } else {
                performAdvancedSearch();
            }
        });

        simplePanel.add(searchLabel);
        simplePanel.add(searchField);
        simplePanel.add(new JLabel("Vai trò:"));
        simplePanel.add(roleFilter);
        simplePanel.add(searchButton);

        // Radio buttons để chọn kiểu tìm kiếm
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        simpleSearchRadio = new JRadioButton("Tìm kiếm đơn giản", true);
        advancedSearchRadio = new JRadioButton("Tìm kiếm nâng cao", false);

        ButtonGroup radioGroup = new ButtonGroup();
        radioGroup.add(simpleSearchRadio);
        radioGroup.add(advancedSearchRadio);

        simpleSearchRadio.addActionListener(e -> toggleAdvancedSearch(false));
        advancedSearchRadio.addActionListener(e -> toggleAdvancedSearch(true));

        radioPanel.add(simpleSearchRadio);
        radioPanel.add(advancedSearchRadio);

        // Panel tìm kiếm nâng cao
        advancedPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        advancedPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        advancedPanel.add(new JLabel("Tên đăng nhập:"));
        usernameField = new JTextField();
        advancedPanel.add(usernameField);

        advancedPanel.add(new JLabel("Họ tên:"));
        nameField = new JTextField();
        advancedPanel.add(nameField);

        advancedPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        advancedPanel.add(emailField);

        advancedPanel.add(new JLabel("Số điện thoại:"));
        phoneField = new JTextField();
        advancedPanel.add(phoneField);

        // Thêm các panel vào panel chính
        panel.add(idPanel);
        panel.add(simplePanel);
        panel.add(radioPanel);
        panel.add(advancedPanel);

        // Mặc định ẩn panel tìm kiếm nâng cao
        advancedPanel.setVisible(false);

        return panel;
    }

    /**
     * Bật/tắt hiển thị panel tìm kiếm nâng cao
     */
    private void toggleAdvancedSearch(boolean show) {
        advancedPanel.setVisible(show);
    }

    /**
     * Xóa trắng các trường tìm kiếm nâng cao
     */
    private void clearAdvancedSearch() {
        usernameField.setText("");
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
    }

    /**
     * Tạo panel hiển thị kết quả tìm kiếm
     */
    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Kết quả tìm kiếm"));

        // Tạo bảng kết quả
        String[] columns = {"ID", "Họ tên", "Vai trò", "Email", "Số điện thoại"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resultTable = new JTable(tableModel);
        resultTable.setRowHeight(25);
        resultTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Khi chọn một hàng, hiển thị thông tin chi tiết
        resultTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && resultTable.getSelectedRow() >= 0 &&
                    resultTable.getSelectedRow() < currentResults.size()) {
                displayUserDetails(currentResults.get(resultTable.getSelectedRow()));
            }
        });

        JScrollPane scrollPane = new JScrollPane(resultTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel hiển thị tổng số kết quả
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalResultsLabel = new JLabel("Tổng số kết quả: 0");
        totalResultsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoPanel.add(totalResultsLabel);

        // Nút lọc nhanh theo vai trò
        JButton doctorFilterButton = new JButton("Chỉ hiện bác sĩ");
        doctorFilterButton.addActionListener(e -> {
            searchField.setText("");
            roleFilter.setSelectedIndex(1); // Bác sĩ
            performSimpleSearch("", Role.DOCTOR);
        });

        JButton patientFilterButton = new JButton("Chỉ hiện bệnh nhân");
        patientFilterButton.addActionListener(e -> {
            searchField.setText("");
            roleFilter.setSelectedIndex(2); // Bệnh nhân
            performSimpleSearch("", Role.PATIENT);
        });

        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(doctorFilterButton);
        infoPanel.add(patientFilterButton);

        panel.add(infoPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Tạo panel hiển thị thông tin chi tiết người dùng
     */
    private JPanel createUserDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin chi tiết"));

        // Panel chứa avatar ở trên
        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(150, 150));
        avatarLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        avatarLabel.setBackground(Color.LIGHT_GRAY);
        avatarLabel.setOpaque(true);

        avatarPanel.add(avatarLabel);
        panel.add(avatarPanel, BorderLayout.NORTH);

        // Panel chứa thông tin chi tiết ở giữa
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        idLabel = createInfoLabel("ID: ");
        nameLabel = createInfoLabel("Họ tên: ");
        roleLabel = createInfoLabel("Vai trò: ");
        emailLabel = createInfoLabel("Email: ");
        phoneLabel = createInfoLabel("Số điện thoại: ");
        statusLabel = createInfoLabel("Trạng thái: ");

        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(idLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(roleLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(emailLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(phoneLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(statusLabel);
        infoPanel.add(Box.createVerticalStrut(10));

        panel.add(new JScrollPane(infoPanel), BorderLayout.CENTER);

        // Panel chứa các nút chức năng ở dưới
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton viewProfileButton = new JButton("Xem hồ sơ đầy đủ");
        viewProfileButton.addActionListener(e -> {
            int selectedRow = resultTable.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < currentResults.size()) {
                viewUserProfile(currentResults.get(selectedRow));
            } else {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn một người dùng",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton contactButton = new JButton("Liên hệ");
        contactButton.addActionListener(e -> {
            int selectedRow = resultTable.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < currentResults.size()) {
                contactUser(currentResults.get(selectedRow));
            } else {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn một người dùng",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        buttonPanel.add(viewProfileButton);
        buttonPanel.add(contactButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Tạo label hiển thị thông tin với định dạng nhất quán
     */
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    /**
     * Lấy vai trò được chọn từ combobox
     */
    private Role getSelectedRole() {
        int selectedIndex = roleFilter.getSelectedIndex();
        switch (selectedIndex) {
            case 1: return Role.DOCTOR;
            case 2: return Role.PATIENT;
            case 3: return Role.ADMIN;
            default: return null;
        }
    }

    /**
     * Tìm kiếm theo ID
     */
    private void searchById() {
        String id = idSearchField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập ID người dùng",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = searchService.searchUserById(id);
        currentResults.clear();

        if (user != null) {
            currentResults.add(user);
            updateResultTable();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy người dùng với ID: " + id,
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            tableModel.setRowCount(0);
            totalResultsLabel.setText("Tổng số kết quả: 0");
            clearUserDetails();
        }
    }

    /**
     * Thực hiện tìm kiếm đơn giản
     */
    private void performSimpleSearch(String searchTerm, Role role) {
        currentResults = searchService.searchUsers(searchTerm, role);
        updateResultTable();
    }

    /**
     * Thực hiện tìm kiếm nâng cao
     */
    private void performAdvancedSearch() {
        SearchCriteria criteria = new SearchCriteria();

        // Thiết lập các tiêu chí tìm kiếm
        criteria.setRole(getSelectedRole());

        String username = usernameField.getText().trim();
        if (!username.isEmpty()) {
            criteria.setUsernameKeyword(username);
        }

        String name = nameField.getText().trim();
        if (!name.isEmpty()) {
            criteria.setNameKeyword(name);
        }

        String email = emailField.getText().trim();
        if (!email.isEmpty()) {
            criteria.setEmailKeyword(email);
        }

        String phone = phoneField.getText().trim();
        if (!phone.isEmpty()) {
            criteria.setPhoneKeyword(phone);
        }

        // Thực hiện tìm kiếm và cập nhật kết quả
        currentResults = searchService.advancedSearch(criteria);
        updateResultTable();
    }

    /**
     * Cập nhật bảng kết quả với dữ liệu mới
     */
    private void updateResultTable() {
        // Xóa dữ liệu cũ
        tableModel.setRowCount(0);

        // Thêm dữ liệu mới
        for (User user : currentResults) {
            Object[] rowData = {
                    user.getUsername(),
                    user.getFullName(),
                    getRoleDisplayName(user.getRole()),
                    user.getEmail() != null ? user.getEmail() : "",
                    user.getPhone() != null ? user.getPhone() : ""
            };
            tableModel.addRow(rowData);
        }

        // Cập nhật label hiển thị tổng số kết quả
        totalResultsLabel.setText("Tổng số kết quả: " + currentResults.size());

        // Nếu có kết quả, chọn hàng đầu tiên
        if (!currentResults.isEmpty()) {
            resultTable.setRowSelectionInterval(0, 0);
            displayUserDetails(currentResults.get(0));
        } else {
            // Nếu không có kết quả, xóa thông tin chi tiết
            clearUserDetails();
        }
    }

    /**
     * Hiển thị thông tin chi tiết của người dùng
     */
    private void displayUserDetails(User user) {
        if (user == null) {
            clearUserDetails();
            return;
        }

        // Cập nhật thông tin
        idLabel.setText("ID: " + user.getUsername());
        nameLabel.setText("Họ tên: " + user.getFullName());
        roleLabel.setText("Vai trò: " + getRoleDisplayName(user.getRole()));
        emailLabel.setText("Email: " + (user.getEmail() != null ? user.getEmail() : "Chưa cung cấp"));
        phoneLabel.setText("Số điện thoại: " + (user.getPhone() != null ? user.getPhone() : "Chưa cung cấp"));
        statusLabel.setText("Trạng thái: Đang hoạt động");

        // Đặt màu nền cho avatar dựa theo vai trò
        if (user.getRole() == Role.DOCTOR) {
            avatarLabel.setBackground(new Color(100, 180, 220));  // Xanh dương cho bác sĩ
        } else if (user.getRole() == Role.PATIENT) {
            avatarLabel.setBackground(new Color(100, 200, 150));  // Xanh lá cho bệnh nhân
        } else if (user.getRole() == Role.ADMIN) {
            avatarLabel.setBackground(new Color(200, 130, 100));  // Cam cho admin
        } else {
            avatarLabel.setBackground(Color.LIGHT_GRAY);  // Mặc định
        }

        // Hiển thị ký tự đầu của tên người dùng trên avatar
        avatarLabel.setText(user.getFullName().substring(0, 1).toUpperCase());
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        avatarLabel.setFont(new Font("Arial", Font.BOLD, 72));
        avatarLabel.setForeground(Color.WHITE);
    }

    /**
     * Xóa thông tin chi tiết người dùng
     */
    private void clearUserDetails() {
        idLabel.setText("ID: ");
        nameLabel.setText("Họ tên: ");
        roleLabel.setText("Vai trò: ");
        emailLabel.setText("Email: ");
        phoneLabel.setText("Số điện thoại: ");
        statusLabel.setText("Trạng thái: ");

        // Đặt lại avatar mặc định
        avatarLabel.setBackground(Color.LIGHT_GRAY);
        avatarLabel.setText("");
    }

    /**
     * Lấy tên hiển thị của vai trò
     */
    private String getRoleDisplayName(Role role) {
        if (role == null) return "Không xác định";

        switch (role) {
            case DOCTOR: return "Bác sĩ";
            case PATIENT: return "Bệnh nhân";
            case ADMIN: return "Quản trị viên";
            default: return "Không xác định";
        }
    }

    /**
     * Xem hồ sơ đầy đủ của người dùng
     */
    private void viewUserProfile(User user) {
        // Tạo dialog hiển thị thông tin đầy đủ của người dùng
        JDialog profileDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Hồ sơ người dùng - " + user.getFullName(), true);
        profileDialog.setLayout(new BorderLayout(10, 10));

        // Panel chứa thông tin chi tiết
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Avatar
        JLabel avatarDisplay = new JLabel();
        avatarDisplay.setPreferredSize(new Dimension(120, 120));
        avatarDisplay.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        avatarDisplay.setBackground(avatarLabel.getBackground());
        avatarDisplay.setOpaque(true);
        avatarDisplay.setText(user.getFullName().substring(0, 1).toUpperCase());
        avatarDisplay.setHorizontalAlignment(SwingConstants.CENTER);
        avatarDisplay.setFont(new Font("Arial", Font.BOLD, 48));
        avatarDisplay.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.insets = new Insets(5, 5, 5, 20);
        infoPanel.add(avatarDisplay, gbc);

        // Thông tin cơ bản
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        infoPanel.add(new JLabel("ID: " + user.getUsername()), gbc);

        gbc.gridy++;
        infoPanel.add(new JLabel("Họ tên: " + user.getFullName()), gbc);

        gbc.gridy++;
        infoPanel.add(new JLabel("Vai trò: " + getRoleDisplayName(user.getRole())), gbc);

        gbc.gridy++;
        infoPanel.add(new JLabel("Email: " + (user.getEmail() != null ? user.getEmail() : "Chưa cung cấp")), gbc);

        gbc.gridy++;
        infoPanel.add(new JLabel("Số điện thoại: " + (user.getPhone() != null ? user.getPhone() : "Chưa cung cấp")), gbc);

        gbc.gridy++;
        infoPanel.add(new JLabel("Trạng thái: Đang hoạt động"), gbc);

        // Thêm thông tin chi tiết dựa trên vai trò
        if (user.getRole() == Role.DOCTOR) {
            gbc.gridy++;
            infoPanel.add(new JLabel("Chuyên khoa: Nội khoa"), gbc);

            gbc.gridy++;
            infoPanel.add(new JLabel("Kinh nghiệm: 5 năm"), gbc);
        } else if (user.getRole() == Role.PATIENT) {
            gbc.gridy++;
            infoPanel.add(new JLabel("Nhóm máu: O"), gbc);

            gbc.gridy++;
            infoPanel.add(new JLabel("Ngày sinh: 01/01/1990"), gbc);
        }

        // Thêm panel vào dialog
        profileDialog.add(new JScrollPane(infoPanel), BorderLayout.CENTER);

        // Thêm nút đóng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> profileDialog.dispose());
        buttonPanel.add(closeButton);

        profileDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Hiển thị dialog
        profileDialog.setSize(500, 400);
        profileDialog.setLocationRelativeTo(this);
        profileDialog.setVisible(true);
    }

    /**
     * Hiển thị dialog liên hệ với người dùng
     */
    private void contactUser(User user) {
        // Tạo dialog liên hệ
        JDialog contactDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Liên hệ với " + user.getFullName(), true);
        contactDialog.setLayout(new BorderLayout(10, 10));

        // Panel nhập nội dung
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        contentPanel.add(new JLabel("Nội dung:"), BorderLayout.NORTH);

        JTextArea messageArea = new JTextArea(10, 30);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        contentPanel.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton sendButton = new JButton("Gửi");
        sendButton.addActionListener(e -> {
            String message = messageArea.getText().trim();
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(contactDialog,
                        "Vui lòng nhập nội dung tin nhắn",
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(contactDialog,
                        "Đã gửi tin nhắn đến " + user.getFullName(),
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                contactDialog.dispose();
            }
        });

        JButton cancelButton = new JButton("Hủy");
        cancelButton.addActionListener(e -> contactDialog.dispose());

        buttonPanel.add(sendButton);
        buttonPanel.add(cancelButton);

        // Thêm các panel vào dialog
        contactDialog.add(contentPanel, BorderLayout.CENTER);
        contactDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Hiển thị dialog
        contactDialog.pack();
        contactDialog.setLocationRelativeTo(this);
        contactDialog.setVisible(true);
    }
}