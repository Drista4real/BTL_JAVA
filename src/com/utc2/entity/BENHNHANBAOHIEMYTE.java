/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.utc2.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BENHNHANBAOHIEMYTE extends BENHNHAN {
    private String MSBH;

    public String getMSBH() {
        return MSBH;
    }

    public void setMSBH(String MSBH) {
        this.MSBH = MSBH;
    }
    
    public BENHNHANBAOHIEMYTE() {
        super();
    }

    public BENHNHANBAOHIEMYTE(char LoaiBH, String MABN, String Hoten, Date Ngaynhapvien, String MSBH, Boolean PhongTYC) {
        super(LoaiBH, MABN, Hoten, Ngaynhapvien, PhongTYC);
        this.MSBH = MSBH;
    }
    
    @Override
    public void Xuat() {
        super.Xuat();
        System.out.println("Mã số bảo hiểm y tế: " + this.MSBH);
        System.out.println("Hóa đơn viện phí: " + this.TinhhoadonVP());
    }
    
    @Override
    public void Nhap() {
        super.Nhap();
        super.LoaiBH = 'y';
        System.out.print("Nhập mã số bảo hiểm y tế: ");
        this.MSBH = super.sc.nextLine();
    }
    
    @Override
    public double TinhhoadonVP() {
        if (super.PhongTYC = true) {
            return (super.Songaynhapvien() * super.DGP * 200000) - ((super.Songaynhapvien() * super.DGP) * 0.7);
        }
        else {
            return (super.Songaynhapvien() * super.DGP) - ((super.Songaynhapvien() * super.DGP) * 0.7);
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
                + "Mabaohiem: " + this.MSBH + "\r\n";      
    }
}