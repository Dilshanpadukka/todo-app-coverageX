-- Sample data for Todo/Task Management Application
-- This script inserts sample data for testing and demonstration

-- Ensure we're using the correct database
USE todo_coveragex;

-- Insert sample tasks (only if tables are empty)
INSERT IGNORE INTO tasks (task_title, description, priority_id, task_status_id, create_date, last_status_change_date) VALUES
-- High Priority Tasks
('Complete project documentation', 
 'Write comprehensive documentation for the todo application including API docs, user guide, and deployment instructions', 
 1, 2, '2025-10-20 09:00:00', '2025-10-21 14:30:00'),

('Implement user authentication', 
 'Add JWT-based authentication system with login, registration, password reset, and role-based access control', 
 1, 1, '2025-10-19 10:15:00', '2025-10-19 10:15:00'),

('Security audit and penetration testing', 
 'Conduct comprehensive security review, vulnerability assessment, and implement necessary security fixes', 
 1, 1, '2025-10-18 11:30:00', '2025-10-18 11:30:00'),

('Database performance optimization', 
 'Optimize database queries, add proper indexes, and implement caching strategies for better performance', 
 1, 4, '2025-10-17 08:45:00', '2025-10-22 16:20:00'),

-- Medium Priority Tasks
('Set up CI/CD pipeline', 
 'Configure GitHub Actions for automated testing, building, and deployment to staging and production environments', 
 2, 1, '2025-10-21 13:20:00', '2025-10-21 13:20:00'),

('Write comprehensive unit tests', 
 'Create unit tests for all service layer methods, repository interfaces, and controller endpoints with high coverage', 
 2, 2, '2025-10-20 15:45:00', '2025-10-21 09:15:00'),

('Create frontend React components', 
 'Develop responsive React components for task management interface including forms, lists, and modals', 
 2, 1, '2025-10-19 14:00:00', '2025-10-19 14:00:00'),

('Implement real-time notifications', 
 'Add WebSocket support for real-time task updates and notifications to improve user experience', 
 2, 1, '2025-10-18 16:30:00', '2025-10-18 16:30:00'),

('API rate limiting and throttling', 
 'Implement rate limiting to prevent API abuse and ensure fair usage across all clients', 
 2, 3, '2025-10-17 12:10:00', '2025-10-20 10:45:00'),

-- Low Priority Tasks
('Implement advanced search functionality', 
 'Add full-text search capability with filters, sorting options, and search result highlighting', 
 3, 1, '2025-10-21 11:00:00', '2025-10-21 11:00:00'),

('Add email notification system', 
 'Send email notifications for task assignments, status changes, and deadline reminders', 
 3, 1, '2025-10-20 12:30:00', '2025-10-20 12:30:00'),

('Create mobile-responsive design', 
 'Ensure the application works seamlessly on mobile devices with touch-friendly interface', 
 3, 2, '2025-10-19 09:20:00', '2025-10-21 11:40:00'),

('Implement task templates', 
 'Allow users to create and use predefined task templates for common workflows', 
 3, 1, '2025-10-18 14:15:00', '2025-10-18 14:15:00'),

('Add data export functionality', 
 'Enable users to export task data in various formats (CSV, Excel, PDF) for reporting purposes', 
 3, 1, '2025-10-17 10:25:00', '2025-10-17 10:25:00'),

('Implement task dependencies', 
 'Add support for task dependencies and prerequisites to manage complex project workflows', 
 3, 3, '2025-10-16 13:50:00', '2025-10-19 15:20:00'),

-- Completed Tasks
('Design database schema', 
 'Create optimized database schema with proper relationships, constraints, and indexes for the todo application', 
 1, 4, '2025-10-15 08:00:00', '2025-10-16 17:30:00'),

('Set up development environment', 
 'Configure development environment with Spring Boot, MySQL, and necessary development tools', 
 2, 4, '2025-10-14 09:30:00', '2025-10-15 12:00:00'),

('Create project structure', 
 'Set up Maven project structure with proper package organization and dependency management', 
 2, 4, '2025-10-13 10:15:00', '2025-10-14 16:45:00'),

-- Closed Tasks
('Research task management solutions', 
 'Research existing task management solutions and identify key features and best practices', 
 3, 5, '2025-10-12 11:00:00', '2025-10-13 14:20:00'),

('Initial project planning', 
 'Define project scope, requirements, and create initial project timeline and milestones', 
 2, 5, '2025-10-11 14:30:00', '2025-10-12 18:00:00');

-- Verify data insertion
SELECT 
    'Priority Types' as table_name, 
    COUNT(*) as record_count 
FROM priority_types
UNION ALL
SELECT 
    'Task Status Types' as table_name, 
    COUNT(*) as record_count 
FROM task_status_types
UNION ALL
SELECT 
    'Tasks' as table_name, 
    COUNT(*) as record_count 
FROM tasks;

-- Show task distribution by status
SELECT 
    tst.type as status,
    COUNT(*) as task_count
FROM tasks t
JOIN task_status_types tst ON t.task_status_id = tst.id
GROUP BY tst.type
ORDER BY task_count DESC;

-- Show task distribution by priority
SELECT 
    pt.type as priority,
    COUNT(*) as task_count
FROM tasks t
JOIN priority_types pt ON t.priority_id = pt.id
GROUP BY pt.type
ORDER BY 
    CASE pt.type 
        WHEN 'HIGH' THEN 1 
        WHEN 'MEDIUM' THEN 2 
        WHEN 'LOW' THEN 3 
    END;
