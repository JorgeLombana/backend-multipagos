-- Script SQL para inicializar la base de datos multipagos
CREATE DATABASE IF NOT EXISTS multipagos 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE multipagos;

-- Crear tabla de usuarios
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(15),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Crear tabla de proveedores (suppliers)
CREATE TABLE IF NOT EXISTS suppliers (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertar proveedores de Puntored
INSERT INTO suppliers (id, name) VALUES
('8753', 'Claro'),
('9773', 'Movistar'),
('3398', 'Tigo'),
('4689', 'WOM')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Crear tabla de transacciones
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transactional_id VARCHAR(255) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    supplier_id VARCHAR(10) NOT NULL,
    cell_phone VARCHAR(10) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status ENUM('PENDING', 'SUCCESS', 'FAILED') DEFAULT 'PENDING',
    external_response TEXT,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE RESTRICT,
    
    INDEX idx_user_transactions (user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_status (status)
);

-- Crear un usuario de prueba (password: 'password' hasheado con BCrypt)
INSERT INTO users (username, email, password, first_name, last_name, phone) VALUES
('testuser', 'test@multipagos.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Usuario', 'Prueba', '3001234567')
ON DUPLICATE KEY UPDATE email = VALUES(email);
