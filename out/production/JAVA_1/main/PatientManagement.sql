-- Xoa va tao lai co so du lieu PatientManagement
DROP DATABASE IF EXISTS PatientManagement;
CREATE DATABASE IF NOT EXISTS PatientManagement
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
USE PatientManagement;

-- Xoa cac bang neu ton tai
DROP TABLE IF EXISTS VitalSigns;
DROP TABLE IF EXISTS MedicalRecords;
DROP TABLE IF EXISTS Appointments;
DROP TABLE IF EXISTS Admissions;
DROP TABLE IF EXISTS RoomUsageRecords;
DROP TABLE IF EXISTS Beds;
DROP TABLE IF EXISTS HospitalRooms;
DROP TABLE IF EXISTS Insurance;
DROP TABLE IF EXISTS Patients;
DROP TABLE IF EXISTS UserAccounts;
DROP TABLE IF EXISTS PrescriptionDetails;
DROP TABLE IF EXISTS Prescriptions;
DROP TABLE IF EXISTS Medications;
DROP TABLE IF EXISTS PaymentRecords;
DROP TABLE IF EXISTS InvoiceDetails;
DROP TABLE IF EXISTS Invoices;

-- Bang UserAccounts (luu thong tin tai khoan nguoi dung: bac si, benh nhan)
CREATE TABLE UserAccounts (
    UserID VARCHAR(50) PRIMARY KEY,
    UserName VARCHAR(50) UNIQUE NOT NULL,
    FullName VARCHAR(100) NOT NULL,
    Role ENUM('Bac si', 'Benh nhan') NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    PhoneNumber VARCHAR(15) UNIQUE,
    Password VARCHAR(255) NOT NULL,
    Note VARCHAR(255) DEFAULT '',
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Bang Patients (luu thong tin benh nhan)
CREATE TABLE Patients (
    PatientID VARCHAR(50) PRIMARY KEY,
    UserID VARCHAR(50) UNIQUE NOT NULL,
    FullName VARCHAR(255) NOT NULL,
    DateOfBirth DATE NOT NULL,
    Gender ENUM('Nam', 'Nu') NOT NULL,
    PhoneNumber VARCHAR(20),
    Address TEXT,
    IllnessInfo TEXT,
    CreatedAt DATE,
    Height DOUBLE,
    Weight DOUBLE,
    BloodType VARCHAR(10),
    FOREIGN KEY (UserID) REFERENCES UserAccounts(UserID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Bang Insurance (luu thong tin bao hiem, chi ho tro BHYT)
CREATE TABLE Insurance (
    InsuranceID VARCHAR(20) PRIMARY KEY,
    PatientID VARCHAR(50),
    Provider ENUM('BHYT') NOT NULL,
    PolicyNumber VARCHAR(100) UNIQUE NOT NULL,
    StartDate DATE NOT NULL,
    ExpirationDate DATE NOT NULL,
    CoverageDetails TEXT,
    Status ENUM('Hoat dong', 'Het han', 'Khong xac dinh') DEFAULT 'Hoat dong',
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID),
    CHECK (StartDate < ExpirationDate)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Bang HospitalRooms (luu thong tin phong benh)
CREATE TABLE HospitalRooms (
    RoomID VARCHAR(36) PRIMARY KEY,
    RoomNumber VARCHAR(50) NOT NULL,
    RoomType ENUM('Tieu chuan', 'VIP', 'ICU', 'Cap cuu') NOT NULL,
    Status ENUM('Trong', 'Dang su dung', 'Day', 'Bao tri', 'Dang ve sinh', 'Da dat truoc') DEFAULT 'Trong',
    TotalBeds INT CHECK (TotalBeds > 0),
    AvailableBeds INT CHECK (AvailableBeds >= 0),
    PricePerDay DOUBLE NOT NULL,
    Department VARCHAR(100),
    FloorNumber INT CHECK (FloorNumber > 0),
    Building VARCHAR(50)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Bang Beds (luu thong tin giuong benh)
CREATE TABLE Beds (
    BedID VARCHAR(36) PRIMARY KEY,
    RoomID VARCHAR(36),
    BedNumber VARCHAR(50) NOT NULL,
    PatientID VARCHAR(50),
    OccupiedSince DATETIME,
    IsAvailable BOOLEAN NOT NULL,
    Notes TEXT,
    FOREIGN KEY (RoomID) REFERENCES HospitalRooms(RoomID),
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Bang RoomUsageRecords (luu lich su su dung phong)
CREATE TABLE RoomUsageRecords (
    RecordID VARCHAR(36) PRIMARY KEY,
    RoomID VARCHAR(36),
    PatientID VARCHAR(50),
    BedID VARCHAR(36),
    CheckInTime DATETIME NOT NULL,
    CheckOutTime DATETIME,
    DoctorInCharge VARCHAR(50),
    Reason TEXT,
    Notes TEXT,
    FOREIGN KEY (RoomID) REFERENCES HospitalRooms(RoomID),
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID),
    FOREIGN KEY (BedID) REFERENCES Beds(BedID),
    FOREIGN KEY (DoctorInCharge) REFERENCES UserAccounts(UserID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Bang Admissions (luu thong tin nhap vien)
CREATE TABLE Admissions (
    AdmissionID VARCHAR(50) PRIMARY KEY,
    PatientID VARCHAR(50),
    DoctorID VARCHAR(50),
    RoomID VARCHAR(50),
    AdmissionDate DATE NOT NULL,
    DischargeDate DATE,
    Notes TEXT,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID),
    FOREIGN KEY (DoctorID) REFERENCES UserAccounts(UserID),
    FOREIGN KEY (RoomID) REFERENCES HospitalRooms(RoomID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Bang Appointments (luu thong tin lich hen)
CREATE TABLE Appointments (
    AppointmentID VARCHAR(50) PRIMARY KEY,
    PatientID VARCHAR(50),
    DoctorID VARCHAR(50),
    AppointmentDate DATETIME NOT NULL,
    Reason TEXT,
    Status ENUM('Da xac nhan', 'Cho xac nhan', 'Huy') DEFAULT 'Cho xac nhan',
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID),
    FOREIGN KEY (DoctorID) REFERENCES UserAccounts(UserID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Bang MedicalRecords (luu ho so y te)
CREATE TABLE MedicalRecords (
    RecordID VARCHAR(50) PRIMARY KEY,
    PatientID VARCHAR(50),
    DoctorID VARCHAR(50),
    Diagnosis TEXT NOT NULL,
    Treatment TEXT,
    RecordDate DATE NOT NULL,
    Notes TEXT,
    LifestyleRecommendations TEXT,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID),
    FOREIGN KEY (DoctorID) REFERENCES UserAccounts(UserID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Bang VitalSigns
CREATE TABLE VitalSigns (
    Id VARCHAR(50) PRIMARY KEY,
    PatientId VARCHAR(50),
    Temperature DECIMAL(4,1),
    SystolicBP INT,
    DiastolicBP INT,
    HeartRate INT,
    SpO2 INT,
    RecordTime DATETIME,
    FOREIGN KEY (PatientId) REFERENCES Patients(PatientID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Bang Prescriptions
CREATE TABLE Prescriptions (
    PrescriptionID VARCHAR(50) PRIMARY KEY,
    PatientID VARCHAR(50),
    DoctorID VARCHAR(50),
    PrescriptionDate DATE NOT NULL,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID),
    FOREIGN KEY (DoctorID) REFERENCES UserAccounts(UserID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Bang PrescriptionDetails
CREATE TABLE PrescriptionDetails (
    DetailID VARCHAR(50) PRIMARY KEY,
    PrescriptionID VARCHAR(50),
    MedicationID VARCHAR(50),
    Dosage VARCHAR(50),
    Instructions TEXT,
    Price DOUBLE,
    Quantity INT,
    FOREIGN KEY (PrescriptionID) REFERENCES Prescriptions(PrescriptionID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Bang Medications
CREATE TABLE Medications (
    MedicationID VARCHAR(50) PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Description TEXT,
    Manufacturer VARCHAR(100) NOT NULL,
    DosageForm VARCHAR(50) NOT NULL,
    SideEffects TEXT,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Bang Invoices
CREATE TABLE Invoices (
    InvoiceID VARCHAR(36) PRIMARY KEY,
    InvoiceNumber VARCHAR(20) UNIQUE NOT NULL,
    PatientID VARCHAR(50) NOT NULL,
    CreatedDate DATETIME NOT NULL,
    DueDate DATETIME NOT NULL,
    PaidDate DATETIME,
    InvoiceType ENUM('ROOM_CHARGE', 'MEDICATION', 'PROCEDURE', 'CONSULTATION', 'SERVICE', 'PACKAGE', 'OTHER') NOT NULL,
    Status ENUM('PENDING', 'PARTIALLY_PAID', 'PAID', 'CANCELLED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
    CreatedBy VARCHAR(50) NOT NULL,
    ApprovedBy VARCHAR(50),
    Notes TEXT,
    TotalAmount DOUBLE NOT NULL DEFAULT 0,
    PaidAmount DOUBLE NOT NULL DEFAULT 0,
    RemainingAmount DOUBLE NOT NULL DEFAULT 0,
    PaymentMethod VARCHAR(50),
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID),
    FOREIGN KEY (CreatedBy) REFERENCES UserAccounts(UserID),
    FOREIGN KEY (ApprovedBy) REFERENCES UserAccounts(UserID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Bang InvoiceDetails
CREATE TABLE InvoiceDetails (
    DetailID VARCHAR(36) PRIMARY KEY,
    InvoiceID VARCHAR(36) NOT NULL,
    ServiceID VARCHAR(50),
    ServiceName VARCHAR(100) NOT NULL,
    ServiceCode VARCHAR(50) NOT NULL,
    Description TEXT,
    Quantity INT NOT NULL,
    UnitPrice DOUBLE NOT NULL,
    TotalPrice DOUBLE NOT NULL,
    DiscountPercent DOUBLE NOT NULL DEFAULT 0,
    DiscountAmount DOUBLE NOT NULL DEFAULT 0,
    FinalPrice DOUBLE NOT NULL,
    Category VARCHAR(50) NOT NULL,
    Unit VARCHAR(20) NOT NULL DEFAULT 'lần',
    PrescribedBy VARCHAR(50),
    PrescribedDate DATETIME,
    PerformedBy VARCHAR(50),
    PerformedDate DATETIME,
    CreatedAt DATETIME NOT NULL,
    CreatedBy VARCHAR(50),
    UpdatedAt DATETIME,
    UpdatedBy VARCHAR(50),
    IsCancelled BOOLEAN NOT NULL DEFAULT FALSE,
    CancelReason TEXT,
    FOREIGN KEY (InvoiceID) REFERENCES Invoices(InvoiceID),
    FOREIGN KEY (PrescribedBy) REFERENCES UserAccounts(UserID),
    FOREIGN KEY (PerformedBy) REFERENCES UserAccounts(UserID),
    FOREIGN KEY (CreatedBy) REFERENCES UserAccounts(UserID),
    FOREIGN KEY (UpdatedBy) REFERENCES UserAccounts(UserID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Bang PaymentRecords
CREATE TABLE PaymentRecords (
    PaymentID VARCHAR(36) PRIMARY KEY,
    InvoiceID VARCHAR(36) NOT NULL,
    PaymentDate DATETIME NOT NULL,
    Amount DOUBLE NOT NULL,
    PaymentMethod VARCHAR(50) NOT NULL,
    ReferenceNumber VARCHAR(50),
    PaidBy VARCHAR(50),
    ReceivedBy VARCHAR(50) NOT NULL,
    Notes TEXT,
    FOREIGN KEY (InvoiceID) REFERENCES Invoices(InvoiceID),
    FOREIGN KEY (PaidBy) REFERENCES Patients(PatientID),
    FOREIGN KEY (ReceivedBy) REFERENCES UserAccounts(UserID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Chen du lieu mau
-- 1. UserAccounts (8 ban ghi: 3 bac si, 5 benh nhan)
INSERT INTO UserAccounts (UserID, UserName, FullName, Role, Email, PhoneNumber, Password, Note) VALUES
    ('U001', 'doctor1', 'Nguyen Van An', 'Bac si', 'nguyenvanan@example.com', '0987654321', 'admin123', 'Bac si chuyen khoa noi'),
    ('U002', 'doctor2', 'Tran Thi Binh', 'Bac si', 'tranthibinh@example.com', '0976543210', 'admin123', 'Bac si chuyen khoa ngoai'),
    ('U003', 'doctor3', 'Le Van Cuong', 'Bac si', 'levancuong@example.com', '0912345678', 'admin123', 'Bac si chuyen khoa tim mach'),
    ('U004', 'patient1', 'Pham Thi Dung', 'Benh nhan', 'phamthidung@example.com', '0908765432', 'patient123', 'Benh nhan moi'),
    ('U005', 'patient2', 'Hoang Van Em', 'Benh nhan', 'hoangvanem@example.com', '0933456789', 'patient123', 'Theo doi dinh ky'),
    ('U006', 'patient3', 'Nguyen Thi Hoa', 'Benh nhan', 'nguyenthihoa@example.com', '0945678901', 'patient123', 'Benh nhan tai kham'),
    ('U007', 'patient4', 'Tran Van Khai', 'Benh nhan', 'tranvankhai@example.com', '0956789012', 'patient123', 'Can theo doi huyet ap'),
    ('U008', 'patient5', 'Le Thi Lan', 'Benh nhan', 'lethilan@example.com', '0967890123', 'patient123', 'Benh nhan tieu hoa');

-- 2. Patients (5 ban ghi, lien ket voi UserAccounts)
INSERT INTO Patients (PatientID, UserID, FullName, DateOfBirth, Gender, PhoneNumber, Address, IllnessInfo, CreatedAt, Height, Weight, BloodType) VALUES
    ('P001', 'U004', 'Pham Thi Dung', '1990-05-15', 'Nu', '0908765432', '123 Duong Lang, Ha Noi', 'Viem phoi nhe', '2025-04-01', 165.0, 55.0, 'A+'),
    ('P002', 'U005', 'Hoang Van Em', '1985-03-20', 'Nam', '0933456789', '456 Duong Giai Phong, Ha Noi', 'Tieu duong type 2', '2025-04-02', 170.0, 70.0, 'B+'),
    ('P003', 'U006', 'Nguyen Thi Hoa', '1992-07-10', 'Nu', '0945678901', '789 Duong Cau Giay, Ha Noi', 'Tang huyet ap', '2025-04-03', 160.0, 50.0, 'O+'),
    ('P004', 'U007', 'Tran Van Khai', '1988-11-25', 'Nam', '0956789012', '101 Duong Kim Ma, Ha Noi', 'Viem da day', '2025-04-04', 175.0, 75.0, 'AB+'),
    ('P005', 'U008', 'Le Thi Lan', '1995-09-30', 'Nu', '0967890123', '202 Duong Nguyen Trai, Ha Noi', 'Soi than', '2025-04-05', 158.0, 48.0, 'A-');

-- 3. Insurance (5 ban ghi, chi BHYT, lien ket voi Patients)
INSERT INTO Insurance (InsuranceID, PatientID, Provider, PolicyNumber, StartDate, ExpirationDate, Status) VALUES
    ('I001', 'P001', 'BHYT', 'BH123456', '2024-01-01', '2026-01-01', 'Hoat dong'),
    ('I002', 'P002', 'BHYT', 'BH654321', '2024-02-01', '2026-02-01', 'Hoat dong'),
    ('I003', 'P003', 'BHYT', 'BH789123', '2024-03-01', '2026-03-01', 'Hoat dong'),
    ('I004', 'P004', 'BHYT', 'BH456789', '2024-04-01', '2026-04-01', 'Hoat dong'),
    ('I005', 'P005', 'BHYT', 'BH321987', '2024-05-01', '2026-05-01', 'Hoat dong');

-- 4. HospitalRooms (5 ban ghi, bao gom cac loai phong khac nhau)
INSERT INTO HospitalRooms (RoomID, RoomNumber, RoomType, Status, TotalBeds, AvailableBeds, PricePerDay, Department, FloorNumber, Building) VALUES
    ('R001', '101', 'Tieu chuan', 'Trong', 4, 4, 1000000, 'Noi khoa', 1, 'A'),
    ('R002', '201', 'VIP', 'Trong', 2, 2, 5000000, 'Noi khoa', 2, 'B'),
    ('R003', '301', 'ICU', 'Trong', 3, 3, 8000000, 'Hoi suc cap cuu', 3, 'C'),
    ('R004', '102', 'Cap cuu', 'Trong', 5, 5, 2000000, 'Cap cuu', 1, 'A'),
    ('R005', '202', 'Tieu chuan', 'Trong', 4, 4, 1000000, 'Ngoai khoa', 2, 'A');

-- 5. Beds (18 ban ghi, lien ket voi HospitalRooms)
INSERT INTO Beds (BedID, RoomID, BedNumber, IsAvailable) VALUES
    ('B001', 'R001', '101-1', TRUE),
    ('B002', 'R001', '101-2', TRUE),
    ('B003', 'R001', '101-3', TRUE),
    ('B004', 'R001', '101-4', TRUE),
    ('B005', 'R002', '201-1', TRUE),
    ('B006', 'R002', '201-2', TRUE),
    ('B007', 'R003', '301-1', TRUE),
    ('B008', 'R003', '301-2', TRUE),
    ('B009', 'R003', '301-3', TRUE),
    ('B010', 'R004', '102-1', TRUE),
    ('B011', 'R004', '102-2', TRUE),
    ('B012', 'R004', '102-3', TRUE),
    ('B013', 'R004', '102-4', TRUE),
    ('B014', 'R004', '102-5', TRUE),
    ('B015', 'R005', '202-1', TRUE),
    ('B016', 'R005', '202-2', TRUE),
    ('B017', 'R005', '202-3', TRUE),
    ('B018', 'R005', '202-4', TRUE);

-- 6. RoomUsageRecords (5 ban ghi, lien ket voi HospitalRooms, Beds, Patients, UserAccounts)
INSERT INTO RoomUsageRecords (RecordID, RoomID, PatientID, BedID, CheckInTime, CheckOutTime, DoctorInCharge, Reason, Notes) VALUES
    ('REC001', 'R002', 'P001', 'B005', '2025-04-01 08:00:00', NULL, 'U001', 'Viem phoi nhe', 'Phong VIP theo yeu cau'),
    ('REC002', 'R001', 'P002', 'B001', '2025-04-02 09:00:00', NULL, 'U002', 'Tieu duong type 2', 'Phong tieu chuan'),
    ('REC003', 'R003', 'P003', 'B007', '2025-04-03 10:00:00', NULL, 'U003', 'Tang huyet ap', 'Chuyen ICU'),
    ('REC004', 'R004', 'P004', 'B010', '2025-04-04 11:00:00', NULL, 'U001', 'Viem da day', 'Cap cuu khan cap'),
    ('REC005', 'R005', 'P005', 'B015', '2025-04-05 12:00:00', NULL, 'U002', 'Soi than', 'Phong tieu chuan');

-- 7. Admissions (5 ban ghi, lien ket voi Patients, UserAccounts, va HospitalRooms)
INSERT INTO Admissions (AdmissionID, PatientID, DoctorID, RoomID, AdmissionDate, Notes) VALUES
    ('AD001', 'P001', 'U001', 'R002', '2025-04-01', 'Phong VIP theo yeu cau'),
    ('AD002', 'P002', 'U002', 'R001', '2025-04-02', 'Phong tieu chuan'),
    ('AD003', 'P003', 'U003', 'R003', '2025-04-03', 'Chuyen ICU'),
    ('AD004', 'P004', 'U001', 'R004', '2025-04-04', 'Cap cuu khan cap'),
    ('AD005', 'P005', 'U002', 'R005', '2025-04-05', 'Phong tieu chuan');

-- 8. Appointments (5 ban ghi, lien ket voi Patients va UserAccounts)
INSERT INTO Appointments (AppointmentID, PatientID, DoctorID, AppointmentDate, Reason, Status) VALUES
    ('AP001', 'P001', 'U001', '2025-04-30 09:00:00', 'Kham dinh ky', 'Da xac nhan'),
    ('AP002', 'P002', 'U002', '2025-04-30 10:00:00', 'Tai kham', 'Cho xac nhan'),
    ('AP003', 'P003', 'U003', '2025-04-30 11:00:00', 'Kham tim mach', 'Da xac nhan'),
    ('AP004', 'P004', 'U001', '2025-04-30 14:00:00', 'Kiem tra huyet ap', 'Cho xac nhan'),
    ('AP005', 'P005', 'U002', '2025-04-30 15:00:00', 'Kham tieu hoa', 'Da xac nhan');

-- 9. MedicalRecords (5 ban ghi, lien ket voi Patients va UserAccounts)
INSERT INTO MedicalRecords (RecordID, PatientID, DoctorID, Diagnosis, Treatment, RecordDate, Notes, LifestyleRecommendations) VALUES
    ('MR001', 'P001', 'U001', 'Viem phoi', 'Khang sinh', '2025-04-01', 'Can theo doi sat', 'Tranh thuc khuya, an do lanh'),
    ('MR002', 'P002', 'U002', 'Tieu duong', 'Insulin', '2025-04-02', 'Kiem tra duong huyet dinh ky', 'Tap the duc deu dan, kiem tra duong huyet hang tuan'),
    ('MR003', 'P003', 'U003', 'Tang huyet ap', 'Thuoc ha ap', '2025-04-03', 'Theo doi huyet ap hang ngay', 'Han che muoi, tap yoga'),
    ('MR004', 'P004', 'U001', 'Viem da day', 'Thuoc bao ve niem mac', '2025-04-04', 'Tai kham sau 2 tuan', 'An nhat, tranh do chien ran'),
    ('MR005', 'P005', 'U002', 'Soi than', 'Phau thuat noi soi', '2025-04-05', 'Uong nhieu nuoc', 'Uong du nuoc, tranh dung lau');

-- 10. VitalSigns
INSERT INTO VitalSigns (Id, PatientId, Temperature, SystolicBP, DiastolicBP, HeartRate, SpO2, RecordTime) VALUES
    ('VS-001', 'P001', 36.8, 120, 80, 75, 98, '2025-05-04 10:00:00'),
    ('VS-002', 'P002', 38.5, 125, 85, 110, 96, '2025-05-04 08:00:00'),
    ('VS-003', 'P003', 36.9, 150, 95, 85, 97, '2025-05-03 10:00:00'),
    ('VS-004', 'P004', 37.2, 115, 75, 90, 92, '2025-05-02 10:00:00');

-- 11. Chen du lieu mau vao bang Prescriptions
INSERT INTO Prescriptions (PrescriptionID, PatientID, DoctorID, PrescriptionDate) VALUES
    ('PRE-001', 'P001', 'U001', '2025-05-04'),
    ('PRE-002', 'P002', 'U002', '2025-04-27'),
    ('PRE-003', 'P003', 'U001', '2025-05-01'),
    ('PRE-004', 'P004', 'U003', '2025-04-30'),
    ('PRE-005', 'P005', 'U002', '2025-05-02'),
    ('PRE-006', 'P001', 'U003', '2025-04-28'),
    ('PRE-007', 'P002', 'U001', '2025-05-03'),
    ('PRE-008', 'P003', 'U002', '2025-04-29'),
    ('PRE-009', 'P004', 'U001', '2025-05-05'),
    ('PRE-010', 'P005', 'U003', '2025-04-26');

-- 12. Chen du lieu mau vao bang PrescriptionDetails
INSERT INTO PrescriptionDetails (DetailID, PrescriptionID, MedicationID, Dosage, Instructions, Price, Quantity) VALUES
    ('DET-001', 'PRE-001', 'MED-001', '500mg', 'Uong 1 vien moi 6 gio', 2000, 20),
    ('DET-002', 'PRE-001', 'MED-002', '250mg', 'Uong 1 vien sau an, ngay 3 lan', 3000, 30),
    ('DET-003', 'PRE-001', 'MED-005', '10mg', 'Uong 1 vien truoc khi ngu', 1500, 15),
    ('DET-004', 'PRE-002', 'MED-003', '1000mg', 'Uong 1 vien moi ngay', 5000, 10),
    ('DET-005', 'PRE-002', 'MED-004', '20mg', 'Uong 1 vien truoc an sang', 4000, 14),
    ('DET-006', 'PRE-002', 'MED-006', '5mg', 'Uong 1 vien moi toi', 2500, 20),
    ('DET-007', 'PRE-003', 'MED-007', '200mg', 'Uong 2 vien moi 8 gio', 1800, 24),
    ('DET-008', 'PRE-003', 'MED-008', '50mg', 'Uong 1 vien sau an trua', 2200, 10),
    ('DET-009', 'PRE-003', 'MED-001', '500mg', 'Uong 1 vien moi 6 gio', 2000, 15),
    ('DET-010', 'PRE-003', 'MED-009', '100mg', 'Uong 1 vien moi sang', 3500, 12),
    ('DET-011', 'PRE-004', 'MED-010', '25mg', 'Uong 1 vien moi 12 gio', 2800, 16),
    ('DET-012', 'PRE-004', 'MED-002', '250mg', 'Uong 1 vien sau an, ngay 3 lan', 3000, 20),
    ('DET-013', 'PRE-004', 'MED-005', '10mg', 'Uong 1 vien truoc khi ngu', 1500, 10),
    ('DET-014', 'PRE-005', 'MED-003', '1000mg', 'Uong 1 vien moi ngay', 5000, 8),
    ('DET-015', 'PRE-005', 'MED-011', '15mg', 'Uong 1 vien moi sang', 3200, 14),
    ('DET-016', 'PRE-006', 'MED-012', '400mg', 'Uong 1 vien moi 8 gio', 2600, 18),
    ('DET-017', 'PRE-006', 'MED-004', '20mg', 'Uong 1 vien truoc an sang', 4000, 12),
    ('DET-018', 'PRE-006', 'MED-007', '200mg', 'Uong 2 vien moi 8 gio', 1800, 20),
    ('DET-019', 'PRE-007', 'MED-013', '50mg', 'Uong 1 vien moi toi', 2300, 15),
    ('DET-020', 'PRE-007', 'MED-008', '50mg', 'Uong 1 vien sau an trua', 2200, 10),
    ('DET-021', 'PRE-007', 'MED-001', '500mg', 'Uong 1 vien moi 6 gio', 2000, 25),
    ('DET-022', 'PRE-007', 'MED-014', '75mg', 'Uong 1 vien moi sang', 2700, 12),
    ('DET-023', 'PRE-008', 'MED-015', '10mg', 'Uong 1 vien moi 12 gio', 3100, 16),
    ('DET-024', 'PRE-008', 'MED-002', '250mg', 'Uong 1 vien sau an, ngay 3 lan', 3000, 20),
    ('DET-025', 'PRE-009', 'MED-003', '1000mg', 'Uong 1 vien moi ngay', 5000, 10),
    ('DET-026', 'PRE-009', 'MED-016', '20mg', 'Uong 1 vien truoc khi ngu', 1900, 15),
    ('DET-027', 'PRE-009', 'MED-009', '100mg', 'Uong 1 vien moi sang', 3500, 14),
    ('DET-028', 'PRE-009', 'MED-005', '10mg', 'Uong 1 vien truoc khi ngu', 1500, 10),
    ('DET-029', 'PRE-010', 'MED-017', '500mg', 'Uong 1 vien moi 6 gio', 2400, 20),
    ('DET-030', 'PRE-010', 'MED-004', '20mg', 'Uong 1 vien truoc an sang', 4000, 12),
    ('DET-031', 'PRE-010', 'MED-018', '30mg', 'Uong 1 vien moi toi', 2900, 15);

-- 13. Medications
INSERT INTO Medications (MedicationID, Name, Description, Manufacturer, DosageForm, SideEffects) VALUES
    ('MED-001', 'Paracetamol', 'Thuoc giam dau, ha sot', 'PharmaCorp', 'Vien nen', 'Buon non, phat ban'),
    ('MED-002', 'Amoxicillin', 'Khang sinh dieu tri nhiem khuan', 'MediPharm', 'Vien nang', 'Tieu chay, di ung'),
    ('MED-003', 'Metformin', 'Thuoc dieu tri tieu duong type 2', 'HealthCare Inc', 'Vien nen', 'Dau bung, buon non'),
    ('MED-004', 'Amlodipine', 'Thuoc ha huyet ap', 'CardioPharm', 'Vien nen', 'Phu ne, chong mat'),
    ('MED-005', 'Omeprazole', 'Thuoc dieu tri loet da day', 'GastroMed', 'Vien nang', 'Dau dau, tieu chay'),
    ('MED-006', 'Loratadine', 'Thuoc khang histamin dieu tri di ung', 'AllergyCare', 'Vien nen', 'Buon ngu, kho mieng'),
    ('MED-007', 'Ibuprofen', 'Thuoc chong viem, giam dau', 'PainRelief Inc', 'Vien nen', 'Dau da day, buon non'),
    ('MED-008', 'Atorvastatin', 'Thuoc giam cholesterol', 'LipidPharm', 'Vien nen', 'Dau co, ton thuong gan'),
    ('MED-009', 'Cefuroxime', 'Khang sinh cephalosporin', 'AntiBac Corp', 'Vien nen', 'Tieu chay, di ung'),
    ('MED-010', 'Losartan', 'Thuoc dieu tri tang huyet ap', 'CardioPharm', 'Vien nen', 'Chong mat, met moi'),
    ('MED-011', 'Salbutamol', 'Thuoc gian phe quan dieu tri hen suyen', 'RespiCare', 'Ong hit', 'Run tay, nhip tim nhanh'),
    ('MED-012', 'Ciprofloxacin', 'Khang sinh dieu tri nhiem khuan', 'MediPharm', 'Vien nen', 'Buon non, dau khop'),
    ('MED-013', 'Fluoxetine', 'Thuoc chong tram cam', 'NeuroPharm', 'Vien nang', 'Mat ngu, lo au'),
    ('MED-014', 'Clopidogrel', 'Thuoc chong ket tap tieu cau', 'CardioPharm', 'Vien nen', 'Chay mau, dau bung'),
    ('MED-015', 'Prednisolone', 'Thuoc corticosteroid chong viem', 'InflameCare', 'Vien nen', 'Tang can, loang xuong'),
    ('MED-016', 'Diazepam', 'Thuoc an than, gian co', 'NeuroPharm', 'Vien nen', 'Buon ngu, phu thuoc thuoc'),
    ('MED-017', 'Azithromycin', 'Khang sinh macrolid', 'AntiBac Corp', 'Vien nen', 'Tieu chay, dau bung'),
    ('MED-018', 'Hydrochlorothiazide', 'Thuoc loi tieu dieu tri tang huyet ap', 'CardioPharm', 'Vien nen', 'Ha kali mau, chong mat');

-- 14. Invoices (5 ban ghi mau, lien ket voi Patients va UserAccounts)
INSERT INTO Invoices (InvoiceID, InvoiceNumber, PatientID, CreatedDate, DueDate, InvoiceType, Status, CreatedBy, TotalAmount, PaidAmount, RemainingAmount) VALUES
    ('INV001', 'INV2025001', 'P001', '2025-05-01 09:00:00', '2025-05-08 09:00:00', 'CONSULTATION', 'PENDING', 'U001', 500000, 0, 500000),
    ('INV002', 'INV2025002', 'P002', '2025-05-02 10:00:00', '2025-05-09 10:00:00', 'MEDICATION', 'PARTIALLY_PAID', 'U002', 300000, 100000, 200000),
    ('INV003', 'INV2025003', 'P003', '2025-05-03 11:00:00', '2025-05-10 11:00:00', 'ROOM_CHARGE', 'PAID', 'U003', 1000000, 1000000, 0),
    ('INV004', 'INV2025004', 'P004', '2025-05-04 14:00:00', '2025-05-11 14:00:00', 'PROCEDURE', 'PENDING', 'U001', 800000, 0, 800000),
    ('INV005', 'INV2025005', 'P005', '2025-05-05 15:00:00', '2025-05-12 15:00:00', 'SERVICE', 'PARTIALLY_PAID', 'U002', 600000, 200000, 400000);

-- 15. InvoiceDetails (5 ban ghi mau, lien ket voi Invoices va UserAccounts)
INSERT INTO InvoiceDetails (DetailID, InvoiceID, ServiceName, ServiceCode, Description, Quantity, UnitPrice, TotalPrice, DiscountAmount, FinalPrice, Category, CreatedAt, CreatedBy) VALUES
    ('DETINV001', 'INV001', 'Kham benh tong quat', 'KB01', 'Kham benh dinh ky', 1, 500000, 500000, 0, 500000, 'CONSULTATION', '2025-05-01 09:00:00', 'U001'),
    ('DETINV002', 'INV002', 'Thuoc khang sinh', 'TH01', 'Amoxicillin 10 ngay', 2, 150000, 300000, 0, 300000, 'MEDICATION', '2025-05-02 10:00:00', 'U002'),
    ('DETINV003', 'INV003', 'Thue phong VIP', 'RP01', 'Phong benh 2 ngay', 2, 500000, 1000000, 0, 1000000, 'ROOM_CHARGE', '2025-05-03 11:00:00', 'U003'),
    ('DETINV004', 'INV004', 'Phau thuat noi soi', 'PT01', 'Dieu tri soi than', 1, 800000, 800000, 0, 800000, 'PROCEDURE', '2025-05-04 14:00:00', 'U001'),
    ('DETINV005', 'INV005', 'Dich vu xet nghiem', 'XN01', 'Xet nghiem mau', 2, 300000, 600000, 0, 600000, 'SERVICE', '2025-05-05 15:00:00', 'U002');

-- 16. PaymentRecords (5 ban ghi mau, lien ket voi Invoices, Patients, va UserAccounts)
INSERT INTO PaymentRecords (PaymentID, InvoiceID, PaymentDate, Amount, PaymentMethod, PaidBy, ReceivedBy, Notes) VALUES
    ('PAY001', 'INV001', '2025-05-01 09:30:00', 50000, 'Tien mat', 'P001', 'U001', 'Da thanh toan mot phan'),
    ('PAY002', 'INV002', '2025-05-02 10:30:00', 100000, 'Chuyen khoan', 'P002', 'U002', 'Da thanh toan mot phan'),
    ('PAY003', 'INV003', '2025-05-03 11:30:00', 1000000, 'Tien mat', 'P003', 'U003', 'Da thanh toan day du'),
    ('PAY004', 'INV004', '2025-05-04 14:30:00', 150000, 'Chuyen khoan', 'P004', 'U001', 'Da thanh toan mot phan'),
    ('PAY005', 'INV005', '2025-05-05 15:30:00', 200000, 'Tien mat', 'P005', 'U002', 'Da thanh toan mot phan');