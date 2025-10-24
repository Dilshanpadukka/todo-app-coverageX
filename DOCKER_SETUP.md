# Docker Setup Guide

This document provides detailed information about the Docker containerization setup for the Todo Application.

## 📋 Table of Contents

1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [Quick Start](#quick-start)
4. [Container Details](#container-details)
5. [Networking](#networking)
6. [Environment Variables](#environment-variables)
7. [Volume Management](#volume-management)
8. [Health Checks](#health-checks)
9. [Troubleshooting](#troubleshooting)
10. [Advanced Usage](#advanced-usage)

## Overview

The application uses Docker Compose to orchestrate three containers:

- **Frontend**: React.js application served by Nginx
- **Backend**: Spring Boot REST API
- **Database**: MySQL 8.0

All containers communicate through a custom Docker bridge network with proper health checks and dependency management.

## Prerequisites

- Docker Desktop (version 20.10+) or Docker Engine (version 20.10+)
- Docker Compose (version 1.29+)
- At least 4GB of available RAM
- Ports 3000, 8080, and 3306 available on your machine

## Quick Start

### 1. Clone and Navigate to Project

```bash
cd todo-app-coverageX
```

### 2. Build and Start All Services

```bash
docker-compose up -d --build
```

This command will:
- Build the frontend image
- Build the backend image
- Pull the MySQL image
- Create and start all containers
- Set up networking and volumes

### 3. Verify Services are Running

```bash
docker-compose ps
```

Expected output:
```
NAME                COMMAND                  SERVICE      STATUS
todo-frontend       "nginx -g daemon off;"   frontend     Up (healthy)
todo-backend        "sh -c java $JAVA_OPT..."  backend    Up (healthy)
todo-mysql          "docker-entrypoint.s..."  database     Up (healthy)
```

### 4. Access the Application

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/swagger-ui.html

### 5. Stop All Services

```bash
docker-compose down
```

## Container Details

### Frontend Container

**Image**: Built from `frontend/Dockerfile`

**Base Image**: 
- Build stage: `node:18-alpine`
- Runtime stage: `nginx:1.25-alpine`

**Features**:
- Multi-stage build for optimized image size (~50MB)
- React 18 with TypeScript
- Vite build tool
- Nginx reverse proxy with gzip compression
- Security headers configured
- API proxy to backend service
- Health checks enabled

**Port**: 3000 (container port 80)

**Build Process**:
1. Install npm dependencies
2. Build React application with Vite
3. Copy built files to Nginx
4. Configure Nginx for SPA routing

### Backend Container

**Image**: Built from `backend/Dockerfile`

**Base Image**:
- Build stage: `maven:3.9.5-eclipse-temurin-21`
- Runtime stage: `eclipse-temurin:21-jre-jammy`

**Features**:
- Multi-stage build for optimized image size (~400MB)
- Spring Boot 3.3.4
- Java 21
- Maven build tool
- JVM memory optimization for containers
- Health checks via Spring Boot Actuator
- Non-root user for security

**Port**: 8080

**Build Process**:
1. Download Maven dependencies
2. Compile Java source code
3. Package Spring Boot JAR
4. Copy JAR to runtime image
5. Configure JVM options

### Database Container

**Image**: `mysql:8.0`

**Features**:
- MySQL 8.0 database
- Persistent volume for data storage
- Automatic schema initialization
- Health checks enabled
- Environment-based configuration

**Port**: 3306

**Initialization**:
- Automatically creates database and user
- Loads schema from `backend/src/main/resources/schema.sql`
- Loads initial data from `backend/src/main/resources/data.sql`

## Networking

### Network Configuration

All containers communicate through a custom Docker bridge network:

```yaml
networks:
  todo-network:
    driver: bridge
    ipam:
      config:
        - subnet: 172.20.0.0/16
```

### Service Communication

- **Frontend → Backend**: Via Nginx proxy at `http://backend:8080/api/`
- **Backend → Database**: Via hostname `database:3306`
- **Frontend → Database**: Not directly connected (only through backend)

### Port Mapping

| Service  | Container Port | Host Port | Protocol |
|----------|----------------|-----------|----------|
| Frontend | 80             | 3000      | HTTP     |
| Backend  | 8080           | 8080      | HTTP     |
| Database | 3306           | 3306      | TCP      |

## Environment Variables

### Database Configuration

```
MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_DATABASE=todo_coveragex
MYSQL_USER=todo_user
MYSQL_PASSWORD=todo_password
```

### Backend Configuration

```
SPRING_DATASOURCE_URL=jdbc:mysql://database:3306/todo_coveragex?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=todo_user
SPRING_DATASOURCE_PASSWORD=todo_password
SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQL8Dialect
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=docker
LOGGING_LEVEL_COM_DILSHAN_COVERAGEX=INFO
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_WEB=WARN
LOGGING_LEVEL_ORG_HIBERNATE_SQL=WARN
```

### Frontend Configuration

```
VITE_API_BASE_URL=/api
```

## Volume Management

### Named Volumes

```yaml
volumes:
  mysql_data:
    driver: local
```

The `mysql_data` volume persists MySQL database files between container restarts.

### Volume Operations

**View volumes**:
```bash
docker volume ls
```

**Inspect volume**:
```bash
docker volume inspect todo-app-coveragex_mysql_data
```

**Remove volume** (WARNING: Deletes all data):
```bash
docker volume rm todo-app-coveragex_mysql_data
```

## Health Checks

### Frontend Health Check

```yaml
healthcheck:
  test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:80/health"]
  interval: 30s
  timeout: 10s
  retries: 3
  start_period: 30s
```

### Backend Health Check

```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
  interval: 30s
  timeout: 10s
  retries: 5
  start_period: 90s
```

### Database Health Check

```yaml
healthcheck:
  test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-p$$MYSQL_ROOT_PASSWORD"]
  interval: 30s
  timeout: 10s
  retries: 5
  start_period: 60s
```

## Troubleshooting

### Containers Won't Start

**Check logs**:
```bash
docker-compose logs -f
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f database
```

### Port Already in Use

Modify `docker-compose.yml` port mappings:
```yaml
ports:
  - "3001:80"      # Frontend
  - "8081:8080"    # Backend
  - "3307:3306"    # Database
```

### Database Connection Issues

**Verify database is running**:
```bash
docker-compose exec database mysqladmin ping -u root -p$$MYSQL_ROOT_PASSWORD
```

**Check database logs**:
```bash
docker-compose logs database
```

### Clear Everything and Start Fresh

```bash
# Stop and remove all containers and volumes
docker-compose down -v

# Remove built images
docker-compose rm -f

# Rebuild and start
docker-compose up -d --build
```

## Advanced Usage

### Build Images Separately

```bash
# Build frontend only
docker-compose build frontend

# Build backend only
docker-compose build backend

# Build database (pulls image)
docker-compose build database
```

### Run Commands in Containers

```bash
# Execute command in backend
docker-compose exec backend bash

# Execute command in frontend
docker-compose exec frontend sh

# Execute command in database
docker-compose exec database mysql -u root -p$$MYSQL_ROOT_PASSWORD
```

### View Container Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend

# Last 100 lines
docker-compose logs --tail=100 backend

# With timestamps
docker-compose logs -f --timestamps backend
```

### Restart Services

```bash
# Restart all services
docker-compose restart

# Restart specific service
docker-compose restart backend
```

### Scale Services

```bash
# Scale backend to 3 instances (requires load balancer)
docker-compose up -d --scale backend=3
```

### Environment File

Create a `.env` file to override default values:

```bash
cp .env.example .env
# Edit .env with your values
docker-compose up -d
```

### Production Considerations

For production deployment:

1. Use strong passwords for database
2. Enable SSL/TLS for HTTPS
3. Use environment-specific configuration
4. Implement proper logging and monitoring
5. Use Docker secrets for sensitive data
6. Configure resource limits
7. Set up backup strategy for database
8. Use container orchestration (Kubernetes)

### Performance Optimization

1. **Frontend**: Multi-stage build reduces image size
2. **Backend**: Maven dependency caching optimizes build time
3. **Database**: Persistent volume prevents data loss
4. **Network**: Custom bridge network for efficient communication
5. **Health Checks**: Ensure services are ready before dependent services start


