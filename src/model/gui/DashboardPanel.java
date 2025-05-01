package model.gui;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DashboardPanel extends JPanel {
    private Map<String, JLabel> statLabels;
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;

    static {
        Properties props = new Properties();
        try (InputStream input = DashboardPanel.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new IOException("Không tìm thấy tệp database.properties");
            }
            props.load(input);
            DB_URL = props.getProperty("url");
            DB_USER = props.getProperty("username");
            DB_PASSWORD = props.getProperty("password");
            Class.forName(props.getProperty("driver"));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Lỗi khi tải cấu hình cơ sở dữ liệu: " + e.getMessage());
        }
    }

    public DashboardPanel() {
        statLabels = new HashMap<>();
        initComponents();
        updateStats();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(248, 249, 250)); // #F8F9FA
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Chỉ giữ 3 thẻ thống kê
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        statsPanel.setBackground(Color.WHITE);

        addStatCard(statsPanel, "Tổng số bệnh nhân", "0");
        addStatCard(statsPanel, "Bệnh nhân BHYT", "0");
        addStatCard(statsPanel, "Phòng theo yêu cầu", "0");

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        bottomPanel.setBackground(Color.WHITE);

        JPanel appointmentPanel = createTablePanel("Lịch hẹn hôm nay",
                new String[]{"Giờ khám", "Tên bệnh nhân", "Bác sĩ", "Ghi chú"});
        JPanel attentionPanel = createTablePanel("Bệnh nhân cần chú ý",
                new String[]{"Tên bệnh nhân", "Lý do", "Mức độ"});

        bottomPanel.add(appointmentPanel);
        bottomPanel.add(attentionPanel);

        mainPanel.add(statsPanel, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createTablePanel(String title, String[] columns) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(227, 242, 253)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(227, 242, 253));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(44, 62, 80));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JTable table = new JTable(new Object[0][columns.length], columns) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 249, 249));
                }
                return comp;
            }
        };
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setEnabled(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setBackground(new Color(227, 242, 253));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        if (title.equals("Lịch hẹn hôm nay")) {
            table.getColumnModel().getColumn(0).setPreferredWidth(100);
            table.getColumnModel().getColumn(1).setPreferredWidth(150);
            table.getColumnModel().getColumn(2).setPreferredWidth(100);
            table.getColumnModel().getColumn(3).setPreferredWidth(150);
        } else if (title.equals("Bệnh nhân cần chú ý")) {
            table.getColumnModel().getColumn(0).setPreferredWidth(150);
            table.getColumnModel().getColumn(1).setPreferredWidth(200);
            table.getColumnModel().getColumn(2).setPreferredWidth(100);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void addStatCard(JPanel panel, String title, String value) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(new Color(248, 249, 250));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(93, 173, 226)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setForeground(new Color(44, 62, 80));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(new Color(93, 173, 226));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        panel.add(card);
        statLabels.put(title, valueLabel);
    }

    private void updateStats() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Tổng số bệnh nhân
            String sqlTotal = "SELECT COUNT(*) FROM Patients";
            PreparedStatement stmtTotal = conn.prepareStatement(sqlTotal);
            ResultSet rsTotal = stmtTotal.executeQuery();
            int totalPatients = rsTotal.next() ? rsTotal.getInt(1) : 0;

            // Bệnh nhân BHYT
            String sqlBHYT = "SELECT COUNT(*) FROM Insurance WHERE Provider = 'BHYT'";
            PreparedStatement stmtBHYT = conn.prepareStatement(sqlBHYT);
            ResultSet rsBHYT = stmtBHYT.executeQuery();
            int bhytCount = rsBHYT.next() ? rsBHYT.getInt(1) : 0;

            // Phòng theo yêu cầu (phòng VIP)
            String sqlPhongTYC = "SELECT COUNT(*) FROM Admissions a JOIN HospitalRooms r ON a.RoomID = r.RoomID WHERE r.RoomType = 'VIP'";
            PreparedStatement stmtPhongTYC = conn.prepareStatement(sqlPhongTYC);
            ResultSet rsPhongTYC = stmtPhongTYC.executeQuery();
            int phongYCCount = rsPhongTYC.next() ? rsPhongTYC.getInt(1) : 0;

            // Cập nhật giao diện
            statLabels.get("Tổng số bệnh nhân").setText(String.valueOf(totalPatients));
            statLabels.get("Bệnh nhân BHYT").setText(String.valueOf(bhytCount));
            statLabels.get("Phòng theo yêu cầu").setText(String.valueOf(phongYCCount));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy thống kê: " + e.getMessage());
        }
    }

    public void refreshStats() {
        updateStats();
    }
}