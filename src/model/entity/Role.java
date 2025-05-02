package model.entity;

public enum Role {
    PATIENT("Benh nhan"),
    DOCTOR("Bac si");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}