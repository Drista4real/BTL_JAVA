package Utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import controller.durgStore.ButtonPaymentController;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import reponsitory.BillReponsitory;
import reponsitory.DrugReponsitory;
import service.ExportToPDF;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class ZaloPayQRPanel extends JPanel {
    private boolean paid;
    private static final Map<String, String> config = new HashMap<String, String>() {{
        put("appid", "2554");
        put("key1", "sdngKKJmqEMzvh5QQcdD2A9XBSKUNaYn");
        put("key2", "Iyz2habzyr7AG8SgvoBCbKwKi3UzlLi3");
        put("endpoint", "https://sandbox.zalopay.com.vn/v001/tpe/createorder");
    }};
    private Map<String, Object> order = new HashMap<>();

    private JLabel qrLabel = new JLabel();

    public ZaloPayQRPanel(int amount, String description) {
        paid = false;
        setLayout(new GridLayout(1, 1));
        qrLabel.setHorizontalAlignment(SwingConstants.CENTER);
        qrLabel.setVerticalAlignment(SwingConstants.CENTER);

        JPanel innerPanel = new JPanel(new GridBagLayout());
        innerPanel.add(qrLabel);
        add(innerPanel);
        qrLabel.setOpaque(true);
        qrLabel.setBackground(Color.WHITE);
        innerPanel.setBackground(Color.WHITE);
        createZaloPayOrder(amount, description);
    }
    public static String hmacSha256(String key, String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmac.init(secretKey);
        byte[] hashBytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
    private void createZaloPayOrder(int amount, String description) {
        try {
            String appid = config.get("appid");
            String key1 = config.get("key1");
            String endpoint = config.get("endpoint");

            String app_trans_id = getAppTransId();
            order.put("appid", appid);
            order.put("appuser", "demo");
            order.put("apptime", System.currentTimeMillis());
            order.put("apptransid", app_trans_id);
            order.put("item", "[]");
            order.put("amount", amount);
            order.put("description", description);
            order.put("embeddata", "{}");
            order.put("bankcode", "zalopayapp");

            String data = appid + "|" + app_trans_id + "|demo|" + amount + "|" + System.currentTimeMillis() + "|{}|[]";
            order.put("mac", hmacSHA256(data, key1));

            HttpResponse<String> response = Unirest.post(endpoint)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .fields(order)
                    .asString();

            JSONObject result = new JSONObject(response.getBody());
            if (result.has("orderurl")) {
                String orderUrl = result.getString("orderurl");
                BufferedImage qrImage = generateQRCodeImage(orderUrl, 300, 300);
                qrLabel.setIcon(new ImageIcon(qrImage));
            } else {
                qrLabel.setText("Không thể tạo mã QR.");
            }
        } catch (Exception e) {
            qrLabel.setText("Lỗi khi tạo QR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getAppTransId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String date = sdf.format(new Date());
        String random = String.valueOf((int)(Math.random() * 1000000));
        return date + "_" + random;
    }

    private String hmacSHA256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hash = new StringBuilder();
        for (byte b : bytes) {
            hash.append(String.format("%02x", b));
        }
        return hash.toString();
    }

    private BufferedImage generateQRCodeImage(String barcodeText, int width, int height) throws WriterException {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, width, height);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }
        return image;
    }
    public static JSONObject checkOrderStatus(String apptransid) throws Exception {
        String appid = config.get("appid");
        String key1 = config.get("key1");

        String data = appid + "|" + apptransid + "|" + key1;
        String mac = hmacSha256(key1, data);

        HttpResponse<String> response = Unirest.post("https://sandbox.zalopay.com.vn/v001/tpe/getstatusbyapptransid")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("appid", appid)
                .field("apptransid", apptransid)
                .field("mac", mac)
                .asString();

        JSONObject result = new JSONObject(response.getBody());
        System.out.println("Kết quả kiểm tra: " + result.getString("returnmessage"));
        return result;
    }
    private volatile boolean isPaid = false;

    public void checkThisOrderStatusAsync(ButtonPaymentController a) {
        Runnable checkPaymentRunnable = () -> {
            while (!isPaid) {
                try {
                    JSONObject result = checkOrderStatus(order.get("apptransid").toString());
                    int returnCode = result.optInt("returncode", -1);
                    if (returnCode == 1) {
                        isPaid = true;
                        System.out.println("Đã thanh toán thành công!");
                        // Cập nhật DB
                        BillReponsitory.updatePaymentStatusToPaid(a.id);
                        // Cập nhật giao diện
                        a.getView().getDrugStorePanel().getBillConfPanel().getStatus().setText("Đã thanh toán");
                        // Xuất PDF
                        ExportToPDF.billToPDF(a.id+"");
                        //  Chuyển panel
                        a.getView().getDrugStorePanel().getCardLayout().show(
                                a.getView().getDrugStorePanel().getCenterPanel(), "BillConf"
                        );
                        // cập nhật lại data thuốc
                        DefaultTableModel model = (DefaultTableModel) a.getView().getDrugStorePanel().getBillPanel().getTableMedicine().getModel();
                        for (int i = 0; i < model.getRowCount(); i++) {
                            String drugName = model.getValueAt(i, 1).toString();        // Tên thuốc
                            int quantity = Integer.parseInt(model.getValueAt(i, 2).toString());  // Số lượng
                            DrugReponsitory.updateDrugQuantity(drugName, quantity);
                        }
                        break;
                    }
                    // Đợi 3 giây trước khi kiểm tra lại
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        // Khởi động luồng
        Thread checkThread = new Thread(checkPaymentRunnable);
        checkThread.start();
    }

    public Map<String, Object> getOrder() {
        return order;
    }

    public void setOrder(Map<String, Object> order) {
        this.order = order;
    }

    public JLabel getQrLabel() {
        return qrLabel;
    }

    public void setQrLabel(JLabel qrLabel) {
        this.qrLabel = qrLabel;
    }
}
