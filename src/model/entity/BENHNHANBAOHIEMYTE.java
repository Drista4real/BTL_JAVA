
package model.entity;

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

    // Thêm getter và setter cho MBHXH để tương thích với BENHNHANBAOHIEMXAHOI
    public String getMBHXH() {
        return MSBH;
    }

    public void setMBHXH(String MBHXH) {
        this.MSBH = MBHXH;
    }

    public BENHNHANBAOHIEMYTE() {
        super();
    }

    public BENHNHANBAOHIEMYTE(char LoaiBH, String MABN, String GhiChu, Date LichHen, String Hoten, Date Ngaynhapvien, String MSBH, Boolean PhongTYC) {
        super(LoaiBH, MABN, GhiChu, LichHen, Hoten, Ngaynhapvien, PhongTYC);
        this.MSBH = MSBH;
    }

    @Override
    public void Xuat() {
        super.Xuat();
        System.out.println("Mã số bảo hiểm: " + this.MSBH);
        System.out.println("Hóa đơn viện phí: " + this.TinhhoadonVP());

        // Nếu là BHXH, hiển thị thêm thông tin tương ứng
        if (super.LoaiBH == 'x') {
            System.out.println("Loại bảo hiểm: Bảo hiểm xã hội");
            System.out.println("Mã số bảo hiểm xã hội: " + this.MSBH);
        } else {
            System.out.println("Loại bảo hiểm: Bảo hiểm y tế");
            System.out.println("Mã số bảo hiểm y tế: " + this.MSBH);
        }
    }

    @Override
    public void Nhap() {
        super.Nhap();
        if (super.LoaiBH == 'x') {
            System.out.print("Nhập mã số bảo hiểm xã hội: ");
        } else {
            super.LoaiBH = 'y';
            System.out.print("Nhập mã số bảo hiểm y tế: ");
        }
        this.MSBH = super.sc.nextLine();
    }

    @Override
    public double TinhhoadonVP() {
        if (super.LoaiBH == 'x') {
            // Logic tính hóa đơn cho BHXH
            if (super.PhongTYC == true) {
                return (super.Songaynhapvien() * super.DGP * 200000);
            }
            else {
                return (super.Songaynhapvien() * super.DGP);
            }
        } else {
            // Logic tính hóa đơn cho BHYT (giữ nguyên mã gốc của BENHNHANBAOHIEMYTE)
            return 0; // Thay bằng công thức tính hóa đơn của BENHNHANBAOHIEMYTE
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