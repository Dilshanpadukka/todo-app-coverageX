# Todo Application - Full Stack

A comprehensive full-stack web application for task management, built with **Spring Boot** backend, **React.js** frontend, and **MySQL** database. This project demonstrates modern software development practices with complete Docker containerization.

## Clone the Repository
```bash
git clone https://github.com/dilshan/todo-app-coverageX.git

cd todo-app-coverageX
```

## 🚀 Quick Start with Docker

### Prerequisites
- **Docker** (version 20.10+)
- **Docker Compose** (version 1.29+)

### Build and Run the Complete Stack

First build the images:
```bash
docker-compose build
```

The entire application stack can be started with a single command:

```bash
docker-compose up -d
```

This command will:
1. Build the React.js frontend container
2. Build the Spring Boot backend container
3. Start the MySQL database container
4. Set up networking between all containers
5. Initialize the database with schema and data

### Access the Application

Once all containers are running, access the application at:

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **API Documentation (Swagger UI)**: http://localhost:8080/swagger-ui.html
- **API Docs (OpenAPI)**: http://localhost:8080/v3/api-docs

### Stop the Application

```bash
docker-compose down
```

To also remove the database volume (WARNING: This will delete all data):

```bash
docker-compose down -v
```

## 📋 Architecture Overview

### Multi-Container Setup

The application uses Docker Compose to orchestrate three containers:

#### 1. **Frontend Container** (React.js + Nginx)
- **Image**: Built from `frontend/Dockerfile`
- **Port**: 3000 (mapped to container port 80)
- **Technology**: React 18 with TypeScript, Material-UI, Vite
- **Features**:
  - Multi-stage build for optimized image size
  - Nginx reverse proxy with gzip compression
  - API proxy to backend service
  - Security headers configured
  - Health checks enabled

#### 2. **Backend Container** (Spring Boot)
- **Image**: Built from `backend/Dockerfile`
- **Port**: 8080
- **Technology**: Spring Boot 3.3.4, Java 21, Maven
- **Features**:
  - Multi-stage Maven build for optimized image size
  - JVM memory optimization for containers
  - Health checks via Spring Boot Actuator
  - Non-root user for security
  - Automatic database schema initialization

#### 3. **Database Container** (MySQL)
- **Image**: mysql:8.0
- **Port**: 3306
- **Features**:
  - Persistent volume for data storage
  - Automatic schema and data initialization
  - Health checks enabled
  - Environment-based configuration

### Network Configuration

All containers communicate through a custom Docker bridge network (`todo-network`):
- **Subnet**: 172.20.0.0/16
- **Frontend** → **Backend**: Via Nginx proxy at `http://backend:8080`
- **Backend** → **Database**: Via hostname `database:3306`

### Environment Variables

#### Database Configuration
```
MYSQL_ROOT_PASSWORD: rootpassword
MYSQL_DATABASE: todo_coveragex
MYSQL_USER: todo_user
MYSQL_PASSWORD: todo_password
```

#### Backend Configuration
```
SPRING_DATASOURCE_URL: jdbc:mysql://database:3306/todo_coveragex
SPRING_DATASOURCE_USERNAME: todo_user
SPRING_DATASOURCE_PASSWORD: todo_password
SPRING_JPA_HIBERNATE_DDL_AUTO: update
SERVER_PORT: 8080
```

#### Frontend Configuration
```
VITE_API_BASE_URL: http://localhost:8080/api
```

## 📁 Project Structure

```
todo-app-coverageX/
├── frontend/                    # React.js Frontend
│   ├── Dockerfile              # Multi-stage frontend build
│   ├── nginx.conf              # Nginx configuration
│   ├── src/                    # React source code
│   ├── package.json            # Node.js dependencies
│   └── vite.config.ts          # Vite configuration
│
├── backend/                    # Spring Boot Backend
│   ├── Dockerfile              # Multi-stage backend build
│   ├── pom.xml                 # Maven configuration
│   ├── src/                    # Java source code
│   └── target/                 # Compiled artifacts
│
├── docker-compose.yml          # Docker Compose orchestration
└── README.md                   # This file
```

## 🔧 Development Workflow

### Local Development (Without Docker)

#### Backend Setup
```bash
cd backend

# Build the application
mvn clean compile

# Run tests
mvn test

# Start the application
mvn spring-boot:run
```

The backend will be available at `http://localhost:8080`

#### Frontend Setup
```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

The frontend will be available at `http://localhost:3000`

### Docker Build Process

#### Frontend Build
1. **Stage 1 (Build)**: Node.js 18 Alpine
   - Installs npm dependencies
   - Builds React application with Vite
   - Generates optimized production build

2. **Stage 2 (Runtime)**: Nginx Alpine
   - Serves static files
   - Proxies API requests to backend
   - Implements security headers and caching

#### Backend Build
1. **Stage 1 (Build)**: Maven 3.9.5 with Java 21
   - Downloads Maven dependencies
   - Compiles Java source code
   - Packages Spring Boot JAR

2. **Stage 2 (Runtime)**: Eclipse Temurin JRE 21
   - Runs the compiled JAR
   - Optimized JVM settings for containers
   - Non-root user for security

## 📊 API Endpoints

### Tasks
- `GET /api/tasks` - Get all tasks with pagination
- `GET /api/tasks/{id}` - Get task by ID
- `POST /api/tasks` - Create new task
- `PUT /api/tasks/{id}` - Update existing task
- `DELETE /api/tasks/{id}` - Soft delete task
- `GET /api/tasks/status/{statusId}` - Filter by status
- `GET /api/tasks/priority/{priorityId}` - Filter by priority
- `GET /api/tasks/search?searchTerm={term}` - Search tasks
- `GET /api/tasks/statistics` - Get task statistics

### Reference Data
- `GET /api/priority-types` - Get all priority types
- `GET /api/task-status-types` - Get all task status types

## 🐛 Troubleshooting

### Containers Won't Start
```bash
# Check container logs
docker-compose logs -f

# Check specific service logs
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f database
```

### Database Connection Issues
```bash
# Verify database is running
docker-compose ps

# Check database health
docker-compose exec database mysqladmin ping -u root -p$$MYSQL_ROOT_PASSWORD
```

### Port Already in Use
If ports 3000, 8080, or 3306 are already in use, modify `docker-compose.yml`:
```yaml
ports:
  - "3001:80"        # Change frontend port
  - "8081:8080"      # Change backend port
  - "3307:3306"      # Change database port
```

### Clear Everything and Start Fresh
```bash
# Stop all containers and remove volumes
docker-compose down -v

# Remove built images
docker-compose rm -f

# Rebuild and start
docker-compose up -d --build
```

## 📚 Additional Resources

- **Backend README**: See `backend/README.md` for detailed backend documentation
- **Frontend README**: See `frontend/README.md` for detailed frontend documentation
- **API Documentation**: Available at http://localhost:8080/swagger-ui.html (when running)

## 🔐 Security Features

- Non-root users in containers
- Security headers configured in Nginx
- Environment-based configuration
- Health checks for all services
- Proper network isolation

## 📝 License

This project is part of a Full Stack Engineer position assessment.

