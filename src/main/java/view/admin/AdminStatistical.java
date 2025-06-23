package view.admin;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;
import java.awt.*;

public class AdminStatistical extends JPanel {

    public AdminStatistical() {
        // Tạo dữ liệu cho biểu đồ
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(12000000, "Doanh thu", "Tháng 1");
        dataset.addValue(15500000, "Doanh thu", "Tháng 2");
        dataset.addValue(9300000, "Doanh thu", "Tháng 3");
        // Thêm dữ liệu cho các tháng tiếp theo...

        // Tạo biểu đồ cột
        JFreeChart chart = ChartFactory.createBarChart(
                "Doanh thu theo tháng", // Tiêu đề
                "Tháng", // Nhãn trục X
                "Doanh thu (VND)", // Nhãn trục Y
                dataset // Dữ liệu
        );

        // Tạo panel để hiển thị biểu đồ
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        chartPanel.setMouseWheelEnabled(true);

        // Thêm panel vào JFrame
        this.setLayout(new BorderLayout());
        this.add(chartPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Biểu đồ doanh thu");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new AdminStatistical());
            frame.pack();
            frame.setVisible(true);
        });
    }
}
