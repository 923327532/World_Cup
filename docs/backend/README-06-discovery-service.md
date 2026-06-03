# README 06: Discovery Service - Eureka Server

## PROMPT PARA EL EQUIPO DE DESARROLLO

Como desarrollador backend, tu tarea es implementar el Eureka Server para el registro y descubrimiento automático de microservicios en el sistema Tecsup World Cup Challenge 2026.

---

## CONFIGURACIÓN SPRING INITIALIZR

### Project
- **Type**: Maven
- **Language**: Java
- **Spring Boot**: 3.5.x

### Project Metadata
- **Group**: pe.tecsup.worldcup
- **Artifact**: discovery-service
- **Name**: discovery-service
- **Package name**: pe.tecsup.worldcup.discovery
- **Packaging**: Jar
- **Java**: 17

### Dependencies
- **Eureka Server**

### Generar en Spring Initializr
```
https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.5.x&packaging=jar&jvmVersion=17&groupId=pe.tecsup.worldcup&artifactId=discovery-service&name=discovery-service&packageName=pe.tecsup.worldcup.discovery&dependencies=eureka-server
```

---

## 1. ESTRUCTURA DEL PROYECTO

```
discovery-service
│
├── resources
│   ├── application.yml
│   └── application-dev.yml
│
└── DiscoveryApplication.java
```

---

## 2. DEPENDENCIAS (pom.xml)

```xml
<dependencies>
    <!-- Eureka Server -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>

    <!-- Spring Boot Actuator -->
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

## 3. CONFIGURACIÓN (application.yml)

```yaml
server:
  port: 8761

spring:
  application:
    name: discovery-service

eureka:
  instance:
    hostname: discovery-service
    prefer-ip-address: true
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://discovery-service:8761/eureka/

  server:
    enable-self-preservation: false
    eviction-interval-timer-in-ms: 5000

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

### DiscoveryApplication.java
```java
package com.tecsup.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryApplication.class, args);
    }
}
```

---

## 5. CONFIGURACIÓN DE CLIENTES EUREKA

### application.yml para cada microservicio
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://discovery-service:8761/eureka/
  instance:
    prefer-ip-address: true
    lease-renewal-interval-in-seconds: 10
    lease-expiration-duration-in-seconds: 30
```

### Dependencia para clientes
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

### Anotación en cada microservicio
```java
@SpringBootApplication
@EnableDiscoveryClient
public class AuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
```

---

## 6. CONFIGURACIÓN DE PEER A PEER (Opcional para alta disponibilidad)

### application-peer1.yml
```yaml
server:
  port: 8761

spring:
  application:
    name: discovery-service
  profiles: peer1

eureka:
  instance:
    hostname: discovery-peer1
  client:
    service-url:
      defaultZone: http://discovery-peer2:8762/eureka/
```

### application-peer2.yml
```yaml
server:
  port: 8762

spring:
  application:
    name: discovery-service
  profiles: peer2

eureka:
  instance:
    hostname: discovery-peer2
  client:
    service-url:
      defaultZone: http://discovery-peer1:8761/eureka/
```

---

## 7. DASHBOARD DE EUREKA

### Acceso
- URL: http://localhost:8761
- Muestra todos los servicios registrados
- Estado de cada instancia
- Información de salud

### Información mostrada
- **Application**: Nombre del servicio
- **Instances**: Instancias disponibles
- **Status**: UP, DOWN, STARTING
- **URL**: URL del servicio
- **Last Heartbeat**: Último heartbeat recibido

---

## 8. TESTING

### Verificar que Eureka está corriendo
```bash
curl http://localhost:8761/eureka/apps
```

### Respuesta esperada
```xml
<applications>
  <applications__hashcode>UP_1_</applications__hashcode>
  <application>
    <name>DISCOVERY-SERVICE</name>
    <instance>
      <instanceId>discovery-service:8761</instanceId>
      <status>UP</status>
      ...
    </instance>
  </application>
</applications>
```

### Verificar que un servicio se registró
```bash
# Después de iniciar auth-service
curl http://localhost:8761/eureka/apps/AUTH-SERVICE
```

---

## 9. CONFIGURACIÓN DE SEGURIDAD (Opcional)

### Agregar dependencia
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### application.yml
```yaml
spring:
  security:
    user:
      name: admin
      password: admin123

eureka:
  client:
    service-url:
      defaultZone: http://admin:admin123@discovery-service:8761/eureka/
```

---

## 10. MONITOREO

### Actuator endpoints
```bash
# Health check
curl http://localhost:8761/actuator/health

# Metrics
curl http://localhost:8761/actuator/metrics

# Eureka info
curl http://localhost:8761/actuator/eureka
```

---

## 11. DEPLOYMENT

### Dockerfile
```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/discovery-service.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### docker-compose.yml
```yaml
discovery-service:
  build: ./discovery-service
  container_name: discovery-service
  ports:
    - "8761:8761"
  environment:
    SPRING_APPLICATION_NAME: discovery-service
  networks:
    - world-cup-network
```

---

## 12. TROUBLESHOOTING

### Servicios no se registran
- Verificar que el servicio tiene la anotación @EnableDiscoveryClient
- Verificar la configuración de eureka.client.service-url
- Verificar que Eureka Server está accesible
- Revisar logs del servicio

### Servicios aparecen como DOWN
- Verificar que el servicio está corriendo
- Verificar el lease-renewal-interval-in-seconds
- Verificar firewall/network

### Self-preservation mode
- Eureka entra en modo self-preservation si pierde heartbeats
- Desactivar en desarrollo: eureka.server.enable-self-preservation=false
- Mantener activo en producción

---

## TAREA SIGUIENTE

Una vez implementado el Discovery Service, procede al README-07-config-service.md para implementar el Config Server.
