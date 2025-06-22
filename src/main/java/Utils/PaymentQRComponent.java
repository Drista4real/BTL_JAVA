package Utils;

import com.sun.net.httpserver.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class PaymentQRComponent extends JPanel {
    private int amount;
    private String content;
    private Runnable onPaymentSuccess;
    private JLabel qrLabel;

    public void setOnPaymentSuccess(Runnable onPaymentSuccess) {
        this.onPaymentSuccess = onPaymentSuccess;
    }

    public PaymentQRComponent(int amount, String content, Runnable onPaymentSuccess) throws Exception {
        this.amount = amount;
        this.content = content;
        this.onPaymentSuccess = onPaymentSuccess;

        setPreferredSize(new Dimension(700, 300));
        setLayout(new GridBagLayout());

        // Tạo và hiển thị QR
        qrLabel = new JLabel();
        add(qrLabel);
        refreshQR();

        // Khởi động webhook
        startWebhookServer();
    }

    // Cập nhật lại mã QR khi thay đổi nội dung/số tiền
    public void setAmountAndContent(int newAmount, String newContent) {
        this.amount = newAmount;
        this.content = newContent;
        refreshQR();
    }

    // Tải lại ảnh QR
    private void refreshQR() {
        try {
            BufferedImage qrImage = generateQR();
            Image scaled = qrImage.getScaledInstance(250, 250, Image.SCALE_SMOOTH);
            qrLabel.setIcon(new ImageIcon(scaled));
            revalidate();
            repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage generateQR() throws Exception {
        String bankSlug = "mbbank";
        String account = "0971811857";
        String accountName = "NGO MINH KHOI";

        String encodedContent = URLEncoder.encode(content, "UTF-8");
        String encodedName = URLEncoder.encode(accountName, "UTF-8");


        String qrUrl = String.format(
                "https://img.vietqr.io/image/%s-%s-compact.png?amount=%d&addInfo=%s&accountName=%s",
                bankSlug, account, amount, encodedContent, encodedName
        );

        return ImageIO.read(new URL(qrUrl));
    }

    private void startWebhookServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/webhook", new WebhookHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Webhook đang chạy tại http://localhost:8080/webhook");
    }

    private class WebhookHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            // Đọc body an toàn trong Java 8
            InputStream is = exchange.getRequestBody();
            Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
            String body = scanner.hasNext() ? scanner.next() : "";
            scanner.close();
            is.close();

            System.out.println("Nhận webhook:");
            System.out.println(body);

            //Gọi callback trong EDT
            SwingUtilities.invokeLater(() -> {
                if (onPaymentSuccess != null) {
                    onPaymentSuccess.run();
                }
            });

            // Gửi phản hồi JSON
            String response = "{\"message\": \"Webhook OK\"}";
            byte[] respBytes = response.getBytes("UTF-8");

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, respBytes.length);

            OutputStream os = exchange.getResponseBody();
            os.write(respBytes);
            os.close();
        }

    }
}
