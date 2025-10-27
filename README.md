# Sky Takeout Backend

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

[English](#english) | [中文](#chinese)

---

<a name="english"></a>
## English

### 📖 Project Overview

Sky Takeout Backend is a comprehensive food delivery management system built with Spring Boot 3.4.1. It provides a complete backend solution for managing restaurants, dishes, orders, and user interactions. The system features separate admin and user portals with real-time order notifications and automated order processing.

### ✨ Key Features

#### Admin Portal
- **Employee Management**: Add, edit, and manage employee accounts with role-based access control
- **Category Management**: Organize dishes and setmeals into categories
- **Dish Management**: Complete CRUD operations for dishes with flavors and pricing
- **Setmeal Management**: Create and manage combo meals with multiple dishes
- **Order Management**: Process orders, track status, and handle order lifecycle
- **Report & Statistics**: Generate business reports including turnover, user statistics, and top-selling items
- **Shop Settings**: Configure shop status and operational parameters

#### User Portal
- **WeChat Authentication**: Login via WeChat OAuth
- **Address Book**: Manage multiple delivery addresses
- **Browse & Search**: View dishes and setmeals by category
- **Shopping Cart**: Add items, modify quantities, and manage cart
- **Order Placement**: Submit orders with payment integration
- **Order Tracking**: Real-time order status updates
- **Payment Integration**: WeChat Pay support

#### System Features
- **Real-time Notifications**: WebSocket-based order notifications to admin
- **Scheduled Tasks**: 
  - Automatic cancellation of unpaid orders after 15 minutes
  - Automatic completion of delivered orders daily at 1 AM
- **File Upload**: Aliyun OSS integration for image management
- **Caching**: Redis-based caching for improved performance
- **API Documentation**: Knife4j/OpenAPI 3.0 interactive documentation

### 🏗️ Architecture

The project follows a modular Maven multi-module architecture:

```
sky-takeout-backend/
├── sky-common/                           # Common utilities and configurations
│   ├── constants/                        # System constants
│   ├── context/                          # Thread-local context
│   ├── enumeration/                      # Enumerations
│   ├── exception/                        # Custom exceptions
│   ├── json/                             # JSON serialization
│   ├── properties/                       # Configuration properties
│   ├── result/                           # Result wrapper classes
│   └── utils/                            # Utility classes (JWT, OSS, WeChat)
│
├── sky-pojo/                             # Data Transfer Objects
│   ├── dto/                              # Data Transfer Objects
│   ├── entity/                           # Domain entities
│   └── vo/                               # View Objects
│
├── sky-server/                           # Core business logic
│   ├── annotation/                       # Custom annotations (AutoFill)
│   ├── aspect/                           # AOP aspects
│   ├── config/                           # Spring configurations
│   ├── controller/                       # REST controllers
│   │   ├── admin/                        # Admin portal endpoints
│   │   └── user/                         # User portal endpoints
│   ├── handler/                          # Global exception handlers
│   ├── interceptor/                      # JWT interceptors
│   ├── mapper/                           # MyBatis mappers
│   ├── service/                          # Business logic services
│   ├── task/                             # Scheduled tasks
│   └── websocket/                        # WebSocket server
│
└── aliyun-oss-operator-spring-boot-parent/  # Custom OSS Starter
    ├── aliyun-oss-operator-spring-boot-autoconfigure/
    └── aliyun-oss-operator-spring-boot-starter/
```

### 🛠️ Technology Stack

| Category | Technology |
|----------|------------|
| **Framework** | Spring Boot 3.4.1 |
| **Language** | Java 21 |
| **ORM** | MyBatis 3.0.4 |
| **Database** | MySQL 8.0.33 |
| **Cache** | Redis (Spring Data Redis) |
| **Connection Pool** | Druid 1.2.23 |
| **Authentication** | JWT (JJWT 0.12.6) |
| **API Documentation** | Knife4j 4.5.0, SpringDoc 2.8.10 |
| **Pagination** | PageHelper 2.1.0 |
| **File Storage** | Aliyun OSS 3.17.4 |
| **Payment** | WeChat Pay API v3 |
| **WebSocket** | Spring WebSocket |
| **Scheduling** | Spring Task |
| **AOP** | AspectJ 1.9.22 |
| **Excel** | Apache POI 5.3.0 |
| **JSON** | Fastjson 2.0.53 |
| **Build Tool** | Maven |

### 📋 Prerequisites

- Java 21 or higher
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+
- Aliyun OSS account (for file upload)
- WeChat Developer account (for user authentication and payment)

### 🚀 Quick Start

#### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/sky-takeout-backend.git
cd sky-takeout-backend
```

#### 2. Configure Database

Create a MySQL database and execute the initialization script:

```sql
CREATE DATABASE sky_takeout CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 3. Configure Application

Create `application-dev.yml` in `sky-server/src/main/resources/`:

```yaml
sky:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    host: localhost
    port: 3306
    database: sky_takeout
    username: your_username
    password: your_password
    
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password
      database: 0
      lettuce:
        pool:
          max-active: 8
          max-wait: -1ms
          max-idle: 8
          min-idle: 0

aliyun:
  oss:
    endpoint: oss-cn-hangzhou.aliyuncs.com
    bucket-name: your-bucket-name
    region: cn-hangzhou
    dir: images/

sky:
  wechat:
    appid: your_wechat_appid
    secret: your_wechat_secret
    mchid: your_merchant_id
    mchSerialNo: your_merchant_serial_no
    privateKeyFilePath: /path/to/apiclient_key.pem
    apiV3Key: your_api_v3_key
    weChatPayCertFilePath: /path/to/wechatpay.pem
    notifyUrl: https://yourdomain.com/user/order/paySuccess
    refundNotifyUrl: https://yourdomain.com/user/order/refundSuccess
    
  shop:
    address: Your Shop Address
    
  baidu:
    ak: your_baidu_map_ak
```

#### 4. Build and Run

```bash
# Build the project
mvn clean package

# Run the application
cd sky-server
mvn spring-boot:run
```

The server will start on `http://localhost:8080`

#### 5. Access API Documentation

Open your browser and navigate to:
- Knife4j UI: `http://localhost:8080/doc.html`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

### 📡 API Endpoints

#### Admin Endpoints (Prefix: `/admin`)

| Module | Method | Endpoint | Description |
|--------|--------|----------|-------------|
| **Employee** | POST | `/admin/employee/login` | Employee login |
| | POST | `/admin/employee/logout` | Employee logout |
| | POST | `/admin/employee` | Add employee |
| | GET | `/admin/employee/page` | Query employees (paginated) |
| **Category** | POST | `/admin/category` | Create category |
| | GET | `/admin/category/page` | Query categories (paginated) |
| | PUT | `/admin/category` | Update category |
| | DELETE | `/admin/category` | Delete category |
| **Dish** | POST | `/admin/dish` | Create dish |
| | GET | `/admin/dish/page` | Query dishes (paginated) |
| | PUT | `/admin/dish` | Update dish |
| | DELETE | `/admin/dish` | Delete dish |
| **Order** | GET | `/admin/order/conditionSearch` | Search orders |
| | GET | `/admin/order/statistics` | Order statistics |
| | PUT | `/admin/order/confirm` | Confirm order |
| | PUT | `/admin/order/rejection` | Reject order |
| | PUT | `/admin/order/cancel` | Cancel order |
| **Report** | GET | `/admin/report/turnoverStatistics` | Turnover statistics |
| | GET | `/admin/report/userStatistics` | User statistics |
| | GET | `/admin/report/ordersStatistics` | Order statistics |
| | GET | `/admin/report/top10` | Top 10 selling items |

#### User Endpoints (Prefix: `/user`)

| Module | Method | Endpoint | Description |
|--------|--------|----------|-------------|
| **User** | POST | `/user/user/login` | WeChat login |
| **Category** | GET | `/user/category/list` | List categories |
| **Dish** | GET | `/user/dish/list` | List dishes by category |
| **Shopping Cart** | POST | `/user/shoppingCart/add` | Add to cart |
| | GET | `/user/shoppingCart/list` | View cart |
| | DELETE | `/user/shoppingCart/clean` | Clear cart |
| **Address** | POST | `/user/addressBook` | Add address |
| | GET | `/user/addressBook/list` | List addresses |
| | PUT | `/user/addressBook` | Update address |
| | DELETE | `/user/addressBook` | Delete address |
| **Order** | POST | `/user/order/submit` | Submit order |
| | GET | `/user/order/historyOrders` | Order history |
| | GET | `/user/order/orderDetail/{id}` | Order details |
| | PUT | `/user/order/cancel` | Cancel order |

### 🔐 Authentication

The system uses JWT (JSON Web Token) for authentication:

- **Admin Token**: Pass token in `token` header
- **User Token**: Pass token in `authentication` header

Token generation is handled automatically upon successful login.

### 📊 Database Schema

Key entities include:
- `employee`: Admin users
- `user`: End users
- `category`: Dish and setmeal categories
- `dish`: Dishes/menu items
- `dish_flavor`: Dish flavor options
- `setmeal`: Combo meals
- `setmeal_dish`: Dish-setmeal relationships
- `shopping_cart`: Shopping cart items
- `orders`: Order records
- `order_detail`: Order line items
- `address_book`: User delivery addresses

### 🔧 Key Features Implementation

#### 1. Auto-fill Fields
Uses AOP to automatically populate common fields (create_time, update_time, create_user, update_user):

```java
@AutoFill(value = OperationType.INSERT)
void insert(Employee employee);
```

#### 2. JWT Interceptor
Validates JWT tokens and extracts user information:

```java
@Component
public class JwtTokenAdminInterceptor implements HandlerInterceptor {
    // Validates admin token
}
```

#### 3. Redis Caching
Implements Spring Cache for improved performance:

```java
@Cacheable(value = "dishCache", key = "#categoryId")
public List<DishVO> listWithFlavor(Long categoryId)
```

#### 4. WebSocket Notifications
Real-time order notifications to admin:

```java
@Component
public class WebSocketServer extends TextWebSocketHandler {
    public void sendMessage(String message)
}
```

#### 5. Scheduled Tasks
Automatic order processing:

```java
@Scheduled(cron = "0 */1 * * * ?")  // Every minute
public void cancelOrderNotPay()

@Scheduled(cron = "0 0 1 * * ?")   // Daily at 1 AM
public void processDeliveryOrder()
```


### 📦 Building for Production

```bash
# Build JAR
mvn clean package -DskipTests

# The JAR file will be in sky-server/target/
java -jar sky-server/target/sky-server-1.0-SNAPSHOT.jar
```

### 🐳 Docker Support

```bash
# Build Docker image
docker build -t sky-takeout-backend .

# Run container
docker run -p 8080:8080 sky-takeout-backend
```

### 📝 Development Notes

1. **Code Style**: Follow Java coding conventions
2. **Exception Handling**: Use custom exceptions in `sky-common/exception`
3. **API Response**: Wrap all responses in `Result<T>` class
4. **Pagination**: Use PageHelper for paginated queries
5. **Transaction Management**: Use `@Transactional` for database transactions
6. **Logging**: Use SLF4J with Lombok's `@Slf4j`

---

<a name="chinese"></a>
## 中文

### 📖 项目概述

Sky Takeout Backend（苍穹外卖后端系统）是一个基于 Spring Boot 3.4.1 构建的综合性餐饮外卖管理系统。它为管理餐厅、菜品、订单和用户交互提供了完整的后端解决方案。系统具有独立的管理端和用户端，支持实时订单通知和自动化订单处理。

### ✨ 核心功能

#### 管理端
- **员工管理**：添加、编辑和管理员工账户，支持基于角色的访问控制
- **分类管理**：将菜品和套餐组织成不同类别
- **菜品管理**：完整的菜品增删改查操作，支持口味和定价
- **套餐管理**：创建和管理包含多个菜品的套餐组合
- **订单管理**：处理订单、跟踪状态并管理订单生命周期
- **报表统计**：生成营业额、用户统计和热销商品等业务报表
- **店铺设置**：配置店铺状态和运营参数

#### 用户端
- **微信授权登录**：通过微信 OAuth 登录
- **地址簿管理**：管理多个配送地址
- **浏览搜索**：按分类查看菜品和套餐
- **购物车**：添加商品、修改数量和管理购物车
- **下单**：提交订单并集成支付功能
- **订单追踪**：实时更新订单状态
- **支付集成**：支持微信支付

#### 系统特性
- **实时通知**：基于 WebSocket 向管理端推送订单通知
- **定时任务**：
  - 15 分钟后自动取消未支付订单
  - 每日凌晨 1 点自动完成派送中的订单
- **文件上传**：集成阿里云 OSS 进行图片管理
- **缓存机制**：基于 Redis 的缓存提升性能
- **API 文档**：Knife4j/OpenAPI 3.0 交互式文档

### 🏗️ 架构设计

项目采用模块化的 Maven 多模块架构：

```
sky-takeout-backend/
├── sky-common/                           # 通用工具和配置
│   ├── constants/                        # 系统常量
│   ├── context/                          # 线程本地上下文
│   ├── enumeration/                      # 枚举类
│   ├── exception/                        # 自定义异常
│   ├── json/                             # JSON 序列化
│   ├── properties/                       # 配置属性类
│   ├── result/                           # 结果包装类
│   └── utils/                            # 工具类（JWT、OSS、微信等）
│
├── sky-pojo/                             # 数据传输对象
│   ├── dto/                              # 数据传输对象
│   ├── entity/                           # 领域实体
│   └── vo/                               # 视图对象
│
├── sky-server/                           # 核心业务逻辑
│   ├── annotation/                       # 自定义注解（AutoFill）
│   ├── aspect/                           # AOP 切面
│   ├── config/                           # Spring 配置
│   ├── controller/                       # REST 控制器
│   │   ├── admin/                        # 管理端接口
│   │   └── user/                         # 用户端接口
│   ├── handler/                          # 全局异常处理器
│   ├── interceptor/                      # JWT 拦截器
│   ├── mapper/                           # MyBatis 映射器
│   ├── service/                          # 业务逻辑服务
│   ├── task/                             # 定时任务
│   └── websocket/                        # WebSocket 服务器
│
└── aliyun-oss-operator-spring-boot-parent/  # 自定义 OSS Starter
    ├── aliyun-oss-operator-spring-boot-autoconfigure/
    └── aliyun-oss-operator-spring-boot-starter/
```

### 🛠️ 技术栈

| 类别 | 技术 |
|------|------|
| **框架** | Spring Boot 3.4.1 |
| **语言** | Java 21 |
| **ORM** | MyBatis 3.0.4 |
| **数据库** | MySQL 8.0.33 |
| **缓存** | Redis (Spring Data Redis) |
| **连接池** | Druid 1.2.23 |
| **认证** | JWT (JJWT 0.12.6) |
| **API 文档** | Knife4j 4.5.0, SpringDoc 2.8.10 |
| **分页** | PageHelper 2.1.0 |
| **文件存储** | 阿里云 OSS 3.17.4 |
| **支付** | 微信支付 API v3 |
| **WebSocket** | Spring WebSocket |
| **定时任务** | Spring Task |
| **AOP** | AspectJ 1.9.22 |
| **Excel** | Apache POI 5.3.0 |
| **JSON** | Fastjson 2.0.53 |
| **构建工具** | Maven |

### 📋 环境要求

- Java 21 或更高版本
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+
- 阿里云 OSS 账号（用于文件上传）
- 微信开发者账号（用于用户认证和支付）

### 🚀 快速开始

#### 1. 克隆仓库

```bash
git clone https://github.com/yourusername/sky-takeout-backend.git
cd sky-takeout-backend
```

#### 2. 配置数据库

创建 MySQL 数据库并执行初始化脚本：

```sql
CREATE DATABASE sky_takeout CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 3. 配置应用

在 `sky-server/src/main/resources/` 中创建 `application-dev.yml`：

```yaml
sky:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    host: localhost
    port: 3306
    database: sky_takeout
    username: your_username
    password: your_password
    
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password
      database: 0
      lettuce:
        pool:
          max-active: 8
          max-wait: -1ms
          max-idle: 8
          min-idle: 0

aliyun:
  oss:
    endpoint: oss-cn-hangzhou.aliyuncs.com
    bucket-name: your-bucket-name
    region: cn-hangzhou
    dir: images/

sky:
  wechat:
    appid: your_wechat_appid
    secret: your_wechat_secret
    mchid: your_merchant_id
    mchSerialNo: your_merchant_serial_no
    privateKeyFilePath: /path/to/apiclient_key.pem
    apiV3Key: your_api_v3_key
    weChatPayCertFilePath: /path/to/wechatpay.pem
    notifyUrl: https://yourdomain.com/user/order/paySuccess
    refundNotifyUrl: https://yourdomain.com/user/order/refundSuccess
    
  shop:
    address: 店铺地址
    
  baidu:
    ak: your_baidu_map_ak
```

#### 4. 构建和运行

```bash
# 构建项目
mvn clean package

# 运行应用
cd sky-server
mvn spring-boot:run
```

服务器将在 `http://localhost:8080` 启动

#### 5. 访问 API 文档

打开浏览器访问：
- Knife4j UI: `http://localhost:8080/doc.html`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

### 📡 API 接口

#### 管理端接口（前缀：`/admin`）

| 模块 | 方法 | 接口路径 | 描述 |
|------|------|---------|------|
| **员工** | POST | `/admin/employee/login` | 员工登录 |
| | POST | `/admin/employee/logout` | 员工登出 |
| | POST | `/admin/employee` | 新增员工 |
| | GET | `/admin/employee/page` | 分页查询员工 |
| **分类** | POST | `/admin/category` | 新增分类 |
| | GET | `/admin/category/page` | 分页查询分类 |
| | PUT | `/admin/category` | 修改分类 |
| | DELETE | `/admin/category` | 删除分类 |
| **菜品** | POST | `/admin/dish` | 新增菜品 |
| | GET | `/admin/dish/page` | 分页查询菜品 |
| | PUT | `/admin/dish` | 修改菜品 |
| | DELETE | `/admin/dish` | 删除菜品 |
| **订单** | GET | `/admin/order/conditionSearch` | 订单搜索 |
| | GET | `/admin/order/statistics` | 订单统计 |
| | PUT | `/admin/order/confirm` | 接单 |
| | PUT | `/admin/order/rejection` | 拒单 |
| | PUT | `/admin/order/cancel` | 取消订单 |
| **报表** | GET | `/admin/report/turnoverStatistics` | 营业额统计 |
| | GET | `/admin/report/userStatistics` | 用户统计 |
| | GET | `/admin/report/ordersStatistics` | 订单统计 |
| | GET | `/admin/report/top10` | 销量前10 |

#### 用户端接口（前缀：`/user`）

| 模块 | 方法 | 接口路径 | 描述 |
|------|------|---------|------|
| **用户** | POST | `/user/user/login` | 微信登录 |
| **分类** | GET | `/user/category/list` | 查询分类列表 |
| **菜品** | GET | `/user/dish/list` | 根据分类查询菜品 |
| **购物车** | POST | `/user/shoppingCart/add` | 添加购物车 |
| | GET | `/user/shoppingCart/list` | 查看购物车 |
| | DELETE | `/user/shoppingCart/clean` | 清空购物车 |
| **地址** | POST | `/user/addressBook` | 新增地址 |
| | GET | `/user/addressBook/list` | 查询地址列表 |
| | PUT | `/user/addressBook` | 修改地址 |
| | DELETE | `/user/addressBook` | 删除地址 |
| **订单** | POST | `/user/order/submit` | 用户下单 |
| | GET | `/user/order/historyOrders` | 历史订单查询 |
| | GET | `/user/order/orderDetail/{id}` | 查询订单详情 |
| | PUT | `/user/order/cancel` | 取消订单 |

### 🔐 身份认证

系统使用 JWT（JSON Web Token）进行身份认证：

- **管理端 Token**：在 `token` 请求头中传递
- **用户端 Token**：在 `authentication` 请求头中传递

Token 在登录成功后自动生成。

### 📊 数据库表结构

主要实体包括：
- `employee`: 管理端用户
- `user`: 终端用户
- `category`: 菜品和套餐分类
- `dish`: 菜品/菜单项目
- `dish_flavor`: 菜品口味选项
- `setmeal`: 套餐组合
- `setmeal_dish`: 菜品与套餐的关联关系
- `shopping_cart`: 购物车条目
- `orders`: 订单记录
- `order_detail`: 订单明细项
- `address_book`: 用户配送地址

### 🔧 关键功能实现

#### 1. 自动填充字段
使用 AOP 自动填充公共字段（创建时间、更新时间、创建人、更新人）：

```java
@AutoFill(value = OperationType.INSERT)
void insert(Employee employee);
```

#### 2. JWT 拦截器
验证 JWT token 并提取用户信息：

```java
@Component
public class JwtTokenAdminInterceptor implements HandlerInterceptor {
    // 验证管理端 token
}
```

#### 3. Redis 缓存
实现 Spring Cache 提升性能：

```java
@Cacheable(value = "dishCache", key = "#categoryId")
public List<DishVO> listWithFlavor(Long categoryId)
```

#### 4. WebSocket 通知
实时向管理端推送订单通知：

```java
@Component
public class WebSocketServer extends TextWebSocketHandler {
    public void sendMessage(String message)
}
```

#### 5. 定时任务
自动处理订单：

```java
@Scheduled(cron = "0 */1 * * * ?")  // 每分钟执行
public void cancelOrderNotPay()

@Scheduled(cron = "0 0 1 * * ?")   // 每天凌晨1点执行
public void processDeliveryOrder()
```

### 📦 生产环境构建

```bash
# 构建 JAR 包
mvn clean package -DskipTests

# JAR 文件位于 sky-server/target/
java -jar sky-server/target/sky-server-1.0-SNAPSHOT.jar
```

### 🐳 Docker 支持

```bash
# 构建 Docker 镜像
docker build -t sky-takeout-backend .

# 运行容器
docker run -p 8080:8080 sky-takeout-backend
```

### 📝 开发注意事项

1. **代码风格**：遵循 Java 编码规范
2. **异常处理**：使用 `sky-common/exception` 中的自定义异常
3. **API 响应**：所有响应包装在 `Result<T>` 类中
4. **分页查询**：使用 PageHelper 进行分页查询
5. **事务管理**：对数据库事务使用 `@Transactional` 注解
6. **日志记录**：使用 SLF4J 配合 Lombok 的 `@Slf4j` 注解

