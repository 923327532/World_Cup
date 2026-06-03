# README 04: Infraestructura - Docker, Redis, RabbitMQ

## PROMPT PARA EL EQUIPO DE DESARROLLO

Como ingeniero de DevOps, tu tarea es configurar la infraestructura base que soportará todos los microservicios del sistema Tecsup World Cup Challenge 2026.

---

## 1. ESTRUCTURA DE INFRAESTRUCTURA

```
infrastructure
│
├── postgres
│   ├── init.sql
│   └── seed.sql
│
├── redis
│   └── redis.conf
│
├── rabbitmq
│   └── rabbitmq.conf
│
├── monitoring
│   ├── prometheus.yml
│   └── grafana
│
└── nginx
    └── nginx.conf
```

---

## 2. DOCKER COMPOSE GENERAL

### docker-compose.yml
```yaml
version: '3.8'

services:
  # PostgreSQL
  postgres:
    image: postgres:15
    container_name: world-cup-postgres
    environment:
      POSTGRES_DB: worldcup
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: admin123
    ports:
      - "5432:5432"
    volumes:
      - ./infrastructure/postgres/init.sql:/docker-entrypoint-initdb.d/init.sql
      - ./infrastructure/postgres/seed.sql:/docker-entrypoint-initdb.d/seed.sql
      - postgres-data:/var/lib/postgresql/data
    networks:
      - world-cup-network

  # Redis
  redis:
    image: redis:7-alpine
    container_name: world-cup-redis
    ports:
      - "6379:6379"
    volumes:
      - ./infrastructure/redis/redis.conf:/usr/local/etc/redis/redis.conf
      - redis-data:/data
    networks:
      - world-cup-network

  # RabbitMQ
  rabbitmq:
    image: rabbitmq:3-management-alpine
    container_name: world-cup-rabbitmq
    environment:
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: admin123
    ports:
      - "5672:5672"
      - "15672:15672"
    volumes:
      - rabbitmq-data:/var/lib/rabbitmq
    networks:
      - world-cup-network

  # Eureka Server
  discovery-service:
    build: ./discovery-service
    container_name: discovery-service
    ports:
      - "8761:8761"
    environment:
      SPRING_APPLICATION_NAME: discovery-service
    networks:
      - world-cup-network
    depends_on:
      - postgres

  # Config Server
  config-service:
    build: ./config-service
    container_name: config-service
    ports:
      - "8888:8888"
    environment:
      SPRING_APPLICATION_NAME: config-service
    networks:
      - world-cup-network
    depends_on:
      - discovery-service

  # API Gateway
  api-gateway:
    build: ./api-gateway
    container_name: api-gateway
    ports:
      - "8080:8080"
    environment:
      SPRING_APPLICATION_NAME: api-gateway
    networks:
      - world-cup-network
    depends_on:
      - discovery-service
      - config-service

volumes:
  postgres-data:
  redis-data:
  rabbitmq-data:

networks:
  world-cup-network:
    driver: bridge
```

---

## 3. POSTGRESQL CONFIGURACIÓN

### infrastructure/postgres/init.sql
```sql
-- Crear bases de datos para cada servicio
CREATE DATABASE auth_db;
CREATE DATABASE organization_db;
CREATE DATABASE worldcup_db;
CREATE DATABASE prediction_db;
CREATE DATABASE scoring_db;
CREATE DATABASE leaderboard_db;
CREATE DATABASE social_db;
CREATE DATABASE gamification_db;
CREATE DATABASE notification_db;
```

### infrastructure/postgres/seed.sql
```sql
-- Datos iniciales comunes
-- Este archivo se ejecutará después de init.sql
-- Incluirá los datos de sedes, departamentos, roles, etc.
```

---

## 4. REDIS CONFIGURACIÓN

### infrastructure/redis/redis.conf
```conf
# Configuración básica de Redis
bind 0.0.0.0
port 6379
maxmemory 256mb
maxmemory-policy allkeys-lru
save 900 1
save 300 10
save 60 10000
```

### Usos de Redis
- **Cache de rankings**: Rankings globales y por categorías
- **Sesiones de usuarios**: Tokens JWT activos
- **Usuarios conectados**: Conteo en tiempo real
- **Estadísticas en vivo**: Predicciones por minuto, partidos activos
- **Cache de predicciones**: Predicciones frecuentemente accedidas

---

## 5. RABBITMQ CONFIGURACIÓN

### infrastructure/rabbitmq/rabbitmq.conf
```conf
# Configuración básica de RabbitMQ
listeners.tcp.default = 5672
management.tcp.port = 15672
```

### Colas de RabbitMQ
```java
// Colas principales
public class QueueConfig {
    
    // Predicciones
    public static final String PREDICTION_CREATED_QUEUE = "prediction.created";
    public static final String PREDICTION_SCORED_QUEUE = "prediction.scored";
    
    // Partidos
    public static final String MATCH_FINISHED_QUEUE = "match.finished";
    public static final String MATCH_GOAL_QUEUE = "match.goal";
    
    // Rankings
    public static final String LEADERBOARD_UPDATED_QUEUE = "leaderboard.updated";
    
    // Gamificación
    public static final String BADGE_UNLOCKED_QUEUE = "badge.unlocked";
    
    // Notificaciones
    public static final String NOTIFICATION_QUEUE = "notification.send";
}
```

### Intercambios
- **prediction.exchange** - Para eventos de predicciones
- **match.exchange** - Para eventos de partidos
- **ranking.exchange** - Para eventos de rankings
- **gamification.exchange** - Para eventos de gamificación
- **notification.exchange** - Para eventos de notificaciones

---

## 6. PROMETHEUS CONFIGURACIÓN (Opcional)

### infrastructure/monitoring/prometheus.yml
```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'api-gateway'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['api-gateway:8080']

  - job_name: 'auth-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['auth-service:8081']

  - job_name: 'worldcup-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['worldcup-service:8082']

  # Agregar resto de servicios...
```

---

## 7. NGINX CONFIGURACIÓN (Opcional para producción)

### infrastructure/nginx/nginx.conf
```nginx
upstream api-gateway {
    server api-gateway:8080;
}

server {
    listen 80;
    server_name localhost;

    location / {
        proxy_pass http://api-gateway;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    location /ws {
        proxy_pass http://api-gateway;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

---

## 8. SCRIPTS DE AUTOMATIZACIÓN

### scripts/build-all.sh
```bash
#!/bin/bash

echo "Construyendo todos los microservicios..."

# Construir cada servicio
cd api-gateway && mvn clean package -DskipTests && cd ..
cd discovery-service && mvn clean package -DskipTests && cd ..
cd config-service && mvn clean package -DskipTests && cd ..
cd auth-service && mvn clean package -DskipTests && cd ..
cd organization-service && mvn clean package -DskipTests && cd ..
cd worldcup-service && mvn clean package -DskipTests && cd ..
cd prediction-service && mvn clean package -DskipTests && cd ..
cd scoring-service && mvn clean package -DskipTests && cd ..
cd leaderboard-service && mvn clean package -DskipTests && cd ..
cd social-service && mvn clean package -DskipTests && cd ..
cd gamification-service && mvn clean package -DskipTests && cd ..
cd notification-service && mvn clean package -DskipTests && cd ..

echo "Construcción completada!"
```

### scripts/start-local.sh
```bash
#!/bin/bash

echo "Iniciando infraestructura local..."
docker-compose up -d postgres redis rabbitmq

echo "Esperando 10 segundos para que la base de datos esté lista..."
sleep 10

echo "Iniciando microservicios..."
docker-compose up -d discovery-service config-service
sleep 5
docker-compose up -d api-gateway
sleep 5
docker-compose up -d auth-service organization-service worldcup-service
sleep 5
docker-compose up -d prediction-service scoring-service leaderboard-service
sleep 5
docker-compose up -d social-service gamification-service notification-service

echo "Todos los servicios iniciados!"
```

### scripts/stop-local.sh
```bash
#!/bin/bash

echo "Deteniendo todos los servicios..."
docker-compose down

echo "Servicios detenidos!"
```

### scripts/reset-db.sh
```bash
#!/bin/bash

echo "Reseteando base de datos..."
docker-compose down -v
docker-compose up -d postgres redis rabbitmq

echo "Base de datos reseteada!"
```

---

## 9. ARCHIVO .ENV

### .env
```env
# PostgreSQL
POSTGRES_DB=worldcup
POSTGRES_USER=admin
POSTGRES_PASSWORD=admin123
POSTGRES_HOST=postgres
POSTGRES_PORT=5432

# Redis
REDIS_HOST=redis
REDIS_PORT=6379

# RabbitMQ
RABBITMQ_HOST=rabbitmq
RABBITMQ_PORT=5672
RABBITMQ_USER=admin
RABBITMQ_PASSWORD=admin123

# Eureka
EUREKA_HOST=discovery-service
EUREKA_PORT=8761

# Config Server
CONFIG_HOST=config-service
CONFIG_PORT=8888

# API Gateway
GATEWAY_PORT=8080

# JWT
JWT_SECRET=tecsup-world-cup-secret-key-2026
JWT_EXPIRATION=86400000
```

---

## 10. DOCKERFILES POR SERVICIO

### docker/api-gateway/Dockerfile
```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/api-gateway.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### docker/auth-service/Dockerfile
```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/auth-service.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### (Similar para cada servicio...)

---

## 11. COMANDOS ÚTILES

### Iniciar todo
```bash
docker-compose up -d
```

### Ver logs
```bash
docker-compose logs -f [nombre-servicio]
```

### Detener todo
```bash
docker-compose down
```

### Reconstruir servicio específico
```bash
docker-compose up -d --build [nombre-servicio]
```

### Entrar a contenedor
```bash
docker exec -it [nombre-contenedor] bash
```

---

## 12. MONITOREO DE INFRAESTRUCTURA

### Verificar servicios
```bash
# Verificar PostgreSQL
docker exec -it world-cup-postgres psql -U admin -d worldcup

# Verificar Redis
docker exec -it world-cup-redis redis-cli

# Verificar RabbitMQ
# Acceder a http://localhost:15672
# Usuario: admin
# Contraseña: admin123
```

### Verificar Eureka
- Acceder a http://localhost:8761
- Ver todos los servicios registrados

### Verificar Config Server
- Acceder a http://localhost:8888
- Ver configuraciones centralizadas

---

## TAREA SIGUIENTE

Una vez configurada la infraestructura base, procede al README-05-api-gateway.md para implementar el API Gateway.
