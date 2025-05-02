package model.entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class BENHNHAN {
    protected char LoaiBH;
    protected String MABN;
    protected String GhiChu;
    protected LocalDate LichHen;
    protected String Hoten;
    protected LocalDate Ngaynhapvien;
    protected boolean PhongTYC;

    public BENHNHAN(char LoaiBH, String MABN, String GhiChu, LocalDate LichHen, String Hoten, LocalDate Ngaynhapvien, boolean PhongTYC) {
        this.LoaiBH = LoaiBH;
        this.MABN = MABN != null ? MABN : "";
        this.GhiChu = GhiChu != null ? GhiChu : "";
        this.LichHen = LichHen;
        this.Hoten = Hoten != null ? Hoten : "";
        this.Ngaynhapvien = Ngaynhapvien;
        this.PhongTYC = PhongTYC;
    }

    public abstract double TinhhoadonVP();

    // Getters and Setters
    public char getLoaiBH() { return LoaiBH; }
    public void setLoaiBH(char LoaiBH) { this.LoaiBH = LoaiBH; }
    public String getMABN() { return MABN; }
    public void setMABN(String MABN) { this.MABN = MABN != null ? MABN : ""; }
    public String getGhiChu() { return GhiChu; }
    public void setGhiChu(String GhiChu) { this.GhiChu = GhiChu != null ? GhiChu : ""; }
    public LocalDate getLichHen() { return LichHen; }
    public void setLichHen(LocalDate LichHen) { this.LichHen = LichHen; }
    public String getHoten() { return Hoten; }
    public String getTenBenhNhan() { return Hoten; } // Alias for GUI compatibility
    public void setHoten(String Hoten) { this.Hoten = Hoten != null ? Hoten : ""; }
    public LocalDate getNgaynhapvien() { return Ngaynhapvien; }
    public void setNgaynhapvien(LocalDate Ngaynhapvien) { this.Ngaynhapvien = Ngaynhapvien; }
    public boolean getPhongTYC() { return PhongTYC; }
    public void setPhongTYC(boolean PhongTYC) { this.PhongTYC = PhongTYC; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String ngaynhapvienStr = Ngaynhapvien != null ? Ngaynhapvien.format(formatter) : "";
        String lichHenStr = LichHen != null ? LichHen.format(formatter) : "";
        return "Mabenhnhan: " + MABN + "\n" +
                "Hoten: " + Hoten + "\n" +
                "Ngaynhapvien: " + ngaynhapvienStr + "\n" +
                "Phongtheoyeucau: " + PhongTYC + "\n" +
                "Loaibaohiem: " + LoaiBH + "\n" +
                "Lichhen: " + lichHenStr + "\n" +
                "Ghichu: " + GhiChu;
    }
}