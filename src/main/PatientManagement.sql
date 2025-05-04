-- Xóa và tạo lại cơ sở dữ liệu PatientManagement với collation utf8mb4
DROP DATABASE IF EXISTS PatientManagement;
CREATE DATABASE IF NOT EXISTS PatientManagement
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE PatientManagement;

-- Xóa các bảng nếu tồn tại
DROP TABLE IF EXISTS VitalSigns;
DROP TABLE IF EXISTS MedicalRecords;
DROP TABLE IF EXISTS Appointments;
DROP TABLE IF EXISTS Admissions;
DROP TABLE IF EXISTS HospitalRooms;
DROP TABLE IF EXISTS Insurance;
DROP TABLE IF EXISTS Patients;
DROP TABLE IF EXISTS UserAccounts;
DROP TABLE IF EXISTS VitalSigns;
DROP TABLE IF EXISTS PrescriptionDetails;
DROP TABLE IF EXISTS Prescriptions;

-- Bảng UserAccounts (lưu thông tin tài khoản người dùng: bác sĩ, bệnh nhân)
CREATE TABLE UserAccounts (
    UserID VARCHAR(50) PRIMARY KEY,
    UserName VARCHAR(50) UNIQUE NOT NULL,
    FullName VARCHAR(100) NOT NULL,
    ALTER TABLE UserAccounts MODIFY COLUMN Role ENUM('DOCTOR', 'PATIENT') NOT NULL;,
    Email VARCHAR(100) UNIQUE,
    PhoneNumber VARCHAR(15) UNIQUE,
    Password VARCHAR(255) NOT NULL,
    Note VARCHAR(255) DEFAULT '',
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Bảng Patients (lưu thông tin bệnh nhân)
CREATE TABLE Patients (
    PatientID VARCHAR(50) PRIMARY KEY,
    UserID VARCHAR(50) UNIQUE NOT NULL,
    FullName VARCHAR(255) NOT NULL,
    DateOfBirth DATE NOT NULL,
    Gender ENUM('Nam', 'Nu') NOT NULL,
    PhoneNumber VARCHAR(20),
    Address TEXT,
    IllnessInfo TEXT,
    CreatedAt DATE, -- Ngày nhập viện
    Height DOUBLE,
    Weight DOUBLE,
    BloodType VARCHAR(10),
    FOREIGN KEY (UserID) REFERENCES UserAccounts(UserID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Bảng Insurance (lưu thông tin bảo hiểm, chỉ hỗ trợ BHYT)
CREATE TABLE Insurance (
    InsuranceID VARCHAR(20) PRIMARY KEY,
    PatientID VARCHAR(50),
    Provider ENUM('BHYT') NOT NULL,
    PolicyNumber VARCHAR(100) UNIQUE NOT NULL,
    StartDate DATE NOT NULL,
    ExpirationDate DATE NOT NULL,
    CoverageDetails TEXT,
    Status ENUM('Hoat Dong', 'Het Han', 'Khong Xac Dinh') DEFAULT 'Hoat Dong',
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID),
    CHECK (StartDate < ExpirationDate)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Bảng HospitalRooms (lưu thông tin phòng bệnh)
CREATE TABLE HospitalRooms (
    RoomID VARCHAR(20) PRIMARY KEY,
    RoomType ENUM('Tieu chuan', 'VIP', 'ICU', 'Cap cuu') NOT NULL,
    TotalBeds INT CHECK (TotalBeds > 0),
    AvailableBeds INT CHECK (AvailableBeds >= 0),
    FloorNumber INT CHECK (FloorNumber > 0),
    Status ENUM('Trong', 'Dang su dung', 'Day', 'Bao tri') DEFAULT 'Trong'
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Bảng Admissions (lưu thông tin nhập viện, liên kết bệnh nhân với phòng)
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

-- Bảng Appointments (lưu thông tin lịch hẹn)
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

-- Bảng MedicalRecords (lưu hồ sơ y tế)
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

-- Tạo bảng VitalSigns
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

-- Tạo bảng Prescriptions
CREATE TABLE Prescriptions (
    PrescriptionID VARCHAR(50) PRIMARY KEY,
    PatientID VARCHAR(50),
    DoctorID VARCHAR(50),
    PrescriptionDate DATE NOT NULL,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID),
    FOREIGN KEY (DoctorID) REFERENCES UserAccounts(UserID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Tạo bảng PrescriptionDetails
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

-- Chèn dữ liệu mẫu
-- 1. UserAccounts (8 bản ghi: 5 bác sĩ, 3 bệnh nhân)
-- Xóa dữ liệu cũ
DELETE FROM UserAccounts;

-- Chèn lại dữ liệu với Role đã sửa
INSERT INTO UserAccounts (UserID, UserName, FullName, Role, Email, PhoneNumber, Password, Note) VALUES
    ('U001', 'doctor1', 'Nguyen Van An', 'DOCTOR', 'nguyenvanan@example.com', '0987654321', 'admin123', 'Bac si chuyen khoa noi'),
    ('U002', 'doctor2', 'Tran Thi Binh', 'DOCTOR', 'tranthibinh@example.com', '0976543210', 'admin123', 'Bac si chuyen khoa ngoai'),
    ('U003', 'doctor3', 'Le Van Cuong', 'DOCTOR', 'levancuong@example.com', '0912345678', 'admin123', 'Bac si chuyen khoa tim mach'),
    ('U004', 'doctor4', 'Pham Thi Dung', 'DOCTOR', 'phamthidung@example.com', '0908765432', 'admin123', 'Bac si chuyen khoa nhi'),
    ('U005', 'doctor5', 'Hoang Van Em', 'DOCTOR', 'hoangvanem@example.com', '0933456789', 'admin123', 'Bac si chuyen khoa than kinh'),
    ('U006', 'patient1', 'Nguyen Thi Hoa', 'PATIENT', 'nguyenthihoa@example.com', '0945678901', 'patient123', 'Benh nhan tai kham'),
    ('U007', 'patient2', 'Tran Van Khai', 'PATIENT', 'tranvankhai@example.com', '0956789012', 'patient123', 'Can theo doi huyet ap'),
    ('U008', 'patient3', 'Le Thi Lan', 'PATIENT', 'lethilan@example.com', '0967890123', 'patient123', 'Benh nhan tieu hoa');
-- 2. Patients (5 bản ghi, liên kết với UserAccounts)
INSERT INTO Patients (PatientID, UserID, FullName, DateOfBirth, Gender, PhoneNumber, Address, IllnessInfo, CreatedAt, Height, Weight, BloodType) VALUES
    ('P001', 'U006', 'Nguyen Thi Hoa', '1992-07-10', 'Nu', '0945678901', '789 Duong Cau Giay, Ha Noi', 'Tang huyet ap', '2025-04-03', 160.0, 50.0, 'O+'),
    ('P002', 'U007', 'Tran Van Khai', '1988-11-25', 'Nam', '0956789012', '101 Duong Kim Ma, Ha Noi', 'Viem da day', '2025-04-04', 175.0, 75.0, 'AB+'),
    ('P003', 'U008', 'Le Thi Lan', '1995-09-30', 'Nu', '0967890123', '202 Duong Nguyen Trai, Ha Noi', 'Soi than', '2025-04-05', 158.0, 48.0, 'A-');

-- 3. Insurance (3 bản ghi, chỉ BHYT, liên kết với Patients)
INSERT INTO Insurance (InsuranceID, PatientID, Provider, PolicyNumber, StartDate, ExpirationDate, Status) VALUES
    ('I001', 'P001', 'BHYT', 'BH789123', '2024-03-01', '2026-03-01', 'Hoat Dong'),
    ('I002', 'P002', 'BHYT', 'BH456789', '2024-04-01', '2026-04-01', 'Hoat Dong'),
    ('I003', 'P003', 'BHYT', 'BH321987', '2024-05-01', '2026-05-01', 'Hoat Dong');

-- 4. HospitalRooms (5 bản ghi, bao gồm các loại phòng khác nhau)
INSERT INTO HospitalRooms (RoomID, RoomType, TotalBeds, AvailableBeds, FloorNumber, Status) VALUES
    ('R001', 'Tieu chuan', 4, 4, 1, 'Trong'),
    ('R002', 'VIP', 2, 2, 2, 'Trong'),
    ('R003', 'ICU', 3, 3, 3, 'Trong'),
    ('R004', 'Cap cuu', 5, 5, 1, 'Trong'),
    ('R005', 'Tieu chuan', 4, 4, 2, 'Trong');

-- 5. Admissions (3 bản ghi, liên kết với Patients, UserAccounts, và HospitalRooms)
INSERT INTO Admissions (AdmissionID, PatientID, DoctorID, RoomID, AdmissionDate, Notes) VALUES
    ('AD001', 'P001', 'U001', 'R003', '2025-04-03', 'Chuyen ICU'),
    ('AD002', 'P002', 'U001', 'R004', '2025-04-04', 'Cap cuu khan cap'),
    ('AD003', 'P003', 'U002', 'R005', '2025-04-05', 'Phong tieu chuan');

-- 6. Appointments (5 bản ghi, liên kết với Patients và UserAccounts)
INSERT INTO Appointments (AppointmentID, PatientID, DoctorID, AppointmentDate, Reason, Status) VALUES
    ('AP001', 'P001', 'U001', '2025-04-30 09:00:00', 'Kham dinh ky', 'Da xac nhan'),
    ('AP002', 'P002', 'U002', '2025-04-30 10:00:00', 'Tai kham', 'Cho xac nhan'),
    ('AP003', 'P001', 'U003', '2025-04-30 11:00:00', 'Kham tim mach', 'Da xac nhan'),
    ('AP004', 'P002', 'U001', '2025-04-30 14:00:00', 'Kiem tra huyet ap', 'Cho xac nhan'),
    ('AP005', 'P003', 'U002', '2025-04-30 15:00:00', 'Kham tieu hoa', 'Da xac nhan');

-- 7. MedicalRecords (5 bản ghi, liên kết với Patients và UserAccounts)
INSERT INTO MedicalRecords (RecordID, PatientID, DoctorID, Diagnosis, Treatment, RecordDate, Notes, LifestyleRecommendations) VALUES
    ('MR001', 'P001', 'U001', 'Tang huyet ap', 'Thuoc ha ap', '2025-04-03', 'Theo doi huyet ap hang ngay', 'Hạn chế muối, tập yoga'),
    ('MR002', 'P002', 'U001', 'Viem da day', 'Thuoc bao ve niem mac', '2025-04-04', 'Tai kham sau 2 tuan', 'Ăn nhạt, tránh đồ chiên rán'),
    ('MR003', 'P003', 'U002', 'Soi than', 'Phau thuat noi soi', '2025-04-05', 'Uong nhieu nuoc', 'Uống đủ nước, tránh đứng lâu');

-- 8. VitalSigns
INSERT INTO VitalSigns (Id, PatientId, Temperature, SystolicBP, DiastolicBP, HeartRate, SpO2, RecordTime) VALUES
('VS-001', 'P001', 36.8, 120, 80, 75, 98, '2025-05-04 10:00:00'),
('VS-002', 'P002', 38.5, 125, 85, 110, 96, '2025-05-04 08:00:00'),
('VS-003', 'P003', 36.9, 150, 95, 85, 97, '2025-05-03 10:00:00');

-- 9. Prescriptions
INSERT INTO Prescriptions (PrescriptionID, PatientID, DoctorID, PrescriptionDate) VALUES
('PRE-001', 'P001', 'U001', '2025-05-04'),
('PRE-002', 'P002', 'U002', '2025-04-27'),
('PRE-003', 'P003', 'U001', '2025-05-01');

-- 10. PrescriptionDetails
INSERT INTO PrescriptionDetails (DetailID, PrescriptionID, MedicationID, Dosage, Instructions, Price, Quantity) VALUES
-- PRE-001
('DET-001', 'PRE-001', 'MED-001', '500mg', 'Uống 1 viên mỗi 6 giờ', 2000, 20),
('DET-002', 'PRE-001', 'MED-002', '250mg', 'Uống 1 viên sau ăn, ngày 3 lần', 3000, 30),
('DET-003', 'PRE-001', 'MED-005', '10mg', 'Uống 1 viên trước khi ngủ', 1500, 15),
-- PRE-002
('DET-004', 'PRE-002', 'MED-003', '1000mg', 'Uống 1 viên mỗi ngày', 5000, 10),
('DET-005', 'PRE-002', 'MED-004', '20mg', 'Uống 1 viên trước ăn sáng', 4000, 14),
('DET-006', 'PRE-002', 'MED-006', '5mg', 'Uống 1 viên mỗi tối', 2500, 20),
-- PRE-003
('DET-007', 'PRE-003', 'MED-007', '200mg', 'Uống 2 viên mỗi 8 giờ', 1800, 24),
('DET-008', 'PRE-003', 'MED-008', '50mg', 'Uống 1 viên sau ăn trưa', 2200, 10),
('DET-009', 'PRE-003', 'MED-001', '500mg', 'Uống 1 viên mỗi 6 giờ', 2000, 15);

UPDATE UserAccounts SET Role = 'DOCTOR' WHERE Role = 'Bac si';
UPDATE UserAccounts SET Role = 'PATIENT' WHERE Role = 'Benh nhan';