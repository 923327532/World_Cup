# README 12: Scoring Service - Sistema de Puntos

## PROMPT PARA EL EQUIPO DE DESARROLLO

Como desarrollador backend, tu tarea es implementar el Scoring Service responsable de calcular puntos, gestionar bonificaciones y rachas de los usuarios.

---

## CONFIGURACIÓN SPRING INITIALIZR

### Project
- **Type**: Maven
- **Language**: Java
- **Spring Boot**: 3.5.x

### Project Metadata
- **Group**: pe.tecsup.worldcup
- **Artifact**: scoring-service
- **Name**: scoring-service
- **Package name**: pe.tecsup.worldcup.scoring
- **Packaging**: Jar
- **Java**: 17

### Dependencies
- **Spring Web**
- **Spring Data JPA**
- **PostgreSQL**
- **RabbitMQ (Spring AMQP)**
- **Redis (Spring Data Redis)**
- **Eureka Discovery Client**
- **Lombok**

### Generar en Spring Initializr
```
https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.5.x&packaging=jar&jvmVersion=17&groupId=pe.tecsup.worldcup&artifactId=scoring-service&name=scoring-service&packageName=pe.tecsup.worldcup.scoring&dependencies=web,data-jpa,postgresql,amqp,data-redis,eureka-client,lombok
```

---

## 1. ESTRUCTURA DEL PROYECTO

```
scoring-service
│
├── controller
│   └── ScoreController
│
├── service
│   ├── ScoreCalculatorService
│   ├── StreakService
│   ├── BonusService
│   └── UserScoreService
│
├── repository
│   ├── UserScoreRepository
│   ├── ScoreHistoryRepository
│   ├── ScoringRuleRepository
│   └── AchievementRuleRepository
│
├── entity
│   ├── UserScore
│   ├── ScoreHistory
│   ├── ScoringRule
│   └── AchievementRule
│
├── dto
│   ├── UserScoreDTO
│   ├── ScoreHistoryDTO
│   └── ScoreUpdateRequest
│
├── listener
│   └── PredictionResultListener
│
├── resources
│   ├── application.yml
│   └── application-dev.yml
│
└── ScoringApplication.java
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

    <!-- PostgreSQL -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
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

### entity/UserScore.java
```java
package com.tecsup.scoring.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserScore {
    
    @Id
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "total_points")
    private Long totalPoints = 0L;
}
```

### entity/ScoreHistory.java
```java
package com.tecsup.scoring.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "score_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    private Integer points;
    
    private String reason;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
```

### entity/ScoringRule.java
```java
package com.tecsup.scoring.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "scoring_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoringRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prediction_type_id")
    private PredictionType predictionType;
    
    private Integer points;
}
```

### entity/AchievementRule.java
```java
package com.tecsup.scoring.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "achievement_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id")
    private Badge badge;
    
    @Column(name = "required_points")
    private Integer requiredPoints;
    
    @Column(name = "required_streak")
    private Integer requiredStreak;
    
    @Column(name = "required_position")
    private Integer requiredPosition;
}
```

---

## 4. SERVICES

### service/ScoreCalculatorService.java
```java
package com.tecsup.scoring.service;

import com.tecsup.scoring.entity.ScoreHistory;
import com.tecsup.scoring.entity.UserScore;
import com.tecsup.scoring.repository.ScoreHistoryRepository;
import com.tecsup.scoring.repository.UserScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScoreCalculatorService {
    
    private final UserScoreRepository userScoreRepository;
    private final ScoreHistoryRepository scoreHistoryRepository;
    private final RabbitTemplate rabbitTemplate;
    private final StreakService streakService;
    private final BonusService bonusService;
    
    @Transactional
    public void addPoints(Long userId, Integer points, String reason) {
        UserScore userScore = userScoreRepository.findById(userId)
            .orElse(new UserScore(userId, 0L));
        
        userScore.setTotalPoints(userScore.getTotalPoints() + points);
        userScoreRepository.save(userScore);
        
        // Record history
        ScoreHistory history = new ScoreHistory();
        history.setUserId(userId);
        history.setPoints(points);
        history.setReason(reason);
        history.setCreatedAt(LocalDateTime.now());
        scoreHistoryRepository.save(history);
        
        // Check for streaks
        int currentStreak = streakService.getCurrentStreak(userId);
        int bonusPoints = bonusService.calculateStreakBonus(currentStreak);
        
        if (bonusPoints > 0) {
            addPoints(userId, bonusPoints, "Streak bonus: " + currentStreak);
        }
        
        // Publish event
        rabbitTemplate.convertAndSend("scoring.exchange", "score.updated", userId);
    }
    
    public Long getUserScore(Long userId) {
        return userScoreRepository.findById(userId)
            .map(UserScore::getTotalPoints)
            .orElse(0L);
    }
}
```

### service/StreakService.java
```java
package com.tecsup.scoring.service;

import com.tecsup.scoring.repository.ScoreHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StreakService {
    
    private final ScoreHistoryRepository scoreHistoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    
    public int getCurrentStreak(Long userId) {
        String cacheKey = "streak:" + userId;
        
        Integer cachedStreak = (Integer) redisTemplate.opsForValue().get(cacheKey);
        if (cachedStreak != null) {
            return cachedStreak;
        }
        
        // Calculate streak from database
        List<ScoreHistory> recentHistory = scoreHistoryRepository
            .findRecentCorrectPredictions(userId, LocalDateTime.now().minusDays(7));
        
        int streak = calculateStreak(recentHistory);
        
        // Cache for 1 hour
        redisTemplate.opsForValue().set(cacheKey, streak, 3600);
        
        return streak;
    }
    
    private int calculateStreak(List<ScoreHistory> history) {
        int streak = 0;
        for (ScoreHistory h : history) {
            if (h.getReason().contains("correct")) {
                streak++;
            } else {
                streak = 0;
            }
        }
        return streak;
    }
    
    public void resetStreak(Long userId) {
        String cacheKey = "streak:" + userId;
        redisTemplate.delete(cacheKey);
    }
}
```

### service/BonusService.java
```java
package com.tecsup.scoring.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BonusService {
    
    @Value("${scoring.streak-3-bonus:20}")
    private int streak3Bonus;
    
    @Value("${scoring.streak-5-bonus:50}")
    private int streak5Bonus;
    
    @Value("${scoring.streak-10-bonus:150}")
    private int streak10Bonus;
    
    @Value("${scoring.perfect-match-bonus:100}")
    private int perfectMatchBonus;
    
    public int calculateStreakBonus(int streak) {
        return switch (streak) {
            case 3 -> streak3Bonus;
            case 5 -> streak5Bonus;
            case 10 -> streak10Bonus;
            default -> 0;
        };
    }
    
    public int getPerfectMatchBonus() {
        return perfectMatchBonus;
    }
}
```

---

## 5. LISTENER

### listener/PredictionResultListener.java
```java
package com.tecsup.scoring.listener;

import com.tecsup.scoring.service.ScoreCalculatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PredictionResultListener {
    
    private final ScoreCalculatorService scoreCalculatorService;
    
    @RabbitListener(queues = "prediction.scored")
    public void handlePredictionScored(PredictionScoredEvent event) {
        log.info("Processing prediction scored: userId={}, points={}", 
            event.getUserId(), event.getPointsEarned());
        
        if (event.isCorrect()) {
            scoreCalculatorService.addPoints(
                event.getUserId(),
                event.getPointsEarned(),
                "Correct prediction: " + event.getMatchId()
            );
        } else {
            // Reset streak on incorrect prediction
            // streakService.resetStreak(event.getUserId());
        }
    }
}
```

---

## 6. CONTROLLERS

### controller/ScoreController.java
```java
package com.tecsup.scoring.controller;

import com.tecsup.scoring.dto.UserScoreDTO;
import com.tecsup.scoring.service.ScoreCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scoring")
@RequiredArgsConstructor
public class ScoreController {
    
    private final ScoreCalculatorService scoreCalculatorService;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserScoreDTO> getUserScore(@PathVariable Long userId) {
        Long totalPoints = scoreCalculatorService.getUserScore(userId);
        return ResponseEntity.ok(new UserScoreDTO(userId, totalPoints));
    }
    
    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<ScoreHistoryDTO>> getUserScoreHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(scoreCalculatorService.getUserScoreHistory(userId));
    }
}
```

---

## 7. REPOSITORIES

### repository/ScoreHistoryRepository.java
```java
package com.tecsup.scoring.repository;

import com.tecsup.scoring.entity.ScoreHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScoreHistoryRepository extends JpaRepository<ScoreHistory, Long> {
    List<ScoreHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<ScoreHistory> findRecentCorrectPredictions(Long userId, LocalDateTime since);
}
```

---

## 8. MAIN APPLICATION

### ScoringApplication.java
```java
package com.tecsup.scoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ScoringApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScoringApplication.class, args);
    }
}
```

---

## 9. TESTING

### Obtener score de usuario
```bash
curl http://localhost:8085/scoring/user/1
```

### Obtener historial de score
```bash
curl http://localhost:8085/scoring/user/1/history
```

---

## TAREA SIGUIENTE

Una vez implementado el Scoring Service, procede al README-13-leaderboard-service.md para implementar el Leaderboard Service.
