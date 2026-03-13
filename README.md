# 🗺️ SUT Navigator

แอปพลิเคชัน Desktop สำหรับนำทางภายในมหาวิทยาลัยเทคโนโลยีสุรนารี (SUT)  
พัฒนาด้วย Java Swing เชื่อมต่อกับฐานข้อมูล MySQL

---

## 📋 สารบัญ

- [ภาพรวมโปรเจค](#ภาพรวมโปรเจค)
- [ฟีเจอร์](#ฟีเจอร์)
- [โครงสร้างโปรเจค](#โครงสร้างโปรเจค)
- [ความต้องการของระบบ](#ความต้องการของระบบ)
- [การติดตั้งและการใช้งาน](#การติดตั้งและการใช้งาน)
- [การตั้งค่าฐานข้อมูล](#การตั้งค่าฐานข้อมูล)
- [โครงสร้างฐานข้อมูล](#โครงสร้างฐานข้อมูล)
- [ระบบ Cache](#ระบบ-cache)
- [บทบาทผู้ใช้](#บทบาทผู้ใช้)

---

## ภาพรวมโปรเจค

**SUT Navigator** คือแอปพลิเคชัน Java Desktop ที่ช่วยให้นักศึกษาและบุคลากรของ มทส.
สามารถค้นหาอาคาร ห้องเรียน และสถานที่ต่างๆ ภายในมหาวิทยาลัยได้สะดวก
พร้อมระบบแผนที่ภายในแอป และสามารถเปิด Google Maps เพื่อนำทางได้โดยตรง

---

## ฟีเจอร์

### 👤 ระบบผู้ใช้
| ฟีเจอร์ | รายละเอียด |
|---------|-----------|
| **เข้าสู่ระบบ** | Login ด้วย username หรือ email |
| **สมัครสมาชิก** | Register บัญชีใหม่ |
| **ลืมรหัสผ่าน** | Reset Password ผ่านหน้าแอป |
| **Auto Login** | จดจำการเข้าสู่ระบบอัตโนมัติ |
| **Guest Mode** | เข้าใช้งานแบบทดลองโดยไม่ต้อง login |
| **Logout** | ออกจากระบบและล้างข้อมูล session |

### 🏫 ข้อมูลสถานที่
| ฟีเจอร์ | รายละเอียด |
|---------|-----------|
| **ค้นหา** | ค้นหาอาคารและห้องเรียนด้วย keyword |
| **รายการอาคาร** | แสดงรายการอาคารทั้งหมดพร้อมหมวดหมู่ |
| **รายละเอียดอาคาร** | ดูข้อมูล ภาพ และห้องภายในอาคาร |
| **แผนที่** | แผนที่ภายในแอปพร้อม marker อาคาร |
| **เปิด Google Maps** | คลิกเพื่อนำทางผ่าน Google Maps |
| **Recommended** | แสดงอาคารยอดนิยม (Top 3) |

### 📊 รายงาน
- สถิติการเข้าชมอาคารแต่ละหมวดหมู่
- ข้อมูลยอดนิยม (Access Log)

### 🔧 Admin (สำหรับผู้ดูแล)
- เพิ่ม / แก้ไข / ลบ อาคาร
- เพิ่ม / แก้ไข / ลบ ห้องภายในอาคาร
- จัดการข้อมูลผู้ใช้ทั้งหมด
- หน้า Home และ List พิเศษสำหรับ Admin

---

## โครงสร้างโปรเจค

```
Sutnavigator/
├── src/
│   ├── MainFrame.java          # Entry point ของแอป
│   ├── Asset/                  # รูปภาพ icon และ logo
│   ├── Components/
│   │   ├── Components_.java    # Reusable UI components (Button, TextField, Label)
│   │   └── SideBar.java        # Header bar และ Navigation menu
│   ├── Data/
│   │   ├── DataAll.java        # Abstract class สำหรับข้อมูลสถานที่
│   │   ├── Building.java       # Model ข้อมูลอาคาร
│   │   └── Room.java           # Model ข้อมูลห้อง
│   ├── Home/
│   │   ├── Home.java           # หน้าหลัก (User)
│   │   └── HomeAdmin.java      # หน้าหลัก (Admin)
│   ├── Search/
│   │   └── Search.java         # หน้าค้นหา
│   ├── ListPage/
│   │   ├── ListPage.java       # รายการอาคาร (User)
│   │   └── AdminListPage.java  # รายการอาคาร + CRUD (Admin)
│   ├── Map/
│   │   ├── Map.java            # Widget แผนที่
│   │   ├── MapPage.java        # หน้าแผนที่เต็ม
│   │   ├── MapAdmin.java       # แผนที่สำหรับ Admin
│   │   ├── BuildingDetailPanel.java  # Panel รายละเอียดอาคาร
│   │   └── CoordinateBuilding.java   # ข้อมูล coordinate อาคาร
│   ├── ReportPage/
│   │   └── ReportPage.java     # หน้ารายงานสถิติ
│   ├── User/
│   │   ├── Datauser.java       # Data layer หลัก (DB + Cache)
│   │   ├── User.java           # Model ข้อมูลผู้ใช้งาน
│   │   ├── Data_user.java      # Model ข้อมูลผู้ใช้งาน (Admin view)
│   │   ├── Login.java          # หน้าเข้าสู่ระบบ
│   │   ├── Register.java       # หน้าสมัครสมาชิก
│   │   ├── Resetpassword.java  # หน้าตั้งรหัสผ่านใหม่
│   │   └── SearchResult.java   # Model ผลลัพธ์การค้นหา
│   └── lib/
│       └── mysql-connector-j-9.5.0.jar  # MySQL JDBC Driver
├── cache_data.txt              # Cache ข้อมูลอาคาร/ห้อง (auto-generated)
├── user.txt                    # Session ผู้ใช้ (auto-generated)
└── README.md
```

---

## ความต้องการของระบบ

| ส่วนประกอบ | รายละเอียด |
|-----------|-----------|
| **Java** | JDK 17 ขึ้นไป (ใช้ Switch Expression และ Text Block) |
| **IDE** | Eclipse IDE for Java Developers (แนะนำ) |
| **Database** | MySQL 8.0 ขึ้นไป |
| **JDBC Driver** | mysql-connector-j-9.5.0.jar (มีอยู่แล้วใน `src/lib/`) |
| **OS** | Windows / Linux / macOS |
| **RAM** | อย่างน้อย 512 MB |

---

## การติดตั้งและการใช้งาน

### 1. Clone / Download โปรเจค

```bash
git clone <repository-url>
```

### 2. เปิดด้วย Eclipse

1. เปิด Eclipse → `File` → `Import` → `Existing Projects into Workspace`
2. เลือกโฟลเดอร์ `Sutnavigator`
3. กด `Finish`

### 3. เพิ่ม JDBC Driver

1. คลิกขวาที่โปรเจค → `Build Path` → `Configure Build Path`
2. แท็บ `Libraries` → `Add JARs`
3. เลือกไฟล์ `src/lib/mysql-connector-j-9.5.0.jar`
4. กด `Apply and Close`

### 4. ตั้งค่า Database Connection

เปิดไฟล์ `src/User/Datauser.java` แล้วแก้ไขค่าต่อไปนี้:

```java
private static final String DB_URL =
    "jdbc:mysql://<HOST>:<PORT>/sut_navigator";

private static final String DB_USER = "<USERNAME>";

private static final String DB_PASS = "<PASSWORD>";
```

### 5. รันโปรแกรม

รันไฟล์ `MainFrame.java` เป็น Java Application

---

## การตั้งค่าฐานข้อมูล

สร้างฐานข้อมูลชื่อ `sut_navigator` แล้ว import schema ด้านล่าง:

```sql
CREATE DATABASE sut_navigator CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sut_navigator;

CREATE TABLE Categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL
);

CREATE TABLE Buildings (
    building_id  INT PRIMARY KEY AUTO_INCREMENT,
    name         VARCHAR(200) NOT NULL,
    description  TEXT,
    category_id  INT,
    image_path   VARCHAR(500),
    map_x        DOUBLE,
    map_y        DOUBLE,
    FOREIGN KEY (category_id) REFERENCES Categories(category_id)
);

CREATE TABLE Rooms (
    room_id     INT PRIMARY KEY AUTO_INCREMENT,
    room_name   VARCHAR(200) NOT NULL,
    floor_id    VARCHAR(20),
    detail      TEXT,
    building_id INT,
    FOREIGN KEY (building_id) REFERENCES Buildings(building_id) ON DELETE CASCADE
);

CREATE TABLE Users (
    user_id       INT PRIMARY KEY AUTO_INCREMENT,
    username      VARCHAR(100) UNIQUE NOT NULL,
    email         VARCHAR(200) UNIQUE NOT NULL,
    password      VARCHAR(255) NOT NULL,
    role          ENUM('user', 'admin') DEFAULT 'user',
    registered_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE AccessLogs (
    log_id      INT PRIMARY KEY AUTO_INCREMENT,
    building_id INT,
    user_id     INT NULL,
    accessed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (building_id) REFERENCES Buildings(building_id),
    FOREIGN KEY (user_id)     REFERENCES Users(user_id)
);

-- สร้าง Admin Account
INSERT INTO Users (username, email, password, role)
VALUES ('admin', 'admin@sut.ac.th', 'admin1234', 'admin');
```

---

## โครงสร้างฐานข้อมูล

```
Categories ──<── Buildings ──<── Rooms
                      │
                      └──<── AccessLogs ──>── Users
```

---

## ระบบ Cache

โปรแกรมใช้ระบบ Cache 2 ชั้นเพื่อเพิ่มประสิทธิภาพ:

```
[MySQL Database]
       │  ทุก login หรือเมื่อ cache ว่าง
       ▼
[File Cache: cache_data.txt]
       │  โหลดเข้า RAM เมื่อเปิดโปรแกรม
       ▼
[Memory Cache: List<DataAll>]
       │  ใช้งานทันที (ไม่ต้องไป DB)
       ▼
[UI แสดงผล]
```

| สถานการณ์ | พฤติกรรม |
|----------|---------|
| เปิดโปรแกรม | โหลดจาก `cache_data.txt` ก่อน |
| ไม่มี cache file | ดึงข้อมูลจาก Database |
| Login สำเร็จ | Refresh cache ใหม่จาก Database |
| เพิ่ม/แก้ไข/ลบข้อมูล | อัปเดต Memory Cache และบันทึกไฟล์ทันที |

---

## บทบาทผู้ใช้

| Role | สิทธิ์การใช้งาน |
|------|----------------|
| **Guest** | ดูข้อมูล, ค้นหา, ดูแผนที่ (ไม่บันทึก log) |
| **User** | ทุกอย่างของ Guest + บันทึก Access Log |
| **Admin** | ทุกอย่างของ User + CRUD อาคาร/ห้อง + จัดการผู้ใช้ |

> **หมายเหตุ:** บัญชี Admin ต้องใช้ username `admin` เท่านั้น

---

## 📌 หมายเหตุ

- ไฟล์ `user.txt` และ `cache_data.txt` จะถูกสร้างอัตโนมัติเมื่อรันโปรแกรม ไม่ต้องสร้างเอง
- ฟอนต์ **Prompt Light** จะถูกดาวน์โหลดอัตโนมัติเมื่อไม่มีในเครื่อง
- หากไม่มีการเชื่อมต่ออินเทอร์เน็ต โปรแกรมจะใช้ข้อมูลจาก `cache_data.txt` แทน
