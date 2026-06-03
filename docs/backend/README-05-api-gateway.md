# README 05: API Gateway - Punto de Entrada Único

## PROMPT PARA EL EQUIPO DE DESARROLLO

Como desarrollador backend, tu tarea es implementar el API Gateway que será el punto único de entrada para todas las peticiones al sistema Tecsup World Cup Challenge 2026.

---

## CONFIGURACIÓN SPRING INITIALIZR

### Project
- **Type**: Maven
- **Language**: Java
- **Spring Boot**: 3.5.x

### Project Metadata
- **Group**: pe.tecsup.worldcup
- **Artifact**: api-gateway
- **Name**: api-gateway
- **Package name**: pe.tecsup.worldcup.gateway
- **Packaging**: Jar
- **Java**: 17

### Dependencies
- **Spring Cloud Gateway**
- **Eureka Discovery Client**
- **Actuator**

### Generar en Spring Initializr
```
https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.5.x&packaging=jar&jvmVersion=17&groupId=pe.tecsup.worldcup&artifactId=api-gateway&name=api-gateway&packageName=pe.tecsup.worldcup.gateway&dependencies=gateway,eureka-client,actuator
```

---

## 1. ESTRUCTURA DEL PROYECTO

```
api-gateway
│
├── config
│   └── SecurityConfig
│
├── filter
│   ├── JwtFilter
│   └── LoggingFilter
│
├── resources
│   ├── application.yml
│   └── application-dev.yml
│
└── ApiGatewayApplication.java
```

---

## 2. DEPENDENCIAS (pom.xml)

```xml
<dependencies>
    <!-- Spring Cloud Gateway -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>

    <!-- Eureka Client -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>

    <!-- Config Client -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>

    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
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

## 3. CONFIGURACIÓN (application.yml)

```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway

  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true

      routes:
        # Auth Service
        - id: auth-service
          uri: lb://auth-service
          predicates:
            - Path=/api/auth/**
          filters:
            - RewritePath=/api/auth/(?<segment>.*), /$\{segment}

        # Organization Service
        - id: organization-service
          uri: lb://organization-service
          predicates:
            - Path=/api/organization/**
          filters:
            - RewritePath=/api/organization/(?<segment>.*), /$\{segment}
            - JwtFilter

        # World Cup Service
        - id: worldcup-service
          uri: lb://worldcup-service
          predicates:
            - Path=/api/worldcup/**
          filters:
            - RewritePath=/api/worldcup/(?<segment>.*), /$\{segment}
            - JwtFilter

        # Prediction Service
        - id: prediction-service
          uri: lb://prediction-service
          predicates:
            - Path=/api/predictions/**
          filters:
            - RewritePath=/api/predictions/(?<segment>.*), /$\{segment}
            - JwtFilter

        # Scoring Service
        - id: scoring-service
          uri: lb://scoring-service
          predicates:
            - Path=/api/scoring/**
          filters:
            - RewritePath=/api/scoring/(?<segment>.*), /$\{segment}
            - JwtFilter

        # Leaderboard Service
        - id: leaderboard-service
          uri: lb://leaderboard-service
          predicates:
            - Path=/api/leaderboard/**
          filters:
            - RewritePath=/api/leaderboard/(?<segment>.*), /$\{segment}
            - JwtFilter

        # Social Service
        - id: social-service
          uri: lb://social-service
          predicates:
            - Path=/api/social/**
          filters:
            - RewritePath=/api/social/(?<segment>.*), /$\{segment}
            - JwtFilter

        # Gamification Service
        - id: gamification-service
          uri: lb://gamification-service
          predicates:
            - Path=/api/gamification/**
          filters:
            - RewritePath=/api/gamification/(?<segment>.*), /$\{segment}
            - JwtFilter

        # Notification Service
        - id: notification-service
          uri: lb://notification-service
          predicates:
            - Path=/api/notifications/**
          filters:
            - RewritePath=/api/notifications/(?<segment>.*), /$\{segment}
            - JwtFilter

      globalcors:
        cors-configurations:
          '[/**]':
            allowedOrigins: "*"
            allowedMethods:
              - GET
              - POST
              - PUT
              - DELETE
              - OPTIONS
            allowedHeaders: "*"

eureka:
  client:
    service-url:
      defaultZone: http://discovery-service:8761/eureka/
  instance:
    prefer-ip-address: true

jwt:
  secret: ${JWT_SECRET:tecsup-world-cup-secret-key-2026}
  expiration: ${JWT_EXPIRATION:86400000}
```

---

## 4. JWT FILTER

### filter/JwtFilter.java
```java
package com.tecsup.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.Config> {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final Key key;

    public JwtFilter() {
        super(Config.class);
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().value();
            
            // Skip JWT validation for auth endpoints
            if (path.contains("/auth/login") || path.contains("/auth/register")) {
                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);

            try {
                Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

                // Add user info to headers
                exchange.getRequest().mutate()
                    .header("X-User-Id", claims.getSubject())
                    .header("X-User-Role", claims.get("role", String.class))
                    .build();

                return chain.filter(exchange);

            } catch (Exception e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    public static class Config {
        // Configuration properties if needed
    }
}
```

---

## 5. LOGGING FILTER

### filter/LoggingFilter.java
```java
package com.tecsup.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        logger.info("Incoming request: {} {}", request.getMethod(), request.getPath());
        logger.info("Remote address: {}", request.getRemoteAddress());
        
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            logger.info("Response status: {}", exchange.getResponse().getStatusCode());
        }));
    }

    @Override
    public int getOrder() {
        return -1; // Execute first
    }
}
```

---

## 6. SECURITY CONFIG

### config/SecurityConfig.java
```java
package com.tecsup.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf().disable()
            .authorizeExchange()
            .pathMatchers("/api/auth/**").permitAll()
            .anyExchange().authenticated()
            .and()
            .httpBasic().disable()
            .formLogin().disable();

        return http.build();
    }
}
```

---

## 7. MAIN APPLICATION

### ApiGatewayApplication.java
```java
package com.tecsup.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
```

---

## 8. RATE LIMITING (Opcional)

### Agregar a application.yml
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: lb://auth-service
          predicates:
            - Path=/api/auth/**
          filters:
            - RewritePath=/api/auth/(?<segment>.*), /$\{segment}
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
```

---

## 9. CIRCUIT BREAKER (Opcional)

### Agregar dependencia
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-reactor-resilience4j</artifactId>
</dependency>
```

### Configuración en application.yml
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: prediction-service
          uri: lb://prediction-service
          predicates:
            - Path=/api/predictions/**
          filters:
            - RewritePath=/api/predictions/(?<segment>.*), /$\{segment}
            - name: CircuitBreaker
              args:
                name: predictionCircuitBreaker
                fallbackUri: forward:/fallback/prediction

resilience4j:
  circuitbreaker:
    instances:
      predictionCircuitBreaker:
        failureRateThreshold: 50
        waitDurationInOpenState: 5000
        slidingWindowSize: 10
```

---

## 10. FALLBACK CONTROLLER (Opcional)

```java
@RestController
public class FallbackController {

    @GetMapping("/fallback/prediction")
    public ResponseEntity<String> predictionFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body("Prediction service is temporarily unavailable. Please try again later.");
    }
}
```

---

## 11. WEBSOCKET ROUTING

### Agregar a application.yml
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: social-websocket
          uri: lb://social-service
          predicates:
            - Path=/ws/chat/**
          filters:
            - RewritePath=/ws/chat/(?<segment>.*), /$\{segment}
```

---

## 12. TESTING

### Test de rutas
```bash
# Test Auth Service (sin JWT)
curl http://localhost:8080/api/auth/login

# Test Prediction Service (con JWT)
curl -H "Authorization: Bearer <token>" http://localhost:8080/api/predictions
```

---

## 13. DEPLOYMENT

### Dockerfile
```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/api-gateway.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Build y Run
```bash
mvn clean package
docker build -t api-gateway .
docker run -p 8080:8080 api-gateway
```

---

## TAREA SIGUIENTE

Una vez implementado el API Gateway, procede al README-06-discovery-service.md para implementar el Eureka Server.
