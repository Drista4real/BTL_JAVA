package model.entity;


import java.util.Objects;
import java.util.UUID;


public class Medication {
    // Các thuộc tính của lớp
    private String medicationId;      // Mã số định danh của thuốc
    private String name;              // Tên thuốc
    private String description;       // Mô tả về thuốc (bao gồm thành phần, công dụng, đối tượng sử dụng)
    private String manufacturer;      // Nhà sản xuất thuốc
    private String dosageForm;        // Dạng bào chế của thuốc (ví dụ: viên, nước, bột,...)
    private String sideEffects;       // Các tác dụng phụ của thuốc

    /**
     * Constructor mặc định không có tham số
     */
    public Medication() {
        this.medicationId = UUID.randomUUID().toString();
    }

    /**
     * Constructor đầy đủ tham số để khởi tạo tất cả các thuộc tính
     *
     * @param name Tên thuốc
     * @param description Mô tả về thuốc
     * @param manufacturer Nhà sản xuất thuốc
     * @param dosageForm Dạng bào chế của thuốc
     * @param sideEffects Các tác dụng phụ của thuốc
     */
    public Medication(String name, String description, String manufacturer, String dosageForm, String sideEffects) {
        this.medicationId = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.manufacturer = manufacturer;
        this.dosageForm = dosageForm;
        this.sideEffects = sideEffects;
    }

    /**
     * Constructor đầy đủ tham số bao gồm ID
     *
     * @param medicationId Mã số định danh của thuốc
     * @param name Tên thuốc
     * @param description Mô tả về thuốc
     * @param manufacturer Nhà sản xuất thuốc
     * @param dosageForm Dạng bào chế của thuốc
     * @param sideEffects Các tác dụng phụ của thuốc
     */
    public Medication(String medicationId, String name, String description, String manufacturer, String dosageForm, String sideEffects) {
        this.medicationId = medicationId;
        this.name = name;
        this.description = description;
        this.manufacturer = manufacturer;
        this.dosageForm = dosageForm;
        this.sideEffects = sideEffects;
    }

    // Các phương thức getter và setter

    public String getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(String medicationId) {
        this.medicationId = medicationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public String getSideEffects() {
        return sideEffects;
    }

    public void setSideEffects(String sideEffects) {
        this.sideEffects = sideEffects;
    }

    @Override
    public String toString() {
        return "Medication{" +
                "medicationId='" + medicationId + '\'' +
                ", name='" + name + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", dosageForm='" + dosageForm + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medication that = (Medication) o;
        return Objects.equals(medicationId, that.medicationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(medicationId);
    }
}
