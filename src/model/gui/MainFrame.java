package model.gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import model.entity.User;
import model.entity.Admission; // Thêm import lớp Admission
import java.time.LocalDate; // Thêm import cho LocalDate

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JButton currentButton;
    private static MainFrame instance;
    private DashboardPanel dashboardPanel;
    private User currentUser;
    private JLabel userNameLabel;
    private JLabel avatarLabel;
    private JButton logoutButton;
    private JPanel navPanel;
    private PatientManagementPanel patientPanel;
    private SearchPanel searchPanel;
    private FileManagementPanel filePanel;
    private AdmissionPanel admissionPanel; // Thêm panel mới cho quản lý nhập viện

    public MainFrame() {
        setTitle("Hệ thống quản lý bệnh nhân");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        // Tạo thanh menu
        JMenuBar menuBar = new JMenuBar();

        // Menu File
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Thoát");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        // Menu Trợ giúp
        JMenu helpMenu = new JMenu("Trợ giúp");
        JMenuItem aboutItem = new JMenuItem("Giới thiệu");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Hệ thống quản lý bệnh nhân\nVersion 1.0",
                "Giới thiệu",
                JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        // Tạo panel chính với CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Tạo các panel một lần và lưu lại
        dashboardPanel = new DashboardPanel();
        patientPanel = new PatientManagementPanel();
        searchPanel = new SearchPanel();
        filePanel = new FileManagementPanel();
        admissionPanel = new AdmissionPanel(); // Khởi tạo panel mới

        // Thêm các panel vào panel chính
        mainPanel.add(new LoginPanel(this), "LOGIN");
        mainPanel.add(dashboardPanel, "DASHBOARD");
        mainPanel.add(patientPanel, "PATIENT");
        mainPanel.add(searchPanel, "SEARCH");
        mainPanel.add(filePanel, "FILE");
        mainPanel.add(admissionPanel, "ADMISSION"); // Thêm panel mới vào CardLayout

        // Tạo navPanel một lần và lưu lại
        navPanel = createNavPanel();

        // Tạo panel nội dung chính
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(mainPanel, BorderLayout.CENTER);

        // Thêm các thành phần vào frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navPanel, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        // Hiển thị màn hình đăng nhập ban đầu
        showLoginScreen();
        instance = this;
    }

    private JButton createNavButton(String text, String iconName) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 45));
        button.setMaximumSize(new Dimension(250, 45));
        button.setMinimumSize(new Dimension(250, 45));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(41, 128, 185));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 25, 0, 0));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Load icon if exists
        try {
            URL iconUrl = getClass().getResource("/model/gui/icons/" + iconName);
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(img));
                button.setIconTextGap(15);
            }
        } catch (Exception e) {
            System.out.println("Không thể tải icon: " + iconName);
        }

        // Add hover effect using MouseListener class
        button.addMouseListener(new ButtonMouseListener(button));

        return button;
    }

    private class ButtonMouseListener extends java.awt.event.MouseAdapter {
        private JButton button;

        public ButtonMouseListener(JButton button) {
            this.button = button;
        }

        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            if (button != currentButton) {
                button.setBackground(new Color(52, 152, 219));
            }
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) {
            if (button != currentButton) {
                button.setBackground(new Color(41, 128, 185));
            }
        }
    }

    private void updateButtonSelection(JButton selectedButton) {
        if (currentButton != null) {
            currentButton.setBackground(new Color(41, 128, 185));
        }

        selectedButton.setBackground(new Color(52, 152, 219));
        currentButton = selectedButton;
    }

    public static MainFrame getInstance() {
        return instance;
    }

    public DashboardPanel getDashboardPanel() {
        return dashboardPanel;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            userNameLabel.setText(user.getFullName());
            // Cập nhật chữ cái đầu cho avatar mặc định
            if (avatarLabel != null && avatarLabel.getIcon() == null) {
                String firstLetter = String.valueOf(user.getFullName().charAt(0)).toUpperCase();
                BufferedImage image = new BufferedImage(150, 150, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = image.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Vẽ nền tròn
                g2d.setColor(new Color(200, 200, 200));
                g2d.fillOval(0, 0, 150, 150);

                // Vẽ chữ cái
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 60));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (150 - fm.stringWidth(firstLetter)) / 2;
                int y = ((150 - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(firstLetter, x, y);

                g2d.dispose();
                avatarLabel.setIcon(new ImageIcon(image));
            }
        }
    }

    public void showLoginScreen() {
        navPanel.setVisible(false);
        if (currentButton != null) {
            currentButton.setBackground(new Color(41, 128, 185));
            currentButton = null;
        }
        cardLayout.show(mainPanel, "LOGIN");
    }

    public void showMainContent() {
        navPanel.setVisible(true);
        // Select home button by default
        Component[] components = navPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                if (button.getText().equals("Trang chủ")) {
                    updateButtonSelection(button);
                    break;
                }
            }
        }
        cardLayout.show(mainPanel, "DASHBOARD");
    }

    // Thêm phương thức để hiển thị màn hình quản lý nhập viện
    public void showAdmissionPanel() {
        navPanel.setVisible(true);
        cardLayout.show(mainPanel, "ADMISSION");
    }

    private JPanel createNavPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(41, 128, 185));
        navPanel.setPreferredSize(new Dimension(250, getHeight()));

        // Avatar panel
        JPanel avatarPanel = new JPanel();
        avatarPanel.setLayout(new BoxLayout(avatarPanel, BoxLayout.Y_AXIS));
        avatarPanel.setBackground(new Color(41, 128, 185));
        avatarPanel.setMaximumSize(new Dimension(250, 200));
        avatarPanel.setPreferredSize(new Dimension(250, 200));
        avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create default avatar
        avatarLabel = new JLabel();
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarLabel.setPreferredSize(new Dimension(120, 120));
        avatarLabel.setMaximumSize(new Dimension(120, 120));

        BufferedImage image = new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillOval(0, 0, 120, 120);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 60));
        FontMetrics fm = g2d.getFontMetrics();
        String letter = "P";
        g2d.drawString(letter, (120 - fm.stringWidth(letter)) / 2, ((120 - fm.getHeight()) / 2) + fm.getAscent());
        g2d.dispose();
        avatarLabel.setIcon(new ImageIcon(image));

        // Add padding above avatar
        avatarPanel.add(Box.createVerticalStrut(30));
        avatarPanel.add(avatarLabel);
        avatarPanel.add(Box.createVerticalStrut(15));

        // User name panel
        userNameLabel = new JLabel("pha");
        userNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userNameLabel.setForeground(Color.WHITE);
        userNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarPanel.add(userNameLabel);

        navPanel.add(avatarPanel);
        navPanel.add(Box.createVerticalStrut(20));

        // Navigation buttons
        JButton btnDashboard = createNavButton("Trang chủ", "home.png");
        JButton btnPatient = createNavButton("Quản lý bệnh nhân", "patient.png");
        JButton btnAdmission = createNavButton("Quản lý nhập viện", "admission.png"); // Thêm nút mới
        JButton btnSearch = createNavButton("Tìm kiếm", "search.png");
        JButton btnFiles = createNavButton("Quản lý tài liệu", "files.png");

        // Thêm sự kiện cho các nút
        btnDashboard.addActionListener(e -> {
            updateButtonSelection(btnDashboard);
            cardLayout.show(mainPanel, "DASHBOARD");
        });

        btnPatient.addActionListener(e -> {
            updateButtonSelection(btnPatient);
            cardLayout.show(mainPanel, "PATIENT");
        });

        btnAdmission.addActionListener(e -> {
            updateButtonSelection(btnAdmission);
            cardLayout.show(mainPanel, "ADMISSION");
        });

        btnSearch.addActionListener(e -> {
            updateButtonSelection(btnSearch);
            cardLayout.show(mainPanel, "SEARCH");
        });

        btnFiles.addActionListener(e -> {
            updateButtonSelection(btnFiles);
            cardLayout.show(mainPanel, "FILE");
        });

        navPanel.add(btnDashboard);
        navPanel.add(btnPatient);
        navPanel.add(btnAdmission); // Thêm nút vào panel
        navPanel.add(btnSearch);
        navPanel.add(btnFiles);

        return navPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }

    // Lớp panel mới để quản lý Admission
    private class AdmissionPanel extends JPanel {
        private JTable admissionTable;
        private DefaultListModel<Admission> admissionListModel;
        private JList<Admission> admissionList;
        private JButton addButton, editButton, deleteButton;
        private JTextField admissionIdField, patientIdField, doctorIdField, roomIdField;
        private JTextArea notesArea;
        private JTextField admissionDateField, dischargeDateField;

        public AdmissionPanel() {
            setLayout(new BorderLayout());

            // Tiêu đề panel
            JLabel titleLabel = new JLabel("Quản lý nhập viện bệnh nhân");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            add(titleLabel, BorderLayout.NORTH);

            // Panel chính chia làm 2 phần
            JPanel mainContent = new JPanel(new BorderLayout());

            // Phần trái: danh sách nhập viện
            JPanel leftPanel = new JPanel(new BorderLayout());
            leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

            // Tạo mô hình danh sách và bảng
            admissionListModel = new DefaultListModel<>();

            // Thêm vài dữ liệu mẫu về Admission
            admissionListModel.addElement(new Admission(
                    "ADM001", "PT001", LocalDate.now(), "DOC001", "RM101",
                    "Bệnh nhân nhập viện để kiểm tra sức khỏe"));

            admissionListModel.addElement(new Admission(
                    "ADM002", "PT002", LocalDate.now().minusDays(5), "DOC002", "RM102",
                    LocalDate.now(), "Bệnh nhân đã xuất viện"));

            admissionList = new JList<>(admissionListModel);
            admissionList.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value,
                                                              int index, boolean isSelected,
                                                              boolean cellHasFocus) {
                    Admission admission = (Admission) value;
                    String text = String.format("Mã: %s - Bệnh nhân: %s - Ngày nhập viện: %s",
                            admission.getAdmissionId(),
                            admission.getPatientId(),
                            admission.getAdmissionDateString());
                    return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
                }
            });

            JScrollPane listScrollPane = new JScrollPane(admissionList);
            leftPanel.add(listScrollPane, BorderLayout.CENTER);

            // Panel nút bấm
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            addButton = new JButton("Thêm");
            editButton = new JButton("Sửa");
            deleteButton = new JButton("Xóa");

            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
            leftPanel.add(buttonPanel, BorderLayout.SOUTH);

            // Phần phải: chi tiết nhập viện
            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
            rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

            // Các field thông tin
            JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

            formPanel.add(new JLabel("Mã nhập viện:"));
            admissionIdField = new JTextField();
            formPanel.add(admissionIdField);

            formPanel.add(new JLabel("Mã bệnh nhân:"));
            patientIdField = new JTextField();
            formPanel.add(patientIdField);

            formPanel.add(new JLabel("Ngày nhập viện:"));
            admissionDateField = new JTextField();
            formPanel.add(admissionDateField);

            formPanel.add(new JLabel("Mã bác sĩ:"));
            doctorIdField = new JTextField();
            formPanel.add(doctorIdField);

            formPanel.add(new JLabel("Mã phòng:"));
            roomIdField = new JTextField();
            formPanel.add(roomIdField);

            formPanel.add(new JLabel("Ngày xuất viện:"));
            dischargeDateField = new JTextField();
            formPanel.add(dischargeDateField);

            formPanel.add(new JLabel("Ghi chú:"));
            notesArea = new JTextArea(5, 20);
            JScrollPane notesScrollPane = new JScrollPane(notesArea);
            formPanel.add(notesScrollPane);

            rightPanel.add(formPanel);

            // Nút lưu thông tin
            JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton saveButton = new JButton("Lưu thông tin");
            savePanel.add(saveButton);
            rightPanel.add(savePanel);

            // Xử lý sự kiện khi chọn một bệnh nhân trong danh sách
            admissionList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && admissionList.getSelectedValue() != null) {
                    Admission selectedAdmission = admissionList.getSelectedValue();
                    displayAdmissionDetails(selectedAdmission);
                }
            });

            // Xử lý sự kiện khi nhấn nút lưu
            saveButton.addActionListener(e -> {
                try {
                    saveAdmissionDetails();
                    JOptionPane.showMessageDialog(this,
                            "Lưu thông tin thành công!",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Lỗi: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            // Xử lý sự kiện khi nhấn nút xóa
            deleteButton.addActionListener(e -> {
                if (admissionList.getSelectedValue() != null) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Bạn có chắc chắn muốn xóa thông tin nhập viện này?",
                            "Xác nhận xóa",
                            JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        admissionListModel.removeElement(admissionList.getSelectedValue());
                        clearForm();
                    }
                }
            });

            // Phân chia không gian
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
            splitPane.setDividerLocation(400);
            mainContent.add(splitPane, BorderLayout.CENTER);

            add(mainContent, BorderLayout.CENTER);
        }

        private void displayAdmissionDetails(Admission admission) {
            admissionIdField.setText(admission.getAdmissionId());
            patientIdField.setText(admission.getPatientId());
            admissionDateField.setText(admission.getAdmissionDateString());
            doctorIdField.setText(admission.getDoctorId());
            roomIdField.setText(admission.getRoomId());
            dischargeDateField.setText(admission.getDischargeDateString() != null ?
                    admission.getDischargeDateString() : "");
            notesArea.setText(admission.getNotes());
        }

        private void saveAdmissionDetails() {
            int selectedIndex = admissionList.getSelectedIndex();
            if (selectedIndex >= 0) {
                // Cập nhật thông tin
                Admission updatedAdmission = new Admission(
                        admissionIdField.getText(),
                        patientIdField.getText(),
                        admissionDateField.getText(),
                        doctorIdField.getText(),
                        roomIdField.getText(),
                        dischargeDateField.getText().isEmpty() ? null : dischargeDateField.getText(),
                        notesArea.getText()
                );

                admissionListModel.set(selectedIndex, updatedAdmission);
            } else {
                // Thêm mới
                Admission newAdmission = new Admission(
                        admissionIdField.getText(),
                        patientIdField.getText(),
                        admissionDateField.getText(),
                        doctorIdField.getText(),
                        roomIdField.getText(),
                        dischargeDateField.getText().isEmpty() ? null : dischargeDateField.getText(),
                        notesArea.getText()
                );

                admissionListModel.addElement(newAdmission);
            }
        }

        private void clearForm() {
            admissionIdField.setText("");
            patientIdField.setText("");
            admissionDateField.setText("");
            doctorIdField.setText("");
            roomIdField.setText("");
            dischargeDateField.setText("");
            notesArea.setText("");
        }
    }
}
