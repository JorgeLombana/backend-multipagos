-- MultiPagos Database Initialization Script

-- Crear base de datos
DROP DATABASE IF EXISTS multipagos;
CREATE DATABASE multipagos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Crear usuario específico
DROP USER IF EXISTS 'multipagos_user'@'localhost';
CREATE USER 'multipagos_user'@'localhost' IDENTIFIED BY 'MultiPagos2024!';

-- Otorgar permisos
GRANT ALL PRIVILEGES ON multipagos.* TO 'multipagos_user'@'localhost';
FLUSH PRIVILEGES;

-- Confirmar creación
SELECT 'Database and user created successfully' AS status;
