# Market Monitor Backend

A Spring Boot RESTful API for financial market monitoring with JWT authentication, user management, and real-time market data integration.

## Features

- **User Authentication**: JWT-based authentication with user registration and login
- **Role-based Access**: Two user levels (USER and ADMIN)
- **Market Data**: Integration with Alpha Vantage API for real-time stock data
- **Favorites Management**: Users can mark/unmark instruments as favorites
- **Search Functionality**: Search instruments by symbol or company name
- **Scheduled Updates**: Automatic market data updates every 5 minutes
- **API Documentation**: Swagger/OpenAPI documentation
- **Caching**: Redis-like caching for API responses

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** with JWT
- **Spring Data JPA**
- **PostgreSQL**
- **Swagger/OpenAPI**
- **WebClient** for HTTP requests
- **Maven**

## Prerequisites

- Java 17 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher
- Alpha Vantage API key (free tier available)

## Setup Instructions

### 1. Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE market_monitor;
CREATE USER postgres WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE market_monitor TO postgres;
```

### 2. Alpha Vantage API Key

1. Go to [Alpha Vantage](https://www.alphavantage.co/support/#api-key)
2. Sign up for a free API key
3. Update the `application.yml` file with your API key

### 3. Configuration

Update `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/market_monitor
    username: your_username
    password: your_password

alpha-vantage:
  api-key: your_api_key_here

jwt:
  secret: your-secret-key-here-make-it-long-and-secure-in-production
```

### 4. Build and Run

```bash
# Navigate to backend directory
cd backend

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

Once the application is running, you can access the Swagger UI at:
`http://localhost:8080/api/swagger-ui.html`

### Authentication Endpoints

#### Register User
```
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

#### Login
```
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

### Instrument Endpoints

#### Get All Instruments
```
GET /api/instruments
Authorization: Bearer <jwt_token>
```

#### Search Instruments
```
GET /api/instruments/search?q=AAPL
Authorization: Bearer <jwt_token>
```

#### Get Instrument Details
```
GET /api/instruments/{id}
Authorization: Bearer <jwt_token>
```

#### Get User Favorites
```
GET /api/instruments/favorites
Authorization: Bearer <jwt_token>
```

#### Add to Favorites
```
POST /api/instruments/{id}/favorite
Authorization: Bearer <jwt_token>
```

#### Remove from Favorites
```
DELETE /api/instruments/{id}/favorite
Authorization: Bearer <jwt_token>
```

### Admin Endpoints

#### Manual Market Data Update
```
POST /api/admin/update-market-data
Authorization: Bearer <jwt_token>
```

#### Initialize Instruments
```
POST /api/admin/initialize-instruments
Authorization: Bearer <jwt_token>
```

## Data Model

### User
- `id`: Primary key
- `name`: User's full name
- `email`: Unique email address
- `password`: Encrypted password
- `role`: USER or ADMIN
- `createdAt`: Account creation timestamp
- `updatedAt`: Last update timestamp

### Instrument
- `id`: Primary key
- `symbol`: Stock symbol (e.g., AAPL)
- `name`: Company name
- `currentPrice`: Current stock price
- `dailyChange`: Daily price change
- `dailyChangePercent`: Daily percentage change
- `weeklyChange`: Weekly price change
- `weeklyChangePercent`: Weekly percentage change
- `dayHigh`: Day's highest price
- `dayLow`: Day's lowest price
- `volume`: Trading volume
- `marketCap`: Market capitalization
- `lastUpdated`: Last data update timestamp
- `active`: Whether the instrument is active

### Favorite
- `id`: Primary key
- `user`: Reference to User entity
- `instrument`: Reference to Instrument entity
- `createdAt`: When the favorite was added

## Scheduled Tasks

The application includes scheduled tasks:

- **Market Data Update**: Runs every 5 minutes to fetch latest stock data
- **Rate Limiting**: Respects Alpha Vantage API limits (5 calls/minute for free tier)

## Security Features

- **JWT Authentication**: Stateless authentication with JSON Web Tokens
- **Password Encryption**: BCrypt password hashing
- **Role-based Authorization**: Different access levels for users and admins
- **CORS Configuration**: Cross-origin resource sharing enabled
- **Input Validation**: Request validation using Bean Validation

## Error Handling

The API returns appropriate HTTP status codes:

- `200`: Success
- `400`: Bad Request (validation errors)
- `401`: Unauthorized (invalid/missing token)
- `403`: Forbidden (insufficient permissions)
- `404`: Not Found
- `500`: Internal Server Error

## Development

### Project Structure

```
src/main/java/com/puentenet/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/            # Data Transfer Objects
├── entity/         # JPA entities
├── repository/     # Data access layer
├── security/       # Security components
└── service/        # Business logic
```

### Adding New Features

1. Create entity classes in `entity/` package
2. Create repository interfaces in `repository/` package
3. Create service classes in `service/` package
4. Create DTOs in `dto/` package
5. Create controllers in `controller/` package
6. Update security configuration if needed

## Production Deployment

### Environment Variables

Set these environment variables in production:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/market_monitor
SPRING_DATASOURCE_USERNAME=your_db_user
SPRING_DATASOURCE_PASSWORD=your_db_password
JWT_SECRET=your-very-long-and-secure-jwt-secret
ALPHA_VANTAGE_API_KEY=your_api_key
```

### Docker Deployment

Create a `Dockerfile`:

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/market-monitor-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## Troubleshooting

### Common Issues

1. **Database Connection**: Ensure PostgreSQL is running and credentials are correct
2. **API Key**: Verify Alpha Vantage API key is valid and has sufficient quota
3. **JWT Secret**: Use a long, secure secret key in production
4. **Port Conflicts**: Ensure port 8080 is available

### Logs

Check application logs for detailed error information:

```bash
tail -f logs/application.log
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License. 