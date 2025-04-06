/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.utc2.backend;

import com.utc2.entity.BENHNHAN;
import com.utc2.entity.BENHNHANBAOHIEMXAHOI;
import com.utc2.entity.BENHNHANBAOHIEMYTE;
import com.utc2.utils.ExceptionUtils;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Scanner;
import javax.swing.JPanel;

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
            String mabn = null, hoten = null, mabh = null;
            Date nnv = null;
            boolean phongtyc = false;
            char lbn = ' ';
            boolean flag = false;
            
            while (scr.hasNextLine()) {
                String thongtin = scr.nextLine();
                System.out.println("Đọc dòng: " + thongtin);
                
                if(thongtin.equals("@") && flag==true) {
                    BENHNHAN benhnhan = null;
                    if(lbn == 'y')
                        benhnhan = new BENHNHANBAOHIEMYTE(lbn, mabn, hoten, nnv, mabh, phongtyc);
                    else
                        benhnhan = new BENHNHANBAOHIEMXAHOI(lbn, mabn, hoten, nnv, mabh, phongtyc);
                    Danhsach.put(benhnhan.getMABN(), benhnhan);
                    System.out.println("Đã thêm bệnh nhân: " + benhnhan.getMABN());
                    flag=false;
                }
                
                if (thongtin.equals("@") && flag == false) {
                    flag = true;
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
                            SimpleDateFormat d = new SimpleDateFormat("dd/MM/yyyy");
                            nnv = d.parse(value);  
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
                    benhnhan = new BENHNHANBAOHIEMYTE(lbn, mabn, hoten, nnv, mabh, phongtyc);
                else
                    benhnhan = new BENHNHANBAOHIEMXAHOI(lbn, mabn, hoten, nnv, mabh, phongtyc);
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
}
