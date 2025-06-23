package model.entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BENHNHANBAOHIEMYTE extends BENHNHAN {
    private String MSBH;
    private Admission admission;
    private static final double DGP = 1000000;

    public BENHNHANBAOHIEMYTE() {
        super('n', "", "", null, "", null, false);
        this.MSBH = "";
        this.admission = null;
    }

    public BENHNHANBAOHIEMYTE(char LoaiBH, String MABN, String GhiChu, LocalDate LichHen, String Hoten, LocalDate Ngaynhapvien, String MSBH, boolean PhongTYC, Admission admission) {
        super(LoaiBH, MABN, GhiChu, LichHen, Hoten, Ngaynhapvien, PhongTYC);
        this.MSBH = MSBH != null ? MSBH : "";
        this.admission = admission;
    }

    public String getMSBH() { return MSBH; }
    public String getMaBHYT() { return MSBH; }
    public void setMSBH(String MSBH) { this.MSBH = MSBH != null ? MSBH : ""; }
    public Admission getAdmission() { return admission; }
    public void setAdmission(Admission admission) { this.admission = admission; }
    public String getMaBenhNhan() { return MABN; }

    @Override
    public double TinhhoadonVP() {
        long days = admission != null ? admission.getHospitalStayDays() : 0;
        double baseFee = DGP;
        double roomFee = PhongTYC ? 500000 : 0;

        if (LoaiBH == 'y') {
            return (baseFee * days * 0.2) + roomFee;
        } else {
            return (baseFee * days) + roomFee;
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String ngaynhapvienStr = Ngaynhapvien != null ? Ngaynhapvien.format(formatter) : "";
        String lichHenStr = LichHen != null ? LichHen.format(formatter) : "";
        return "@\r\n" +
                "Mabenhnhan: " + MABN + "\r\n" +
                "Hoten: " + Hoten + "\r\n" +
                "Ngaynhapvien: " + ngaynhapvienStr + "\r\n" +
                "Phongtheoyeucau: " + PhongTYC + "\r\n" +
                "Loaibaohiem: " + LoaiBH + "\r\n" +
                "Mabaohiem: " + MSBH + "\r\n" +
                "Lichhen: " + lichHenStr + "\r\n" +
                "Ghichu: " + (GhiChu != null ? GhiChu : "") + "\r\n";
    }
}