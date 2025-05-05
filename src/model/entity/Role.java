package model.entity;

public enum Role {
    DOCTOR("Bac si"),
    PATIENT("Benh nhan");

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