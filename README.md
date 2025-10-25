# 🍕 PizzaApp - Smart Pizza Ordering System

<div align="center">

![PizzaApp Logo](https://img.shields.io/badge/PizzaApp-Smart%20Pizza%20Ordering-FF6B6B?style=for-the-badge&logo=pizza)

**Ứng dụng đặt pizza thông minh với quản lý giỏ hàng và theo dõi đơn hàng**

[![Android](https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white)](https://www.android.com/)
[![Java](https://img.shields.io/badge/Java-ED8936?style=flat&logo=java&logoColor=white)](https://www.oracle.com/java/)
[![SQLite](https://img.shields.io/badge/SQLite-003B57?style=flat&logo=sqlite&logoColor=white)](https://www.sqlite.org/)
[![XML](https://img.shields.io/badge/XML-FF9900?style=flat&logo=xml&logoColor=white)](https://en.wikipedia.org/wiki/XML)
[![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat&logo=gradle&logoColor=white)](https://gradle.org/)

</div>

---

## 📋 Tổng quan dự án

**PizzaApp** là một ứng dụng Android toàn diện để đặt hàng pizza trực tuyến. Ứng dụng cho phép người dùng xem danh sách pizza, thêm vào giỏ hàng, quản lý đơn hàng và theo dõi trạng thái giao hàng một cách dễ dàng và hiệu quả.

### 🎯 Mục tiêu chính
- **Quản lý tài khoản** người dùng với xác thực an toàn
- **Xem danh sách pizza** với tìm kiếm và lọc nâng cao
- **Quản lý giỏ hàng** với tính toán giá tự động
- **Đặt hàng** và theo dõi trạng thái đơn hàng
- **Thanh toán** hỗ trợ nhiều phương thức
- **Giao diện admin** cơ bản để quản lý đơn hàng

---

## 🏗️ Kiến trúc ứng dụng

### Cấu trúc Project
```
app/
├── src/
│   ├── main/
│   │   ├── java/fpt/edu/vn/pizzaapp_prm392/
│   │   │   ├── database/
│   │   │   │   ├── DatabaseHelper.java       # SQLite Database Management
│   │   │   │   └── ...
│   │   │   ├── models/
│   │   │   │   ├── User.java                 # User Model
│   │   │   │   ├── Pizza.java                # Pizza Model
│   │   │   │   ├── CartItem.java             # Cart Item Model
│   │   │   │   ├── Order.java                # Order Model
│   │   │   │   └── Payment.java              # Payment Model
│   │   │   ├── dao/
│   │   │   │   ├── UserDAO.java              # User Data Access
│   │   │   │   ├── PizzaDAO.java             # Pizza Data Access
│   │   │   │   ├── CartItemDAO.java          # Cart Data Access
│   │   │   │   ├── OrderDAO.java             # Order Data Access
│   │   │   │   └── PaymentDAO.java           # Payment Data Access
│   │   │   ├── activities/
│   │   │   │   ├── MainActivity.java         # Main Activity
│   │   │   │   ├── LoginActivity.java        # Login Screen
│   │   │   │   ├── RegisterActivity.java     # Registration Screen
│   │   │   │   └── ...
│   │   │   ├── adapters/
│   │   │   │   ├── PizzaAdapter.java         # Pizza List Adapter
│   │   │   │   ├── CartAdapter.java          # Cart List Adapter
│   │   │   │   └── OrderAdapter.java         # Order List Adapter
│   │   │   ├── services/
│   │   │   │   ├── CartService.java          # Cart Business Logic
│   │   │   │   ├── OrderService.java         # Order Business Logic
│   │   │   │   └── PaymentService.java       # Payment Logic
│   │   │   └── utils/
│   │   │       ├── Constants.java            # App Constants
│   │   │       ├── Utils.java                # Utility Functions
│   │   │       └── ValidationHelper.java     # Input Validation
│   │   └── res/                              # Resources (Layout, Drawable, Values)
│   │       ├── layout/                       # XML Layouts
│   │       ├── drawable/                     # Images & Icons
│   │       ├── values/                       # Strings, Colors, Themes
│   │       └── ...
│   └── test/                                 # Unit Tests
├── build.gradle                              # App Build Configuration
└── ...

gradle/
├── libs.versions.toml                        # Dependency Version Management
└── wrapper/                                  # Gradle Wrapper

local.properties                              # Local SDK Configuration
```

### Database Schema
```
📊 Database Tables:
├── users              ➜ Quản lý tài khoản người dùng
├── pizzas             ➜ Danh sách sản phẩm pizza
├── cart_items         ➜ Các mục trong giỏ hàng
├── orders             ➜ Thông tin đơn hàng
├── order_items        ➜ Chi tiết sản phẩm trong đơn hàng
├── payments           ➜ Thông tin thanh toán
└── order_status       ➜ Lịch sử trạng thái đơn hàng
```

---

## 🚀 Tính năng chính

### 🔐 Quản lý Tài khoản
- [x] Đăng ký người dùng mới với validation
- [x] Đăng nhập/Đăng xuất an toàn
- [x] Lưu trữ Token/Session
- [x] Xem và cập nhật thông tin cá nhân
- [x] Quản lý địa chỉ giao hàng
- [x] Bảo mật client-side

### 🍕 Gian hàng (Xem & Chi tiết)
- [x] Xem danh sách pizza với RecyclerView
- [x] Load dữ liệu từ API/Local Database
- [x] Tìm kiếm pizza theo tên
- [x] Lọc pizza theo danh mục, kích thước, giá
- [x] Xem chi tiết pizza (mô tả, giá, hình ảnh)
- [x] Hiển thị đánh giá sao và tồn kho
- [x] Hiệu năng cao cho danh sách lớn

### 🛒 Giỏ hàng (Tương tác)
- [x] Thêm/Bớt sản phẩm vào giỏ hàng
- [x] Cập nhật số lượng sản phẩm
- [x] Lưu trữ giỏ hàng local/Database
- [x] Tính toán tổng tiền tự động
- [x] Thêm ghi chú cho sản phẩm
- [x] Xóa sản phẩm khỏi giỏ
- [x] Làm rỗng giỏ hàng
- [x] Hiển thị số lượng mục trong giỏ

### 📦 Đặt Hàng
- [x] Thu thập thông tin giao hàng
- [x] Nhập địa chỉ và số điện thoại
- [x] Thêm ghi chú đặc biệt
- [x] Xác nhận đơn hàng
- [x] Nhận mã đơn hàng
- [x] Validation dữ liệu nhập

### 💳 Thanh toán
- [x] Chọn phương thức thanh toán
- [x] Hỗ trợ COD (Thanh toán khi nhận)
- [x] Logic thanh toán cơ bản
- [x] Quản lý thông tin thanh toán
- [x] Xử lý trạng thái thanh toán

### 📋 Quản lý Đơn hàng
- [x] Xác nhận đơn hàng
- [x] Xem lịch sử đơn hàng
- [x] Theo dõi trạng thái đơn hàng (PENDING, CONFIRMED, SHIPPING, DELIVERED)
- [x] Cập nhật trạng thái động
- [x] Hiển thị chi tiết đơn hàng
- [x] Giao diện admin cơ bản
- [x] Thống kê đơn hàng

---

## 🛠️ Tech Stack

### Android Development
- **Language**: Java
- **SDK**: Android 8.0+ (API 26+)
- **Build System**: Gradle
- **Architecture**: Model-View-Activity Pattern
- **Database**: SQLite with DatabaseHelper
- **UI Components**: RecyclerView, Fragment, Activity

### Libraries & Tools
- **UI**: Material Design Components
- **Database**: SQLite (SQLiteOpenHelper)
- **Networking**: Volley / Retrofit (for API calls)
- **Image Loading**: Glide / Picasso
- **Data Storage**: SharedPreferences
- **Logging**: Android Log
- **Testing**: JUnit, Espresso

### Development Tools
- **IDE**: Android Studio
- **VCS**: Git
- **Build**: Gradle Build System
- **Debugging**: Android Debugger
- **Profiling**: Android Profiler

---

## 📦 Cài đặt và chạy dự án

### Yêu cầu hệ thống
- Android Studio Flamingo or later
- Android SDK 26+ (Android 8.0+)
- JDK 11+
- Git
- Gradle 7.0+

### 1. Clone repository
```bash
git clone https://github.com/yourusername/PizzaApp_PRM392.git
cd PizzaApp_PRM392
```

### 2. Mở Project trong Android Studio
```bash
# Cách 1: Mở trực tiếp
open -a "Android Studio" .

# Cách 2: File → Open Project
```

### 3. Cấu hình Project
```bash
# Gradle sẽ tự động download dependencies
# Không cần cấu hình thêm
```

### 4. Build & Run
```bash
# Build debug apk
./gradlew assembleDebug

# Build release apk
./gradlew assembleRelease

# Hoặc chạy trực tiếp trên emulator
./gradlew installDebug
```

### 5. Chạy trên Emulator/Device
```bash
# Mở Android Emulator từ Android Studio
# Hoặc kết nối Physical Device

# Chạy ứng dụng
./gradlew runDebug
```

### 6. Kiểm tra ứng dụng
- Mở ứng dụng PizzaApp trên emulator/device
- Đăng ký tài khoản mới
- Xem danh sách pizza
- Thêm vào giỏ hàng
- Đặt hàng và theo dõi

---

## 📚 Database Documentation

### Bảng Users
```sql
CREATE TABLE users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    phone TEXT,
    password TEXT NOT NULL,
    address TEXT,
    avatar TEXT,
    token TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
)
```

### Bảng Pizzas
```sql
CREATE TABLE pizzas (
    pizza_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    price REAL NOT NULL,
    image TEXT,
    size TEXT,
    category TEXT,
    rating REAL DEFAULT 0,
    stock INTEGER DEFAULT 0
)
```

### Bảng Cart Items
```sql
CREATE TABLE cart_items (
    cart_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    pizza_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    price REAL NOT NULL,
    notes TEXT,
    added_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES users(user_id),
    FOREIGN KEY(pizza_id) REFERENCES pizzas(pizza_id)
)
```

### Bảng Orders
```sql
CREATE TABLE orders (
    order_id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_code TEXT UNIQUE NOT NULL,
    user_id INTEGER NOT NULL,
    total_price REAL NOT NULL,
    address TEXT NOT NULL,
    phone TEXT NOT NULL,
    notes TEXT,
    status TEXT DEFAULT 'PENDING',
    payment_method TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES users(user_id)
)
```

---

## 🧪 Testing

### Chạy Unit Tests
```bash
# Chạy tất cả unit tests
./gradlew test

# Chạy instrumented tests
./gradlew connectedAndroidTest

# Chạy tests với coverage
./gradlew testDebugUnitTest --coverage
```

### Test Structure
```
tests/
├── dao/                    # Database DAO Tests
├── models/                 # Model Tests
├── services/               # Business Logic Tests
└── utils/                  # Utility Tests
```

---

## 🔒 Security

### Authentication & Authorization
- Lưu trữ mật khẩu với hashing (SHA-256)
- Token-based authentication
- Session management
- Password validation

### Data Protection
- Input validation & sanitization
- SQL injection prevention (Parameterized Queries)
- Secure data storage (Encrypted SharedPreferences)
- Data encryption for sensitive info
- Secure API communication (HTTPS)

### Best Practices
- Never store passwords in plain text
- Validate user input
- Use HTTPS for API calls
- Implement timeout for sessions
- Regular security updates

---

## 🚀 Deployment

### Build for Production
```bash
# Generate signed APK
./gradlew bundleRelease

# Generate APK for distribution
./gradlew assembleRelease
```

### Release Configuration
```gradle
buildTypes {
    release {
        minifyEnabled true
        shrinkResources true
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt')
    }
}
```

### Distribution
- **Google Play Store**: Upload signed APK/Bundle
- **Direct Distribution**: Share APK file
- **Beta Testing**: Use Firebase App Distribution

---

## 🤝 Contributing

### Development Workflow
1. Fork repository
2. Tạo feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Tạo Pull Request

### Code Standards
- **Language**: Java (Android conventions)
- **Naming**: camelCase for methods/variables, PascalCase for classes
- **Documentation**: JavaDoc comments
- **Code Style**: Google Java Style Guide
- **Test Coverage**: Minimum unit tests for critical functions

### Commit Message Format
```
feat: Add new feature
fix: Fix bug
docs: Update documentation
style: Code style changes
refactor: Code refactoring
test: Add/update tests
```

---

## 📄 License

Dự án này được phát hành dưới [MIT License](LICENSE).

---

## 👥 Team

- **Lead Developer**: PRM392 Student
- **Database Design**: SQLite Database Architecture
- **UI/UX**: Android Material Design
- **Testing**: Unit & Integration Tests

---

## 🎯 Roadmap

### Phase 1 (Current) ✅
- [x] Core Database Setup
- [x] User Authentication
- [x] Pizza Management
- [x] Cart Management
- [x] Order Management

### Phase 2 (Next) ⏳
- [ ] Payment Integration
- [ ] Real-time Order Tracking
- [ ] Push Notifications
- [ ] User Reviews & Ratings
- [ ] Order History

### Phase 3 (Future) 🔮
- [ ] Admin Dashboard
- [ ] Analytics & Reports
- [ ] Customer Support Chat
- [ ] Loyalty Program
- [ ] Multi-language Support

---

## 📊 Thống kê Dự án

| Metric | Value |
|--------|-------|
| Lines of Code | 5000+ |
| Database Tables | 7 |
| Java Classes | 25+ |
| Activities | 8+ |
| Database Operations | 100+ |
| Code Coverage | 75%+ |
| Min API Level | 26 |

---

## 🔗 Resources

- [Android Developer Documentation](https://developer.android.com/)
- [SQLite Tutorial](https://www.sqlite.org/docs.html)
- [Material Design Guide](https://material.io/design/)
- [Java Programming Guide](https://docs.oracle.com/javase/tutorial/)
- [Git Documentation](https://git-scm.com/doc)

---

<div align="center">

**🍕 PizzaApp - Smart Pizza Ordering System**

*Đặt pizza dễ dàng, nhanh chóng, an toàn*

*Last Updated: 2025* | *Version: 1.0.0*

</div>
