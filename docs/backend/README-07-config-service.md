# README 07: Config Service - Configuración Centralizada

## PROMPT PARA EL EQUIPO DE DESARROLLO

Como desarrollador backend, tu tarea es implementar el Config Server para la configuración centralizada de todos los microservicios en el sistema Tecsup World Cup Challenge 2026.

---

## CONFIGURACIÓN SPRING INITIALIZR

### Project
- **Type**: Maven
- **Language**: Java
- **Spring Boot**: 3.5.x

### Project Metadata
- **Group**: pe.tecsup.worldcup
- **Artifact**: config-service
- **Name**: config-service
- **Package name**: pe.tecsup.worldcup.config
- **Packaging**: Jar
- **Java**: 17

### Dependencies
- **Config Server**
- **Eureka Discovery Client**

### Generar en Spring Initializr
```
https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.5.x&packaging=jar&jvmVersion=17&groupId=pe.tecsup.worldcup&artifactId=config-service&name=config-service&packageName=pe.tecsup.worldcup.config&dependencies=config-server,eureka-client
```

---

## 1. ESTRUCTURA DEL PROYECTO

```
config-service
│
├── resources
│   ├── application.yml
│   ├── application-dev.yml
│   └── config
│       ├── api-gateway.yml
│       ├── discovery-service.yml
│       ├── config-service.yml
│       ├── auth-service.yml
│       ├── organization-service.yml
│       ├── worldcup-service.yml
│       ├── prediction-service.yml
│       ├── scoring-service.yml
│       ├── leaderboard-service.yml
│       ├── social-service.yml
│       ├── gamification-service.yml
│       ├── notification-service.yml
│       ├── admin-service.yml
│       └── group-service.yml
│
└── ConfigApplication.java
```

---

## 2. DEPENDENCIAS (pom.xml)

```xml
<dependencies>
    <!-- Config Server -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
    </dependency>

    <!-- Eureka Client -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>

    <!-- Actuator -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

---

## 3. CONFIGURACIÓN PRINCIPAL (application.yml)

```yaml
server:
  port: 8888

spring:
  application:
    name: config-service
  profiles:
    active: native
  cloud:
    config:
      server:
        native:
          search-locations: classpath:/config,file:./config,file:../config

eureka:
  client:
    service-url:
      defaultZone: http://discovery-service:8761/eureka/
  instance:
    prefer-ip-address: true

management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: always
```

---

## 4. MAIN APPLICATION

### ConfigApplication.java
```java
package com.tecsup.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableDiscoveryClient;

@SpringBootApplication
@EnableConfigServer
@EnableDiscoveryClient
public class ConfigApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigApplication.class, args);
    }
}
```

---

## 5. CONFIGURACIONES POR SERVICIO

Estado actual del proyecto: se publican configuraciones para infraestructura (gateway/discovery/config) y servicios de negocio (incluyendo admin/group).

### config/auth-service.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/auth_db
    username: admin
    password: admin123
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

  redis:
    host: redis
    port: 6379

  rabbitmq:
    host: rabbitmq
    port: 5672
    username: admin
    password: admin123

jwt:
  secret: ${JWT_SECRET:tecsup-world-cup-secret-key-2026}
  expiration: ${JWT_EXPIRATION:86400000}

logging:
  level:
    com.tecsup: DEBUG
```

### config/organization-service.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/organization_db
    username: admin
    password: admin123
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

  redis:
    host: redis
    port: 6379

logging:
  level:
    com.tecsup: DEBUG
```

### config/worldcup-service.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/worldcup_db
    username: admin
    password: admin123
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

  redis:
    host: redis
    port: 6379

  rabbitmq:
    host: rabbitmq
    port: 5672
    username: admin
    password: admin123

api:
  football:
    api-key: ${API_FOOTBALL_KEY:your-api-key}
    base-url: https://api-football-v1.p.rapidapi.com

logging:
  level:
    com.tecsup: DEBUG
```

### config/prediction-service.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/prediction_db
    username: admin
    password: admin123
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

  redis:
    host: redis
    port: 6379

  rabbitmq:
    host: rabbitmq
    port: 5672
    username: admin
    password: admin123

prediction:
  lock-minutes-before-match: 5

logging:
  level:
    com.tecsup: DEBUG
```

### config/scoring-service.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/scoring_db
    username: admin
    password: admin123
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

  redis:
    host: redis
    port: 6379

  rabbitmq:
    host: rabbitmq
    port: 5672
    username: admin
    password: admin123

scoring:
  streak-3-bonus: 20
  streak-5-bonus: 50
  streak-10-bonus: 150
  perfect-match-bonus: 100

logging:
  level:
    com.tecsup: DEBUG
```

### config/leaderboard-service.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/leaderboard_db
    username: admin
    password: admin123
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

  redis:
    host: redis
    port: 6379

  rabbitmq:
    host: rabbitmq
    port: 5672
    username: admin
    password: admin123

leaderboard:
  cache-ttl-seconds: 300

logging:
  level:
    com.tecsup: DEBUG
```

### config/social-service.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/social_db
    username: admin
    password: admin123
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

  redis:
    host: redis
    port: 6379

  rabbitmq:
    host: rabbitmq
    port: 5672
    username: admin
    password: admin123

websocket:
  allowed-origins: "*"

logging:
  level:
    com.tecsup: DEBUG
```

### config/gamification-service.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/gamification_db
    username: admin
    password: admin123
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

  redis:
    host: redis
    port: 6379

  rabbitmq:
    host: rabbitmq
    port: 5672
    username: admin
    password: admin123

logging:
  level:
    com.tecsup: DEBUG
```

### config/notification-service.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/notification_db
    username: admin
    password: admin123
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

  redis:
    host: redis
    port: 6379

  rabbitmq:
    host: rabbitmq
    port: 5672
    username: admin
    password: admin123

email:
  host: smtp.gmail.com
  port: 587
  username: ${EMAIL_USERNAME}
  password: ${EMAIL_PASSWORD}

logging:
  level:
    com.tecsup: DEBUG
```

---

## 6. CONFIGURACIÓN DE CLIENTES

### bootstrap.yml para cada microservicio
```yaml
spring:
  application:
    name: auth-service
  cloud:
    config:
      uri: http://config-service:8888
      fail-fast: true
      retry:
        initial-interval: 1000
        max-attempts: 6
```

### Dependencia para clientes
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

---

## 7. TESTING

### Verificar configuración de un servicio
```bash
curl http://localhost:8888/auth-service/default
```

### Verificar configuración con perfil
```bash
curl http://localhost:8888/auth-service/dev
```

### Verificar todas las configuraciones
```bash
curl http://localhost:8888/actuator/env
```

---

## 8. REFRESH DE CONFIGURACIÓN EN TIEMPO DE EJECUCIÓN

### Agregar @RefreshScope en beans
```java
@RestController
@RefreshScope
public class SomeController {
    
    @Value("${some.property}")
    private String someProperty;
    
    @GetMapping("/property")
    public String getProperty() {
        return someProperty;
    }
}
```

### Endpoint para refresh
```bash
curl -X POST http://localhost:8081/actuator/refresh
```

---

## 9. ENCRYPTACIÓN DE PROPIEDADES SENSIBLES (Opcional)

### Habilitar encriptación
```yaml
encrypt:
  key: my-secret-key-for-encryption
```

### Encriptar valor
```bash
curl http://localhost:8888/encrypt -d my-password
```

### Usar valor encriptado en config
```yaml
spring:
  datasource:
    password: '{cipher}AQAZz4F9...'
```

---

## 10. DEPLOYMENT

### Dockerfile
```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/config-service.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### docker-compose.yml
```yaml
config-service:
  build: ./config-service
  container_name: config-service
  ports:
    - "8888:8888"
  environment:
    SPRING_APPLICATION_NAME: config-service
  volumes:
    - ./config-repo:/home/app/config-repo
  networks:
    - world-cup-network
  depends_on:
    - discovery-service
```

---

## 11. MONITOREO

### Actuator endpoints
```bash
# Health check
curl http://localhost:8888/actuator/health

# Environment
curl http://localhost:8888/actuator/env

# Refresh
curl -X POST http://localhost:8888/actuator/refresh
```

---

## TAREA SIGUIENTE

Una vez implementado el Config Service, procede al README-08-auth-service.md para implementar el Auth Service.
