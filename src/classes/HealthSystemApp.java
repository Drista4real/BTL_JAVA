package classes;

import model.entity.Role;
import model.entity.User;
import model.entity.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ứng dụng quản lý hệ thống y tế cho phép quản lý người dùng (bác sĩ, bệnh nhân)
 */
public class HealthSystemApp extends JFrame {
    private UserService userService;
    private User currentUser;
    private JTabbedPane tabbedPane;
    private UserSearchPanel searchPanel;

    /**
     * Constructor khởi tạo ứng dụng
     */
    public HealthSystemApp() {
        // Khởi tạo dịch vụ và dữ liệu mẫu
        userService = new UserService();
        initializeSampleData();

        // Thiết lập giao diện
        setupUI();
    }

    /**
     * Khởi tạo dữ liệu mẫu cho ứng dụng
     */
    private void initializeSampleData() {
        try {
            // Thêm bác sĩ
            userService.addDoctor("bsnam", "doctor123", "Bác Sĩ Hoàng Nam",
                    "nam@hospital.com", "0912345678", "Khoa Nội");
            userService.addDoctor("bslinh", "doctor123", "Bác Sĩ Thanh Linh",
                    "linh@hospital.com", "0923456789", "Khoa Ngoại");
            userService.addDoctor("bsminh", "doctor123", "Bác Sĩ Đức Minh",
                    "minh@hospital.com", "0934567890", "Khoa Tim Mạch");

            // Thêm bệnh nhân
            userService.addPatient("bnhoa", "patient123", "Nguyễn Thị Hoa",
                    "hoa@gmail.com", "0945678901", "Đau đầu, sốt");
            userService.addPatient("bntuan", "patient123", "Trần Văn Tuấn",
                    "tuan@gmail.com", "0956789012", "Đau dạ dày");
            userService.addPatient("bnthuy", "patient123", "Lê Thị Thủy",
                    "thuy@gmail.com", "0967890123", "Viêm khớp");
            userService.addPatient("bnhai", "patient123", "Phạm Văn Hải",
                    "hai@gmail.com", "0978901234", "Cảm cúm");

            System.out.println("Khởi tạo dữ liệu mẫu thành công!");
        } catch (Exception e) {
            System.err.println("Lỗi khi khởi tạo dữ liệu mẫu: " + e.getMessage());
        }
    }

    /**
     * Thiết lập giao diện người dùng chính
     */
    private void setupUI() {
        // Thiết lập cơ bản của JFrame
        setTitle("Hệ Thống Quản Lý Y Tế");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 700));

        // Tạo menu bar
        setJMenuBar(createMenuBar());

        // Tạo panel chứa nội dung chính
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tạo header với thông tin người dùng đăng nhập
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Tạo tabbed pane để chứa các chức năng
        tabbedPane = new JTabbedPane();

        // Tab tìm kiếm người dùng
        searchPanel = new UserSearchPanel(currentUser, userService);
        tabbedPane.addTab("Tìm kiếm người dùng", new ImageIcon(), searchPanel, "Tìm kiếm và xem thông tin người dùng");

        // Tab thêm người dùng mới
        tabbedPane.addTab("Thêm người dùng", new ImageIcon(), createUserAddPanel(), "Thêm người dùng mới vào hệ thống");

        // Tab báo cáo thống kê
        tabbedPane.addTab("Báo cáo thống kê", new ImageIcon(), createReportPanel(), "Xem báo cáo thống kê hệ thống");

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Thêm panel chính vào frame
        setContentPane(mainPanel);

        // Hiển thị cửa sổ
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Tạo menu bar cho ứng dụng
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu Hệ thống
        JMenu systemMenu = new JMenu("Hệ thống");

        JMenuItem loginItem = new JMenuItem("Đăng nhập");
        loginItem.addActionListener(e -> showLoginDialog());

        JMenuItem logoutItem = new JMenuItem("Đăng xuất");
        logoutItem.addActionListener(e -> performLogout());

        JMenuItem exitItem = new JMenuItem("Thoát");
        exitItem.addActionListener(e -> System.exit(0));

        systemMenu.add(loginItem);
        systemMenu.add(logoutItem);
        systemMenu.addSeparator();
        systemMenu.add(exitItem);

        // Menu Quản lý
        JMenu manageMenu = new JMenu("Quản lý");

        JMenuItem userItem = new JMenuItem("Quản lý người dùng");
        userItem.addActionListener(e -> tabbedPane.setSelectedIndex(0));

        JMenuItem doctorItem = new JMenuItem("Quản lý bác sĩ");
        doctorItem.addActionListener(e -> {
            tabbedPane.setSelectedIndex(0);
            // Gọi hàm lọc chỉ hiển thị bác sĩ
            // Giả định là có phương thức này trong searchPanel
        });

        JMenuItem patientItem = new JMenuItem("Quản lý bệnh nhân");
        patientItem.addActionListener(e -> {
            tabbedPane.setSelectedIndex(0);
            // Gọi hàm lọc chỉ hiển thị bệnh nhân
            // Giả định là có phương thức này trong searchPanel
        });

        manageMenu.add(userItem);
        manageMenu.add(doctorItem);
        manageMenu.add(patientItem);

        // Menu Trợ giúp
        JMenu helpMenu = new JMenu("Trợ giúp");

        JMenuItem aboutItem = new JMenuItem("Giới thiệu");
        aboutItem.addActionListener(e -> showAboutDialog());

        helpMenu.add(aboutItem);

        // Thêm các menu vào menu bar
        menuBar.add(systemMenu);
        menuBar.add(manageMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    /**
     * Tạo panel header hiển thị thông tin người dùng đăng nhập
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Tiêu đề phía trái
        JLabel titleLabel = new JLabel("HỆ THỐNG QUẢN LÝ Y TẾ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.WEST);

        // Thông tin người dùng phía phải
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);

        JLabel userInfoLabel = new JLabel("Xin chào, " + currentUser.getFullName());
        userInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.addActionListener(e -> performLogout());

        userPanel.add(userInfoLabel);
        userPanel.add(logoutButton);

        panel.add(userPanel, BorderLayout.EAST);

        return panel;
    }

    /**
     * Tạo panel thêm người dùng mới
     */
    private JPanel createUserAddPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tiêu đề
        JLabel titleLabel = new JLabel("THÊM NGƯỜI DÙNG MỚI");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Panel form nhập liệu
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Tên đăng nhập
        formPanel.add(new JLabel("Tên đăng nhập:"));
        JTextField usernameField = new JTextField();
        formPanel.add(usernameField);

        // Mật khẩu
        formPanel.add(new JLabel("Mật khẩu:"));
        JPasswordField passwordField = new JPasswordField();
        formPanel.add(passwordField);

        // Họ tên
        formPanel.add(new JLabel("Họ tên:"));
        JTextField nameField = new JTextField();
        formPanel.add(nameField);

        // Email
        formPanel.add(new JLabel("Email:"));
        JTextField emailField = new JTextField();
        formPanel.add(emailField);

        // Số điện thoại
        formPanel.add(new JLabel("Số điện thoại:"));
        JTextField phoneField = new JTextField();
        formPanel.add(phoneField);

        // Vai trò
        formPanel.add(new JLabel("Vai trò:"));
        String[] roles = {"Bác sĩ", "Bệnh nhân"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        formPanel.add(roleComboBox);

        // Thông tin bổ sung
        formPanel.add(new JLabel("Thông tin bổ sung:"));
        JTextArea infoArea = new JTextArea(3, 20);
        infoArea.setLineWrap(true);
        formPanel.add(new JScrollPane(infoArea));

        panel.add(formPanel, BorderLayout.CENTER);

        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton saveButton = new JButton("Lưu");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lấy thông tin từ form
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String fullName = nameField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                String additionalInfo = infoArea.getText().trim();

                // Xác định vai trò
                Role role = roleComboBox.getSelectedIndex() == 0 ? Role.DOCTOR : Role.PATIENT;

                try {
                    // Gọi service để thêm người dùng
                    boolean success = userService.addUser(new User(username, password, fullName,
                            email, phone, additionalInfo, role));

                    if (!success) {
                        throw new IllegalArgumentException("Không thể thêm người dùng, hãy kiểm tra lại thông tin.");
                    }

                    User newUser = userService.findUserByUsername(username);

                    // Hiển thị thông báo thành công
                    JOptionPane.showMessageDialog(panel,
                            "Thêm người dùng thành công: " + newUser.getFullName(),
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);

                    // Xóa form
                    usernameField.setText("");
                    passwordField.setText("");
                    nameField.setText("");
                    emailField.setText("");
                    phoneField.setText("");
                    infoArea.setText("");

                    // Cập nhật lại panel tìm kiếm - cần sửa vì performSimpleSearch là private
                    refreshSearchResults();

                } catch (IllegalArgumentException ex) {
                    // Hiển thị thông báo lỗi
                    JOptionPane.showMessageDialog(panel,
                            "Lỗi: " + ex.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton clearButton = new JButton("Xóa trắng");
        clearButton.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
            nameField.setText("");
            emailField.setText("");
            phoneField.setText("");
            infoArea.setText("");
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Làm mới kết quả tìm kiếm trong UserSearchPanel
     */
    private void refreshSearchResults() {
        // Tạo mới panel tìm kiếm để làm mới dữ liệu
        searchPanel = new UserSearchPanel(currentUser, userService);
        tabbedPane.setComponentAt(0, searchPanel);
        tabbedPane.repaint();
    }

    /**
     * Tạo panel báo cáo thống kê
     */
    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tiêu đề
        JLabel titleLabel = new JLabel("BÁO CÁO THỐNG KÊ HỆ THỐNG");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Panel chứa các báo cáo
        JPanel reportContentPanel = new JPanel();
        reportContentPanel.setLayout(new BoxLayout(reportContentPanel, BoxLayout.Y_AXIS));
        reportContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Thống kê người dùng
        JPanel userStatsPanel = createStatisticPanel("Thống kê người dùng");

        // Tổng số người dùng
        int totalUsers = userService.getAllUsers().size();
        addStatisticItem(userStatsPanel, "Tổng số người dùng:", String.valueOf(totalUsers));

        // Số lượng bác sĩ
        int doctorCount = (int) userService.getAllUsers().stream()
                .filter(user -> user.getRole() == Role.DOCTOR)
                .count();
        addStatisticItem(userStatsPanel, "Số lượng bác sĩ:", String.valueOf(doctorCount));

        // Số lượng bệnh nhân
        int patientCount = (int) userService.getAllUsers().stream()
                .filter(user -> user.getRole() == Role.PATIENT)
                .count();
        addStatisticItem(userStatsPanel, "Số lượng bệnh nhân:", String.valueOf(patientCount));

        // Thêm các panel thống kê vào panel chính
        reportContentPanel.add(userStatsPanel);
        reportContentPanel.add(Box.createVerticalStrut(20));

        // Thêm nút làm mới
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton refreshButton = new JButton("Làm mới thống kê");
        refreshButton.addActionListener(e -> {
            // Cập nhật lại panel báo cáo
            tabbedPane.setComponentAt(2, createReportPanel());
            tabbedPane.setSelectedIndex(2);
        });
        buttonPanel.add(refreshButton);

        panel.add(new JScrollPane(reportContentPanel), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Tạo panel thống kê
     */
    private JPanel createStatisticPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    /**
     * Thêm thống kê vào panel
     */
    private void addStatisticItem(JPanel container, String label, String value) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));

        item.add(labelComponent);
        item.add(valueComponent);

        container.add(item);
    }

    /**
     * Hiển thị dialog đăng nhập
     */
    private void showLoginDialog() {
        JDialog loginDialog = new JDialog(this, "Đăng nhập", true);
        loginDialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Tên đăng nhập:"));
        JTextField usernameField = new JTextField(15);
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Mật khẩu:"));
        JPasswordField passwordField = new JPasswordField(15);
        formPanel.add(passwordField);

        loginDialog.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton loginButton = new JButton("Đăng nhập");
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            User user = userService.authenticate(username, password);
            if (user != null) {
                currentUser = user;
                updateUI();
                loginDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(loginDialog,
                        "Tên đăng nhập hoặc mật khẩu không đúng",
                        "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Hủy");
        cancelButton.addActionListener(e -> loginDialog.dispose());

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        loginDialog.add(buttonPanel, BorderLayout.SOUTH);

        loginDialog.pack();
        loginDialog.setLocationRelativeTo(this);
        loginDialog.setVisible(true);
    }

    /**
     * Thực hiện đăng xuất
     */
    private void performLogout() {
        int option = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Đăng xuất", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            // Quay trở lại đăng nhập
            showLoginDialog();
        }
    }

    /**
     * Cập nhật giao diện sau khi đăng nhập/đăng xuất
     */
    private void updateUI() {
        // Cập nhật thông tin người dùng trong header
        JPanel headerPanel = createHeaderPanel();
        Container contentPane = getContentPane();

        if (contentPane instanceof JPanel) {
            JPanel mainPanel = (JPanel) contentPane;

            // Xóa header cũ
            mainPanel.remove(0);

            // Thêm header mới
            mainPanel.add(headerPanel, BorderLayout.NORTH, 0);
        }

        // Cập nhật panel tìm kiếm với người dùng mới
        searchPanel = new UserSearchPanel(currentUser, userService);
        tabbedPane.setComponentAt(0, searchPanel);

        // Cập nhật lại giao diện
        revalidate();
        repaint();
    }

    /**
     * Hiển thị dialog giới thiệu
     */
    private void showAboutDialog() {
        JDialog aboutDialog = new JDialog(this, "Giới thiệu", true);
        aboutDialog.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("HỆ THỐNG QUẢN LÝ Y TẾ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel versionLabel = new JLabel("Phiên bản 1.0");
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("Phần mềm quản lý người dùng trong hệ thống y tế");
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel copyrightLabel = new JLabel("© 2024 - Bản quyền thuộc về...");
        copyrightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(versionLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(descLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(copyrightLabel);

        aboutDialog.add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> aboutDialog.dispose());
        buttonPanel.add(closeButton);

        aboutDialog.add(buttonPanel, BorderLayout.SOUTH);

        aboutDialog.pack();
        aboutDialog.setLocationRelativeTo(this);
        aboutDialog.setVisible(true);
    }
}
