package model.gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import model.entity.User;
import model.entity.Appointment; // Import lớp Appointment
import model.entity.Admission; // Thêm import lớp Admission
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
    private AppointmentPanel appointmentPanel; // Panel quản lý cuộc hẹn
    private AdmissionPanel admissionPanel; // Thêm panel quản lý nhập viện

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
        appointmentPanel = new AppointmentPanel(); // Panel cuộc hẹn
        admissionPanel = new AdmissionPanel(); // Khởi tạo panel nhập viện

        // Thêm các panel vào panel chính
        mainPanel.add(new LoginPanel(this), "LOGIN");
        mainPanel.add(dashboardPanel, "DASHBOARD");
        mainPanel.add(patientPanel, "PATIENT");
        mainPanel.add(searchPanel, "SEARCH");
        mainPanel.add(filePanel, "FILE");
        mainPanel.add(appointmentPanel, "APPOINTMENT");
        mainPanel.add(admissionPanel, "ADMISSION"); // Thêm panel nhập viện vào CardLayout

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

    // Phương thức để hiển thị màn hình quản lý cuộc hẹn
    public void showAppointmentPanel() {
        navPanel.setVisible(true);
        cardLayout.show(mainPanel, "APPOINTMENT");
    }

    // Phương thức để hiển thị màn hình quản lý nhập viện
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
        JButton btnAppointment = createNavButton("Quản lý cuộc hẹn", "calendar.png");
        JButton btnAdmission = createNavButton("Quản lý nhập viện", "admission.png"); // Thêm nút nhập viện
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

        btnAppointment.addActionListener(e -> {
            updateButtonSelection(btnAppointment);
            cardLayout.show(mainPanel, "APPOINTMENT");
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
        navPanel.add(btnAppointment);
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

    // Lớp panel để quản lý Appointment (cuộc hẹn)
    private class AppointmentPanel extends JPanel {
        private DefaultListModel<Appointment> appointmentListModel;
        private JList<Appointment> appointmentList;
        private JButton addButton, editButton, deleteButton;
        private JTextField idField, doctorField, patientField;
        private JTextArea reasonArea;
        private JTextField dateField, timeField;
        private JComboBox<String> statusComboBox, paymentStatusComboBox;

        public AppointmentPanel() {
            setLayout(new BorderLayout());

            // Tiêu đề panel
            JLabel titleLabel = new JLabel("Quản lý cuộc hẹn");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            add(titleLabel, BorderLayout.NORTH);

            // Panel chính chia làm 2 phần
            JPanel mainContent = new JPanel(new BorderLayout());

            // Phần trái: danh sách cuộc hẹn
            JPanel leftPanel = new JPanel(new BorderLayout());
            leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

            // Tạo mô hình danh sách và hiển thị
            appointmentListModel = new DefaultListModel<>();

            // Thêm vài dữ liệu mẫu
            appointmentListModel.addElement(new Appointment(
                    "APT001",
                    LocalDate.now(),
                    LocalTime.of(9, 30),
                    "BS. Nguyễn Văn A",
                    "Trần Văn B",
                    "Khám tổng quát",
                    Appointment.AppointmentStatus.PENDING,
                    Appointment.PaymentStatus.UNPAID));

            appointmentListModel.addElement(new Appointment(
                    "APT002",
                    LocalDate.now().plusDays(2),
                    LocalTime.of(14, 15),
                    "BS. Lê Thị C",
                    "Phạm Thị D",
                    "Tái khám",
                    Appointment.AppointmentStatus.PENDING,
                    Appointment.PaymentStatus.PAID));

            appointmentList = new JList<>(appointmentListModel);
            appointmentList.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value,
                                                              int index, boolean isSelected,
                                                              boolean cellHasFocus) {
                    Appointment appointment = (Appointment) value;
                    String text = String.format("%s - %s - Bác sĩ: %s - BN: %s - %s",
                            appointment.getId(),
                            appointment.getDateString(),
                            appointment.getDoctor(),
                            appointment.getPatient(),
                            appointment.getStatusDisplay());
                    return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
                }
            });

            JScrollPane listScrollPane = new JScrollPane(appointmentList);
            leftPanel.add(listScrollPane, BorderLayout.CENTER);

            // Panel nút bấm
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            addButton = new JButton("Thêm mới");
            editButton = new JButton("Chỉnh sửa");
            deleteButton = new JButton("Xóa");

            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
            leftPanel.add(buttonPanel, BorderLayout.SOUTH);

            // Phần phải: chi tiết cuộc hẹn
            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
            rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

            // Các field thông tin
            JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

            formPanel.add(new JLabel("Mã cuộc hẹn:"));
            idField = new JTextField();
            formPanel.add(idField);

            formPanel.add(new JLabel("Ngày hẹn:"));
            dateField = new JTextField();
            formPanel.add(dateField);

            formPanel.add(new JLabel("Giờ hẹn:"));
            timeField = new JTextField();
            formPanel.add(timeField);

            formPanel.add(new JLabel("Bác sĩ:"));
            doctorField = new JTextField();
            formPanel.add(doctorField);

            formPanel.add(new JLabel("Bệnh nhân:"));
            patientField = new JTextField();
            formPanel.add(patientField);

            formPanel.add(new JLabel("Trạng thái:"));
            String[] statusOptions = {
                    Appointment.AppointmentStatus.PENDING.getDisplayValue(),
                    Appointment.AppointmentStatus.COMPLETED.getDisplayValue(),
                    Appointment.AppointmentStatus.CANCELLED.getDisplayValue()
            };
            statusComboBox = new JComboBox<>(statusOptions);
            formPanel.add(statusComboBox);

            formPanel.add(new JLabel("Trạng thái thanh toán:"));
            String[] paymentOptions = {
                    Appointment.PaymentStatus.PAID.getDisplayValue(),
                    Appointment.PaymentStatus.UNPAID.getDisplayValue()
            };
            paymentStatusComboBox = new JComboBox<>(paymentOptions);
            formPanel.add(paymentStatusComboBox);

            formPanel.add(new JLabel("Lý do khám:"));
            reasonArea = new JTextArea(5, 20);
            JScrollPane reasonScrollPane = new JScrollPane(reasonArea);
            formPanel.add(reasonScrollPane);

            rightPanel.add(formPanel);

            // Nút lưu thông tin
            JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton saveButton = new JButton("Lưu thông tin");
            savePanel.add(saveButton);
            rightPanel.add(savePanel);

            // Xử lý sự kiện khi chọn một cuộc hẹn trong danh sách
            appointmentList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && appointmentList.getSelectedValue() != null) {
                    Appointment selectedAppointment = appointmentList.getSelectedValue();
                    displayAppointmentDetails(selectedAppointment);
                }
            });

            // Xử lý sự kiện khi nhấn nút lưu
            saveButton.addActionListener(e -> {
                try {
                    saveAppointmentDetails();
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

            // Xử lý sự kiện khi nhấn nút thêm mới
            addButton.addActionListener(e -> {
                clearForm();
                // Tạo ID mới tự động dựa trên số lượng hiện có
                idField.setText("APT" + String.format("%03d", appointmentListModel.getSize() + 1));
                // Đặt ngày hiện tại
                dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                // Đặt thời gian mặc định
                timeField.setText("09:00");
            });

            // Xử lý sự kiện khi nhấn nút xóa
            deleteButton.addActionListener(e -> {
                if (appointmentList.getSelectedValue() != null) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Bạn có chắc chắn muốn xóa cuộc hẹn này?",
                            "Xác nhận xóa",
                            JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        appointmentListModel.removeElement(appointmentList.getSelectedValue());
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

        private void displayAppointmentDetails(Appointment appointment) {
            idField.setText(appointment.getId());
            dateField.setText(appointment.getDateString());
            timeField.setText(appointment.getTimeString());
            doctorField.setText(appointment.getDoctor());
            patientField.setText(appointment.getPatient());
            reasonArea.setText(appointment.getReason());
            statusComboBox.setSelectedItem(appointment.getStatusDisplay());
            paymentStatusComboBox.setSelectedItem(appointment.getPaymentStatusDisplay());
        }

        private void saveAppointmentDetails() {
            int selectedIndex = appointmentList.getSelectedIndex();

            String statusStr = (String) statusComboBox.getSelectedItem();
            String paymentStatusStr = (String) paymentStatusComboBox.getSelectedItem();

            // Tạo đối tượng Appointment từ dữ liệu form
            Appointment updatedAppointment = new Appointment(
                    idField.getText(),
                    dateField.getText(),
                    timeField.getText(),
                    doctorField.getText(),
                    patientField.getText(),
                    reasonArea.getText(),
                    statusStr,
                    paymentStatusStr
            );

            if (selectedIndex >= 0) {
                // Cập nhật thông tin
                appointmentListModel.set(selectedIndex, updatedAppointment);
            } else {
                // Thêm mới
                appointmentListModel.addElement(updatedAppointment);
            }
        }

        private void clearForm() {
            idField.setText("");
            dateField.setText("");
            timeField.setText("");
            doctorField.setText("");
            patientField.setText("");
            reasonArea.setText("");
            statusComboBox.setSelectedIndex(0);
            paymentStatusComboBox.setSelectedIndex(0);
        }
    }

    // Lớp panel quản lý Admission (nhập viện)
    private class AdmissionPanel extends JPanel {
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
                            admission.getAdmissionDate());
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

            // Xử lý sự kiện khi nhấn nút thêm mới
            addButton.addActionListener(e -> {
                clearForm();
                admissionIdField.setText("ADM" + String.format("%03d", admissionListModel.getSize() + 1));
                admissionDateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
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
            admissionDateField.setText(admission.getAdmissionDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            doctorIdField.setText(admission.getDoctorId());
            roomIdField.setText(admission.getRoomId());
            dischargeDateField.setText(admission.getDischargeDate() != null ?
                    admission.getDischargeDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
            notesArea.setText(admission.getNotes());
        }

        private void saveAdmissionDetails() {
            int selectedIndex = admissionList.getSelectedIndex();

            LocalDate admissionDate = LocalDate.parse(admissionDateField.getText(),
                    DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            LocalDate dischargeDate = null;
            if (dischargeDateField.getText() != null && !dischargeDateField.getText().isEmpty()) {
                dischargeDate = LocalDate.parse(dischargeDateField.getText(),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }

            // Tạo đối tượng Admission từ dữ liệu form
            Admission updatedAdmission = new Admission(
                    admissionIdField.getText(),
                    patientIdField.getText(),
                    admissionDate,
                    doctorIdField.getText(),
                    roomIdField.getText(),
                    dischargeDate,
                    notesArea.getText()
            );

            if (selectedIndex >= 0) {
                // Cập nhật thông tin
                admissionListModel.set(selectedIndex, updatedAdmission);
            } else {
                // Thêm mới
                admissionListModel.addElement(updatedAdmission);
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
