# Frontend Setup Guide

## Quick Fix for Missing Dependencies

The frontend is missing the `date-fns` dependency. Here's how to fix it:

### Option 1: Install Missing Dependency
```bash
cd frontend
npm install date-fns
npm run dev
```

### Option 2: Use the Install Script
**Windows:**
```bash
cd frontend
install-deps.bat
npm run dev
```

**Linux/Mac:**
```bash
cd frontend
chmod +x install-deps.sh
./install-deps.sh
npm run dev
```

### Option 3: Reinstall All Dependencies
```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
npm run dev
```

## After Installation

Once the dependencies are installed, the development server should start successfully:

```
VITE v5.4.21  ready in 4235 ms

➜  Local:   http://localhost:3000/
➜  Network: use --host to expose
```

## Accessing the Application

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080 (make sure backend is running)
- **API Documentation**: http://localhost:8080/swagger-ui.html

## Troubleshooting

If you encounter other import errors:

1. **Check if backend is running**: The frontend expects the backend API at `http://localhost:8080`
2. **Clear cache**: `npm run dev -- --force`
3. **Restart development server**: Stop with Ctrl+C and run `npm run dev` again

## Features Available

Once running, you can:
- ✅ View the dashboard with task statistics
- ✅ Create, edit, and delete tasks
- ✅ Filter and search tasks
- ✅ Toggle between dark/light themes
- ✅ Use keyboard shortcuts (Ctrl+N for new task)
- ✅ View responsive design on mobile devices
