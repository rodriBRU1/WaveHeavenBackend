# WaveHeaven Backend

Backend API REST para WaveHeaven, una plataforma de reservas de productos. Desarrollado con Spring Boot 3.5.7 y Java 17.

## Tabla de Contenidos

- [Tecnologías](#tecnologías)
- [Requisitos Previos](#requisitos-previos)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Ejecución](#ejecución)
- [API Endpoints](#api-endpoints)
- [Autenticación](#autenticación)
- [Funcionalidades](#funcionalidades)
- [Base de Datos](#base-de-datos)
- [Testing](#testing)

## Tecnologías

- **Java 17**
- **Spring Boot 3.5.7**
- **Spring Security** - Autenticación JWT
- **Spring Data JPA** - Persistencia
- **PostgreSQL** - Base de datos
- **Flyway** - Migraciones
- **Twilio** - WhatsApp API
- **Thymeleaf** - Templates de email
- **Swagger/OpenAPI** - Documentación API
- **Lombok** - Reducción de boilerplate
- **Maven** - Gestión de dependencias

## Requisitos Previos

- Java 17 o superior
- Maven 3.6+
- PostgreSQL 12+
- Cuenta de Twilio (opcional, para WhatsApp)
- Cuenta de Gmail (opcional, para emails)

## Instalación

1. **Clonar el repositorio:**
   ```bash
   git clone <repository-url>
   cd back
   ```

2. **Crear la base de datos:**
   ```sql
   CREATE DATABASE waveheaven;
   ```

3. **Instalar dependencias:**
   ```bash
   ./mvnw clean install -DskipTests
   ```

## Configuración

### Variables de Entorno

Crea un archivo `.env` o configura las siguientes variables de entorno:

```properties
# Base de datos
DB_URL=jdbc:postgresql://localhost:5432/waveheaven
DB_USERNAME=postgres
DB_PASSWORD=tu_password

# JWT
JWT_SECRET=tu-clave-secreta-muy-larga-y-segura

# Email (Gmail)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tu-email@gmail.com
MAIL_PASSWORD=tu-app-password
MAIL_FROM=tu-email@gmail.com

# Frontend URL
FRONTEND_URL=http://localhost:3000

# Twilio WhatsApp
TWILIO_ACCOUNT_SID=ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
TWILIO_AUTH_TOKEN=tu_auth_token
TWILIO_WHATSAPP_FROM=whatsapp:+14155238886
TWILIO_WHATSAPP_TO=whatsapp:+52xxxxxxxxxx
```

### Archivo application.properties

El archivo `src/main/resources/application.properties` ya está configurado para usar variables de entorno con valores por defecto para desarrollo local.

### Configuración de Gmail

Para enviar emails con Gmail:

1. Activa la verificación en dos pasos en tu cuenta de Google
2. Genera una "Contraseña de aplicación" en [Seguridad de Google](https://myaccount.google.com/apppasswords)
3. Usa esa contraseña en `MAIL_PASSWORD`

### Configuración de Twilio WhatsApp

1. Crea una cuenta en [Twilio](https://www.twilio.com/)
2. Ve a **Messaging > Try WhatsApp**
3. Sigue las instrucciones del Sandbox
4. Desde el número destino, envía el mensaje de unión al sandbox
5. Copia tus credenciales (Account SID y Auth Token)

## Ejecución

### Desarrollo

```bash
./mvnw spring-boot:run
```

### Producción

```bash
./mvnw clean package -DskipTests
java -jar target/back-0.0.1-SNAPSHOT.jar
```

### Con Docker (opcional)

```bash
docker build -t waveheaven-backend .
docker run -p 8080:8080 waveheaven-backend
```

La aplicación estará disponible en `http://localhost:8080`

## API Endpoints

### Documentación Swagger

Accede a la documentación interactiva en:
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

### Autenticación

| Método | Endpoint | Descripción | Acceso |
|--------|----------|-------------|--------|
| POST | `/api/auth/register` | Registrar usuario | Público |
| POST | `/api/auth/login` | Iniciar sesión | Público |

### Productos

| Método | Endpoint | Descripción | Acceso |
|--------|----------|-------------|--------|
| GET | `/api/products` | Listar productos (paginado) | Público |
| GET | `/api/products/{id}` | Obtener producto por ID | Público |
| GET | `/api/products/random` | Productos aleatorios | Público |
| GET | `/api/products/search` | Buscar productos | Público |
| GET | `/api/products/category/{id}` | Productos por categoría | Público |
| POST | `/api/products` | Crear producto | Admin |
| PUT | `/api/products/{id}` | Actualizar producto | Admin |
| DELETE | `/api/products/{id}` | Eliminar producto | Admin |

**Parámetros de búsqueda:**
- `name` - Nombre del producto
- `categoryId` - ID de categoría
- `startDate` - Fecha inicio disponibilidad (YYYY-MM-DD)
- `endDate` - Fecha fin disponibilidad (YYYY-MM-DD)
- `page` - Número de página (default: 0)
- `size` - Tamaño de página (default: 10)

### Categorías

| Método | Endpoint | Descripción | Acceso |
|--------|----------|-------------|--------|
| GET | `/api/categories` | Listar categorías | Público |
| GET | `/api/categories/{id}` | Obtener categoría | Público |
| POST | `/api/categories` | Crear categoría | Admin |
| PUT | `/api/categories/{id}` | Actualizar categoría | Admin |
| DELETE | `/api/categories/{id}` | Eliminar categoría | Admin |

### Características

| Método | Endpoint | Descripción | Acceso |
|--------|----------|-------------|--------|
| GET | `/api/characteristics` | Listar características | Público |
| GET | `/api/characteristics/{id}` | Obtener característica | Público |
| POST | `/api/characteristics` | Crear característica | Admin |
| PUT | `/api/characteristics/{id}` | Actualizar característica | Admin |
| DELETE | `/api/characteristics/{id}` | Eliminar característica | Admin |

### Reservas

| Método | Endpoint | Descripción | Acceso |
|--------|----------|-------------|--------|
| POST | `/api/reservations` | Crear reserva | Autenticado |
| GET | `/api/reservations/{id}` | Obtener reserva | Autenticado |
| GET | `/api/reservations/my-reservations` | Mis reservas | Autenticado |
| GET | `/api/reservations/my-reservations/paginated` | Mis reservas (paginado) | Autenticado |
| POST | `/api/reservations/{id}/cancel` | Cancelar reserva | Autenticado |
| GET | `/api/reservations/product/{id}/availability` | Disponibilidad del producto | Público |

### Favoritos

| Método | Endpoint | Descripción | Acceso |
|--------|----------|-------------|--------|
| GET | `/api/favorites` | Listar favoritos | Autenticado |
| GET | `/api/favorites/ids` | IDs de favoritos | Autenticado |
| POST | `/api/favorites/{productId}` | Agregar a favoritos | Autenticado |
| DELETE | `/api/favorites/{productId}` | Quitar de favoritos | Autenticado |
| GET | `/api/favorites/{productId}/check` | Verificar si es favorito | Autenticado |

### Reviews

| Método | Endpoint | Descripción | Acceso |
|--------|----------|-------------|--------|
| GET | `/api/reviews/product/{id}` | Reviews del producto | Público |
| GET | `/api/reviews/product/{id}/rating` | Rating promedio | Público |
| POST | `/api/reviews` | Crear review | Autenticado |
| PUT | `/api/reviews/product/{id}` | Actualizar mi review | Autenticado |
| DELETE | `/api/reviews/product/{id}` | Eliminar mi review | Autenticado |
| GET | `/api/reviews/product/{id}/my-review` | Mi review | Autenticado |

### WhatsApp

| Método | Endpoint | Descripción | Acceso |
|--------|----------|-------------|--------|
| POST | `/api/whatsapp/send` | Enviar mensaje | Público |
| GET | `/api/whatsapp/status` | Estado del servicio | Público |

### Usuarios (Admin)

| Método | Endpoint | Descripción | Acceso |
|--------|----------|-------------|--------|
| GET | `/api/users` | Listar usuarios | Admin |
| GET | `/api/users/{id}` | Obtener usuario | Admin |
| PUT | `/api/users/{id}/role` | Cambiar rol | Admin |

## Autenticación

### Registro

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@ejemplo.com",
    "password": "password123",
    "firstName": "Juan",
    "lastName": "Pérez"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@ejemplo.com",
    "password": "password123"
  }'
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "usuario@ejemplo.com",
  "firstName": "Juan",
  "lastName": "Pérez",
  "role": "USER"
}
```

### Usar el Token

Incluye el token en el header `Authorization`:

```bash
curl -X GET http://localhost:8080/api/favorites \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## Funcionalidades

### Sprint 1-2: Base del Sistema
- Autenticación y autorización con JWT
- CRUD de productos con imágenes
- CRUD de categorías
- CRUD de características
- Gestión de usuarios y roles

### Sprint 3: Búsqueda y Favoritos
- Búsqueda de productos por nombre, categoría y fechas
- Sistema de favoritos
- Políticas de productos
- Sistema de reviews y puntuaciones
- Eliminación de categorías

### Sprint 4: Reservas y Notificaciones
- Sistema de reservas con validación de disponibilidad
- Historial de reservas del usuario
- Cancelación de reservas
- Notificación por email de confirmación de reserva
- Integración con WhatsApp vía Twilio

## Base de Datos

### Migraciones

Las migraciones se ejecutan automáticamente al iniciar la aplicación. Están ubicadas en:

```
src/main/resources/db/migration/
├── V1__create_users_table.sql
├── V2__create_categories_table.sql
├── V3__create_products_table.sql
├── V4__create_characteristics_table.sql
├── V5__create_product_images_table.sql
├── V6__create_product_characteristics_table.sql
├── V7__create_reservations_table.sql
├── V8__create_favorites_table.sql
├── V9__create_policies_table.sql
└── V10__create_reviews_table.sql
```

### Estructura Principal

```
users
├── id, email, password, first_name, last_name, role

products
├── id, name, description, category_id

categories
├── id, title, description, image_url

product_images
├── id, product_id, url, is_primary

characteristics
├── id, name, icon

product_characteristics
├── product_id, characteristic_id

reservations
├── id, user_id, product_id, start_date, end_date, status

favorites
├── id, user_id, product_id

reviews
├── id, user_id, product_id, rating, comment

policies
├── id, product_id, title, description
```

## Testing

### Ejecutar todos los tests

```bash
./mvnw test
```

### Ejecutar un test específico

```bash
./mvnw test -Dtest=NombreDelTest
```

### Ejecutar tests con cobertura

```bash
./mvnw test jacoco:report
```

## Estructura del Proyecto

```
src/main/java/com/waveheaven/back/
├── auth/                    # Autenticación y JWT
│   ├── config/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
├── categories/              # Gestión de categorías
├── characteristics/         # Características de productos
├── config/                  # Configuración global
├── email/                   # Servicio de emails
├── favorites/               # Sistema de favoritos
├── products/                # Gestión de productos
├── reservations/            # Sistema de reservas
├── reviews/                 # Sistema de reviews
├── shared/                  # Componentes compartidos
│   ├── entity/
│   └── exception/
├── users/                   # Gestión de usuarios
└── whatsapp/                # Integración WhatsApp
```

## Ejemplos de Uso

### Crear una reserva

```bash
curl -X POST http://localhost:8080/api/reservations \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "startDate": "2024-12-20",
    "endDate": "2024-12-25"
  }'
```

### Enviar mensaje por WhatsApp

```bash
curl -X POST http://localhost:8080/api/whatsapp/send \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Hola, tengo una consulta sobre el producto",
    "productId": 1
  }'
```

### Buscar productos disponibles

```bash
curl "http://localhost:8080/api/products/search?name=kayak&startDate=2024-12-01&endDate=2024-12-10&page=0&size=10"
```

### Agregar review

```bash
curl -X POST http://localhost:8080/api/reviews \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "rating": 5,
    "comment": "Excelente producto, muy recomendado"
  }'
```

## Troubleshooting

### Error de conexión a la base de datos

Verifica que PostgreSQL esté corriendo y las credenciales sean correctas:
```bash
psql -U postgres -d waveheaven
```

### Error de envío de emails

1. Verifica que las credenciales de Gmail sean correctas
2. Asegúrate de usar una "Contraseña de aplicación"
3. Revisa los logs: `log.error("Error al enviar email...")`

### Error de WhatsApp

1. Verifica las credenciales de Twilio
2. Asegúrate de que el número destino esté unido al sandbox
3. Consulta el endpoint `/api/whatsapp/status`

### Migraciones fallidas

Si una migración falla, puedes limpiar el historial:
```sql
DELETE FROM flyway_schema_history WHERE success = false;
```

## Licencia

Este proyecto es de uso educativo.

## Contacto

Para dudas o sugerencias sobre el proyecto.
