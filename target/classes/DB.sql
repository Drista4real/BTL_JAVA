-- XÓA & TẠO MỚI CƠ SỞ DỮ LIỆU
DROP DATABASE IF EXISTS phongkhamnhakhoa;
CREATE DATABASE phongkhamnhakhoa;
USE phongkhamnhakhoa;

-- NHÂN VIÊN
CREATE TABLE Employee (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    birthDate DATE,
    address VARCHAR(255),
    gender INT,
    phoneNumber VARCHAR(15) UNIQUE,
    idCard VARCHAR(12) UNIQUE NULL,
    username VARCHAR(50) UNIQUE NULL,
    password VARCHAR(255),
    salary DOUBLE,
    experienceYears INT,
    role ENUM('Lễ tân', 'Bác sĩ', 'Nhân viên quầy thuốc','Admin')
);

-- BÁC SĨ
CREATE TABLE Doctor (
    id INT PRIMARY KEY,
    specialty VARCHAR(255),
    FOREIGN KEY (id) REFERENCES Employee(id) ON DELETE CASCADE
);

-- BỆNH NHÂN
CREATE TABLE Patient (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    birthDate DATE,
    address VARCHAR(255),
    gender INT,
    phoneNumber VARCHAR(15) UNIQUE,
    idCard VARCHAR(12) UNIQUE
);

-- THUỐC
CREATE TABLE Drug (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DOUBLE,
    stockQuantity INT
);

-- DỊCH VỤ
CREATE TABLE Service (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DOUBLE
);

-- ĐƠN THUỐC
CREATE TABLE Prescription (
    id INT AUTO_INCREMENT PRIMARY KEY,
    doctor_id INT,
    patient_id INT,
    diagnosis TEXT,
    treatment TEXT,
    symptom TEXT,
    advice TEXT,
    preDate DATETIME,
    paymentStatus ENUM('Đã thanh toán', 'Chưa thanh toán', 'Đang xử lý') DEFAULT 'Chưa thanh toán',
    FOREIGN KEY (doctor_id) REFERENCES Doctor(id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES Patient(id) ON DELETE CASCADE
);

-- CHI TIẾT THUỐC TRONG ĐƠN
CREATE TABLE PrescriptionDrugDetail (
    prescription_id INT,
    drug_id INT,
    quantity INT,
    morningDose TINYINT DEFAULT 1 CHECK (morningDose BETWEEN 0 AND 2),
    noonDose TINYINT DEFAULT 1 CHECK (noonDose BETWEEN 0 AND 2),
    eveningDose TINYINT DEFAULT 1 CHECK (eveningDose BETWEEN 0 AND 2),
    PRIMARY KEY (prescription_id, drug_id),
    FOREIGN KEY (prescription_id) REFERENCES Prescription(id) ON DELETE CASCADE,
    FOREIGN KEY (drug_id) REFERENCES Drug(id) ON DELETE CASCADE
);

-- CHI TIẾT DỊCH VỤ TRONG ĐƠN
CREATE TABLE PrescriptionServiceDetail (
    prescription_id INT,
    service_id INT,
    quantity INT DEFAULT 1 CHECK (quantity >= 1),
    PRIMARY KEY (prescription_id, service_id),
    FOREIGN KEY (prescription_id) REFERENCES Prescription(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES Service(id) ON DELETE CASCADE
);


-- CUỘC KHÁM
CREATE TABLE Examination (
    id INT AUTO_INCREMENT PRIMARY KEY,
    doctor_id INT,
    patient_id INT,
    status ENUM('Chưa khám', 'Đã khám') DEFAULT 'Chưa khám',
    FOREIGN KEY (doctor_id) REFERENCES Doctor(id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES Patient(id) ON DELETE CASCADE
);

-- DỮ LIỆU MẪU CHO NHÂN VIÊN
INSERT INTO Employee (name, birthDate, address, gender, phoneNumber, idCard, username, password, salary, experienceYears, role) VALUES
('Nguyễn Hữu Tín', '1980-05-12', 'Hà Nội', 1, '0912345678', '123456789012', 'bacsia', 'password', 30000000, 15, 'Bác sĩ'),
('Trần Thị Trâm', '1985-08-25', 'Hồ Chí Minh', 0, '0912345679', '123456789013', 'bacsib', 'password', 32000000, 12, 'Bác sĩ'),
('Lê Văn Nam', '1975-12-05', 'Đà Nẵng', 1, '0912345680', '123456789014', 'bacsic', 'password', 31000000, 20, 'Bác sĩ'),
('Phạm Hồng Kim', '1990-07-15', 'Hải Phòng', 1, '0912345681', '123456789015', 'bacsid', 'password', 29000000, 10, 'Bác sĩ'),
('Hoàng Minh', '1982-04-10', 'Cần Thơ', 1, '0912345682', '123456789016', 'bacsie', 'password', 33000000, 18, 'Bác sĩ'),
('Nguyễn Thị Hà', '1995-09-20', 'Huế', 0, '0912345683', '123456789017', 'letan1', 'password', 12000000, 5, 'Lễ tân'),
('Đặng Thùy Y', '1998-03-11', 'Nha Trang', 0, '0912345684', '123456789018', 'letan2', 'password', 12500000, 3, 'Lễ tân'),
('Đỗ Hồng Dư', '1990-11-30', 'Vũng Tàu', 1, '0912345685', '123456789019', 'nvqt1', 'password', 15000000, 8, 'Nhân viên quầy thuốc'),
('Võ Thị H', '1992-06-18', 'Buôn Ma Thuột', 0, '0912345686', '123456789020', 'nvqt2', 'password', 15500000, 7, 'Nhân viên quầy thuốc'),
('Lương Minh Khôi', '1997-01-25', 'Đồng Nai', 1, '0912345687', '123456789021', 'admin', 'admin', 14000000, 6, 'Admin');

-- DỮ LIỆU BÁC SĨ
INSERT INTO Doctor (id, specialty) VALUES
(1, 'Răng hàm mặt'),
(2, 'Chỉnh nha'),
(3, 'Nha chu'),
(4, 'Nội nha'),
(5, 'Phẫu thuật miệng');

-- DỮ LIỆU BỆNH NHÂN
INSERT INTO Patient (name, birthDate, address, gender, phoneNumber, idCard) VALUES
('Trần Mỹ Trinh', '1990-05-12', 'Hà Nội', 0, '0987654321', '234567890123'),
('Thôi Kiêm Việt', '1985-08-25', 'Hồ Chí Minh', 1, '0987654322', '234567890124'),
('Vũ Tấn Vinh', '1985-08-25', 'Hồ Chí Minh', 1, '0987654325', '234567890127'),
('Lê Như Ý ', '1980-01-10', 'Đà Nẵng', 0, '0987654323', '234567890125'),
('Cao Thị Yến ', '1982-08-25', 'Bình Dương', 0, '0987655580', '234567890126');

-- DỮ LIỆU THUỐC
INSERT INTO Drug (name, description, price, stockQuantity) VALUES
('Paracetamol', 'Giảm đau, hạ sốt', 500, 100),
('Amoxicillin', 'Kháng sinh', 2000, 50),
('Erythromycin', 'Kháng sinh nhóm macrolid', 1800, 90),
('Lidocaine', 'Thuốc gây tê', 90000, 50),
('Chlorhexidine', 'Dung dịch sát khuẩn miệng', 12000, 100),
('Dexamethasone', 'Chống viêm, giảm sưng', 2500, 75);

-- DỮ LIỆU DỊCH VỤ
INSERT INTO Service (name, price) VALUES
('Khám răng tổng quát', 500000),
('Trám răng sâu', 500000),
('Niềng răng', 15000000),
('Tẩy trắng răng', 3000000),
('Chữa viêm tủy', 800000),
('Nhổ răng', 1000000);

-- DỮ LIỆU ĐƠN THUỐC
INSERT INTO Prescription (doctor_id, patient_id, diagnosis, treatment, symptom, advice, preDate, paymentStatus) VALUES
(2, 2, 'Viêm tủy', 'Chữa tủy', 'Đau răng liên tục', 'Không ăn đồ ngọt', NOW(), 'Đã thanh toán'),
(3, 3, 'Răng xỉn màu', 'Tẩy trắng răng', 'Không đau', 'Tránh uống cà phê', NOW(), 'Chưa thanh toán'),
(1, 5, 'Mọc răng khôn', 'Khám tổng quát', 'Đau hàm dưới', 'Uống nước ấm, theo dõi thêm', NOW(), 'Chưa thanh toán');

-- CHI TIẾT THUỐC TRONG ĐƠN
INSERT INTO PrescriptionDrugDetail (prescription_id, drug_id, quantity, morningDose, noonDose, eveningDose) VALUES
(1, 1, 10, 1, 1, 1),
(1, 2, 5, 1, 1, 1),
(2, 2, 7, 1, 2, 1),
(2, 3, 6, 1, 1, 1),
(3, 3, 8, 2, 1, 1);

-- CHI TIẾT DỊCH VỤ TRONG ĐƠN
INSERT INTO PrescriptionServiceDetail (prescription_id, service_id, quantity) VALUES
(1, 2, 2),
(2, 1, 1), (2, 5, 1),
(3, 5, 1), (3, 4, 1);

-- DỮ LIỆU EXAMINATION (CÓ TRẠNG THÁI 'ĐÃ KHÁM')
INSERT INTO Examination (doctor_id, patient_id, status) VALUES
(1, 4, 'Chưa khám');


