# README 08: Auth Service - Autenticación y Autorización

## PROMPT PARA EL EQUIPO DE DESARROLLO

Como desarrollador backend, tu tarea es implementar el Auth Service responsable de la autenticación, autorización y gestión de usuarios en el sistema Tecsup World Cup Challenge 2026.

---

## CONFIGURACIÓN SPRING INITIALIZR

### Project
- **Type**: Maven
- **Language**: Java
- **Spring Boot**: 3.5.x

### Project Metadata
- **Group**: pe.tecsup.worldcup
- **Artifact**: auth-service
- **Name**: auth-service
- **Package name**: pe.tecsup.worldcup.auth
- **Packaging**: Jar
- **Java**: 17

### Dependencies
- **Spring Web**
- **Spring Security**
- **Spring Data JPA**
- **PostgreSQL Driver**
- **Validation**
- **Actuator**
- **OpenFeign**
- **Eureka Discovery Client**
- **Lombok**

### Generar en Spring Initializr
```
https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.5.x&packaging=jar&jvmVersion=17&groupId=pe.tecsup.worldcup&artifactId=auth-service&name=auth-service&packageName=pe.tecsup.worldcup.auth&dependencies=web,security,data-jpa,postgresql,validation,actuator,openfeign,eureka-client,lombok
```

---

## 1. ESTRUCTURA DEL PROYECTO

```
auth-service
│
├── controller
│   ├── AuthController
│   └── VerificationController
│
├── service
│   ├── AuthService
│   ├── JwtService
│   └── EmailVerificationService
│
├── repository
│   ├── UserRepository
│   ├── RoleRepository
│   └── TokenRepository
│
├── entity
│   ├── User
│   ├── Role
│   └── EmailVerificationToken
│
├── dto
│   ├── LoginRequest
│   ├── RegisterRequest
│   ├── VerifyEmailRequest
│   └── AuthResponse
│
├── security
│   ├── JwtFilter
│   ├── JwtProvider
│   └── SecurityConfig
│
├── exception
│   └── AuthException
│
├── resources
│   ├── application.yml
│   └── application-dev.yml
│
└── AuthApplication.java
```

---

## 2. DEPENDENCIAS (pom.xml)

```xml
<dependencies>
    <!-- Spring Boot Starter Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Boot Starter Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- Spring Boot Starter Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- Spring Boot Starter Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- PostgreSQL -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
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

    <!-- Redis -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <!-- RabbitMQ -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-amqp</artifactId>
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

## 3. ENTIDADES

### entity/Role.java
```java
package com.tecsup.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String name;
    
    @Column(length = 255)
    private String description;
}
```

### entity/User.java
```java
package com.tecsup.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    
    @Column(name = "avatar_id")
    private Long avatarId;
    
    @Column(unique = true, nullable = false, length = 150)
    private String email;
    
    @Column(nullable = false)
    private String passwordHash;
    
    @Column(nullable = false, length = 100)
    private String firstName;
    
    @Column(nullable = false, length = 100)
    private String lastName;
    
    @Column(name = "email_verified")
    private Boolean emailVerified = false;
    
    @Column(length = 20)
    private String status = "ACTIVE";
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

### entity/EmailVerificationToken.java
```java
package com.tecsup.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, length = 10)
    private String token;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    
    private Boolean used = false;
}
```

---

## 4. DTOS

### dto/RegisterRequest.java
```java
package com.tecsup.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    @Size(min = 8, max = 20)
    private String password;
    
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;
    
    @NotBlank
    private String role; // STUDENT or TEACHER
    
    private String studentCode; // Required for students
}
```

### dto/LoginRequest.java
```java
package com.tecsup.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    private String password;
}
```

### dto/VerifyEmailRequest.java
```java
package com.tecsup.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerifyEmailRequest {
    
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    @Size(min = 6, max = 6)
    private String token;
}
```

### dto/AuthResponse.java
```java
package com.tecsup.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String email;
    private String role;
}
```

---

## 5. REPOSITORIES

### repository/UserRepository.java
```java
package com.tecsup.auth.repository;

import com.tecsup.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
}
```

### repository/RoleRepository.java
```java
package com.tecsup.auth.repository;

import com.tecsup.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
```

### repository/TokenRepository.java
```java
package com.tecsup.auth.repository;

import com.tecsup.auth.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByTokenAndUserEmail(String token, String email);
}
```

---

## 6. SERVICES

### service/JwtService.java
```java
package com.tecsup.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
    
    public String generateToken(Long userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(String.valueOf(userId))
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(getSigningKey())
            .compact();
    }
    
    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
    
    public Long extractUserId(String token) {
        return Long.parseLong(validateToken(token).getSubject());
    }
    
    public String extractRole(String token) {
        return validateToken(token).get("role", String.class);
    }
}
```

### service/EmailVerificationService.java
```java
package com.tecsup.auth.service;

import com.tecsup.auth.entity.EmailVerificationToken;
import com.tecsup.auth.entity.User;
import com.tecsup.auth.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    
    private final TokenRepository tokenRepository;
    
    public String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
    
    public EmailVerificationToken createToken(User user) {
        String otp = generateOtp();
        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(user);
        token.setToken(otp);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        token.setUsed(false);
        
        return tokenRepository.save(token);
    }
    
    public boolean verifyToken(String email, String otp) {
        return tokenRepository.findByTokenAndUserEmail(otp, email)
            .filter(t -> !t.getUsed() && t.getExpiresAt().isAfter(LocalDateTime.now()))
            .map(t -> {
                t.setUsed(true);
                tokenRepository.save(t);
                return true;
            })
            .orElse(false);
    }
}
```

### service/AuthService.java
```java
package com.tecsup.auth.service;

import com.tecsup.auth.dto.AuthResponse;
import com.tecsup.auth.dto.LoginRequest;
import com.tecsup.auth.dto.RegisterRequest;
import com.tecsup.auth.entity.Role;
import com.tecsup.auth.entity.User;
import com.tecsup.auth.repository.RoleRepository;
import com.tecsup.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailVerificationService emailVerificationService;
    
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        Role role = roleRepository.findByName(request.getRole())
            .orElseThrow(() -> new RuntimeException("Role not found"));
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(role);
        user.setEmailVerified(false);
        
        user = userRepository.save(user);
        
        // Generate and send OTP
        String otp = emailVerificationService.createToken(user).getToken();
        sendOtpEmail(request.getEmail(), otp);
        
        return new AuthResponse(null, null, user.getId(), user.getEmail(), role.getName());
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        if (!user.getEmailVerified()) {
            throw new RuntimeException("Email not verified");
        }
        
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().getName());
        
        return new AuthResponse(token, "Bearer", user.getId(), user.getEmail(), user.getRole().getName());
    }
    
    public boolean verifyEmail(String email, String token) {
        boolean verified = emailVerificationService.verifyToken(email, token);
        
        if (verified) {
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            user.setEmailVerified(true);
            userRepository.save(user);
        }
        
        return verified;
    }
    
    private void sendOtpEmail(String email, String otp) {
        // Implement email sending logic
        // For now, just log it
        System.out.println("Sending OTP " + otp + " to " + email);
    }
}
```

---

## 7. CONTROLLERS

### controller/AuthController.java
```java
package com.tecsup.auth.controller;

import com.tecsup.auth.dto.AuthResponse;
import com.tecsup.auth.dto.LoginRequest;
import com.tecsup.auth.dto.RegisterRequest;
import com.tecsup.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
```

### controller/VerificationController.java
```java
package com.tecsup.auth.controller;

import com.tecsup.auth.dto.VerifyEmailRequest;
import com.tecsup.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class VerificationController {
    
    private final AuthService authService;
    
    @PostMapping("/verify-email")
    public ResponseEntity<Boolean> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        return ResponseEntity.ok(authService.verifyEmail(request.getEmail(), request.getToken()));
    }
}
```

---

## 8. SECURITY CONFIG

### config/SecurityConfig.java
```java
package com.tecsup.auth.config;

import com.tecsup.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final AuthService authService;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(authService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests()
            .requestMatchers("/auth/register", "/auth/login", "/auth/verify-email").permitAll()
            .anyRequest().authenticated()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
        return http.build();
    }
}
```

---

## 9. MAIN APPLICATION

### AuthApplication.java
```java
package com.tecsup.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
```

---

## 10. SEED DATA

### DataInitializer.java
```java
package com.tecsup.auth.config;

import com.tecsup.auth.entity.Role;
import com.tecsup.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final RoleRepository roleRepository;
    
    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(null, "ADMIN", "Administrator"));
            roleRepository.save(new Role(null, "STUDENT", "Student"));
            roleRepository.save(new Role(null, "TEACHER", "Teacher"));
        }
    }
}
```

---

## 11. TESTING

### Test de registro
```bash
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "student@tecsup.edu.pe",
    "password": "password123",
    "firstName": "Juan",
    "lastName": "Perez",
    "role": "STUDENT",
    "studentCode": "2023001"
  }'
```

### Test de login
```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "student@tecsup.edu.pe",
    "password": "password123"
  }'
```

### Test de verificación de email
```bash
curl -X POST http://localhost:8081/auth/verify-email \
  -H "Content-Type: application/json" \
  -d '{
    "email": "student@tecsup.edu.pe",
    "token": "123456"
  }'
```

---

## TAREA SIGUIENTE

Una vez implementado el Auth Service, procede al README-09-organization-service.md para implementar el Organization Service.
