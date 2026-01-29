-- Insert an admin user for testing
-- Password is: admin123 (BCrypt encoded)
INSERT INTO users (username, email, password, role, active) 
VALUES ('admin', 'admin@cms.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'ADMIN', 1);

-- Insert a director user for testing  
-- Password is: director123
INSERT INTO users (username, email, password, role, active)
VALUES ('director', 'director@cms.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'DIRECTOR', 1);

-- Insert an engineer user for testing
-- Password is: engineer123  
INSERT INTO users (username, email, password, role, active)
VALUES ('engineer', 'engineer@cms.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'ENGINEER', 1);
