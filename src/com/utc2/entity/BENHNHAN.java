
package com.utc2.entity;

import com.utc2.backend.Demo1;
import com.utc2.entity.BENHNHANBAOHIEMXAHOI;
import com.utc2.entity.BENHNHANBAOHIEMYTE;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

public abstract class BENHNHAN implements IVIENPHI{
    protected String MABN;
    protected String Hoten;
    protected Date Ngaynhapvien;
    public Boolean PhongTYC = false;
    protected Scanner sc;
    protected char LoaiBH;
//    protected final double DGP = 150000;

    public char getLoaiBH() {
        return LoaiBH;
    }

    public void setLoaiBH(char LoaiBH) {
        this.LoaiBH = LoaiBH;
    }
    
    public String getMABN() {
        return MABN;
    }

    public void setMABN(String MABN) {
        this.MABN = MABN;
    }

    public String getHoten() {
        return Hoten;
    }

    public void setHoten(String Hoten) {
        this.Hoten = Hoten;
    }

    public Date getNgaynhapvien() {
        return Ngaynhapvien;
    }

    public void setNgaynhapvien(Date Ngaynhapvien) {
        this.Ngaynhapvien = Ngaynhapvien;
    }
    
    public Boolean getPhongTYC() {
        return PhongTYC;
    }

    public void setPhongTYC(Boolean PhongTYC) {
        this.PhongTYC = PhongTYC;
    }

    public BENHNHAN() {
        this.sc = new Scanner(System.in);
    }

    public BENHNHAN(char LoaiBH, String MABN, String Hoten, Date Ngaynhapvien, Boolean PhongTYC) {
        this.sc = new Scanner(System.in);
        this.LoaiBH = LoaiBH;
        this.MABN = MABN;
        this.Hoten = Hoten;
        this.Ngaynhapvien = Ngaynhapvien;
        this.PhongTYC = PhongTYC;
    }
    
    public void Xuat() {
        SimpleDateFormat fmd = new SimpleDateFormat("dd/MM/yyyy");
        
        System.out.println("Mã bệnh nhân: " + this.MABN);
        System.out.println("Họ tên: " + this.Hoten);
        System.out.println("Ngày nhập viện: " + fmd.format(this.Ngaynhapvien));
        System.out.print("Phòng theo yêu cầu: ");
        if (this.PhongTYC)
            System.out.println("Có");
        else
            System.out.println("Không");
        System.out.println("Loại bảo hiểm: " + this.LoaiBH);
    }
    
    public void Nhap() {
        try {
            SimpleDateFormat fmd = new SimpleDateFormat("dd/MM/yyyy");
            
            System.out.print("Nhập mã bệnh nhân: "); this.MABN = this.sc.nextLine();
            System.out.print("Nhập họ tên: "); this.Hoten = this.sc.nextLine();
            System.out.print("Nhập ngày nhập viện: "); this.Ngaynhapvien = fmd.parse(this.sc.nextLine());
            System.out.print("Nhập 'y/Y' cho phòng theo yêu cầu: ");
            char tmp = sc.nextLine().charAt(0);
            if ((tmp == 'y') || (tmp == 'Y'))
                this.PhongTYC = true;
            else
                this.PhongTYC = false;
        } catch (Exception e) {
        }
    }
    
    public long Songaynhapvien() {
        Calendar NRV = new GregorianCalendar();
        NRV.setTime(Calendar.getInstance().getTime());
        
        Calendar NNV = new GregorianCalendar();
        NNV.setTime(this.Ngaynhapvien);
        
        long snnv = (NRV.getTimeInMillis() - NNV.getTimeInMillis()) / (24 * 3600 * 1000);
        return snnv;
    }
    
//    @Override
//    public abstract double TinhhoadonVP();
    
    @Override
    public abstract String toString();
    public Object[] toArray()
    {
        return new Object[]{MABN,Hoten,Ngaynhapvien,PhongTYC,LoaiBH,TinhhoadonVP()};
    }
}
