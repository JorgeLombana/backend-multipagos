# Multi-Pagos - Portal Transaccional de Recargas Móviles

**Desafío Técnico - Integración con API Puntored**

Sistema completo de recargas móviles desarrollado como parte del desafío técnico para crear un portal transaccional integral. Implementa backend en Spring Boot y frontend en React + TypeScript, integrando los servicios API de Puntored para recargas móviles.

## Niveles Implementados

- **Nivel 0**: ✅ API Spring Boot con integración Puntored (auth, getSuppliers, buy)
- **Nivel 1**: ✅ Base de datos MySQL con almacenamiento transaccional e histórico
- **Nivel 2**: ✅ Frontend React con módulo de recargas, consulta de transacciones y autenticación
- **Nivel 3**: ⚠️ Despliegue en nube (opcional - no implementado)
- **Nivel 4**: ✅ Sistema de logs y monitoreo con Spring Boot Actuator
- **Nivel 5**: ✅ Tests unitarios implementados

## Requisitos del Sistema

### Software Necesario

- **Java**: 21 o superior (OpenJDK recomendado)
- **Node.js**: 18.0+ (recomendado 20.x LTS)
- **npm**: 9.0+ (incluido con Node.js)
- **Maven**: 3.6+ (incluido con Java)
- **MySQL**: 8.0+
- **Git**: Para clonación del repositorio

### Verificar Instalaciones

```bash
# Verificar versiones instaladas
java -version
node --version
npm --version
mvn --version
mysql --version
```

## Stack Tecnológico

### Backend (Spring Boot)

- **Spring Boot**: 3.5.4
- **Spring Data JPA**: Persistencia de datos
- **MySQL Connector**: Base de datos
- **JWT (JJWT)**: 0.12.6 - Autenticación
- **Spring Security Crypto**: Encriptación BCrypt
- **Bucket4j**: 7.6.0 - Rate limiting
- **OWASP Encoder**: 1.2.3 - Sanitización de entrada
- **Lombok**: Reducción de código boilerplate
- **Spring Boot Actuator**: Monitoreo y health checks

### Frontend (React + TypeScript)

- **React**: 19.1.1
- **TypeScript**: 5.8.3
- **Vite**: 7.1.2 - Build tool y dev server
- **TailwindCSS**: 4.1.12 - Estilos
- **React Router DOM**: 7.8.0 - Navegación
- **React Hook Form**: 7.62.0 - Manejo de formularios
- **Zod**: 4.0.17 - Validación de esquemas
- **Axios**: 1.11.0 - Cliente HTTP
- **TanStack Query**: 5.85.3 - Estado del servidor
- **Radix UI**: Componentes accesibles
- **Lucide React**: Iconos
- **React Hot Toast**: Notificaciones

## Guía de Instalación Completa

### IMPORTANTE: Pasos Críticos para Evitar Errores

### 1. Verificar Requisitos del Sistema

```bash
# Verificar versiones EXACTAS requeridas
java -version    # Debe ser Java 21+
node --version   # Debe ser Node.js 18.0+
npm --version    # Debe ser npm 9.0+
mysql --version  # Debe ser MySQL 8.0+

# Si alguna versión no cumple, instalar la correcta ANTES de continuar
```

### 2. Clonar el Repositorio

```bash
git clone https://github.com/JorgeLombana/multipagos
cd multi-pagos

# Verificar estructura del proyecto
ls -la
# Debe mostrar: multipagos-backend/ multipagos-frontend/ README.md
```

### 3. CONFIGURACIÓN CRÍTICA DE BASE DE DATOS

#### 3.1 Iniciar MySQL (OBLIGATORIO)

```bash
# Windows
net start mysql
# O desde Services.msc buscar MySQL y iniciar

# macOS
brew services start mysql
# O: sudo /usr/local/mysql/support-files/mysql.server start

# Linux
sudo systemctl start mysql
sudo systemctl enable mysql  # Para que inicie automáticamente
```

#### 3.2 Crear Base de Datos y Usuario

```sql
# PASO 1: Conectar como root
mysql -u root -p
# Introducir contraseña de root de MySQL

# PASO 2: Crear base de datos (OBLIGATORIO)
CREATE DATABASE multipagos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# PASO 3: Crear usuario específico (RECOMENDADO para producción)
CREATE USER 'multipagos_user'@'localhost' IDENTIFIED BY 'MultiPagos2024!';
GRANT ALL PRIVILEGES ON multipagos.* TO 'multipagos_user'@'localhost';
FLUSH PRIVILEGES;

# PASO 4: Verificar creación
SHOW DATABASES;
SELECT User, Host FROM mysql.user WHERE User = 'multipagos_user';

# PASO 5: Salir
EXIT;
```

#### 3.3 Probar Conexión

```bash
# Probar conexión con nuevo usuario
mysql -u multipagos_user -p multipagos
# Debe conectar sin errores
```

### 4. CONFIGURACIÓN DEL BACKEND

#### 4.1 Configuración application.properties

El archivo `multipagos-backend/src/main/resources/application.properties` incluye toda la configuración necesaria:

**Configuración de Puntored (YA CONFIGURADA)**:
```properties
puntored.api.base-url=https://us-central1-puntored-dev.cloudfunctions.net/technicalTest-developer/api
puntored.api.key=mtrQF6Q11eosqyQnkMY0JGFbGqcxVg5icvfVnX1ifIyWDvwGApJ8WUM8nHVrdSkN
puntored.api.username=user0147
puntored.api.password=#3Q34Sh0NlDS
```

**Configuración de Base de Datos (AJUSTAR SI ES NECESARIO)**:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/multipagos?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=jorge
```

**IMPORTANTE**: Si tu configuración de MySQL es diferente, ajusta únicamente las líneas de base de datos. Las credenciales de Puntored ya están configuradas según las especificaciones del desafío.

#### 4.2 Variables de Entorno (OPCIONAL pero RECOMENDADO)

```bash
# Windows (CMD)
set JWT_SECRET=tu-clave-super-secreta-de-256-bits-minimo-para-jwt-authentication
set CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173

# Windows (PowerShell)
$env:JWT_SECRET="tu-clave-super-secreta-de-256-bits-minimo-para-jwt-authentication"
$env:CORS_ALLOWED_ORIGINS="http://localhost:3000,http://localhost:5173"

# macOS/Linux
export JWT_SECRET="tu-clave-super-secreta-de-256-bits-minimo-para-jwt-authentication"
export CORS_ALLOWED_ORIGINS="http://localhost:3000,http://localhost:5173"
```

### 5. CONFIGURACIÓN DEL FRONTEND

#### 5.1 Crear archivo .env

```bash
cd multipagos-frontend

# Crear archivo .env (si no existe)
echo "VITE_API_BASE_URL=http://localhost:8080/api/v1" > .env
echo "VITE_API_TIMEOUT=10000" >> .env
echo "VITE_APP_NAME=Multi-Pagos" >> .env
echo "VITE_APP_VERSION=1.0.0" >> .env
echo "VITE_DEV_TOOLS=true" >> .env
```

### 6. INSTALACIÓN DE DEPENDENCIAS

#### 6.1 Backend (Spring Boot)

```bash
cd multipagos-backend

# PASO 1: Limpiar y compilar (OBLIGATORIO)
./mvnw clean compile
# En Windows: mvnw.cmd clean compile

# PASO 2: Verificar que no hay errores de compilación
# Si hay errores, revisar:
# - Java version (debe ser 21+)
# - Conexión a internet (para descargar dependencias)
# - Permisos de escritura en directorio target/

# PASO 3: Ejecutar tests (RECOMENDADO)
./mvnw test
# Si fallan tests de base de datos, verificar que MySQL esté corriendo

# PASO 4: Verificar dependencias críticas
./mvnw dependency:tree | grep -E "(mysql|spring-boot|jwt)"
```

#### 6.2 Frontend (React + TypeScript)

```bash
cd multipagos-frontend

# PASO 1: Limpiar cache (si existe instalación previa)
rm -rf node_modules package-lock.json
# Windows: rmdir /s node_modules & del package-lock.json

# PASO 2: Instalar dependencias
npm install
# Si falla, probar: npm install --legacy-peer-deps

# PASO 3: Verificar instalación
npm run lint
# Debe ejecutar sin errores

# PASO 4: Verificar build
npm run build
# Debe generar carpeta dist/ sin errores
```

## EJECUCIÓN DEL PROYECTO

### ORDEN DE EJECUCIÓN IMPORTANTE

#### 7.1 PRIMERO: Iniciar Backend

```bash
cd multipagos-backend

# IMPORTANTE: Verificar que MySQL esté corriendo ANTES de iniciar
# Windows: net start mysql
# macOS: brew services start mysql
# Linux: sudo systemctl start mysql

# Iniciar aplicación Spring Boot
./mvnw spring-boot:run
# Windows: mvnw.cmd spring-boot:run

# ESPERAR hasta ver este mensaje:
# "Started MultipagosBackendApplication in X.XXX seconds"
# "Tomcat started on port(s): 8080 (http)"
```

#### 7.2 SEGUNDO: Verificar Backend

```bash
# En otra terminal, verificar que el backend responde:
curl http://localhost:8080/api/v1/health
# Debe devolver: {"status":"UP","timestamp":"..."}

# Si no responde, revisar logs en la terminal del backend
# Errores comunes:
# - "Connection refused": MySQL no está corriendo
# - "Access denied": Usuario/contraseña incorrectos
# - "Unknown database": Base de datos 'multipagos' no existe
```

#### 7.3 TERCERO: Iniciar Frontend

```bash
# En nueva terminal
cd multipagos-frontend

# Iniciar servidor de desarrollo
npm run dev

# ESPERAR hasta ver:
# "Local:   http://localhost:5173/"
# "Network: http://192.168.x.x:5173/"
```

#### 7.4 CUARTO: Verificar Aplicación Completa

```bash
# Abrir navegador en: http://localhost:5173
# Debe cargar la interfaz de Multi-Pagos

# Probar endpoints principales:
curl http://localhost:8080/api/v1/suppliers
# Debe devolver lista de proveedores (Claro, Movistar, etc.)
```

### Ejecución con Configuración Personalizada

#### Backend con perfil específico:

```bash
./mvnw spring-boot:run -Dspring.profiles.active=development
```

#### Frontend con puerto personalizado:

```bash
npm run dev -- --port 3000
```

### Detener Servicios

```bash
# Backend: Ctrl+C en terminal del backend
# Frontend: Ctrl+C en terminal del frontend
# MySQL (si es necesario):
#   Windows: net stop mysql
#   macOS: brew services stop mysql
#   Linux: sudo systemctl stop mysql
```

## Scripts Disponibles

### Backend (Maven)

```bash
# Compilar
./mvnw clean compile

# Ejecutar tests
./mvnw test

# Ejecutar con perfil específico
./mvnw spring-boot:run -Dspring.profiles.active=development

# Generar JAR para producción
./mvnw clean package
```

### Frontend (npm)

```bash
# Desarrollo con hot reload
npm run dev

# Build para producción
npm run build

# Preview del build
npm run preview

# Linting
npm run lint
```

## Arquitectura del Proyecto

### Backend - Hexagonal Architecture

```
multipagos-backend/src/main/java/com/multipagos/multipagos_backend/
├── auth/                     # Autenticación y autorización
│   ├── application/          # Casos de uso
│   ├── domain/              # Entidades y reglas de negocio
│   ├── infrastructure/      # Adaptadores externos
│   └── presentation/        # Controladores REST
├── topup/                   # Recargas móviles y transacciones
│   ├── application/         # Servicios de aplicación
│   ├── domain/             # Lógica de dominio
│   ├── infrastructure/     # Integración con Puntored
│   └── presentation/       # API REST
├── shared/                 # Configuraciones y servicios compartidos
│   ├── config/            # Configuración Spring
│   ├── security/          # Filtros de seguridad
│   └── utils/             # Utilidades comunes
└── MultipagosBackendApplication.java
```

### Frontend - Component Architecture

```
multipagos-frontend/src/
├── components/              # Componentes reutilizables
│   ├── ui/                 # Componentes base (Radix UI)
│   ├── forms/              # Formularios específicos
│   └── layout/             # Layout components
├── pages/                  # Páginas de la aplicación
├── services/               # Servicios API (Axios)
├── hooks/                  # Custom React hooks
├── types/                  # Definiciones TypeScript
├── utils/                  # Utilidades y helpers
└── App.tsx                 # Componente principal
```

**Módulos Backend:**

- **auth**: Registro, login, JWT, validaciones
- **topup**: Recargas, proveedores, historial transaccional
- **shared**: CORS, seguridad, health checks, configuración

**Módulos Frontend:**

- **Authentication**: Login, registro, protección de rutas
- **Topup**: Interfaz de recargas, selección de operadores
- **History**: Historial de transacciones con paginación
- **Dashboard**: Panel principal del usuario

## 📚 Documentación Swagger/OpenAPI

### Acceso a la Documentación Interactiva

Una vez que el servidor esté ejecutándose, puedes acceder a la documentación completa de la API:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **Información de la API**: http://localhost:8080/api/info

### Características de la Documentación

- ✅ **Documentación completa** de todos los endpoints
- ✅ **Ejemplos de request/response** para cada endpoint
- ✅ **Interfaz interactiva** para probar la API directamente
- ✅ **Esquemas de validación** detallados
- ✅ **Autenticación JWT** integrada en la interfaz
- ✅ **Modelos de datos** completamente documentados

### Cómo usar Swagger UI

1. **Iniciar el backend**: `./mvnw spring-boot:run`
2. **Abrir Swagger**: http://localhost:8080/swagger-ui.html
3. **Probar endpoints públicos**: Suppliers, Health, API Info
4. **Para endpoints protegidos**:
   - Registrar usuario en `/auth/register`
   - Hacer login en `/auth/login` para obtener token
   - Hacer clic en "Authorize" en Swagger UI
   - Ingresar: `Bearer {tu-token-jwt}`
   - Probar endpoints de recargas e historial

## API Endpoints

**Base URL:** `http://localhost:8080/api/v1`

### Autenticación

#### POST /auth/register

```json
{
  "name": "Juan Pérez",
  "email": "user@example.com",
  "password": "Password123@"
}
```

**Validaciones:**

- name: 2-100 caracteres
- email: formato válido
- password: mínimo 8 caracteres, 1 minúscula, 1 mayúscula, 1 número, 1 carácter especial

#### POST /auth/login

```json
{
  "email": "user@example.com",
  "password": "Password123@"
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

### Recargas (Requiere Authorization: Bearer token)

#### POST /topup

```json
{
  "cellPhone": "3197821272",
  "value": 10000,
  "supplierId": "8753"
}
```

**Validaciones:**

- cellPhone: inicia con "3", exactamente 10 dígitos
- value: entre $1,000 y $100,000
- supplierId: 8753(Claro), 9773(Movistar), 3398(Tigo), 4689(WOM)

**Response:**

```json
{
  "success": true,
  "message": "Recarga procesada exitosamente",
  "data": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "cellPhone": "3197821272",
    "value": 10000,
    "supplierName": "Claro",
    "status": "SUCCESS",
    "transactionalID": "4e8e273d-73a1-4ea5-8c2e-e45bf556d03a",
    "createdAt": "2025-08-14T15:30:00Z",
    "message": "Recarga exitosa"
  }
}
```

#### GET /topup/history

**Parámetros:** `page=0&size=20&sortField=createdAt&sortDirection=DESC`

**Response:** Lista paginada de transacciones del usuario

### Proveedores

#### GET /suppliers

**Response:**

```json
{
  "success": true,
  "data": [
    { "id": "8753", "name": "Claro" },
    { "id": "9773", "name": "Movistar" },
    { "id": "3398", "name": "Tigo" },
    { "id": "4689", "name": "WOM" }
  ]
}
```

### Sistema

#### GET /health

**Response:** Status del sistema y conexiones

## Integración con API Puntored

### Servicios Implementados

1. **auth (POST)**: Obtención de token Bearer para autenticación
2. **getSuppliers (GET)**: Listado de proveedores de recargas disponibles
3. **buy (POST)**: Realización de compra de recarga o transacción

### Configuración API Puntored

La configuración está incluida en `application.properties`:

```properties
# Puntored API Configuration
puntored.api.base-url=https://us-central1-puntored-dev.cloudfunctions.net/technicalTest-developer/api
puntored.api.key=mtrQF6Q11eosqyQnkMY0JGFbGqcxVg5icvfVnX1ifIyWDvwGApJ8WUM8nHVrdSkN
puntored.api.username=user0147
puntored.api.password=#3Q34Sh0NlDS
```

**IMPORTANTE**: Estas credenciales están configuradas por defecto según las especificaciones del desafío. Para producción, deben configurarse como variables de entorno.

## Reglas de Negocio Implementadas

### Validaciones de Recargas (Según Especificaciones)

- **Valor mínimo de transacción**: $1,000
- **Valor máximo de transacción**: $100,000
- **Número de teléfono**: Debe iniciar en "3", tener longitud de 10 caracteres y solo aceptar valores numéricos
- **Proveedores válidos**: 8753(Claro), 9773(Movistar), 3398(Tigo), 4689(WOM)

### Estados de Transacciones

- **PENDING**: Transacción en proceso
- **SUCCESS**: Transacción exitosa
- **FAILED**: Transacción fallida
- **CANCELLED**: Transacción cancelada

### Sistema de Autenticación (Opcional Implementado)

- **Registro de usuarios**: Validación de email único y contraseña segura
- **Login con JWT**: Autenticación mediante tokens Bearer
- **Protección de rutas**: Endpoints protegidos requieren autenticación

## Seguridad

### Implementada

- **JWT Authentication** con HMAC SHA-256
- **Rate Limiting** por IP (Bucket4j)
- **BCrypt** para contraseñas
- **OWASP Encoder** para sanitización
- **Security Headers** (XSS, CSRF protection)
- **Input Validation** en todos los endpoints

### Filtros (Orden de ejecución)

1. SecurityHeadersFilter
2. SecurityMonitoringFilter
3. RateLimitingFilter

## Puertos y URLs

### Desarrollo

- **Backend**: http://localhost:8080
- **Frontend**: http://localhost:5173
- **Base de datos**: localhost:3306

### Endpoints Principales

- **API Base**: http://localhost:8080/api/v1
- **Health Check**: http://localhost:8080/api/v1/health

## Profiles y Entornos

### Backend

```bash
# Desarrollo
./mvnw spring-boot:run -Dspring.profiles.active=development

# Producción
./mvnw spring-boot:run -Dspring.profiles.active=production
```

### Frontend

```bash
# Desarrollo
npm run dev

# Producción (build)
npm run build && npm run preview
```

## SOLUCIÓN DE PROBLEMAS CRÍTICOS

### PROBLEMAS DEL BACKEND (MÁS COMUNES)

#### Error: "Connection refused" o "Communications link failure"

```bash
# CAUSA: MySQL no está corriendo
# SOLUCIÓN:
# Windows:
net start mysql
# O desde Services.msc → MySQL → Start

# macOS:
brew services start mysql
sudo /usr/local/mysql/support-files/mysql.server start

# Linux:
sudo systemctl start mysql
sudo systemctl status mysql  # Verificar estado
```

#### Error: "Access denied for user 'root'@'localhost'"

```sql
# CAUSA: Contraseña incorrecta o usuario no existe
# SOLUCIÓN 1: Resetear contraseña de root
# Detener MySQL y reiniciar en modo seguro, luego:
ALTER USER 'root'@'localhost' IDENTIFIED BY 'nueva_password';
FLUSH PRIVILEGES;

# SOLUCIÓN 2: Usar credenciales correctas en application.properties
spring.datasource.username=tu_usuario_mysql
spring.datasource.password=tu_password_mysql
```

#### Error: "Unknown database 'multipagos'"

```sql
# CAUSA: Base de datos no fue creada
# SOLUCIÓN:
mysql -u root -p
CREATE DATABASE multipagos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;
```

#### Error: "Port 8080 was already in use"

```bash
# CAUSA: Puerto ocupado por otra aplicación
# SOLUCIÓN 1: Cambiar puerto en application.properties
server.port=8081

# SOLUCIÓN 2: Matar proceso que usa puerto 8080
# Windows:
netstat -ano | findstr :8080
taskkill /PID <PID_NUMBER> /F

# macOS/Linux:
lsof -ti:8080 | xargs kill -9
```

#### Error: "Failed to configure a DataSource"

```properties
# CAUSA: Configuración de base de datos incorrecta
# SOLUCIÓN: Verificar en application.properties:
spring.datasource.url=jdbc:mysql://localhost:3306/multipagos?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=tu_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

#### Error: Maven "No compiler is provided in this environment"

```bash
# CAUSA: JAVA_HOME no está configurado correctamente
# SOLUCIÓN:
# Windows:
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%

# macOS/Linux:
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
export PATH=$JAVA_HOME/bin:$PATH

# Verificar:
echo $JAVA_HOME
java -version
```

### PROBLEMAS DEL FRONTEND

#### Error: "ECONNREFUSED" al hacer requests

```bash
# CAUSA: Backend no está corriendo o puerto incorrecto
# SOLUCIÓN:
# 1. Verificar que backend esté en http://localhost:8080
curl http://localhost:8080/api/v1/health

# 2. Verificar .env del frontend:
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

#### Error: "Module not found" o dependencias

```bash
# SOLUCIÓN COMPLETA:
rm -rf node_modules package-lock.json
npm cache clean --force
npm install

# Si persiste:
npm install --legacy-peer-deps
```

#### Error: "Port 5173 is already in use"

```bash
# SOLUCIÓN: Usar puerto diferente
npm run dev -- --port 3000
```

#### Error de TypeScript: "Cannot find module '@/components'"

```bash
# CAUSA: Alias de TypeScript no configurado
# SOLUCIÓN: Verificar que existe tsconfig.json con:
{
  "compilerOptions": {
    "baseUrl": ".",
    "paths": {
      "@/*": ["./src/*"]
    }
  }
}
```

### PROBLEMAS DE BASE DE DATOS

#### Tablas no se crean automáticamente

```properties
# VERIFICAR en application.properties:
spring.jpa.hibernate.ddl-auto=create-drop
# O para producción: spring.jpa.hibernate.ddl-auto=update
```

#### Error: "Table doesn't exist"

```sql
# SOLUCIÓN: Recrear base de datos
DROP DATABASE multipagos;
CREATE DATABASE multipagos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
# Luego reiniciar backend
```

#### Verificar estado completo de la base de datos:

```sql
mysql -u root -p
USE multipagos;
SHOW TABLES;
DESCRIBE users;      -- Ver estructura de tabla usuarios
DESCRIBE transactions; -- Ver estructura de tabla transacciones
SELECT COUNT(*) FROM users; -- Contar registros
```

### COMANDOS DE DIAGNÓSTICO

#### Verificar todo el stack:

```bash
# 1. Java
java -version

# 2. MySQL
mysql --version
mysql -u root -p -e "SELECT VERSION();"

# 3. Node.js
node --version
npm --version

# 4. Puertos ocupados
netstat -tulpn | grep -E ':(3306|8080|5173)'  # Linux/macOS
netstat -ano | findstr -E ":(3306|8080|5173)" # Windows

# 5. Procesos Java
jps -l  # Listar procesos Java corriendo
```

## Estructura de Respuesta API

Todos los endpoints siguen el patrón:

```json
{
  "success": boolean,
  "message": "string",
  "data": object,
  "timestamp": "ISO-8601"
}
```

## Monitoreo

- **Health Check**: `/api/v1/health`
- **Actuator**: `/actuator/health` (configuración adicional requerida)
- **Logs**: Configurados para desarrollo local y producción (CloudWatch ready)

## CHECKLIST DE INSTALACIÓN EXITOSA

### Verificación Paso a Paso

#### 1. Requisitos del Sistema

- [ ] Java 21+ instalado (`java -version`)
- [ ] Node.js 18+ instalado (`node --version`)
- [ ] MySQL 8.0+ instalado y corriendo (`mysql --version`)
- [ ] Maven 3.6+ disponible (`mvn --version` o usar `./mvnw`)

#### 2. Base de Datos

- [ ] MySQL service iniciado
- [ ] Base de datos `multipagos` creada
- [ ] Usuario `multipagos_user` creado (opcional)
- [ ] Conexión probada: `mysql -u root -p multipagos`

#### 3. Backend

- [ ] Dependencias descargadas: `./mvnw clean compile`
- [ ] Tests pasando: `./mvnw test`
- [ ] Aplicación inicia: `./mvnw spring-boot:run`
- [ ] Health check responde: `curl http://localhost:8080/api/v1/health`
- [ ] Suppliers endpoint responde: `curl http://localhost:8080/api/v1/suppliers`

#### 4. Frontend

- [ ] Dependencias instaladas: `npm install`
- [ ] Build exitoso: `npm run build`
- [ ] Desarrollo inicia: `npm run dev`
- [ ] Aplicación carga en: `http://localhost:5173`
- [ ] Puede hacer login/registro

#### 5. Integración Completa

- [ ] Frontend se conecta al backend
- [ ] Registro de usuario funciona
- [ ] Login funciona y devuelve JWT
- [ ] Lista de proveedores se carga
- [ ] Proceso de recarga funciona (con datos de prueba)

### ESTRUCTURA DE ARCHIVOS CRÍTICOS

```
multi-pagos/
├── multipagos-backend/
│   ├── pom.xml                           # Dependencias Maven
│   ├── mvnw, mvnw.cmd                   # Maven Wrapper (usar estos)
│   ├── src/main/resources/
│   │   └── application.properties        # CONFIGURACIÓN CRÍTICA
│   ├── src/main/java/com/multipagos/multipagos_backend/
│   │   ├── auth/                        # Autenticación JWT
│   │   ├── topup/                       # Lógica de recargas
│   │   ├── shared/                      # Configuración compartida
│   │   └── MultipagosBackendApplication.java # Clase principal
│   └── target/                          # Archivos compilados (auto-generado)
├── multipagos-frontend/
│   ├── package.json                     # Dependencias npm
│   ├── .env                            # VARIABLES DE ENTORNO
│   ├── vite.config.ts                  # Configuración Vite
│   ├── tsconfig.json                   # Configuración TypeScript
│   ├── components.json                 # Configuración Shadcn/ui
│   ├── src/
│   │   ├── components/                 # Componentes React
│   │   ├── pages/                      # Páginas de la app
│   │   ├── services/                   # Servicios API
│   │   ├── types/                      # Tipos TypeScript
│   │   └── App.tsx                     # Componente principal
│   ├── dist/                           # Build de producción (auto-generado)
│   └── node_modules/                   # Dependencias (auto-generado)
└── README.md                           # Esta guía
```

### ARCHIVOS DE CONFIGURACIÓN CLAVE

#### Backend - application.properties (CRÍTICO)

```properties
# Base de datos - AJUSTAR SEGÚN TU CONFIGURACIÓN
spring.datasource.url=jdbc:mysql://localhost:3306/multipagos?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=tu_password_mysql

# JWT - CAMBIAR EN PRODUCCIÓN
jwt.secret=${JWT_SECRET:clave-super-secreta-256-bits-minimo}

# CORS - AJUSTAR SEGÚN FRONTEND
app.cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:3000,http://localhost:5173}

# Hibernate - IMPORTANTE PARA CREAR TABLAS
spring.jpa.hibernate.ddl-auto=create-drop
```

#### Frontend - .env (CRÍTICO)

```bash
# API del backend - DEBE COINCIDIR CON PUERTO DEL BACKEND
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_API_TIMEOUT=10000
VITE_APP_NAME="Multi-Pagos"
VITE_APP_VERSION=1.0.0
VITE_DEV_TOOLS=true
```

## Scripts de Base de Datos (Nivel 1)

### Script de Inicialización

El archivo `database/init.sql` contiene el script de inicialización:

```sql
-- Crear base de datos
CREATE DATABASE IF NOT EXISTS multipagos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE multipagos;

-- Las tablas se crean automáticamente por Hibernate
-- Estructura generada automáticamente:
-- - users: id, name, email, password, created_at, updated_at
-- - transactions: id, user_id, cell_phone, value, supplier_id, supplier_name, status, transactional_id, message, created_at, updated_at
```

**IMPORTANTE**: Las tablas se crean automáticamente gracias a `spring.jpa.hibernate.ddl-auto=create-drop`. Solo es necesario crear la base de datos.

## Entrega del Proyecto

### Archivos Incluidos

1. **Código Fuente Backend (Nivel 0)**: `multipagos-backend/`
   - API Spring Boot con integración Puntored
   - Servicios: auth, getSuppliers, buy
   - Tests unitarios incluidos

2. **Scripts de Base de Datos (Nivel 1)**: `database/init.sql`
   - Script de inicialización de MySQL
   - Estructura de tablas documentada

3. **Código Fuente Frontend (Nivel 2)**: `multipagos-frontend/`
   - Aplicación React + TypeScript
   - Módulo de recargas móviles
   - Consulta de transacciones
   - Sistema de autenticación opcional

4. **Instrucciones de Ejecución**: Este README
   - Guía completa de instalación
   - Configuración paso a paso
   - Solución de problemas

5. **Repositorio**: Código completo en control de versiones
   - Estructura organizada por niveles
   - Documentación completa

### URLs de Desarrollo

- **API Backend**: http://localhost:8080/api/v1
- **Frontend**: http://localhost:5173
- **Health Check**: http://localhost:8080/api/v1/health

### Funcionalidades Implementadas

#### Nivel 0 - API Spring Boot
- ✅ Integración completa con API Puntored
- ✅ Endpoint auth para obtener token Bearer
- ✅ Endpoint getSuppliers para listar proveedores
- ✅ Endpoint buy para realizar transacciones
- ✅ Tests unitarios implementados

#### Nivel 1 - Base de Datos
- ✅ Almacenamiento de información transaccional
- ✅ Consulta de histórico de transacciones
- ✅ Relaciones entre usuarios y transacciones

#### Nivel 2 - Frontend React
- ✅ Interfaz para realizar recargas móviles
- ✅ Resumen y ticket de transacciones
- ✅ Módulo de consulta de transacciones
- ✅ Sistema de login y autenticación (opcional)
- ✅ Validaciones según reglas de negocio

#### Extras Implementados
- ✅ **Nivel 4**: Sistema de logs con Spring Boot Actuator
- ✅ **Nivel 5**: Tests unitarios para backend y frontend
- ✅ Arquitectura hexagonal
- ✅ Seguridad con JWT
- ✅ Rate limiting y validaciones

## Recursos Adicionales

### Documentación Técnica

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev/)
- [Puntored API Integration](https://us-central1-puntored-dev.cloudfunctions.net/technicalTest-developer/api)

### APIs Integradas

- **Puntored API**: Servicio de recargas móviles (credenciales incluidas)
- **MySQL**: Base de datos relacional con auto-generación de esquema

---

**Multi-Pagos v1.0** - Desafío Técnico Completado

**Niveles Implementados**: 0, 1, 2 + Extras (4, 5)
- Backend: Spring Boot + Hexagonal Architecture + Puntored Integration
- Frontend: React + TypeScript + Vite
- Base de Datos: MySQL con Hibernate ORM
- Tests: Unitarios incluidos
- Logs: Spring Boot Actuator
