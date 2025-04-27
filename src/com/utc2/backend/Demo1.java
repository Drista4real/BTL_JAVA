/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.utc2.backend;
import javax.swing.JFrame;  
import javax.swing.JPanel;
import com.utc2.entity.BENHNHAN;
import com.utc2.entity.BENHNHANBAOHIEMXAHOI;
import com.utc2.entity.BENHNHANBAOHIEMYTE;
import com.utc2.utils.ExceptionUtils;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Scanner;
import javax.swing.JPanel;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Demo1 {
    private Hashtable<String, BENHNHAN> Danhsach;
    private BufferedReader br;
    private JPanel parentPanel;

    public Hashtable<String, BENHNHAN> getDanhsach() {
        return Danhsach;
    }

    public void setDanhsach(Hashtable<String, BENHNHAN> Danhsach) {
        this.Danhsach = Danhsach;
    }

    public Demo1(JPanel parentPanel) {
        this.Danhsach = new Hashtable<String, BENHNHAN>();
        this.parentPanel = parentPanel;
    }
    
    public void GhiFile() throws IOException {
        FileWriter fw = null;
        
        try {
            // Tạo thư mục nếu chưa tồn tại
            File directory = new File("data");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            fw = new FileWriter("data/DSBENHNHAN.txt");
            String str = "";
            for(BENHNHAN vbn: this.Danhsach.values()) {
                str += vbn.toString();
            } 
            fw.write(str);
            fw.close(); 
        } catch (IOException e) {
            ExceptionUtils.handleFileException(parentPanel, e);
            throw e;
        }
    }
    public void DocFile() {
        try {
            Danhsach.clear();
            File file = new File("data/DSBENHNHAN.txt");
            if (!file.exists()) {
                System.out.println("File không tồn tại: " + file.getAbsolutePath());
                return;
            }

            System.out.println("Đang đọc file: " + file.getAbsolutePath());
            Scanner scr = new Scanner(new FileReader(file));
            String mabn = null, hoten = null, mabh = null , ghichu = null;
            Date nnv = null, lichhen = null;
            boolean phongtyc = false;
            char lbn = ' ';
            boolean flag = false;

            while (scr.hasNextLine()) {
                String thongtin = scr.nextLine();
                System.out.println("Đọc dòng: " + thongtin);
                if (thongtin.equals("@") && flag == true) {
                    BENHNHAN benhnhan = null;

                    // Kiểm tra các giá trị bắt buộc có đầy đủ không
                    if (mabn != null && hoten != null && nnv != null && mabh != null) {
                        try {
                            if (lbn == 'y') {
                                // Sử dụng constructor BENHNHANBAOHIEMXAHOI
                                benhnhan = new BENHNHANBAOHIEMXAHOI(lbn, mabn, hoten, nnv, mabh, phongtyc);
                            } else {
                                // Sử dụng constructor BENHNHANBAOHIEMYTE
                                benhnhan = new BENHNHANBAOHIEMYTE(lbn, mabn, hoten, nnv, mabh, phongtyc);
                            }

                            Danhsach.put(benhnhan.getMABN(), benhnhan);
                            System.out.println("Đã thêm bệnh nhân: " + benhnhan.getMABN());
                        } catch (Exception e) {
                            System.out.println("Lỗi khi tạo đối tượng bệnh nhân: " + e.getMessage());
                        }
                    } else {
                        System.out.println("Thiếu thông tin, bỏ qua bệnh nhân.");
                    }

                    flag = false;
                }

                else {
                    int vitri = thongtin.indexOf(":");
                    if (vitri == -1) continue;

                    String thuoctinh = thongtin.substring(0, vitri);
                    String value = thongtin.substring(vitri + 2, thongtin.length());      

                    switch(thuoctinh) {
                        case "Mabenhnhan":
                            mabn = value;
                            break;
                        case "Hoten":
                            hoten = value;
                            break;
                        case "Ngaynhapvien":
                            try {
                                SimpleDateFormat d = new SimpleDateFormat("dd/MM/yyyy");
                                nnv = d.parse(value);
                            } catch (ParseException e) {
                                System.out.println("Lỗi định dạng ngày (dd/MM/yyyy): " + value);
                                nnv = null;
                            }
                            break;

                        case "Phongtheoyeucau":
                            phongtyc = Boolean.parseBoolean(value);
                            break;
                        case "Loaibaohiem":
                            lbn = value.charAt(0);
                            break;
                        case "Mabaohiem":
                            mabh = value;
                            break;
                    }  
                }
            }

            if (flag) {
                BENHNHAN benhnhan = null;
                if (lbn == 'y') {
                    // Sử dụng constructor BENHNHANBAOHIEMXAHOI
                    benhnhan = new BENHNHANBAOHIEMXAHOI(lbn, mabn, hoten, nnv, mabh, phongtyc);
                } else {
                    // Sử dụng constructor BENHNHANBAOHIEMYTE
                    benhnhan = new BENHNHANBAOHIEMYTE(lbn, mabn, hoten, nnv, mabh, phongtyc);
                }
                Danhsach.put(benhnhan.getMABN(), benhnhan);
            }

            scr.close();
        } catch (Exception e) {
            ExceptionUtils.handleGeneralException(parentPanel, e);
        }
    }

    public void NhapGUI(BENHNHAN benhnhan) {
        Danhsach.put(benhnhan.getMABN(), benhnhan);
    }
    
    public void SuaGUI(BENHNHAN benhnhan) {
        Danhsach.replace(benhnhan.getMABN(), benhnhan);
    }
    
    public BENHNHAN Tim(String mabn) {
        if (mabn == null || mabn.trim().isEmpty()) {
            return null;
        }
        
        mabn = mabn.trim();
        for (BENHNHAN bn : this.Danhsach.values()) {
            if (bn.getMABN().equals(mabn)) {
                return bn;
            }
        }
        return null;
    }
    
    public void Xoa(String mabn) {
        this.Danhsach.remove(mabn);
    }
    
    public void TongtienTL() {
        double TongYT = 0, TongXH = 0, Tong = 0;
        
        for (BENHNHAN vbn: this.Danhsach.values()) {
            if (vbn instanceof BENHNHANBAOHIEMYTE)
                TongYT += vbn.TinhhoadonVP();
            else 
                TongXH += vbn.TinhhoadonVP();
        }
        
        Tong = TongYT + TongXH;
    }
    public void DatLichHen(String mabn , String NgayLichHen) throws Exception{
        BENHNHAN benhnhan = Tim(mabn);
        if(benhnhan != null){
            SimpleDateFormat formatter = new SimpleDateFormat ("dd/MM/YYYY HH:mm");
            Date LichHen = formatter. parse( NgayLichHen);
            benhnhan.DatLichHen(LichHen);
            System.out.println (" Lịch hẹn đã được đặt cho bệnh nhân " + benhnhan.getHoten()+ " vào"+ NgayLichHen);
       }else {
            System.out.println (" Bệnh nhân không tồn tại");
        }
    }
    public void GhiChuBS(String mabn , String GhiChumoi) throws Exception{
        BENHNHAN benhnhan = Tim(mabn);
        if (benhnhan != null){
            benhnhan.GhiChuBS(GhiChumoi);
            // kiểm trea nếu có ghi chú  thì in ra thông báo
            if (benhnhan.getGhiChu()!= null && !benhnhan. getGhiChu().isEmpty()){
            System.out.println(" Ghi chú cuả Bác Sĩ " + benhnhan.getGhiChu());
            }else{
                 System.out.println(" Không có Ghi chú cuả Bác Sĩ ");
            }
        }else{
            throw new Exception (" Bệnh nhân không tồn tại");
        }
    }
        public void TongLichHen(){
            int Tong = 0 ;
            // kiểm tra danh sách bệnh nhân để xem có bệnh nhân nào đã có lịch hẹn chưa
            for(BENHNHAN benhnhan : Danhsach.values()){
                if(benhnhan.getLichHen()!=null){
                    Tong++;
                }
            }
            System.out.println(" Tổng lịch hẹn đã được đặt"+ Tong);
        }
        // In danh sách bệnh nhân đã đặt lịch hẹn
        public void InDanhSachBenhNhanCoLichHen() {
            boolean found = false;
            System.out.println("Danh sách bệnh nhân đã đặt lịch hẹn:");
            for (BENHNHAN benhnhan : Danhsach.values()) {
                if (benhnhan.getLichHen() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    System.out.println("Mã BN: " + benhnhan.getMABN() + " | Họ tên: " + benhnhan.getHoten() + " | Lịch hẹn: " + sdf.format(benhnhan.getLichHen()));
                    found = true;
                }
            }
            if (!found) {
                System.out.println("Không có bệnh nhân nào đã đặt lịch hẹn.");
            }
        }

        // Tìm kiếm bệnh nhân có ghi chú chứa từ khóa
        public void TimBenhNhanTheoGhiChu(String tuKhoa) {
            boolean found = false;
            System.out.println("Kết quả tìm kiếm bệnh nhân theo ghi chú chứa từ khóa: \"" + tuKhoa + "\"");
            for (BENHNHAN benhnhan : Danhsach.values()) {
                if (benhnhan.getGhiChu() != null && benhnhan.getGhiChu().toLowerCase().contains(tuKhoa.toLowerCase())) {
                    System.out.println("Mã BN: " + benhnhan.getMABN() + " | Họ tên: " + benhnhan.getHoten() + " | Ghi chú: " + benhnhan.getGhiChu());
                    found = true;
                }
            }
            if (!found) {
                System.out.println("Không tìm thấy bệnh nhân nào có ghi chú chứa từ khóa \"" + tuKhoa + "\".");
            }
        }

        // Đếm số lượng bệnh nhân có ghi chú
        public void DemSoBenhNhanCoGhiChu() {
            int count = 0;
            for (BENHNHAN benhnhan : Danhsach.values()) {
                if (benhnhan.getGhiChu() != null && !benhnhan.getGhiChu().isEmpty()) {
                    count++;
                }
            }
            System.out.println("Số lượng bệnh nhân có ghi chú: " + count);
        }
        // Thêm bệnh nhân mới vào danh sách
        public void ThemNhanVien(BENHNHAN benhnhan) {
            if (benhnhan != null) {
                // Kiểm tra nếu bệnh nhân đã tồn tại
                if (Danhsach.containsKey(benhnhan.getMABN())) {
                    System.out.println("Bệnh nhân đã tồn tại trong hệ thống!");
                } else {
                    Danhsach.put(benhnhan.getMABN(), benhnhan);
                    System.out.println("Bệnh nhân " + benhnhan.getHoten() + " đã được thêm vào hệ thống.");
                }
            } else {
                System.out.println("Thông tin bệnh nhân không hợp lệ.");
            }
        }

        // Sửa thông tin bệnh nhân
        public void SuaNhanVien(String mabn, BENHNHAN benhnhanMoi) {
            BENHNHAN benhnhanCu = Tim(mabn);
            if (benhnhanCu != null) {
                Danhsach.put(mabn, benhnhanMoi);
                System.out.println("Thông tin bệnh nhân " + mabn + " đã được cập nhật.");
            } else {
                System.out.println("Bệnh nhân không tồn tại!");
            }
        }

        // Xóa bệnh nhân khỏi hệ thống
        public void XoaNhanVien(String mabn) {
            BENHNHAN benhnhan = Tim(mabn);
            if (benhnhan != null) {
                Danhsach.remove(mabn);
                System.out.println("Bệnh nhân " + mabn + " đã bị xóa khỏi hệ thống.");
            } else {
                System.out.println("Bệnh nhân không tồn tại.");
            }
        }
        public void XoaForm() {
            if (parentPanel != null) {
                // Đóng cửa sổ JFrame chứa JPanel
                if (parentPanel.getParent() instanceof JFrame) {
                    JFrame frame = (JFrame) parentPanel.getParent();
                    frame.dispose();  // Đóng JFrame
                    System.out.println("Form đã được đóng.");
                } else {
                    // Nếu không phải là JFrame, ta ẩn JPanel
                    parentPanel.setVisible(false); // Ẩn form hiện tại
                    System.out.println("Form đã được ẩn.");
                }
            } else {
                System.out.println("Không có form để xóa.");
            }
        }
        public void XoaForm1() {
            if (parentPanel != null) {
                // Đóng cửa sổ JFrame chứa JPanel
                if (parentPanel.getParent() instanceof JFrame) {
                    JFrame frame = (JFrame) parentPanel.getParent();
                    frame.dispose();  // Đóng JFrame
                    System.out.println("Form đã được đóng.");
                } else {
                    // Nếu không phải là JFrame, ta ẩn JPanel
                    parentPanel.setVisible(false); // Ẩn form hiện tại
                    System.out.println("Form đã được ẩn.");
                }
            } else {
                System.out.println("Không có form để xóa.");
            }
        }


}
