# Multipagos Backend API - Documentación

## 🚀 Acceso a la Documentación Swagger

Una vez que el servidor esté ejecutándose en el puerto 8080, puedes acceder a la documentación interactiva de Swagger en:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **Información de la API**: http://localhost:8080/api/info

## 📋 Endpoints Disponibles

### 🔐 Autenticación (`/auth`)
- `POST /auth/register` - Registrar nuevo usuario
- `POST /auth/login` - Iniciar sesión y obtener token JWT

### 🏢 Proveedores (`/suppliers`)
- `GET /suppliers` - Obtener lista de proveedores disponibles

### 📱 Recargas (`/topup`)
- `POST /topup` - Procesar recarga móvil (requiere autenticación)
- `GET /topup/history` - Obtener historial de transacciones (requiere autenticación)

### 🏥 Sistema (`/health`)
- `GET /health` - Verificar estado del sistema

## 🔑 Autenticación

La API utiliza JWT (JSON Web Tokens) para la autenticación. Para acceder a los endpoints protegidos:

1. Registra un usuario con `POST /auth/register`
2. Inicia sesión con `POST /auth/login` para obtener el token
3. Incluye el token en el header `Authorization: Bearer {token}` en las peticiones protegidas

## 📊 Estructura de Respuestas

Todas las respuestas de la API siguen una estructura consistente:

### Respuesta Exitosa
```json
{
  "success": true,
  "data": { /* datos de respuesta */ },
  "message": "Operación completada exitosamente",
  "timestamp": "2025-01-14T10:30:00.000Z"
}
```

### Respuesta de Error
```json
{
  "success": false,
  "error": "Mensaje de error descriptivo",
  "path": "/endpoint/que/genero/el/error",
  "timestamp": "2025-01-14T10:30:00.000Z"
}
```

## 🔧 Configuración de Desarrollo

### Requisitos
- Java 21
- Maven 3.6+
- MySQL 8.0+
- Puerto 8080 disponible

### Ejecutar la Aplicación
```bash
# Clonar el repositorio
git clone <repository-url>

# Navegar al directorio
cd multipagos-backend

# Ejecutar con Maven
./mvnw spring-boot:run

# O compilar y ejecutar
./mvnw clean package
java -jar target/multipagos-backend-0.0.1-SNAPSHOT.jar
```

### Base de Datos
Asegúrate de tener MySQL ejecutándose con la configuración especificada en `application.properties`:
- Host: localhost:3306
- Database: multipagos
- Usuario: root
- Contraseña: jorge

## 📝 Ejemplos de Uso

### 1. Registrar Usuario
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Pérez",
    "email": "juan@example.com",
    "password": "MiPassword123!"
  }'
```

### 2. Iniciar Sesión
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "juan@example.com",
    "password": "MiPassword123!"
  }'
```

### 3. Obtener Proveedores
```bash
curl -X GET http://localhost:8080/suppliers
```

### 4. Procesar Recarga
```bash
curl -X POST http://localhost:8080/topup \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {tu-jwt-token}" \
  -d '{
    "cellPhone": "3001234567",
    "value": 10000,
    "supplierId": "claro"
  }'
```

### 5. Obtener Historial
```bash
curl -X GET "http://localhost:8080/topup/history?page=0&size=20" \
  -H "Authorization: Bearer {tu-jwt-token}"
```

## 🛡️ Seguridad

- Todas las contraseñas se almacenan hasheadas con BCrypt
- Los tokens JWT tienen una expiración configurable (por defecto 24 horas)
- Validación de entrada en todos los endpoints
- Rate limiting implementado para prevenir abuso
- Headers de seguridad configurados

## 📱 Validaciones

### Números de Celular
- Deben ser números colombianos (10 dígitos iniciando con 3)
- Formato: `3XXXXXXXXX`

### Montos de Recarga
- Mínimo: $1,000 COP
- Máximo: $100,000 COP

### Contraseñas
- Mínimo 8 caracteres
- Debe incluir: mayúscula, minúscula, número y carácter especial

## 🔍 Monitoreo

- Endpoint de health check: `/health`
- Logs estructurados para monitoreo

## 🐛 Solución de Problemas

### Error de Conexión a Base de Datos
Verifica que MySQL esté ejecutándose y la configuración en `application.properties` sea correcta.

### Token JWT Inválido
Asegúrate de incluir el prefijo "Bearer " en el header Authorization.

### Error 404 en Swagger
Verifica que la aplicación esté ejecutándose en el puerto correcto (8080 por defecto).

## 📞 Soporte

Para soporte técnico o preguntas sobre la API, contacta al equipo de desarrollo en dev@multipagos.com