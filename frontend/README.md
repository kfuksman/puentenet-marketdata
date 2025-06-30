# Market Monitor Frontend

Una aplicación React moderna para monitorear instrumentos financieros con autenticación JWT y gestión de favoritos.

## 🚀 Características

- **SPA React** con TypeScript
- **Gestión de estado** con Redux Toolkit
- **Autenticación JWT** con persistencia en localStorage
- **Diseño responsive** con Tailwind CSS
- **Formularios validados** con React Hook Form + Yup
- **Navegación protegida** con React Router
- **Iconos** de Heroicons
- **Interceptores HTTP** para manejo automático de tokens

## 🛠️ Tecnologías

- **React 18** con TypeScript
- **Redux Toolkit** para gestión de estado
- **React Router** para navegación
- **Tailwind CSS** para estilos
- **React Hook Form** + **Yup** para validación
- **Axios** para HTTP requests
- **Heroicons** para iconos

## 📦 Instalación

1. **Instalar dependencias:**
```bash
npm install
```

2. **Iniciar en modo desarrollo:**
```bash
npm start
```

3. **Construir para producción:**
```bash
npm run build
```

## 🔧 Configuración

### Variables de Entorno

Crea un archivo `.env` en la raíz del proyecto:

```env
REACT_APP_API_URL=http://localhost:8092/api
```

### Backend

Asegúrate de que el backend esté corriendo en `http://localhost:8092` antes de usar el frontend.

## 📁 Estructura del Proyecto

```
src/
├── app/
│   ├── store.ts          # Configuración de Redux store
│   └── hooks.ts          # Hooks tipados para Redux
├── components/
│   └── Navbar.tsx        # Barra de navegación
├── features/
│   ├── auth/
│   │   ├── authSlice.ts  # Slice de autenticación
│   │   ├── LoginPage.tsx # Página de login
│   │   └── RegisterPage.tsx # Página de registro
│   └── instruments/
│       ├── instrumentsSlice.ts # Slice de instrumentos
│       ├── InstrumentsPage.tsx # Lista de instrumentos
│       └── FavoritesPage.tsx   # Página de favoritos
├── services/
│   └── api.ts            # Configuración de Axios
└── App.tsx               # Componente principal con rutas
```

## 🔐 Autenticación

### Flujo de Login
1. Usuario ingresa email y contraseña
2. Se valida el formulario con Yup
3. Se envía request al backend
4. Se almacena el JWT en localStorage
5. Se actualiza el estado de Redux
6. Se redirige al usuario a la página principal

### Rutas Protegidas
- `/` - Lista de instrumentos (requiere autenticación)
- `/favorites` - Favoritos del usuario (requiere autenticación)
- `/login` - Página de login (pública)
- `/register` - Página de registro (pública)

## 📊 Funcionalidades

### Lista de Instrumentos
- **Visualización** de 20 instrumentos principales
- **Datos en tiempo real** (precio, cambio diario, volumen)
- **Búsqueda** por símbolo o nombre de empresa
- **Favoritos** - agregar/remover con un clic
- **Diseño responsive** - se adapta a diferentes pantallas

### Gestión de Favoritos
- **Lista dedicada** de instrumentos favoritos
- **Persistencia** en base de datos
- **Sincronización** automática con el backend
- **Eliminación** fácil de favoritos

### Navegación
- **Barra de navegación** con estado de autenticación
- **Enlaces** a instrumentos y favoritos
- **Logout** automático con limpieza de estado
- **Redirección** automática en caso de token expirado

## 🎨 Diseño

### Tailwind CSS
- **Sistema de diseño** consistente
- **Componentes** reutilizables
- **Responsive design** mobile-first
- **Temas** personalizables

### Componentes
- **Cards** para instrumentos
- **Formularios** con validación visual
- **Botones** con estados de hover/loading
- **Iconos** de Heroicons

## 🔄 Estado Global (Redux)

### Auth Slice
```typescript
{
  token: string | null;
  user: User | null;
  loading: boolean;
  error: string | null;
}
```

### Instruments Slice
```typescript
{
  instruments: Instrument[];
  favorites: Instrument[];
  loading: boolean;
  error: string | null;
}
```

## 🌐 API Integration

### Configuración de Axios
- **Base URL** configurada
- **Interceptores** para tokens JWT
- **Manejo automático** de errores 401
- **Headers** automáticos

### Endpoints Utilizados
- `POST /auth/login` - Autenticación
- `POST /auth/register` - Registro
- `GET /instruments` - Lista de instrumentos
- `GET /instruments/favorites` - Favoritos del usuario
- `POST /instruments/{id}/favorite` - Agregar favorito
- `DELETE /instruments/{id}/favorite` - Remover favorito

## 🚀 Deployment

### Build de Producción
```bash
npm run build
```

### Variables de Entorno de Producción
```env
REACT_APP_API_URL=https://tu-backend.com/api
```

### Servidor Web
El build genera archivos estáticos que pueden ser servidos por cualquier servidor web (Nginx, Apache, etc.).

## 🧪 Testing

```bash
npm test
```

## 📝 Scripts Disponibles

- `npm start` - Inicia el servidor de desarrollo
- `npm run build` - Construye la aplicación para producción
- `npm test` - Ejecuta las pruebas
- `npm run eject` - Expone la configuración de webpack (irreversible)

## 🔧 Desarrollo

### Agregar Nuevas Funcionalidades
1. Crear slice en `features/` si es necesario
2. Agregar componentes en `components/` o `features/`
3. Actualizar rutas en `App.tsx`
4. Agregar tipos TypeScript si es necesario

### Estilos
- Usar clases de Tailwind CSS
- Crear componentes reutilizables
- Seguir el sistema de diseño establecido

## 🐛 Troubleshooting

### Problemas Comunes

1. **Error de CORS**
   - Verificar que el backend esté corriendo
   - Verificar la URL del backend en `services/api.ts`

2. **Token no válido**
   - Limpiar localStorage
   - Volver a hacer login

3. **Errores de compilación**
   - Verificar que todas las dependencias estén instaladas
   - Ejecutar `npm install` nuevamente

## 📄 Licencia

Este proyecto es parte del sistema Market Monitor.
