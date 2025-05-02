DROP DATABASE IF EXISTS PatientManagement;
CREATE DATABASE IF NOT EXISTS PatientManagement;
USE PatientManagement;

-- Tiếp theo là DROP TABLE IF EXISTS (nếu có)
DROP TABLE IF EXISTS MedicalRecords;
DROP TABLE IF EXISTS Appointments;
DROP TABLE IF EXISTS Admissions;
DROP TABLE IF EXISTS HospitalRooms;
DROP TABLE IF EXISTS Insurance;
DROP TABLE IF EXISTS Patients;
DROP TABLE IF EXISTS UserAccounts;

-- Và sau đó là các câu lệnh CREATE TABLE...

-- Bang UserAccounts (luu thong tin tai khoan nguoi dung: bac si, benh nhan)
CREATE TABLE UserAccounts (
    UserID VARCHAR(50) PRIMARY KEY,
    UserName VARCHAR(50) UNIQUE NOT NULL,
    FullName VARCHAR(100) NOT NULL,
    Role ENUM('Bac si', 'Benh nhan') NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    PhoneNumber VARCHAR(15) UNIQUE,
    Password VARCHAR(255) NOT NULL,
    Note VARCHAR(255) DEFAULT '', -- Them cot Note
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
    IllnessInfo TEXT, -- Them cot IllnessInfo
    CreatedAt DATE, -- Ngay nhap vien
    FOREIGN KEY (UserID) REFERENCES UserAccounts(UserID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Bang Insurance (luu thong tin bao hiem, chi ho tro BHYT)
CREATE TABLE Insurance (
    InsuranceID VARCHAR(20) PRIMARY KEY,
    PatientID VARCHAR(50),
    Provider ENUM('BHYT') NOT NULL, -- Chi ho tro BHYT
    PolicyNumber VARCHAR(100) UNIQUE NOT NULL,
    StartDate DATE NOT NULL,
    ExpirationDate DATE NOT NULL,
    CoverageDetails TEXT,
    Status ENUM('Hoat Dong', 'Het Han', 'Khong Xac Dinh') DEFAULT 'Hoat Dong',
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID),
    CHECK (StartDate < ExpirationDate)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Bang HospitalRooms (luu thong tin phong benh)
CREATE TABLE HospitalRooms (
    RoomID VARCHAR(20) PRIMARY KEY,
    RoomType ENUM('Tieu chuan', 'VIP', 'ICU', 'Cap cuu') NOT NULL,
    TotalBeds INT CHECK (TotalBeds > 0),
    AvailableBeds INT CHECK (AvailableBeds >= 0),
    FloorNumber INT CHECK (FloorNumber > 0),
    Status ENUM('Trong', 'Dang su dung', 'Day', 'Bao tri') DEFAULT 'Trong'
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Bang Admissions (luu thong tin nhap vien, lien ket benh nhan voi phong)
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
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID),
    FOREIGN KEY (DoctorID) REFERENCES UserAccounts(UserID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Su dung co so du lieu
USE PatientManagement;

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
INSERT INTO Patients (PatientID, UserID, FullName, DateOfBirth, Gender, PhoneNumber, Address, IllnessInfo, CreatedAt) VALUES
    ('P001', 'U004', 'Pham Thi Dung', '1990-05-15', 'Nu', '0908765432', '123 Duong Lang, Ha Noi', 'Viem phoi nhe', '2025-04-01'),
    ('P002', 'U005', 'Hoang Van Em', '1985-03-20', 'Nam', '0933456789', '456 Duong Giai Phong, Ha Noi', 'Tieu duong type 2', '2025-04-02'),
    ('P003', 'U006', 'Nguyen Thi Hoa', '1992-07-10', 'Nu', '0945678901', '789 Duong Cau Giay, Ha Noi', 'Tang huyet ap', '2025-04-03'),
    ('P004', 'U007', 'Tran Van Khai', '1988-11-25', 'Nam', '0956789012', '101 Duong Kim Ma, Ha Noi', 'Viem da day', '2025-04-04'),
    ('P005', 'U008', 'Le Thi Lan', '1995-09-30', 'Nu', '0967890123', '202 Duong Nguyen Trai, Ha Noi', 'Soi than', '2025-04-05');

-- 3. Insurance (5 ban ghi, chi BHYT, lien ket voi Patients)
INSERT INTO Insurance (InsuranceID, PatientID, Provider, PolicyNumber, StartDate, ExpirationDate, Status) VALUES
    ('I001', 'P001', 'BHYT', 'BH123456', '2024-01-01', '2026-01-01', 'Hoat Dong'),
    ('I002', 'P002', 'BHYT', 'BH654321', '2024-02-01', '2026-02-01', 'Hoat Dong'),
    ('I003', 'P003', 'BHYT', 'BH789123', '2024-03-01', '2026-03-01', 'Hoat Dong'),
    ('I004', 'P004', 'BHYT', 'BH456789', '2024-04-01', '2026-04-01', 'Hoat Dong'),
    ('I005', 'P005', 'BHYT', 'BH321987', '2024-05-01', '2026-05-01', 'Hoat Dong');

-- 4. HospitalRooms (5 ban ghi, bao gom cac loai phong khac nhau)
INSERT INTO HospitalRooms (RoomID, RoomType, TotalBeds, AvailableBeds, FloorNumber, Status) VALUES
    ('R001', 'Tieu chuan', 4, 4, 1, 'Trong'),
    ('R002', 'VIP', 2, 2, 2, 'Trong'),
    ('R003', 'ICU', 3, 3, 3, 'Trong'),
    ('R004', 'Cap cuu', 5, 5, 1, 'Trong'),
    ('R005', 'Tieu chuan', 4, 4, 2, 'Trong');

-- 5. Admissions (5 ban ghi, lien ket voi Patients, UserAccounts, va HospitalRooms)
INSERT INTO Admissions (AdmissionID, PatientID, DoctorID, RoomID, AdmissionDate, Notes) VALUES
    ('AD001', 'P001', 'U001', 'R002', '2025-04-01', 'Phong VIP theo yeu cau'),
    ('AD002', 'P002', 'U002', 'R001', '2025-04-02', 'Phong tieu chuan'),
    ('AD003', 'P003', 'U003', 'R003', '2025-04-03', 'Chuyen ICU'),
    ('AD004', 'P004', 'U001', 'R004', '2025-04-04', 'Cap cuu khan cap'),
    ('AD005', 'P005', 'U002', 'R005', '2025-04-05', 'Phong tieu chuan');

-- 6. Appointments (5 ban ghi, lien ket voi Patients va UserAccounts)
INSERT INTO Appointments (AppointmentID, PatientID, DoctorID, AppointmentDate, Reason, Status) VALUES
    ('AP001', 'P001', 'U001', '2025-04-30 09:00:00', 'Kham dinh ky', 'Da xac nhan'),
    ('AP002', 'P002', 'U002', '2025-04-30 10:00:00', 'Tai kham', 'Cho xac nhan'),
    ('AP003', 'P003', 'U003', '2025-04-30 11:00:00', 'Kham tim mach', 'Da xac nhan'),
    ('AP004', 'P004', 'U001', '2025-04-30 14:00:00', 'Kiem tra huyet ap', 'Cho xac nhan'),
    ('AP005', 'P005', 'U002', '2025-04-30 15:00:00', 'Kham tieu hoa', 'Da xac nhan');

-- 7. MedicalRecords (5 ban ghi, lien ket voi Patients va UserAccounts)
INSERT INTO MedicalRecords (RecordID, PatientID, DoctorID, Diagnosis, Treatment, RecordDate, Notes) VALUES
    ('MR001', 'P001', 'U001', 'Viem phoi', 'Khang sinh', '2025-04-01', 'Can theo doi sat'),
    ('MR002', 'P002', 'U002', 'Tieu duong', 'Insulin', '2025-04-02', 'Kiem tra duong huyet dinh ky'),
    ('MR003', 'P003', 'U003', 'Tang huyet ap', 'Thuoc ha ap', '2025-04-03', 'Theo doi huyet ap hang ngay'),
    ('MR004', 'P004', 'U001', 'Viem da day', 'Thuoc bao ve niem mac', '2025-04-04', 'Tai kham sau 2 tuan'),
    ('MR005', 'P005', 'U002', 'Soi than', 'Phau thuat noi soi', '2025-04-05', 'Uong nhieu nuoc');