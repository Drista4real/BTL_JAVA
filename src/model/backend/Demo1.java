/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.backend;

import model.entity.BENHNHAN;
import model.entity.BENHNHANBAOHIEMXAHOI;
import model.entity.BENHNHANBAOHIEMYTE;
import model.utils.ExceptionUtils;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Scanner;
import javax.swing.JPanel;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

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
                                benhnhan = new BENHNHANBAOHIEMXAHOI(lbn, mabn,ghichu,lichhen, hoten, nnv, mabh, phongtyc);
                            } else {
                            	 benhnhan = new BENHNHANBAOHIEMYTE(lbn, mabn,ghichu,lichhen, hoten, nnv, mabh, phongtyc);
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
                if(lbn == 'y')
                	benhnhan = new BENHNHANBAOHIEMXAHOI(lbn, mabn,ghichu,lichhen, hoten, nnv, mabh, phongtyc);
                else
                	 benhnhan = new BENHNHANBAOHIEMYTE(lbn, mabn,ghichu,lichhen, hoten, nnv, mabh, phongtyc);
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
}
