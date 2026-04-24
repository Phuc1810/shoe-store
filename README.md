# Shoe Store

`shoe-store` là project Spring Boot cho website bán giày online của nhóm 2. Giai đoạn hiện tại chỉ dựng project, cấu trúc thư mục và các file khởi tạo để nhóm có thể chia việc rõ ràng.

## Công nghệ sử dụng

- Java 17
- Spring Boot
- Maven
- Thymeleaf
- Bootstrap
- Spring Web
- Spring Data JPA
- MySQL Driver
- Spring Security
- Validation
- Lombok
- DevTools

## Cấu trúc thư mục

```text
src/main/java/com/group2/shoestore
├── config
├── controller
│   ├── user
│   └── admin
├── entity
├── repository
├── service
│   ├── user
│   └── admin
├── dto
│   ├── request
│   └── response
├── constant
├── exception
└── util

src/main/resources
├── templates
│   ├── layout
│   ├── user
│   └── admin
├── static
│   ├── css
│   ├── js
│   └── images
└── application*.properties

docs
├── database
├── diagrams
└── report
```

## Chức năng dự kiến User

- Xem trang chủ
- Xem danh sách sản phẩm
- Xem chi tiết sản phẩm
- Thêm sản phẩm vào giỏ hàng
- Thanh toán đơn hàng
- Xem lịch sử đơn hàng
- Đăng ký, đăng nhập, đăng xuất

## Chức năng dự kiến Admin

- Xem dashboard quản trị
- Quản lý sản phẩm
- Quản lý danh mục
- Quản lý thương hiệu
- Quản lý đơn hàng

## Cấu hình database Aiven

Không hard-code thông tin database trong source code. Hãy cấu hình bằng biến môi trường:

```bash
DB_URL=jdbc:mysql://<host>:<port>/<database>?ssl-mode=REQUIRED
DB_USERNAME=<username>
DB_PASSWORD=<password>
```

Ví dụ trên PowerShell:

```powershell
$env:DB_URL="jdbc:mysql://<host>:<port>/<database>?ssl-mode=REQUIRED"
$env:DB_USERNAME="<username>"
$env:DB_PASSWORD="<password>"
```

File `application.properties` đã đọc các biến này:

```properties
spring.datasource.url=${DB_URL:}
spring.datasource.username=${DB_USERNAME:}
spring.datasource.password=${DB_PASSWORD:}
```

## Cách chạy project bằng Maven

Build project:

```bash
mvn clean compile
```

Chạy với database đã cấu hình:

```bash
mvn spring-boot:run
```

Chạy demo ban đầu khi chưa có database:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Sau khi chạy, có thể kiểm tra:

- `http://localhost:8080/`
- `http://localhost:8080/admin`

## Quy tắc làm việc nhóm

- Không push thẳng vào branch `main`.
- Tạo branch theo phạm vi công việc:
  - `feature/user-pages`
  - `feature/admin-pages`
  - `feature/database`
- Không commit password database, file `.env`, hoặc `application-local.properties`.
- User side code đặt trong `controller/user`, `service/user`, `templates/user`.
- Admin side code đặt trong `controller/admin`, `service/admin`, `templates/admin`.
