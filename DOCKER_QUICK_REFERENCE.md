# Docker Quick Reference Guide

## 🚀 Essential Commands

### Start the Application
```bash
# Build and start all services
docker-compose up -d --build

# Start without rebuilding
docker-compose up -d

# Start with logs visible
docker-compose up
```

### Stop the Application
```bash
# Stop all services (keep volumes)
docker-compose down

# Stop and remove volumes (WARNING: deletes data)
docker-compose down -v

# Stop specific service
docker-compose stop backend
```

### View Status
```bash
# List all containers
docker-compose ps

# View logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f database

# View last 100 lines
docker-compose logs --tail=100 backend
```

### Access Services
```bash
# Frontend: http://localhost:3000
# Backend API: http://localhost:8080/api
# Swagger UI: http://localhost:8080/swagger-ui.html
# Database: localhost:3306
```

### Execute Commands in Containers
```bash
# Run bash in backend
docker-compose exec backend bash

# Run shell in frontend
docker-compose exec frontend sh

# Run MySQL CLI in database
docker-compose exec database mysql -u root -p$$MYSQL_ROOT_PASSWORD

# Run Maven command in backend
docker-compose exec backend mvn clean test
```

### Rebuild Services
```bash
# Rebuild all images
docker-compose build

# Rebuild specific service
docker-compose build backend

# Rebuild without cache
docker-compose build --no-cache
```

### Restart Services
```bash
# Restart all services
docker-compose restart

# Restart specific service
docker-compose restart backend

# Restart and view logs
docker-compose restart backend && docker-compose logs -f backend
```

## 🔍 Troubleshooting Commands

### Check Container Health
```bash
# View container details
docker-compose ps

# Inspect container
docker inspect todo-backend

# Check container logs for errors
docker-compose logs backend | grep -i error
```

### Database Troubleshooting
```bash
# Connect to database
docker-compose exec database mysql -u root -p$$MYSQL_ROOT_PASSWORD

# Check database exists
docker-compose exec database mysql -u root -p$$MYSQL_ROOT_PASSWORD -e "SHOW DATABASES;"

# Check tables
docker-compose exec database mysql -u root -p$$MYSQL_ROOT_PASSWORD -e "USE todo_coveragex; SHOW TABLES;"

# Check data
docker-compose exec database mysql -u root -p$$MYSQL_ROOT_PASSWORD -e "USE todo_coveragex; SELECT COUNT(*) FROM tasks;"
```

### Network Troubleshooting
```bash
# Check network
docker network ls

# Inspect network
docker network inspect todo-app-coveragex_todo-network

# Test connectivity between containers
docker-compose exec backend ping database
docker-compose exec frontend ping backend
```

### Volume Management
```bash
# List volumes
docker volume ls

# Inspect volume
docker volume inspect todo-app-coveragex_mysql_data

# Remove volume
docker volume rm todo-app-coveragex_mysql_data

# Backup database
docker-compose exec database mysqldump -u root -p$$MYSQL_ROOT_PASSWORD todo_coveragex > backup.sql

# Restore database
docker-compose exec -T database mysql -u root -p$$MYSQL_ROOT_PASSWORD todo_coveragex < backup.sql
```

## 🧹 Cleanup Commands

### Remove Unused Resources
```bash
# Remove stopped containers
docker container prune

# Remove unused images
docker image prune

# Remove unused volumes
docker volume prune

# Remove unused networks
docker network prune

# Remove everything (WARNING: removes all unused Docker resources)
docker system prune -a
```

### Complete Reset
```bash
# Stop all services
docker-compose down -v

# Remove images
docker-compose rm -f

# Remove volumes
docker volume rm todo-app-coveragex_mysql_data

# Rebuild and start fresh
docker-compose up -d --build
```

## 📊 Monitoring Commands

### View Resource Usage
```bash
# Real-time container stats
docker stats

# Specific container stats
docker stats todo-backend

# View container processes
docker top todo-backend
```

### View Container Details
```bash
# Inspect container
docker inspect todo-backend

# View container logs with timestamps
docker-compose logs -f --timestamps backend

# View logs since specific time
docker-compose logs --since 2025-10-24T10:00:00 backend
```

## 🔐 Security Commands

### View Secrets and Env Variables
```bash
# Inspect environment variables
docker inspect todo-backend | grep -A 20 "Env"

# Check running processes
docker top todo-backend
```

## 📝 Configuration

### Environment Variables
Create `.env` file to override defaults:
```bash
cp .env.example .env
# Edit .env with your values
docker-compose up -d
```

### Port Mapping
Edit `docker-compose.yml` to change ports:
```yaml
ports:
  - "3001:80"      # Frontend
  - "8081:8080"    # Backend
  - "3307:3306"    # Database
```

## 🐛 Common Issues and Solutions

### Port Already in Use
```bash
# Find process using port
lsof -i :3000
lsof -i :8080
lsof -i :3306

# Kill process
kill -9 <PID>

# Or change port in docker-compose.yml
```

### Container Won't Start
```bash
# Check logs
docker-compose logs backend

# Rebuild
docker-compose build --no-cache backend

# Start with verbose output
docker-compose up backend
```

### Database Connection Failed
```bash
# Check database is running
docker-compose ps database

# Check database logs
docker-compose logs database

# Test connection
docker-compose exec database mysqladmin ping -u root -p$$MYSQL_ROOT_PASSWORD
```

### Frontend Can't Connect to Backend
```bash
# Check backend is running
docker-compose ps backend

# Check backend logs
docker-compose logs backend

# Test connectivity
docker-compose exec frontend ping backend

# Check Nginx configuration
docker-compose exec frontend cat /etc/nginx/conf.d/default.conf
```

## 📚 Useful Links

- **Docker Documentation**: https://docs.docker.com/
- **Docker Compose Documentation**: https://docs.docker.com/compose/
- **Docker Best Practices**: https://docs.docker.com/develop/dev-best-practices/
- **Spring Boot Docker**: https://spring.io/guides/gs/spring-boot-docker/
- **React Docker**: https://create-react-app.dev/deployment/docker/

## 💡 Tips and Tricks

### View Real-time Logs
```bash
docker-compose logs -f --tail=50
```

### Execute Multiple Commands
```bash
docker-compose exec backend bash -c "mvn clean && mvn test"
```

### Copy Files from Container
```bash
docker cp todo-backend:/app/app.jar ./app.jar
```

### Copy Files to Container
```bash
docker cp ./file.txt todo-backend:/app/file.txt
```

### Create Database Backup
```bash
docker-compose exec database mysqldump -u root -p$$MYSQL_ROOT_PASSWORD todo_coveragex > backup_$(date +%Y%m%d_%H%M%S).sql
```

### View Container IP Address
```bash
docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' todo-backend
```


