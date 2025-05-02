
package model.entity;
import java.util.Date;
import java.util.Scanner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;



public abstract class BENHNHAN implements IVIENPHI{
	protected char LoaiBH;
	 protected String MABN;
    protected String GhiChu;
    protected Date LichHen;
    protected String Hoten;
    protected Date Ngaynhapvien;
    protected Boolean PhongTYC ;
    protected Scanner sc;

//    protected final double DGP = 150000

    public char getLoaiBH() {
        return LoaiBH;
    }

    public void setLoaiBH(char LoaiBH) {
        this.LoaiBH = LoaiBH;
    }
    
    public String getMABN() {
        return MABN;
    }
    public String getGhiChu(){
        return GhiChu;
        
    }

    public void setMABN(String MABN) {
        this.MABN = MABN;
    }
    public void setGhiChu(String GhiChu){
        this.GhiChu = GhiChu;
    }
    public void GhiChuBS (String GhiChumoi){
        this.GhiChu = GhiChumoi;
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
    public Date getLichHen(){
        return LichHen;
    }
    public void setLichHen(Date LichHen){
        this.LichHen = LichHen;
    }
        
   public void DatLichHen(Date LichHen){   
       this.LichHen = LichHen;
   }
   public void setPhongTYC(Boolean PhongTYC) {
        this.PhongTYC = PhongTYC;
        
    }

    public BENHNHAN() {
        this.sc = new Scanner(System.in);
    }

    public BENHNHAN(char LoaiBH,String MABN,String GhiChu, Date LichHen , String Hoten, Date Ngaynhapvien, Boolean PhongTYC) {
        this.sc = new Scanner(System.in);
        this.LoaiBH = LoaiBH;
        this.MABN = MABN;
        this.GhiChu = GhiChu;
        this.LichHen = LichHen;
        this.Hoten = Hoten;
        this.Ngaynhapvien = Ngaynhapvien;
        this.PhongTYC = PhongTYC;
      
        
    }
    
    public void Xuat() {
        SimpleDateFormat fmd = new SimpleDateFormat("dd/MM/yyyy");
        
        System.out.println("Mã bệnh nhân: " + this.MABN);
        System.out.println("Họ tên: " + this.Hoten);
        System.out.println("Ngày nhập viện: " + fmd.format(this.Ngaynhapvien));
        System.out.println("Lịch hẹn của Bác Sĩ"+ fmd.format(this.LichHen));
        System.out.println(" Ghi chú của Bác Sĩ"+ this.GhiChu);
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
            System.out.print ("Nhập lịch hẹn của bác sĩ"); this.LichHen = fmd.parse( this.sc.nextLine());
            System.out.print ("Nhập ghi chú của bác sĩ"); this.GhiChu = this.sc.nextLine();
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
    public long ngaylichhen() {
        Calendar NRV = new GregorianCalendar();
        NRV.setTime(Calendar.getInstance().getTime());
        
        Calendar NNV = new GregorianCalendar();
        NNV.setTime(this.LichHen);
        
        long snnv = (NRV.getTimeInMillis() - NNV.getTimeInMillis()) / (24 * 3600 * 1000);
        return snnv;
    }
    
    
//    @Override
//    public abstract double TinhhoadonVP();
    
    @Override
    public abstract String toString();
    public Object[] toArray()
    {
        return new Object[]{MABN,Hoten,Ngaynhapvien, LichHen, GhiChu,PhongTYC,LoaiBH,TinhhoadonVP()};

    }
}

