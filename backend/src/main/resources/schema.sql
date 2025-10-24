-- Database schema for Todo/Task Management Application
-- This script creates the required tables with proper relationships and constraints

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS todo_coveragex;
USE todo_coveragex;

-- Table: priority_types
-- Stores predefined priority levels for tasks
CREATE TABLE IF NOT EXISTS priority_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL UNIQUE,
    CONSTRAINT chk_priority_type CHECK (type IN ('HIGH', 'MEDIUM', 'LOW'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: task_status_types
-- Stores predefined status types for tasks
CREATE TABLE IF NOT EXISTS task_status_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL UNIQUE,
    CONSTRAINT chk_status_type CHECK (type IN ('OPEN', 'IN_PROGRESS', 'HOLD', 'DONE', 'CLOSED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: tasks
-- Main table for storing task information
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_title VARCHAR(255) NOT NULL,
    description TEXT,
    create_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_status_change_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    priority_id BIGINT NOT NULL,
    task_status_id BIGINT NOT NULL,
    
    -- Foreign key constraints
    CONSTRAINT fk_tasks_priority 
        FOREIGN KEY (priority_id) REFERENCES priority_types(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    
    CONSTRAINT fk_tasks_status 
        FOREIGN KEY (task_status_id) REFERENCES task_status_types(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    
    -- Indexes for better performance
    INDEX idx_tasks_priority (priority_id),
    INDEX idx_tasks_status (task_status_id),
    INDEX idx_tasks_create_date (create_date),
    INDEX idx_tasks_status_change_date (last_status_change_date),
    INDEX idx_tasks_title (task_title),
    
    -- Full-text index for search functionality
    FULLTEXT INDEX ft_idx_tasks_search (task_title, description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert default priority types
INSERT IGNORE INTO priority_types (type) VALUES 
    ('HIGH'),
    ('MEDIUM'),
    ('LOW');

-- Insert default task status types
INSERT IGNORE INTO task_status_types (type) VALUES 
    ('OPEN'),
    ('IN_PROGRESS'),
    ('HOLD'),
    ('DONE'),
    ('CLOSED');

-- Create a view for task statistics
CREATE OR REPLACE VIEW task_statistics AS
SELECT 
    COUNT(*) as total_tasks,
    SUM(CASE WHEN tst.type IN ('DONE', 'CLOSED') THEN 1 ELSE 0 END) as completed_tasks,
    SUM(CASE WHEN tst.type IN ('OPEN', 'IN_PROGRESS', 'HOLD') THEN 1 ELSE 0 END) as active_tasks,
    SUM(CASE WHEN pt.type = 'HIGH' THEN 1 ELSE 0 END) as high_priority_tasks,
    SUM(CASE WHEN pt.type = 'MEDIUM' THEN 1 ELSE 0 END) as medium_priority_tasks,
    SUM(CASE WHEN pt.type = 'LOW' THEN 1 ELSE 0 END) as low_priority_tasks
FROM tasks t
JOIN task_status_types tst ON t.task_status_id = tst.id
JOIN priority_types pt ON t.priority_id = pt.id;

-- Create a view for task details with type names
CREATE OR REPLACE VIEW task_details AS
SELECT 
    t.id,
    t.task_title,
    t.description,
    t.create_date,
    t.last_status_change_date,
    pt.type as priority_type,
    tst.type as status_type,
    pt.id as priority_id,
    tst.id as status_id
FROM tasks t
JOIN priority_types pt ON t.priority_id = pt.id
JOIN task_status_types tst ON t.task_status_id = tst.id;

-- Create stored procedure for task status change audit
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS UpdateTaskStatus(
    IN task_id BIGINT,
    IN new_status_id BIGINT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    UPDATE tasks 
    SET task_status_id = new_status_id,
        last_status_change_date = CURRENT_TIMESTAMP
    WHERE id = task_id;
    
    COMMIT;
END //
DELIMITER ;

-- Create function to get task count by status
DELIMITER //
CREATE FUNCTION IF NOT EXISTS GetTaskCountByStatus(status_name VARCHAR(50))
RETURNS INT
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE task_count INT DEFAULT 0;
    
    SELECT COUNT(*) INTO task_count
    FROM tasks t
    JOIN task_status_types tst ON t.task_status_id = tst.id
    WHERE tst.type = status_name;
    
    RETURN task_count;
END //
DELIMITER ;

-- Create trigger to automatically update last_status_change_date
DELIMITER //
CREATE TRIGGER IF NOT EXISTS tr_tasks_status_update
    BEFORE UPDATE ON tasks
    FOR EACH ROW
BEGIN
    IF OLD.task_status_id != NEW.task_status_id THEN
        SET NEW.last_status_change_date = CURRENT_TIMESTAMP;
    END IF;
END //
DELIMITER ;
