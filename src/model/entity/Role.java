package model.entity;

public enum Role {
    DOCTOR("Bac si"),
    PATIENT("Benh nhan"),
    ADMIN("Admin");
    
    private final String dbValue;
    
    Role(String dbValue) {
        this.dbValue = dbValue;
    }
    
    public String getDbValue() {
        return dbValue;
    }
    
    public static Role fromDbValue(String dbValue) {
        if (dbValue == null) {
            throw new IllegalArgumentException("Database role value cannot be null");
        }
        
        // Kiểm tra chính xác
        for (Role role : values()) {
            if (role.dbValue.equals(dbValue)) {
                return role;
            }
        }
        
        // Kiểm tra không phân biệt chữ hoa/thường và dấu cách
        String cleanDbValue = dbValue.toLowerCase().trim();
        
        // In ra để debug
        System.out.println("Chuyển đổi role từ DB: '" + dbValue + "' sang '" + cleanDbValue + "'");
        
        if (cleanDbValue.contains("benh") || cleanDbValue.contains("nhan") || cleanDbValue.contains("patient")) {
            System.out.println("Xác định role là PATIENT");
            return PATIENT;
        } else if (cleanDbValue.contains("bac") || cleanDbValue.contains("si") || cleanDbValue.contains("doctor")) {
            System.out.println("Xác định role là DOCTOR");
            return DOCTOR;
        } else if (cleanDbValue.contains("admin")) {
            System.out.println("Xác định role là ADMIN");
            return ADMIN;
        }
        
        // Nếu không xác định được thì mặc định là bệnh nhân
        System.out.println("Không xác định được role, mặc định là PATIENT");
        return PATIENT;
    }
}