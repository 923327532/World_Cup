# README 15: Gamification y Notification Services - Insignias y Notificaciones

## PROMPT PARA EL EQUIPO DE DESARROLLO

Como desarrollador backend, tu tarea es implementar los servicios de Gamification (insignias y premios) y Notification (notificaciones y alertas).

---

## CONFIGURACIÓN SPRING INITIALIZR

### Gamification Service

### Project
- **Type**: Maven
- **Language**: Java
- **Spring Boot**: 3.5.x

### Project Metadata
- **Group**: pe.tecsup.worldcup
- **Artifact**: gamification-service
- **Name**: gamification-service
- **Package name**: pe.tecsup.worldcup.gamification
- **Packaging**: Jar
- **Java**: 17

### Dependencies
- **Spring Web**
- **Spring Data JPA**
- **PostgreSQL**
- **RabbitMQ (Spring AMQP)**
- **Eureka Discovery Client**
- **Lombok**

### Generar en Spring Initializr
```
https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.5.x&packaging=jar&jvmVersion=17&groupId=pe.tecsup.worldcup&artifactId=gamification-service&name=gamification-service&packageName=pe.tecsup.worldcup.gamification&dependencies=web,data-jpa,postgresql,amqp,eureka-client,lombok
```

---

### Notification Service

### Project
- **Type**: Maven
- **Language**: Java
- **Spring Boot**: 3.5.x

### Project Metadata
- **Group**: pe.tecsup.worldcup
- **Artifact**: notification-service
- **Name**: notification-service
- **Package name**: pe.tecsup.worldcup.notification
- **Packaging**: Jar
- **Java**: 17

### Dependencies
- **Spring Web**
- **Spring Mail**
- **RabbitMQ (Spring AMQP)**
- **WebSocket**
- **Eureka Discovery Client**
- **Lombok**

### Generar en Spring Initializr
```
https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.5.x&packaging=jar&jvmVersion=17&groupId=pe.tecsup.worldcup&artifactId=notification-service&name=notification-service&packageName=pe.tecsup.worldcup.notification&dependencies=web,mail,amqp,websocket,eureka-client,lombok
```

---

## PARTE 1: GAMIFICATION SERVICE

## 1. ESTRUCTURA DEL PROYECTO

```
gamification-service
│
├── controller
│   ├── BadgeController
│   └── RewardController
│
├── service
│   ├── BadgeService
│   ├── AchievementService
│   └── RewardService
│
├── repository
│   ├── BadgeRepository
│   ├── UserBadgeRepository
│   ├── RewardRepository
│   └── AchievementRuleRepository
│
├── entity
│   ├── Badge
│   ├── UserBadge
│   ├── Reward
│   └── AchievementRule
│
├── dto
│   ├── BadgeDTO
│   ├── UserBadgeDTO
│   └── RewardDTO
│
├── listener
│   └── ScoreUpdateListener
│
├── resources
│   ├── application.yml
│   └── application-dev.yml
│
└── GamificationApplication.java
```

## 2. ENTIDADES GAMIFICATION

### entity/Badge.java
```java
package com.tecsup.gamification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "badges")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Badge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    private String description;
    
    @Column(length = 255)
    private String icon;
}
```

### entity/UserBadge.java
```java
package com.tecsup.gamification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_badges")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBadge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id")
    private Badge badge;
    
    @Column(name = "earned_at")
    private LocalDateTime earnedAt;
}
```

### entity/Reward.java
```java
package com.tecsup.gamification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rewards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reward {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Integer position;
    
    private String title;
    
    private String description;
}
```

## 3. SERVICE GAMIFICATION

### service/AchievementService.java
```java
package com.tecsup.gamification.service;

import com.tecsup.gamification.entity.Badge;
import com.tecsup.gamification.entity.UserBadge;
import com.tecsup.gamification.repository.AchievementRuleRepository;
import com.tecsup.gamification.repository.BadgeRepository;
import com.tecsup.gamification.repository.UserBadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AchievementService {
    
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final AchievementRuleRepository achievementRuleRepository;
    private final RabbitTemplate rabbitTemplate;
    
    public void checkAchievements(Long userId, Long totalPoints, int streak) {
        achievementRuleRepository.findAll().forEach(rule -> {
            if (meetsCriteria(rule, totalPoints, streak)) {
                awardBadge(userId, rule.getBadge());
            }
        });
    }
    
    private boolean meetsCriteria(AchievementRule rule, Long points, int streak) {
        if (rule.getRequiredPoints() != null && points < rule.getRequiredPoints()) {
            return false;
        }
        if (rule.getRequiredStreak() != null && streak < rule.getRequiredStreak()) {
            return false;
        }
        return true;
    }
    
    public void awardBadge(Long userId, Badge badge) {
        if (userBadgeRepository.findByUserIdAndBadgeId(userId, badge.getId()).isPresent()) {
            return; // Already has badge
        }
        
        UserBadge userBadge = new UserBadge();
        userBadge.setUser(new User(userId));
        userBadge.setBadge(badge);
        userBadge.setEarnedAt(LocalDateTime.now());
        
        userBadgeRepository.save(userBadge);
        
        // Publish event
        rabbitTemplate.convertAndSend("gamification.exchange", "badge.unlocked", userId);
    }
}
```

---

## PARTE 2: NOTIFICATION SERVICE

## 4. ESTRUCTURA DEL PROYECTO

```
notification-service
│
├── controller
│   └── NotificationController
│
├── service
│   ├── NotificationService
│   └── EmailService
│
├── repository
│   └── NotificationRepository
│
├── entity
│   └── Notification
│
├── dto
│   ├── NotificationDTO
│   └── CreateNotificationRequest
│
├── listener
│   └── NotificationEventListener
│
├── resources
│   ├── application.yml
│   └── application-dev.yml
│
└── NotificationApplication.java
```

## 5. ENTIDADES NOTIFICATION

### entity/Notification.java
```java
package com.tecsup.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    private String title;
    
    private String message;
    
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
```

## 6. SERVICE NOTIFICATION

### service/NotificationService.java
```java
package com.tecsup.notification.service;

import com.tecsup.notification.dto.NotificationDTO;
import com.tecsup.notification.entity.Notification;
import com.tecsup.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    
    public Notification createNotification(Long userId, String title, String message) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        
        notification = notificationRepository.save(notification);
        
        // Publish to WebSocket via Redis
        String channel = "notifications:" + userId;
        redisTemplate.convertAndSend(channel, notification);
        
        return notification;
    }
    
    public List<NotificationDTO> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
            .map(this::toDTO)
            .toList();
    }
    
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
    
    private NotificationDTO toDTO(Notification notification) {
        return new NotificationDTO(
            notification.getId(),
            notification.getUserId(),
            notification.getTitle(),
            notification.getMessage(),
            notification.getIsRead(),
            notification.getCreatedAt()
        );
    }
}
```

### service/EmailService.java
```java
package com.tecsup.notification.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${email.username}")
    private String fromEmail;
    
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        
        mailSender.send(message);
    }
}
```

## 7. LISTENER NOTIFICATION

### listener/NotificationEventListener.java
```java
package com.tecsup.notification.listener;

import com.tecsup.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {
    
    private final NotificationService notificationService;
    
    @RabbitListener(queues = "notification.send")
    public void handleNotification(NotificationEvent event) {
        log.info("Sending notification to user: {}", event.getUserId());
        notificationService.createNotification(
            event.getUserId(),
            event.getTitle(),
            event.getMessage()
        );
    }
}
```

---

## 8. CONTROLLERS

### controller/NotificationController.java
```java
package com.tecsup.notification.controller;

import com.tecsup.notification.dto.NotificationDTO;
import com.tecsup.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }
    
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
```

### controller/BadgeController.java
```java
package com.tecsup.gamification.controller;

import com.tecsup.gamification.dto.BadgeDTO;
import com.tecsup.gamification.dto.UserBadgeDTO;
import com.tecsup.gamification.service.BadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/badges")
@RequiredArgsConstructor
public class BadgeController {
    
    private final BadgeService badgeService;
    
    @GetMapping
    public ResponseEntity<List<BadgeDTO>> getAllBadges() {
        return ResponseEntity.ok(badgeService.getAllBadges());
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserBadgeDTO>> getUserBadges(@PathVariable Long userId) {
        return ResponseEntity.ok(badgeService.getUserBadges(userId));
    }
}
```

---

## 9. MAIN APPLICATIONS

### GamificationApplication.java
```java
package com.tecsup.gamification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GamificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(GamificationApplication.class, args);
    }
}
```

### NotificationApplication.java
```java
package com.tecsup.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class NotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class, args);
    }
}
```

---

## 10. SEED DATA GAMIFICATION

### DataInitializer.java
```java
package com.tecsup.gamification.config;

import com.tecsup.gamification.entity.AchievementRule;
import com.tecsup.gamification.entity.Badge;
import com.tecsup.gamification.entity.Reward;
import com.tecsup.gamification.repository.AchievementRuleRepository;
import com.tecsup.gamification.repository.BadgeRepository;
import com.tecsup.gamification.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final BadgeRepository badgeRepository;
    private final RewardRepository rewardRepository;
    private final AchievementRuleRepository achievementRuleRepository;
    
    @Override
    public void run(String... args) {
        if (badgeRepository.count() == 0) {
            Badge firstGoal = badgeRepository.save(new Badge(null, "Primer Gol", "Primera predicción acertada", "⚽"));
            Badge streak3 = badgeRepository.save(new Badge(null, "Racha 3", "3 aciertos consecutivos", "🔥"));
            Badge streak5 = badgeRepository.save(new Badge(null, "Racha 5", "5 aciertos consecutivos", "🚀"));
            Badge streak10 = badgeRepository.save(new Badge(null, "Racha 10", "10 aciertos consecutivos", "💎"));
            Badge expert = badgeRepository.save(new Badge(null, "Experto Mundialista", "1000 puntos", "🏆"));
            Badge master = badgeRepository.save(new Badge(null, "Maestro de Predicciones", "2500 puntos", "👑"));
            Badge legend = badgeRepository.save(new Badge(null, "Leyenda Tecsup", "Top 10", "🌟"));
            Badge champion = badgeRepository.save(new Badge(null, "Campeón Mundial", "Top 1", "🥇"));
            
            // Create achievement rules
            AchievementRule rule1 = new AchievementRule();
            rule1.setBadge(firstGoal);
            rule1.setRequiredPoints(10);
            achievementRuleRepository.save(rule1);
            
            AchievementRule rule2 = new AchievementRule();
            rule2.setBadge(streak3);
            rule2.setRequiredStreak(3);
            achievementRuleRepository.save(rule2);
            
            AchievementRule rule3 = new AchievementRule();
            rule3.setBadge(expert);
            rule3.setRequiredPoints(1000);
            achievementRuleRepository.save(rule3);
        }
        
        if (rewardRepository.count() == 0) {
            rewardRepository.save(new Reward(null, 1, "Laptop Gamer", "Laptop de alta gama"));
            rewardRepository.save(new Reward(null, 2, "Tablet Pro", "Tablet profesional"));
            rewardRepository.save(new Reward(null, 3, "Gift Card $500", "Tarjeta de regalo"));
            rewardRepository.save(new Reward(null, 4, "Gift Card $300", "Tarjeta de regalo"));
            rewardRepository.save(new Reward(null, 5, "Gift Card $200", "Tarjeta de regalo"));
            rewardRepository.save(new Reward(null, 6, "Gift Card $100", "Tarjeta de regalo"));
            rewardRepository.save(new Reward(null, 7, "Gift Card $50", "Tarjeta de regalo"));
            rewardRepository.save(new Reward(null, 8, "Merchandising Tecsup", "Kit de merchandising"));
            rewardRepository.save(new Reward(null, 9, "Merchandising Tecsup", "Kit de merchandising"));
            rewardRepository.save(new Reward(null, 10, "Merchandising Tecsup", "Kit de merchandising"));
        }
    }
}
```

---

## 11. TESTING

### Obtener insignias de usuario
```bash
curl http://localhost:8088/badges/user/1
```

### Obtener notificaciones de usuario
```bash
curl http://localhost:8089/notifications/user/1
```

### Marcar notificación como leída
```bash
curl -X PUT http://localhost:8089/notifications/1/read
```

---

## RESUMEN DE BACKEND

Has completado los 15 READMEs para backend:

1. ✅ README-01-requisitos.md - Requisitos del proyecto
2. ✅ README-02-base-datos.md - Esquema de base de datos
3. ✅ README-03-arquitectura-general.md - Arquitectura de microservicios
4. ✅ README-04-infraestructura.md - Docker, Redis, RabbitMQ
5. ✅ README-05-api-gateway.md - API Gateway
6. ✅ README-06-discovery-service.md - Eureka Server
7. ✅ README-07-config-service.md - Config Server
8. ✅ README-08-auth-service.md - Auth Service
9. ✅ README-09-organization-service.md - Organization Service
10. ✅ README-10-worldcup-service.md - World Cup Service
11. ✅ README-11-prediction-service.md - Prediction Service
12. ✅ README-12-scoring-service.md - Scoring Service
13. ✅ README-13-leaderboard-service.md - Leaderboard Service
14. ✅ README-14-social-service.md - Social Service
15. ✅ README-15-gamification-notification.md - Gamification y Notification Services

---

## TAREA SIGUIENTE

Ahora procede a crear los 10 READMEs para frontend en la carpeta docs/frontend/.
