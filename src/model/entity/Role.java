package model.entity;

public enum Role {
    DOCTOR("Bac si"),
    PATIENT("Benh nhan");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public static Role fromDbValue(String role) {
        if (role == null) {
            return null;
        }

        // Kiểm tra trực tiếp với tên enum
        for (Role r : Role.values()) {
            // Kiểm tra tên enum (DOCTOR, PATIENT)
            if (r.name().equalsIgnoreCase(role)) {
                return r;
            }
            // Kiểm tra tên hiển thị (Bac si, Benh nhan)
            if (r.displayName.equalsIgnoreCase(role)) {
                return r;
            }
        }
        return null;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public String getDbValue() {
        return name();
    }
}