package model.entity;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class VitalSigns dùng để quản lý thông tin các dấu hiệu sinh tồn
 * của bệnh nhân trong hệ thống y tế.
 */
public class VitalSigns {
    private String id;             // ID của bản ghi dấu hiệu sinh tồn
    private String patientId;      // ID của bệnh nhân
    private double temperature;    // Nhiệt độ cơ thể (°C)
    private int systolicBP;        // Huyết áp tâm thu (mmHg)
    private int diastolicBP;       // Huyết áp tâm trương (mmHg)
    private int heartRate;         // Nhịp tim (bpm)
    private int spO2;              // Độ bão hòa oxy trong máu (%)
    private LocalDateTime recordTime; // Thời điểm ghi nhận dữ liệu

    /**
     * Constructor không tham số, tự động gán thời gian hiện tại
     */
    public VitalSigns() {
        this.recordTime = LocalDateTime.now();
    }

    /**
     * Constructor đầy đủ với các tham số, kiểm tra tính hợp lệ
     *
     * @param id ID của bản ghi
     * @param patientId ID của bệnh nhân
     * @param temperature Nhiệt độ cơ thể (°C)
     * @param systolicBP Huyết áp tâm thu (mmHg)
     * @param diastolicBP Huyết áp tâm trương (mmHg)
     * @param heartRate Nhịp tim (bpm)
     * @param spO2 Độ bão hòa oxy trong máu (%)
     * @param recordTime Thời điểm ghi nhận
     * @throws IllegalArgumentException nếu giá trị không hợp lệ
     */
    public VitalSigns(String id, String patientId, double temperature, int systolicBP,
                      int diastolicBP, int heartRate, int spO2, LocalDateTime recordTime) {
        this.id = id;
        this.patientId = patientId;

        // Kiểm tra tính hợp lệ của các tham số
        setTemperature(temperature);
        setSystolicBP(systolicBP);
        setDiastolicBP(diastolicBP);
        setHeartRate(heartRate);
        setSpO2(spO2);

        // Nếu thời gian không được cung cấp, sử dụng thời gian hiện tại
        this.recordTime = (recordTime != null) ? recordTime : LocalDateTime.now();
    }

    /**
     * @return ID của bản ghi
     */
    public String getId() {
        return id;
    }

    /**
     * @param id ID của bản ghi
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return ID của bệnh nhân
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * @param patientId ID của bệnh nhân
     */
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    /**
     * @return Nhiệt độ cơ thể (°C)
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * @param temperature Nhiệt độ cơ thể (°C)
     * @throws IllegalArgumentException nếu nhiệt độ nằm ngoài khoảng 30-45°C
     */
    public void setTemperature(double temperature) {
        if (temperature < 30 || temperature > 45) {
            throw new IllegalArgumentException("Nhiệt độ phải nằm trong khoảng 30-45°C");
        }
        this.temperature = temperature;
    }

    /**
     * @return Huyết áp tâm thu (mmHg)
     */
    public int getSystolicBP() {
        return systolicBP;
    }

    /**
     * @param systolicBP Huyết áp tâm thu (mmHg)
     * @throws IllegalArgumentException nếu huyết áp tâm thu nằm ngoài khoảng 50-250 mmHg
     */
    public void setSystolicBP(int systolicBP) {
        if (systolicBP < 50 || systolicBP > 250) {
            throw new IllegalArgumentException("Huyết áp tâm thu phải nằm trong khoảng 50-250 mmHg");
        }
        this.systolicBP = systolicBP;
    }

    /**
     * @return Huyết áp tâm trương (mmHg)
     */
    public int getDiastolicBP() {
        return diastolicBP;
    }

    /**
     * @param diastolicBP Huyết áp tâm trương (mmHg)
     * @throws IllegalArgumentException nếu huyết áp tâm trương nằm ngoài khoảng 30-150 mmHg
     */
    public void setDiastolicBP(int diastolicBP) {
        if (diastolicBP < 30 || diastolicBP > 150) {
            throw new IllegalArgumentException("Huyết áp tâm trương phải nằm trong khoảng 30-150 mmHg");
        }
        this.diastolicBP = diastolicBP;
    }

    /**
     * @return Nhịp tim (bpm)
     */
    public int getHeartRate() {
        return heartRate;
    }

    /**
     * @param heartRate Nhịp tim (bpm)
     * @throws IllegalArgumentException nếu nhịp tim nằm ngoài khoảng 30-250 bpm
     */
    public void setHeartRate(int heartRate) {
        if (heartRate < 30 || heartRate > 250) {
            throw new IllegalArgumentException("Nhịp tim phải nằm trong khoảng 30-250 bpm");
        }
        this.heartRate = heartRate;
    }

    /**
     * @return Độ bão hòa oxy trong máu (%)
     */
    public int getSpO2() {
        return spO2;
    }

    /**
     * @param spO2 Độ bão hòa oxy trong máu (%)
     * @throws IllegalArgumentException nếu SpO2 nằm ngoài khoảng 50-100%
     */
    public void setSpO2(int spO2) {
        if (spO2 < 50 || spO2 > 100) {
            throw new IllegalArgumentException("SpO2 phải nằm trong khoảng 50-100%");
        }
        this.spO2 = spO2;
    }

    /**
     * @return Thời điểm ghi nhận dữ liệu
     */
    public LocalDateTime getRecordTime() {
        return recordTime;
    }

    /**
     * @param recordTime Thời điểm ghi nhận dữ liệu
     */
    public void setRecordTime(LocalDateTime recordTime) {
        this.recordTime = recordTime;
    }

    /**
     * @return Chuỗi định dạng thời gian ghi nhận dữ liệu
     */
    public String getFormattedRecordTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return recordTime.format(formatter);
    }

    /**
     * @return Chuỗi mô tả đầy đủ thông tin dấu hiệu sinh tồn
     */
    @Override
    public String toString() {
        return "VitalSigns{" +
                "id='" + id + '\'' +
                ", patientId='" + patientId + '\'' +
                ", temperature=" + temperature + "°C" +
                ", systolicBP=" + systolicBP + " mmHg" +
                ", diastolicBP=" + diastolicBP + " mmHg" +
                ", heartRate=" + heartRate + " bpm" +
                ", spO2=" + spO2 + "%" +
                ", recordTime=" + getFormattedRecordTime() +
                '}';
    }
}
