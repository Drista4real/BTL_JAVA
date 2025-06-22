package Utils;

import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame {
    public MainApp() throws Exception {
        setTitle("Thanh toán");
        setLayout(new BorderLayout());

        PaymentQRComponent qr = new PaymentQRComponent(
                20000,
                "Thanh toán đơn 123",
                () -> System.out.println("Đã xử lý callback sau thanh toán!")
        );

        add(qr, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        new MainApp();
    }
}
