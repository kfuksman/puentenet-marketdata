# Puentenet - Market Monitor

Una aplicación completa para monitorear instrumentos financieros (acciones y criptomonedas) con datos en tiempo real.

## 🚀 Características

- **Datos en tiempo real** de acciones y criptomonedas
- **APIs integradas**: Alpha Vantage (acciones) y CoinGecko (criptomonedas)
- **Sistema de favoritos** para usuarios
- **Vista detallada** de instrumentos con métricas completas
- **Autenticación JWT** segura
- **Interfaz moderna** con React y Tailwind CSS
- **Backend robusto** con Spring Boot y manejo de errores personalizado

## 🏗️ Arquitectura

```
puentenet/
├── backend/          # API Spring Boot (Java 17)
├── frontend/         # Aplicación React (TypeScript)
└── docker-compose.yml # Orquestación completa
```

## 📋 Prerrequisitos

- **Docker** y **Docker Compose** instalados
- **API Key de Alpha Vantage** (gratuita en [alphavantage.co](https://www.alphavantage.co/support/#api-key))

## 🛠️ Configuración Rápida

### 1. Clonar el repositorio
```bash
git clone <repository-url>
cd puentenet
```

### 2. Configurar API Key (Opcional)
Por defecto se usa una API key de prueba. Para usar tu propia key:

```bash
export ALPHA_VANTAGE_API_KEY=tu_api_key_aqui
```

### 3. Ejecutar con Docker Compose
```bash
docker-compose up -d
```

### 4. Acceder a la aplicación
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8092/api
- **Swagger UI**: http://localhost:8092/api/swagger-ui.html

## 🔧 Configuración Detallada

### Variables de Entorno

| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| `ALPHA_VANTAGE_API_KEY` | API Key de Alpha Vantage | `IU9W74WEF0DEWLCF` |
| `POSTGRES_PASSWORD` | Contraseña de PostgreSQL | `password` |

### Puertos

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| Frontend | 3000 | Aplicación React |
| Backend | 8092 | API Spring Boot |
| PostgreSQL | 5432 | Base de datos |

## 📊 APIs Disponibles

### Autenticación
- `POST /api/auth/register` - Registrar usuario
- `POST /api/auth/login` - Iniciar sesión

### Instrumentos
- `GET /api/instruments` - Listar todos los instrumentos
- `GET /api/instruments/{id}` - Obtener instrumento por ID
- `GET /api/instruments/search?q={query}` - Buscar instrumentos
- `GET /api/instruments/favorites` - Obtener favoritos del usuario
- `POST /api/instruments/{id}/favorite` - Agregar a favoritos
- `DELETE /api/instruments/{id}/favorite` - Remover de favoritos

### Admin
- `POST /api/admin/update-market-data` - Actualizar datos de mercado
- `POST /api/admin/initialize-instruments` - Inicializar instrumentos

## 🗄️ Base de Datos

### Esquema Principal
- **users** - Usuarios del sistema
- **instruments** - Instrumentos financieros
- **favorites** - Relación usuario-instrumento favorito

### Usuarios de la Aplicación

La aplicación se inicializa automáticamente con los siguientes usuarios pre-creados:

| Usuario | Contraseña | Rol | Descripción |
|---------|------------|-----|-------------|
| `admin@puentenet.com` | `user123` | ADMIN | Administrador del sistema con acceso completo |
| `user@puentenet.com` | `user123` | USER | Usuario regular con acceso básico |

**Nota**: Estos usuarios se crean automáticamente en la tabla `users` con contraseñas encriptadas usando BCrypt. Puedes usar estas credenciales para iniciar sesión en la aplicación.

### Inicialización
La base de datos se inicializa automáticamente con:
- Tablas creadas por JPA/Hibernate
- Datos de instrumentos cargados desde APIs externas
- Usuarios de aplicación creados según el script `init-users.sql`

## 🔍 Monitoreo y Logs

### Ver logs en tiempo real
```bash
# Todos los servicios
docker-compose logs -f

# Servicio específico
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres
```

### Health Checks
```bash
# Verificar estado de servicios
docker-compose ps

# Verificar health checks
curl http://localhost:8092/api/health
```

## 🛠️ Desarrollo

### Ejecutar solo el backend
```bash
cd backend
./mvnw spring-boot:run
```

### Ejecutar solo el frontend
```bash
cd frontend
npm install
npm start
```

### Ejecutar solo la base de datos
```bash
docker-compose up postgres -d
```

## 🧪 Testing

### Backend
```bash
cd backend
./mvnw test
```

### Frontend
```bash
cd frontend
npm test
```

## 📁 Estructura del Proyecto

```
puentenet/
├── backend/
│   ├── src/main/java/com/puentenet/
│   │   ├── config/           # Configuraciones
│   │   ├── controller/       # Controladores REST
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # Entidades JPA
│   │   ├── exception/       # Excepciones personalizadas
│   │   ├── repository/      # Repositorios JPA
│   │   ├── security/        # Configuración de seguridad
│   │   └── service/         # Lógica de negocio
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/      # Componentes React
│   │   ├── features/        # Características (auth, instruments)
│   │   ├── services/        # Servicios API
│   │   └── app/            # Configuración Redux
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
├── docker-compose.yml
└── README.md
```

## 🚨 Manejo de Errores

El sistema incluye manejo de errores personalizado:

- **InstrumentNotFoundException** - Instrumento no encontrado
- **MarketDataException** - Errores de datos de mercado
- **ServiceUnavailableException** - Servicios externos no disponibles

Todos los errores devuelven respuestas HTTP apropiadas con detalles estructurados.

## 🔒 Seguridad

- **JWT Authentication** para APIs protegidas
- **CORS** configurado para desarrollo
- **Headers de seguridad** en frontend
- **Validación de entrada** en backend

## 📈 Escalabilidad

- **Docker** para fácil despliegue
- **Health checks** para monitoreo
- **Logging estructurado** para debugging
- **Manejo de errores robusto**

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 🆘 Soporte

Si encuentras algún problema:

1. Revisa los logs: `docker-compose logs`
2. Verifica la configuración de APIs
3. Asegúrate de que los puertos estén disponibles
4. Abre un issue en el repositorio

## 🔄 Actualizaciones

Para actualizar el proyecto:

```bash
# Detener servicios
docker-compose down

# Actualizar código
git pull

# Reconstruir y ejecutar
docker-compose up -d --build
``` 