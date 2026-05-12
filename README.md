# 🏢 Hệ Thống Quản Lý Bất Động Sản (Building Management System)

## 📋 Mô Tả Dự Án

Hệ thống quản lý bất động sản là một ứng dụng web full-stack được xây dựng bằng **Spring Boot** và **Thymeleaf**, cho phép quản lý các tòa nhà, phân quyền nhân viên, quản lý đơn hàng và các chức năng liên quan đến bất động sản. Dự án áp dụng kiến trúc **MVC** (Model-View-Controller) và các best practices trong phát triển ứng dụng Spring Boot.

## ✨ Tính Năng Chính

### 🏗️ Quản Lý Tòa Nhà
- ✅ Thêm, sửa, xóa thông tin tòa nhà
- ✅ Tìm kiếm tòa nhà theo nhiều tiêu chí (tên, quận, loại tòa nhà, diện tích,...)
- ✅ Quản lý diện tích cho thuê (Rent Area)
- ✅ Hiển thị danh sách tòa nhà với phân trang
- ✅ Sắp xếp và lọc dữ liệu

### 👥 Quản Lý Người Dùng & Phân Quyền
- ✅ Đăng nhập, đăng xuất với Spring Security
- ✅ Phân quyền theo vai trò (ROLE_ADMIN, ROLE_STAFF)
- ✅ Quản lý nhân viên
- ✅ Gán tòa nhà cho nhân viên phụ trách

### 📦 Quản Lý Đơn Hàng
- ✅ Tạo, cập nhật đơn hàng
- ✅ Quản lý chi tiết đơn hàng (Order Details)
- ✅ Theo dõi trạng thái đơn hàng

### 🔐 Bảo Mật
- ✅ Authentication & Authorization với Spring Security
- ✅ Mã hóa mật khẩu
- ✅ Bảo vệ các endpoint theo vai trò người dùng

## 🛠️ Công Nghệ Sử Dụng

### Backend
- **Framework**: Spring Boot 3.4.12
- **Java**: Version 17
- **Spring Security**: Xác thực và phân quyền
- **Spring Data JPA**: Tương tác với cơ sở dữ liệu
- **Hibernate**: ORM framework
- **MySQL**: Cơ sở dữ liệu quan hệ

### Frontend
- **Thymeleaf**: Template engine
- **jQuery**: JavaScript library (3.7.1)
- **Bootstrap**: CSS framework (UI/UX)
- **HTML5/CSS3**: Giao diện người dùng

### Utilities & Libraries
- **Lombok**: Giảm boilerplate code
- **ModelMapper**: Mapping giữa Entity và DTO
- **Apache Commons Lang3**: Utility functions
- **Jakarta Validation**: Validation dữ liệu

### Build Tool
- **Maven**: Quản lý dependencies và build project

## 📁 Cấu Trúc Dự Án

```
Project-Java-Building/
└── Spring-boot-web-devon/
    ├── src/
    │   ├── main/
    │   │   ├── java/com/devon/building/
    │   │   │   ├── api/              # REST API Controllers
    │   │   │   ├── builder/          # Builder Pattern implementations
    │   │   │   ├── config/           # Configuration classes
    │   │   │   ├── constant/         # Constants
    │   │   │   ├── controller/       # MVC Controllers
    │   │   │   ├── convert/          # Converters (DTO <-> Entity)
    │   │   │   ├── entity/           # JPA Entities
    │   │   │   ├── enums/            # Enum types
    │   │   │   ├── exception/        # Custom exceptions
    │   │   │   ├── form/             # Form objects
    │   │   │   ├── model/            # DTOs (Data Transfer Objects)
    │   │   │   ├── pagination/       # Pagination utilities
    │   │   │   ├── repository/       # JPA Repositories
    │   │   │   ├── security/         # Security configuration
    │   │   │   ├── service/          # Business logic layer
    │   │   │   ├── utils/            # Utility classes
    │   │   │   └── validator/        # Custom validators
    │   │   └── resources/
    │   │       ├── templates/        # Thymeleaf templates
    │   │       │   ├── admin/        # Admin pages
    │   │       │   │   ├── building/ # Building management views
    │   │       │   │   └── user/     # User management views
    │   │       │   └── web/          # Public views
    │   │       └── application.properties
    │   └── test/                     # Unit tests
    └── pom.xml                       # Maven configuration
```

## 🗄️ Database Schema

Database: `estateadvance`

### Các Bảng Chính:
- **building**: Thông tin tòa nhà
- **rentarea**: Diện tích cho thuê
- **user**: Người dùng hệ thống
- **assignmentbuilding**: Phân công tòa nhà cho nhân viên
- **order**: Đơn hàng
- **orderdetail**: Chi tiết đơn hàng

## 🚀 Hướng Dẫn Cài Đặt

### Yêu Cầu Hệ Thống
- Java Development Kit (JDK) 17 trở lên
- MySQL Server 5.7+
- Maven 3.6+
- IDE: IntelliJ IDEA / Eclipse / VS Code

### Các Bước Cài Đặt

1. **Clone repository**
```bash
git clone https://github.com/your-username/Project-Java-Building.git
cd Project-Java-Building/Spring-boot-web-devon
```

2. **Cấu hình Database**
   
   Tạo database MySQL:
```sql
CREATE DATABASE estateadvance;
```

3. **Cấu hình file `application.properties`**
   
   Cập nhật thông tin kết nối database tại `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/estateadvance?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
```

4. **Import database**
   
   (Nếu có file SQL) Import các bảng và dữ liệu mẫu vào database

5. **Build project**
```bash
mvn clean install
```

6. **Chạy ứng dụng**
```bash
mvn spring-boot:run
```

7. **Truy cập ứng dụng**
   
   Mở trình duyệt và truy cập: `http://localhost:9095`

## 👤 Tài Khoản Mặc Định

```
Admin Account:
- Username: admin
- Password: [check database]

Staff Account:
- Username: staff
- Password: [check database]
```

## 🔧 Cấu Hình

### Application Port
- **Port**: 9095 (có thể thay đổi trong `application.properties`)

### JPA Configuration
- **Show SQL**: Enabled (để debug)
- **DDL Auto**: None (không tự động tạo/sửa schema)
- **Dialect**: MySQL

## 📝 API Endpoints

### Building Management
- `GET /admin/building-list` - Danh sách tòa nhà
- `GET /admin/building-edit` - Form thêm tòa nhà
- `GET /admin/building-edit-{id}` - Form sửa tòa nhà
- `POST /api/building` - Thêm/cập nhật tòa nhà
- `DELETE /api/building/{id}` - Xóa tòa nhà
- `POST /api/building/assignment` - Gán nhân viên cho tòa nhà

### User Management
- `GET /admin/user-list` - Danh sách người dùng
- `GET /admin/user-edit` - Form thêm người dùng
- `GET /admin/user-edit-{id}` - Form sửa người dùng
- `POST /api/user` - Thêm/cập nhật người dùng
- `DELETE /api/user/{ids}` - Xóa người dùng

## 🏗️ Kiến Trúc & Design Patterns

### Layered Architecture
1. **Presentation Layer** (Controller/API)
2. **Service Layer** (Business Logic)
3. **Repository Layer** (Data Access)
4. **Entity Layer** (Database Models)

### Design Patterns Sử Dụng
- **Builder Pattern**: Xây dựng query động
- **DTO Pattern**: Chuyển đổi dữ liệu giữa các layers
- **Repository Pattern**: Truy xuất dữ liệu
- **Dependency Injection**: Spring IoC Container
- **MVC Pattern**: Tổ chức code

## 🧪 Testing

Chạy unit tests:
```bash
mvn test
```

## 📚 Tài Liệu Tham Khảo

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Thymeleaf](https://www.thymeleaf.org/)

## 🤝 Đóng Góp

Mọi đóng góp đều được chào đón! Vui lòng:
1. Fork dự án
2. Tạo branch mới (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Mở Pull Request

## 📄 License

Dự án này được phát hành dưới giấy phép MIT License.

## 👨‍💻 Tác Giả

**Your Name**
- Email: your.email@example.com
- GitHub: [@your-username](https://github.com/your-username)

## 🙏 Lời Cảm Ơn

Cảm ơn đã quan tâm đến dự án này!

---

Phát triển với ❤️ bằng Spring Boot
