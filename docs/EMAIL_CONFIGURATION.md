# Configuración de Email - WaveHeaven

## Descripción

El sistema de email envía notificaciones automáticas a los usuarios cuando se registran en la plataforma. El email incluye:
- Saludo personalizado
- Datos de la cuenta (nombre y email)
- Enlace para iniciar sesión

## Configuración

### Variables de Entorno

Configura las siguientes variables de entorno antes de ejecutar la aplicación:

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `MAIL_HOST` | Servidor SMTP | `smtp.gmail.com` |
| `MAIL_PORT` | Puerto SMTP | `587` |
| `MAIL_USERNAME` | Email de envío | `tu-email@gmail.com` |
| `MAIL_PASSWORD` | Contraseña o App Password | `xxxx xxxx xxxx xxxx` |
| `MAIL_FROM` | Email remitente | `noreply@waveheaven.com` |
| `FRONTEND_URL` | URL del frontend | `http://localhost:3000` |

### Configuración para Gmail

1. Ve a tu cuenta de Google > Seguridad
2. Activa la verificación en dos pasos
3. Genera una "Contraseña de aplicación":
   - Ir a: https://myaccount.google.com/apppasswords
   - Seleccionar "Correo" y "Ordenador Windows"
   - Copiar la contraseña generada (16 caracteres)

4. Usa esa contraseña en `MAIL_PASSWORD`

### Configuración para Otros Proveedores

#### Outlook/Hotmail
```
MAIL_HOST=smtp.office365.com
MAIL_PORT=587
```

#### Yahoo
```
MAIL_HOST=smtp.mail.yahoo.com
MAIL_PORT=587
```

## Uso en Desarrollo

### Opción 1: Variables de Entorno del Sistema

Windows (PowerShell):
```powershell
$env:MAIL_USERNAME="tu-email@gmail.com"
$env:MAIL_PASSWORD="tu-app-password"
.\mvnw.cmd spring-boot:run
```

### Opción 2: Archivo .env (recomendado)

Crea un archivo `.env` en la raíz del proyecto (NO commitear):

```env
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tu-email@gmail.com
MAIL_PASSWORD=tu-app-password
MAIL_FROM=tu-email@gmail.com
FRONTEND_URL=http://localhost:3000
```

Luego usa una herramienta como `dotenv` o configura tu IDE para cargar estas variables.

### Opción 3: application-local.properties

Crea `src/main/resources/application-local.properties`:

```properties
spring.mail.username=tu-email@gmail.com
spring.mail.password=tu-app-password
app.mail.from=tu-email@gmail.com
```

Ejecuta con el perfil local:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

## Funcionamiento

### Flujo de Registro

1. Usuario hace POST a `/api/auth/register`
2. Se crea el usuario en la base de datos
3. Se envía email de confirmación (asíncrono)
4. Se retorna el token JWT inmediatamente

```
POST /api/auth/register
{
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan@email.com",
  "password": "123456"
}

Response 200:
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "email": "juan@email.com",
  "firstName": "Juan",
  "lastName": "Pérez",
  "initials": "JP",
  "role": "USER"
}
```

El email se envía en segundo plano usando `@Async`, por lo que no bloquea la respuesta.

### Manejo de Errores

Si el envío de email falla:
- El registro del usuario NO se revierte
- Se registra el error en los logs
- El usuario puede seguir usando la aplicación normalmente

Los errores se registran en formato:
```
ERROR EmailService - Error al enviar email de confirmación de registro a juan@email.com: [mensaje de error]
```

## Personalización

### Modificar Plantilla

La plantilla del email está en:
```
src/main/resources/templates/email/registration-confirmation.html
```

Variables disponibles en la plantilla:
- `${firstName}` - Nombre del usuario
- `${lastName}` - Apellido del usuario
- `${email}` - Email del usuario
- `${loginUrl}` - URL de login (ej: http://localhost:3000/login)
- `${frontendUrl}` - URL base del frontend

### Agregar Nuevos Emails

Para crear un nuevo tipo de email:

1. Crear plantilla en `src/main/resources/templates/email/`
2. Agregar método en `EmailService`:

```java
@Async
public void sendNuevoTipoEmail(String to, String param1) {
    try {
        Context context = new Context();
        context.setVariable("param1", param1);

        String htmlContent = templateEngine.process("email/nuevo-template", context);
        sendHtmlEmail(to, "Asunto del Email", htmlContent);

        log.info("Email enviado a: {}", to);
    } catch (Exception e) {
        log.error("Error al enviar email a {}: {}", to, e.getMessage());
    }
}
```

## Testing

### Probar sin SMTP Real

Para desarrollo sin servidor de correo, puedes usar:

1. **MailHog** (servidor SMTP local):
   ```
   docker run -p 1025:1025 -p 8025:8025 mailhog/mailhog
   ```
   Configura: `MAIL_HOST=localhost`, `MAIL_PORT=1025`
   Ver emails en: http://localhost:8025

2. **Mailtrap** (servicio gratuito):
   - Crear cuenta en https://mailtrap.io
   - Usar credenciales SMTP proporcionadas

### Verificar Logs

Al registrar un usuario, verifica en la consola:
```
INFO EmailService - Email de confirmación de registro enviado a: usuario@email.com
```

## Solución de Problemas

### Error: Authentication failed

- Verifica que `MAIL_USERNAME` y `MAIL_PASSWORD` sean correctos
- Para Gmail, asegúrate de usar App Password, no la contraseña normal

### Error: Connection timed out

- Verifica `MAIL_HOST` y `MAIL_PORT`
- Revisa firewall/antivirus que puedan bloquear el puerto 587

### Error: Template not found

- Verifica que el archivo exista en `src/main/resources/templates/email/`
- El nombre debe coincidir exactamente (sin extensión .html en el código)

### Email no llega

- Revisa la carpeta de spam
- Verifica que el email remitente (`MAIL_FROM`) sea válido
- Algunos proveedores requieren que `MAIL_FROM` coincida con `MAIL_USERNAME`
