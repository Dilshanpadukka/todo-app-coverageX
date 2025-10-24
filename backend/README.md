# Full-Stack Todo/Task Management Application

A comprehensive full-stack web application for task management, built with Spring Boot backend and React.js frontend. This project demonstrates modern software development practices and is designed for a Full Stack Engineer position assessment.

## 🚀 Features

### Core Functionality
- ✅ **CRUD Operations**: Create, Read, Update, and Delete tasks
- ✅ **Task Prioritization**: HIGH, MEDIUM, LOW priority levels
- ✅ **Status Management**: OPEN, IN_PROGRESS, HOLD, DONE, CLOSED statuses
- ✅ **Soft Delete**: Tasks are marked as CLOSED instead of being permanently deleted
- ✅ **Audit Trail**: Automatic tracking of creation and status change dates

### Advanced Features
- 🔍 **Search Functionality**: Full-text search across task titles and descriptions
- 📊 **Filtering & Sorting**: Filter by status, priority, and custom criteria
- 📈 **Statistics Dashboard**: Comprehensive task statistics and analytics
- 📄 **Pagination**: Efficient handling of large datasets
- 🔄 **Real-time Updates**: Automatic timestamp updates on status changes

### Technical Features
- 🛡️ **Input Validation**: Comprehensive validation using Bean Validation
- 🚨 **Exception Handling**: Global exception handling with meaningful error responses
- 📚 **API Documentation**: Interactive Swagger/OpenAPI documentation
- 🧪 **Unit Testing**: Comprehensive test coverage for service layer
- 🔧 **Configuration**: Externalized configuration with profiles support

## 🏗️ Architecture

### Technology Stack
- **Backend**: Java 22, Spring Boot 3.3.4
- **Database**: MySQL 8.0+
- **Build Tool**: Maven
- **Documentation**: Swagger/OpenAPI 3
- **Testing**: JUnit 5, Mockito
- **Mapping**: ModelMapper
- **Validation**: Bean Validation (Hibernate Validator)

### Project Structure
```
src/
├── main/
│   ├── java/com/dilshan/coveragex/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # JPA entities
│   │   ├── exception/      # Custom exceptions
│   │   ├── repository/     # Data access layer
│   │   ├── service/        # Business logic layer
│   │   └── util/           # Utility classes
│   └── resources/
│       ├── application.yml  # Application configuration
│       ├── schema.sql      # Database schema
│       └── data.sql        # Sample data
└── test/
    └── java/               # Unit tests
```

## 🗄️ Database Schema

### Tables
1. **priority_types**: Stores priority levels (HIGH, MEDIUM, LOW)
2. **task_status_types**: Stores status types (OPEN, IN_PROGRESS, HOLD, DONE, CLOSED)
3. **tasks**: Main table storing task information with foreign key relationships

### Relationships
- `tasks.priority_id` → `priority_types.id`
- `tasks.task_status_id` → `task_status_types.id`

## 🚀 Getting Started

### Prerequisites
- Java 22 or higher
- Maven 3.6+
- MySQL 8.0+
- Git

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd todo-app-coverageX
   ```

2. **Set up MySQL Database**
   ```sql
   CREATE DATABASE todo_coveragex;
   CREATE USER 'todo_user'@'localhost' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON todo_coveragex.* TO 'todo_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. **Configure Database Connection**
   Update `src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/todo_coveragex
       username: todo_user
       password: your_password
   ```

4. **Build and Run**
   ```bash
   # Build the application
   mvn clean compile

   # Run tests
   mvn test

   # Start the application
   mvn spring-boot:run
   ```

5. **Access the Application**
   - API Base URL: `http://localhost:8080/api`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - API Docs: `http://localhost:8080/v3/api-docs`

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Main Endpoints

#### Tasks
- `GET /tasks` - Get all tasks with pagination
- `GET /tasks/{id}` - Get task by ID
- `POST /tasks` - Create new task
- `PUT /tasks/{id}` - Update existing task
- `DELETE /tasks/{id}` - Soft delete task
- `GET /tasks/status/{statusId}` - Filter tasks by status
- `GET /tasks/priority/{priorityId}` - Filter tasks by priority
- `GET /tasks/search?searchTerm={term}` - Search tasks
- `GET /tasks/filter` - Get tasks with multiple filters
- `GET /tasks/statistics` - Get task statistics

#### Reference Data
- `GET /priority-types` - Get all priority types
- `GET /task-status-types` - Get all task status types

### Request/Response Examples

#### Create Task
```json
POST /api/tasks
{
  "taskTitle": "Complete project documentation",
  "description": "Write comprehensive API documentation",
  "priorityId": 1,
  "taskStatusId": 1
}
```

#### Response
```json
{
  "id": 1,
  "taskTitle": "Complete project documentation",
  "description": "Write comprehensive API documentation",
  "createDate": "2025-10-22T10:30:00",
  "lastStatusChangeDate": "2025-10-22T10:30:00",
  "priority": {
    "id": 1,
    "type": "HIGH"
  },
  "taskStatus": {
    "id": 1,
    "type": "OPEN"
  }
}
```

## 🧪 Testing

### Run Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=TaskServiceTest

# Run tests with coverage
mvn test jacoco:report
```

### Test Coverage
- Service layer: Comprehensive unit tests with Mockito
- Repository layer: Integration tests with @DataJpaTest
- Controller layer: Web layer tests with @WebMvcTest

## 🔧 Configuration

### Application Profiles
- `default`: Development configuration
- `test`: Test environment configuration
- `prod`: Production configuration

### Key Configuration Properties
```yaml
server:
  port: 8080

spring:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  
  datasource:
    url: jdbc:mysql://localhost:3306/todo_coveragex
    username: root
    password: 12345
```

## 🚀 Deployment

### Production Deployment
1. **Build production JAR**
   ```bash
   mvn clean package -Pprod
   ```

2. **Run with production profile**
   ```bash
   java -jar target/todo-app-coverageX-1.0-SNAPSHOT.jar --spring.profiles.active=prod
   ```

### Docker Deployment
```dockerfile
FROM openjdk:22-jdk-slim
COPY target/todo-app-coverageX-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Dilshan**
- Email: dilshan@example.com
- GitHub: [@dilshan](https://github.com/dilshan)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- MySQL team for the robust database system
- Swagger team for API documentation tools
- All contributors and the open-source community
