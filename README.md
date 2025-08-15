# MultiPagos - Portal Transaccional

> **Desafío Técnico: Portal transaccional integral para recargas móviles con integración a API Puntored**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19.1.1-blue.svg)](https://reactjs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.8.3-blue.svg)](https://www.typescriptlang.org/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)

---

## Tabla de Contenidos

- [Instalación y Configuración](#instalación-y-configuración)
- [Levantar el Proyecto](#levantar-el-proyecto)
- [Endpoints de la API](#endpoints-de-la-api)
- [Descripción del Proyecto](#descripción-del-proyecto)
- [Niveles Implementados](#niveles-implementados)
- [Tecnologías](#tecnologías)
- [Estructura del Proyecto](#estructura-del-proyecto)

---

## Instalación y Configuración

### Prerrequisitos

**IMPORTANTE: Verificar que tienes instalado:**

```bash
Java 21+          # java -version
Maven 3.8+        # mvn -version (o usar ./mvnw incluido)
Node.js 20+       # node --version
npm 9+            # npm --version  
MySQL 8.0+        # mysql --version
Git               # git --version
```

### 1. Clonar el Repositorio

```bash
git clone https://github.com/JorgeLombana/multi-pagos.git
cd multi-pagos
```

### 2. Configuración de Base de Datos

#### 2.1 Iniciar MySQL

```bash
# Windows
net start mysql

# macOS
brew services start mysql

# Linux
sudo systemctl start mysql
```

#### 2.2 Ejecutar Script de Inicialización

```bash
# Conectar a MySQL como root
mysql -u root -p

# Ejecutar el script init.sql (ubicado en /database/init.sql)
```

**Contenido del script de inicialización (`database/init.sql`):**

```sql
-- Crear base de datos
CREATE DATABASE IF NOT EXISTS multipagos 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos
USE multipagos;

-- Crear usuario específico (opcional pero recomendado)
CREATE USER IF NOT EXISTS 'multipagos_user'@'localhost' 
IDENTIFIED BY 'YOUR_SECURE_PASSWORD';

GRANT ALL PRIVILEGES ON multipagos.* TO 'multipagos_user'@'localhost';
FLUSH PRIVILEGES;

-- Las tablas se crean automáticamente por Hibernate al iniciar el backend
-- Estructura que se creará automáticamente:
-- - users: Gestión de usuarios y autenticación
-- - transactions: Almacenamiento de transacciones de recargas

-- Verificar creación
SELECT 'Base de datos multipagos creada exitosamente' AS status;
```

#### 2.3 Verificar Configuración

```sql
# Verificar que la base de datos existe
SHOW DATABASES;

# Salir de MySQL
EXIT;

# Probar conexión con nuevo usuario (opcional)
mysql -u multipagos_user -p multipagos
```

### 3. Configuración del Backend

#### 3.1 Ajustar Conexión a Base de Datos (Si es necesario)

Editar `multipagos-backend/src/main/resources/application.properties`:

```properties
# Configuración de Base de Datos - AJUSTAR SEGÚN TU CONFIGURACIÓN
spring.datasource.url=jdbc:mysql://localhost:3306/multipagos?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD

# O si creaste el usuario específico:
# spring.datasource.username=multipagos_user  
# spring.datasource.password=YOUR_SECURE_PASSWORD

# Puntored API - CONFIGURAR CON TUS CREDENCIALES
puntored.api.base-url=https://your-puntored-api-url/api
puntored.api.key=YOUR_PUNTORED_API_KEY
puntored.api.username=YOUR_PUNTORED_USERNAME
puntored.api.password=YOUR_PUNTORED_PASSWORD

# Configuración JPA - Para crear tablas automáticamente
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

### 4. Configuración del Frontend

#### 4.1 Crear archivo de variables de entorno

```bash
# Ir al directorio del frontend
cd multipagos-frontend

# Crear archivo .env
echo "VITE_API_BASE_URL=http://localhost:8080" > .env
echo "VITE_APP_NAME=MultiPagos" >> .env
```

---

## Levantar el Proyecto

### ORDEN CRÍTICO DE EJECUCIÓN

#### 1. PRIMERO: Levantar Backend

```bash
# Abrir terminal en el directorio raíz del proyecto
cd multipagos-backend

# Compilar y ejecutar (esto creará las tablas automáticamente)
./mvnw spring-boot:run

# En Windows usar: mvnw.cmd spring-boot:run

# ESPERAR hasta ver estos mensajes:
# "Started MultipagosBackendApplication in X.XXX seconds"
# "Tomcat started on port(s): 8080 (http)"
```

#### 2. VERIFICAR Backend

```bash
# En otra terminal, verificar que el backend responde:
curl http://localhost:8080/api/v1/suppliers

# Debe devolver la lista de proveedores:
# [{"id":"8753","name":"Claro"},{"id":"9773","name":"Movistar"},{"id":"3398","name":"Tigo"},{"id":"4689","name":"WOM"}]
```

#### 3. SEGUNDO: Levantar Frontend

```bash
# En nueva terminal, ir al frontend
cd multipagos-frontend

# Instalar dependencias
npm install

# Iniciar servidor de desarrollo
npm run dev

# ESPERAR hasta ver:
# "Local: http://localhost:5173/"
```

#### 4. VERIFICAR Aplicación Completa

```bash
# Abrir navegador en: http://localhost:5173
# Debe cargar la interfaz de MultiPagos

# Probar funcionalidades:
# 1. Registro de usuario
# 2. Login
# 3. Ver proveedores de recargas
# 4. Realizar recarga de prueba (usar datos válidos)
```

### Solución de Problemas Comunes

#### Backend no inicia:
```bash
# Verificar que MySQL esté corriendo
mysql -u root -p -e "SELECT VERSION();"

# Verificar logs del backend para errores de conexión
```

#### Frontend no se conecta al backend:
```bash
# Verificar que el backend esté en puerto 8080
curl http://localhost:8080/api/v1/suppliers

# Verificar archivo .env del frontend
cat .env
```

---

## Endpoints de la API

**Base URL:** `http://localhost:8080/api/v1`

### Autenticación (Sistema Propio)

#### POST /auth/register
Registrar nuevo usuario en el sistema.

**Request:**
```json
{
  "name": "Juan Pérez",
  "email": "user@example.com",
  "password": "SecurePass123@"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Usuario registrado exitosamente",
  "data": "Usuario registrado exitosamente"
}
```

#### POST /auth/login
Autenticación en el sistema (genera JWT).

**Request:**
```json
{
  "email": "user@example.com", 
  "password": "SecurePass123@"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "user": {
      "id": 1,
      "name": "Juan Pérez",
      "email": "user@example.com"
    }
  }
}
```

### Proveedores de Recargas (Integración con Puntored)

#### GET /suppliers
Obtener lista de proveedores disponibles.

**Response:**
```json
{
  "success": true,
  "message": "Proveedores obtenidos exitosamente",
  "data": [
    {"id": "8753", "name": "Claro"},
    {"id": "9773", "name": "Movistar"}, 
    {"id": "3398", "name": "Tigo"},
    {"id": "4689", "name": "WOM"}
  ]
}
```

### Recargas (Requiere Authorization: Bearer {token})

#### POST /topup
Realizar recarga móvil (integra con Puntored API /buy).

**Headers:**
```
Authorization: Bearer {jwt-token}
Content-Type: application/json
```

**Request:**
```json
{
  "cellPhone": "3012345678",
  "value": 10000,
  "supplierId": "8753"
}
```

**Validaciones aplicadas:**
- `cellPhone`: Debe iniciar en "3", exactamente 10 dígitos
- `value`: Entre $1,000 y $100,000
- `supplierId`: Debe ser uno de [8753, 9773, 3398, 4689]

**Response Exitosa:**
```json
{
  "success": true,
  "message": "Recarga procesada exitosamente",
  "data": {
    "id": "123",
    "cellPhone": "3012345678",
    "value": 10000,
    "supplierName": "Claro", 
    "status": "SUCCESS",
    "transactionalID": "4e8e273d-73a1-4ea5-8c2e-e45bf556d03a",
    "createdAt": "2025-08-14T15:30:00Z",
    "message": "Recarga exitosa"
  }
}
```

**Errores posibles (según Puntored):**
```json
// Error número teléfono
{
  "success": false,
  "message": "\"cellPhone\" length must be 10 characters long"
}

// Error valor
{
  "success": false, 
  "message": "\"value\" must be greater than or equal to 1000"
}

// Error proveedor
{
  "success": false,
  "message": "\"supplierId\" must be one of [8753, 9773, 3398, 4689]"
}
```

### Historial de Transacciones

#### GET /topup/history
Consultar historial de transacciones del usuario autenticado.

**Headers:**
```
Authorization: Bearer {jwt-token}
```

**Query Parameters:**
- `page`: Número de página (default: 0)
- `size`: Tamaño de página (default: 20)
- `sortField`: Campo de ordenamiento (default: createdAt)
- `sortDirection`: Dirección (ASC/DESC, default: DESC)

**Response:**
```json
{
  "success": true,
  "message": "Transacciones obtenidas exitosamente",
  "data": {
    "content": [
      {
        "id": "123",
        "cellPhone": "3012345678", 
        "value": 10000,
        "supplierName": "Claro",
        "status": "SUCCESS",
        "transactionalID": "4e8e273d-73a1-4ea5-8c2e-e45bf556d03a",
        "createdAt": "2025-08-14T15:30:00Z",
        "message": "Recarga exitosa"
      }
    ],
    "pageable": {
      "page": 0,
      "size": 20,
      "totalElements": 1,
      "totalPages": 1
    }
  }
}
```

### Health Check

#### GET /health
Verificar estado del sistema.

**Response:**
```json
{
  "success": true,
  "message": "Sistema operativo",
  "data": {
    "status": "UP",
    "timestamp": "2025-08-14T15:30:00Z",
    "services": {
      "database": "UP",
      "puntored": "UP"
    }
  }
}
```

---

## Descripción del Proyecto

### Contexto del Desafío Técnico

**MultiPagos** es el resultado de un desafío técnico para desarrollar un **portal transaccional integral** que ofrezca recargas a operadores móviles, pagos de servicios, compra de pines de contenido y transferencias bancarias. 

**El desafío inicial** se enfoca en crear el primer módulo de **recargas móviles**, consumiendo los servicios API de **Puntored** mediante:
- Una **API en Spring Boot** que se conecte a los servicios de Puntored
- Un **frontend en React** que permita interactuar con la API para realizar recargas móviles
- Un **módulo de login** con sistema de autenticación propio (implementado)
- **Despliegue en la nube** para obtener puntos adicionales (opcional)

### Objetivos Cumplidos

**Desarrollar una API Spring Boot** que se conecte a los servicios de Puntored:
- auth (POST): Obtener token de tipo "Bearer" ✓
- getSuppliers (GET): Listar proveedores de recargas disponibles ✓  
- buy (POST): Realizar compra de recarga o transacción ✓

**Implementar frontend en React** que permita:
- Realizar recargas de operadores móviles ✓
- Mostrar resumen y ticket de transacción ✓
- Consultar transacciones realizadas ✓
- Sistema de autenticación propio ✓

**Almacenamiento de datos:**
- Guardar información de cada transacción en base de datos ✓
- Consulta de histórico transaccional ✓

### Reglas de Negocio Implementadas

Según especificaciones del desafío:
- **Valor mínimo de transacción**: $1,000 ✓
- **Valor máximo de transacción**: $100,000 ✓
- **Número de teléfono**: Debe iniciar en "3", tener longitud de 10 caracteres y solo aceptar valores numéricos ✓

### Integración con API de Puntored

**URL Base:** `https://your-puntored-api-url/api`

**Credenciales configuradas:**
```
API Key: YOUR_PUNTORED_API_KEY
Usuario: YOUR_PUNTORED_USERNAME  
Password: YOUR_PUNTORED_PASSWORD
```

**Proveedores soportados:**
- Claro (ID: 8753)
- Movistar (ID: 9773) 
- Tigo (ID: 3398)
- WOM (ID: 4689)

---

## Niveles Implementados

### ✓ Nivel 0: API Spring Boot
- **Completado:** API Java con Spring Boot que integra servicios de Puntored (auth, getSuppliers, buy)
- **Extra:** Tests unitarios implementados
- **Arquitectura:** Hexagonal/Clean Architecture con separación de responsabilidades

### ✓ Nivel 1: Base de Datos
- **Completado:** Almacenamiento de información transaccional en MySQL
- **Completado:** Listar transacciones realizadas por el usuario (consulta de histórico)
- **Extra:** Paginación, filtros y búsqueda avanzada

### ✓ Nivel 2: Frontend React
- **Completado:** Frontend React que consume la API del Nivel 0
- **Completado:** Permitir al usuario realizar recargas de operadores móviles
- **Completado:** Al finalizar compra, mostrar resumen y ticket retornado por Puntored
- **Completado:** Módulo de consulta de transacciones realizadas
- **Extra:** Módulo de login con autenticación propia (JWT)
- **Extra:** Tests unitarios para componentes críticos

### Nivel 3: Despliegue en Nube
- **Estado:** Preparado para despliegue (AWS/GCP/Azure)
- **Configuración:** CloudWatch logs, variables de entorno, Docker ready

### ✓ Nivel 4: Logs y Monitoreo (Extra)
- **Completado:** Sistema de logs estructurados con Spring Boot Actuator
- **Completado:** Monitoreo de health checks y métricas
- **Preparado:** Integración con AWS CloudWatch

### ✓ Nivel 5: Pruebas Automatizadas (Extra)
- **Completado:** Tests unitarios para servicios críticos del backend
- **Completado:** Tests de integración para endpoints principales
- **Completado:** Tests unitarios para componentes React principales

---

## Tecnologías

### Backend Stack
```yaml
Framework: Spring Boot 3.5.4
Lenguaje: Java 21
Base de Datos: MySQL 8.0+
Seguridad: JWT + BCrypt
Arquitectura: Hexagonal (Clean Architecture)
Testing: JUnit 5 + Mockito
Documentación: README detallado + API docs
Monitoreo: Spring Boot Actuator
```

### Frontend Stack  
```yaml
Framework: React 19.1.1
Lenguaje: TypeScript 5.8.3
Estilos: Tailwind CSS 4.1.12  
Componentes: Radix UI
Gestión Estado: React Context + Custom Hooks
Formularios: React Hook Form + Zod
Cliente HTTP: Axios
Build Tool: Vite 7.1.2
Testing: Jest + React Testing Library
```

### DevOps & Database
```yaml
Base de Datos: MySQL 8.0+ con JPA/Hibernate
ORM: Hibernate con DDL automático
Logs: Estructurados para CloudWatch
Containerización: Docker ready
CI/CD: GitHub Actions ready
Monitoreo: Health checks + métricas
```

---

## Estructura del Proyecto

### Entregables por Nivel

```
multi-pagos/
├── multipagos-backend/              # 📦 Nivel 0: Código Fuente Backend
│   ├── src/main/java/
│   │   ├── auth/                    # Sistema de autenticación propio  
│   │   ├── topup/                   # Módulo de recargas (integración Puntored)
│   │   └── shared/                  # Configuraciones y utilidades
│   ├── src/test/                    # 🧪 Tests unitarios
│   └── pom.xml                      # Dependencias Maven
│
├── database/                        # 📊 Nivel 1: Scripts de Base de Datos
│   ├── init.sql                     # Script de inicialización MySQL
│   └── README.md                    # Instrucciones de base de datos
│
├── multipagos-frontend/             # ⚛️ Nivel 2: Código Fuente Frontend  
│   ├── src/
│   │   ├── components/              # Componentes reutilizables
│   │   ├── pages/                   # Páginas (Login, Dashboard)
│   │   ├── services/                # Servicios API (integración backend)
│   │   └── contexts/                # Contexto de autenticación
│   ├── src/__tests__/               # 🧪 Tests unitarios React
│   └── package.json                 # Dependencias NPM
│
├── README.md                        # 📖 Nivel 4: Instrucciones de Ejecución
└── docs/                           # 📚 Documentación adicional
```

### Funcionalidades Implementadas

#### Autenticación y Seguridad
- **Registro de usuarios** con validación de email único
- **Login seguro** con JWT tokens
- **Protección de rutas** en frontend y backend
- **Rate limiting** y validaciones de entrada
- **Encriptación BCrypt** para contraseñas

#### Módulo de Recargas
- **Integración completa** con API Puntored (auth, getSuppliers, buy)
- **Validación de reglas de negocio** (teléfono, montos, proveedores)
- **Selección de operadores** con imágenes y colores distintivos
- **Confirmación de transacciones** con ticket detallado
- **Estados en tiempo real** (Pending, Success, Failed)

#### Historial y Consultas  
- **Lista paginada** de transacciones por usuario
- **Filtros avanzados** por fecha, estado y teléfono
- **Búsqueda rápida** y ordenamiento
- **Exportación de datos** (preparado)
- **Métricas y estadísticas** transaccionales

#### Interfaz de Usuario
- **Diseño responsivo** mobile-first
- **Componentes reutilizables** con Radix UI
- **Estados de loading** y skeleton screens
- **Manejo de errores** user-friendly
- **Animaciones fluidas** con Tailwind CSS

### Arquitectura Técnica

**Backend - Hexagonal Architecture:**
```
Domain Layer (Reglas de Negocio)
├── Entities: User, Transaction, Supplier
├── Value Objects: PhoneNumber, Amount, SupplierId  
├── Business Rules: Validaciones de Puntored
└── Domain Services: Lógica transaccional

Application Layer (Casos de Uso)
├── Auth Service: Login, Register, JWT
├── TopUp Service: Procesar recargas
├── Transaction Service: Consultas e histórico
└── Supplier Service: Gestión de proveedores

Infrastructure Layer (Adaptadores)
├── Puntored Adapter: Integración API externa
├── JPA Repositories: Persistencia MySQL
├── JWT Generator: Generación y validación tokens
└── REST Controllers: Exposición API

Presentation Layer (Interfaz)
├── REST API: Endpoints JSON
├── Error Handling: Respuestas estructuradas
├── Security Filters: Rate limiting, CORS
└── Documentation: API specs
```

**Frontend - Component Architecture:**
```
Pages (Páginas principales)
├── LoginPage: Autenticación de usuarios
├── RegisterPage: Registro de nuevos usuarios  
└── DashboardPage: Panel principal

Components (Reutilizables)
├── TopupModule: Interfaz de recargas
├── HistoryModule: Historial transaccional
├── Sidebar: Navegación lateral
└── UI Components: Botones, inputs, cards

Services (Integración API)
├── authService: Login, register, logout
├── topupService: Recargas, proveedores, historial
└── apiClient: Cliente HTTP configurado

Contexts (Estado Global)
├── AuthContext: Usuario autenticado
└── ThemeContext: Modo claro/oscuro

Utils (Utilidades)
├── Validations: Esquemas Zod
├── Formatters: Moneda, fechas, teléfonos  
└── Constants: URLs, límites, configuraciones
```

---

## Información Adicional del Proyecto

### Cumplimiento de Especificaciones

**✓ Integración con API de Puntored:**
- auth (POST): Obtener token Bearer - Implementado
- getSuppliers (GET): Listar proveedores - Implementado  
- buy (POST): Realizar compra - Implementado

**✓ Reglas de Negocio:**
- Valor mínimo: $1,000 - Validado
- Valor máximo: $100,000 - Validado
- Teléfono: Inicia en "3", 10 dígitos, solo números - Validado

**✓ Almacenamiento de Datos:**
- Información transaccional guardada - Implementado
- Consulta de histórico - Implementado con paginación

### Entrega del Proyecto

**✓ 1. Código Fuente Backend (Nivel 0)**
- Ubicación: `multipagos-backend/`
- Spring Boot 3.5.4 con Java 21
- Arquitectura hexagonal completa
- Tests unitarios incluidos

**✓ 2. Scripts de Base de Datos (Nivel 1)**  
- Ubicación: `database/init.sql`
- Inicialización automática de MySQL
- Creación de usuario específico
- Documentación completa

**✓ 3. Código Fuente Frontend (Nivel 2)**
- Ubicación: `multipagos-frontend/`  
- React 19.1.1 + TypeScript 5.8.3
- Sistema de autenticación implementado
- Módulo de recargas y consultas

**✓ 4. Instrucciones de Ejecución**
- README principal con guía paso a paso
- README específico para cada módulo
- Solución de problemas comunes

**✓ 5. Repositorio GitHub**
- Estructura organizada por niveles
- Control de versiones completo
- Sin archivos comprimidos

### URLs del Proyecto

**Desarrollo Local:**
- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8080/api/v1
- **Health Check**: http://localhost:8080/api/v1/health

**Repositorio:**
- **GitHub**: https://github.com/JorgeLombana/multi-pagos

### Niveles Adicionales Implementados

**✓ Nivel 4: Sistema de Logs y Monitoreo**
- Spring Boot Actuator configurado
- Logs estructurados con formato profesional
- Logging por niveles (INFO, WARN, ERROR) en todos los controladores
- Trazabilidad completa de operaciones críticas (auth, recargas, transacciones)
- Health checks automatizados (/actuator/health)
- Formato optimizado para AWS CloudWatch
- Configuración preparada para producción y desarrollo

**✓ Nivel 5: Pruebas Automatizadas**
- Tests unitarios backend (JUnit 5 + Mockito)
- Tests de integración para endpoints
- Tests unitarios frontend (Jest + React Testing Library)
- Cobertura de casos críticos

### Comandos de Verificación

**Verificar Sistema de Logs:**
```bash
# Iniciar backend y verificar logs en tiempo real
cd multipagos-backend
./mvnw spring-boot:run

# En otra terminal - probar endpoints y ver logs
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test@test.com","password":"123456"}'

# Verificar health check y métricas
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/info
curl http://localhost:8080/actuator/metrics
```

**Verificar Frontend:**
```bash
# Verificar build
npm run build

# Verificar linting
npm run lint

# Ejecutar tests
npm test
```

### Características Destacadas

**Seguridad y Monitoreo Implementados:**
- JWT Authentication con expiración configurable
- BCrypt para hashing de contraseñas  
- Rate limiting por IP
- Validación y sanitización de entrada
- CORS configurado específicamente
- Headers de seguridad (XSS, CSRF protection)
- **Sistema de logging completo con trazabilidad**
  - Logs estructurados por módulos: `[AUTH CONTROLLER]`, `[TOPUP CONTROLLER]`
  - Niveles apropiados: INFO para operaciones, WARN para validaciones, ERROR para excepciones
  - Contexto detallado: emails, IDs, mensajes de error (sin datos sensibles)
  - Formato optimizado para CloudWatch: timestamps, niveles, componentes
- **Monitoreo con Spring Actuator:**
  - Health checks: `/actuator/health`
  - Métricas de aplicación: `/actuator/metrics`
  - Info de aplicación: `/actuator/info`

**Performance y Escalabilidad:**
- Paginación en consultas de histórico
- Lazy loading en componentes React
- Conexiones de base de datos optimizadas
- Caching de proveedores
- Build optimizado para producción

**Monitoreo y Observabilidad:**
- **Logging estructurado** con niveles configurables
  - Formato para desarrollo: `%d{HH:mm:ss.SSS} %highlight(%-5level) %cyan(%-20.20c{0}) %msg%n`
  - Formato para producción/CloudWatch: `%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level %-20.20c{0} %msg%n`
  - Contexto por operación: `[AUTH CONTROLLER] Registration request for email: user@example.com`
  - Trazabilidad completa: request → validation → business logic → response
- **Métricas de aplicación** con Actuator
  - Health checks automatizados (`/actuator/health`)
  - Métricas del sistema (`/actuator/metrics`)  
  - Información de la aplicación (`/actuator/info`)
- **Error tracking y alertas**
  - Captura de excepciones con contexto completo
  - Logging de errores sin exposición de datos sensibles
- **Preparado para herramientas de APM**
  - Compatible con AWS CloudWatch Logs
  - Estructura JSON ready para ELK Stack
  - Formato compatible con Grafana/Prometheus

---

### Instalación Rápida (Resumen)

**Pre-requisitos:** Java 21, Node.js 20, MySQL 8, Maven 3.8

```bash
# 1. Clonar y configurar
git clone https://github.com/JorgeLombana/multi-pagos.git
cd multi-pagos

# 2. Inicializar base de datos
mysql -u root -p < database/init.sql

# 3. Iniciar backend  
cd multipagos-backend && ./mvnw spring-boot:run

# 4. Iniciar frontend (nueva terminal)
cd multipagos-frontend && npm install && npm run dev

# 5. Abrir aplicación
# http://localhost:5173
```

**¡El proyecto está listo para evaluación!** ✅

---

**Desarrollado como parte del desafío técnico para MultiPagos**  
**Niveles completados:** 0, 1, 2 + Extras (4, 5)  
**Tecnologías:** Spring Boot + React + TypeScript + MySQL  
**Integración:** API Puntored completa
