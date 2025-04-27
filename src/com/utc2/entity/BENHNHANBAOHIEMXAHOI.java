
package com.utc2.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class BENHNHANBAOHIEMXAHOI extends BENHNHAN {
    private String MBHXH;
    public String getMBHXH() {
        return MBHXH;
    }

    public void setMBHXH(String MBHXH) {
        this.MBHXH = MBHXH;
    }

    public BENHNHANBAOHIEMXAHOI() {
        super();
    }
    public BENHNHANBAOHIEMXAHOI(char LoaiBH, String MABN, String Hoten, Date Ngaynhapvien, String MaBHXH, Boolean PhongTYC) {
        super(LoaiBH, MABN, "", null, Hoten, Ngaynhapvien, PhongTYC);
        this.MBHXH = MBHXH;
    }

    
    @Override
    public void Xuat() {
        super.Xuat();
        System.out.println("Mã số bảo hiểm xã hội: " + this.MBHXH);
        System.out.println("Hóa đơn viện phí: " + this.TinhhoadonVP());
    }
    
    @Override
    public void Nhap() {
        super.Nhap();
        super.LoaiBH = 'x';
        System.out.print("Nhập mã số bảo hiểm xã hội: ");
        this.MBHXH = super.sc.nextLine();
    }
    
    @Override
    public double TinhhoadonVP() {
        if (super.PhongTYC = true) {
            return (super.Songaynhapvien() * super.DGP * 200000);
        }
        else {
            return (super.Songaynhapvien() * super.DGP);
        }
    }
    
    @Override
    public String toString() {
        SimpleDateFormat fmd = new SimpleDateFormat("dd/MM/yyyy");
        return  "@" + "\r\n"
                + "Mabenhnhan: " + super.MABN + "\r\n"
                + "Hoten: " + super.Hoten + "\r\n"
                + "Ngaynhapvien: " + fmd.format(super.Ngaynhapvien) + "\r\n"
                + "Phongtheoyeucau: " + super.PhongTYC + "\r\n"
                + "Loaibaohiem: " + super.LoaiBH + "\r\n"
                + "Mabaohiem: " + this.MBHXH + "\r\n";      
    }
}

