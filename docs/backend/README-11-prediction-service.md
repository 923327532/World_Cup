# README 11: Prediction Service - Núcleo Principal del Sistema

## PROMPT PARA EL EQUIPO DE DESARROLLO

Como desarrollador backend, tu tarea es implementar el Prediction Service, el núcleo principal del sistema responsable de gestionar todas las predicciones de los usuarios.

---

## CONFIGURACIÓN SPRING INITIALIZR

### Project
- **Type**: Maven
- **Language**: Java
- **Spring Boot**: 3.5.x

### Project Metadata
- **Group**: pe.tecsup.worldcup
- **Artifact**: prediction-service
- **Name**: prediction-service
- **Package name**: pe.tecsup.worldcup.prediction
- **Packaging**: Jar
- **Java**: 17

### Dependencies
- **Spring Web**
- **Spring Data JPA**
- **PostgreSQL**
- **Validation**
- **OpenFeign**
- **RabbitMQ (Spring AMQP)**
- **Eureka Discovery Client**
- **Lombok**

### Generar en Spring Initializr
```
https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.5.x&packaging=jar&jvmVersion=17&groupId=pe.tecsup.worldcup&artifactId=prediction-service&name=prediction-service&packageName=pe.tecsup.worldcup.prediction&dependencies=web,data-jpa,postgresql,validation,openfeign,amqp,eureka-client,lombok
```

---

## 1. ESTRUCTURA DEL PROYECTO

```
prediction-service
│
├── controller
│   ├── PredictionController
│   └── PredictionTypeController
│
├── service
│   ├── PredictionService
│   ├── PredictionTypeService
│   ├── LockPredictionService
│   └── PredictionLockScheduler
│
├── repository
│   ├── PredictionRepository
│   ├── PredictionTypeRepository
│   ├── PredictionResultRepository
│   └── PredictionLockRepository
│
├── entity
│   ├── Prediction
│   ├── PredictionType
│   ├── PredictionResult
│   └── UserPredictionLock
│
├── dto
│   ├── PredictionDTO
│   ├── CreatePredictionRequest
│   ├── UpdatePredictionRequest
│   └── PredictionTypeDTO
│
├── listener
│   └── PredictionEventListener
│
├── resources
│   ├── application.yml
│   └── application-dev.yml
│
└── PredictionApplication.java
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

### entity/PredictionType.java
```java
package com.tecsup.prediction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prediction_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, length = 50)
    private String code;
    
    private String name;
    
    private Integer points;
}
```

### entity/Prediction.java
```java
package com.tecsup.prediction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "predictions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prediction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prediction_type_id", nullable = false)
    private PredictionType predictionType;
    
    @Column(length = 500)
    private String predictionValue;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

### entity/PredictionResult.java
```java
package com.tecsup.prediction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prediction_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prediction_id", nullable = false)
    private Prediction prediction;
    
    @Column(name = "is_correct")
    private Boolean isCorrect;
    
    @Column(name = "points_earned")
    private Integer pointsEarned;
}
```

### entity/UserPredictionLock.java
```java
package com.tecsup.prediction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_predictions_lock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPredictionLock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prediction_id", nullable = false)
    private Prediction prediction;
    
    @Column(name = "locked_at")
    private LocalDateTime lockedAt;
    
    private String reason;
}
```

---

## 4. DTOS

### dto/CreatePredictionRequest.java
```java
package com.tecsup.prediction.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePredictionRequest {
    
    @NotNull
    private Long matchId;
    
    @NotNull
    private Long predictionTypeId;
    
    @NotNull
    private String predictionValue;
}
```

### dto/PredictionDTO.java
```java
package com.tecsup.prediction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionDTO {
    private Long id;
    private Long userId;
    private Long matchId;
    private String homeTeam;
    private String awayTeam;
    private Long predictionTypeId;
    private String predictionType;
    private String predictionValue;
    private Integer points;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isLocked;
}
```

---

## 5. SERVICES

### service/PredictionService.java
```java
package com.tecsup.prediction.service;

import com.tecsup.prediction.dto.CreatePredictionRequest;
import com.tecsup.prediction.dto.PredictionDTO;
import com.tecsup.prediction.entity.Match;
import com.tecsup.prediction.entity.Prediction;
import com.tecsup.prediction.entity.PredictionType;
import com.tecsup.prediction.repository.MatchRepository;
import com.tecsup.prediction.repository.PredictionRepository;
import com.tecsup.prediction.repository.PredictionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PredictionService {
    
    private final PredictionRepository predictionRepository;
    private final PredictionTypeRepository predictionTypeRepository;
    private final MatchRepository matchRepository;
    private final RabbitTemplate rabbitTemplate;
    
    public PredictionDTO createPrediction(Long userId, CreatePredictionRequest request) {
        Match match = matchRepository.findById(request.getMatchId())
            .orElseThrow(() -> new RuntimeException("Match not found"));
        
        PredictionType predictionType = predictionTypeRepository.findById(request.getPredictionTypeId())
            .orElseThrow(() -> new RuntimeException("Prediction type not found"));
        
        // Check if match is locked
        if (match.getKickoffTime().isBefore(LocalDateTime.now().plusMinutes(5))) {
            throw new RuntimeException("Predictions are locked for this match");
        }
        
        Prediction prediction = new Prediction();
        prediction.setUserId(userId);
        prediction.setMatch(match);
        prediction.setPredictionType(predictionType);
        prediction.setPredictionValue(request.getPredictionValue());
        prediction.setCreatedAt(LocalDateTime.now());
        prediction.setUpdatedAt(LocalDateTime.now());
        
        prediction = predictionRepository.save(prediction);
        
        // Publish event
        rabbitTemplate.convertAndSend("prediction.exchange", "prediction.created", prediction);
        
        return toDTO(prediction);
    }
    
    public PredictionDTO updatePrediction(Long userId, Long predictionId, String newValue) {
        Prediction prediction = predictionRepository.findById(predictionId)
            .orElseThrow(() -> new RuntimeException("Prediction not found"));
        
        if (!prediction.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        // Check if locked
        if (isPredictionLocked(predictionId)) {
            throw new RuntimeException("Prediction is locked");
        }
        
        prediction.setPredictionValue(newValue);
        prediction.setUpdatedAt(LocalDateTime.now());
        
        prediction = predictionRepository.save(prediction);
        
        return toDTO(prediction);
    }
    
    private boolean isPredictionLocked(Long predictionId) {
        // Check if prediction is locked
        return false; // Implement lock logic
    }
    
    private PredictionDTO toDTO(Prediction prediction) {
        return new PredictionDTO(
            prediction.getId(),
            prediction.getUserId(),
            prediction.getMatch().getId(),
            prediction.getMatch().getHomeTeam().getName(),
            prediction.getMatch().getAwayTeam().getName(),
            prediction.getPredictionType().getId(),
            prediction.getPredictionType().getName(),
            prediction.getPredictionValue(),
            prediction.getPredictionType().getPoints(),
            prediction.getCreatedAt(),
            prediction.getUpdatedAt(),
            false
        );
    }
}
```

### service/PredictionLockScheduler.java
```java
package com.tecsup.prediction.service;

import com.tecsup.prediction.entity.Match;
import com.tecsup.prediction.entity.Prediction;
import com.tecsup.prediction.entity.UserPredictionLock;
import com.tecsup.prediction.repository.MatchRepository;
import com.tecsup.prediction.repository.PredictionRepository;
import com.tecsup.prediction.repository.PredictionLockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PredictionLockScheduler {
    
    private final MatchRepository matchRepository;
    private final PredictionRepository predictionRepository;
    private final PredictionLockRepository predictionLockRepository;
    
    @Scheduled(fixedRate = 60000) // Every minute
    public void lockPredictions() {
        LocalDateTime lockTime = LocalDateTime.now().plusMinutes(5);
        
        List<Match> matchesStartingSoon = matchRepository
            .findByKickoffTimeBetween(LocalDateTime.now(), lockTime);
        
        matchesStartingSoon.forEach(match -> {
            List<Prediction> predictions = predictionRepository.findByMatchId(match.getId());
            
            predictions.forEach(prediction -> {
                if (!isPredictionLocked(prediction.getId())) {
                    UserPredictionLock lock = new UserPredictionLock();
                    lock.setPrediction(prediction);
                    lock.setLockedAt(LocalDateTime.now());
                    lock.setReason("Match starting in 5 minutes");
                    
                    predictionLockRepository.save(lock);
                    log.info("Locked prediction {} for match {}", prediction.getId(), match.getId());
                }
            });
        });
    }
    
    private boolean isPredictionLocked(Long predictionId) {
        return predictionLockRepository.findByPredictionId(predictionId).isPresent();
    }
}
```

---

## 6. CONTROLLERS

### controller/PredictionController.java
```java
package com.tecsup.prediction.controller;

import com.tecsup.prediction.dto.CreatePredictionRequest;
import com.tecsup.prediction.dto.PredictionDTO;
import com.tecsup.prediction.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/predictions")
@RequiredArgsConstructor
public class PredictionController {
    
    private final PredictionService predictionService;
    
    @PostMapping
    public ResponseEntity<PredictionDTO> createPrediction(
        @RequestHeader("X-User-Id") Long userId,
        @RequestBody CreatePredictionRequest request
    ) {
        return ResponseEntity.ok(predictionService.createPrediction(userId, request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PredictionDTO> updatePrediction(
        @RequestHeader("X-User-Id") Long userId,
        @PathVariable Long id,
        @RequestBody String newValue
    ) {
        return ResponseEntity.ok(predictionService.updatePrediction(userId, id, newValue));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PredictionDTO>> getUserPredictions(@PathVariable Long userId) {
        return ResponseEntity.ok(predictionService.getUserPredictions(userId));
    }
    
    @GetMapping("/match/{matchId}")
    public ResponseEntity<List<PredictionDTO>> getMatchPredictions(@PathVariable Long matchId) {
        return ResponseEntity.ok(predictionService.getMatchPredictions(matchId));
    }
}
```

---

## 7. REPOSITORIES

### repository/PredictionRepository.java
```java
package com.tecsup.prediction.repository;

import com.tecsup.prediction.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    List<Prediction> findByUserId(Long userId);
    List<Prediction> findByMatchId(Long matchId);
}
```

### repository/PredictionLockRepository.java
```java
package com.tecsup.prediction.repository;

import com.tecsup.prediction.entity.UserPredictionLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PredictionLockRepository extends JpaRepository<UserPredictionLock, Long> {
    Optional<UserPredictionLock> findByPredictionId(Long predictionId);
}
```

---

## 8. RABBITMQ EVENT LISTENER

### listener/PredictionEventListener.java
```java
package com.tecsup.prediction.listener;

import com.tecsup.prediction.entity.Prediction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PredictionEventListener {
    
    @RabbitListener(queues = "prediction.created")
    public void handlePredictionCreated(Prediction prediction) {
        log.info("Prediction created: {}", prediction.getId());
        // Handle prediction created event
    }
}
```

---

## 9. MAIN APPLICATION

### PredictionApplication.java
```java
package com.tecsup.prediction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class PredictionApplication {

    public static void main(String[] args) {
        SpringApplication.run(PredictionApplication.class, args);
    }
}
```

---

## 10. SEED DATA

### DataInitializer.java
```java
package com.tecsup.prediction.config;

import com.tecsup.prediction.entity.PredictionType;
import com.tecsup.prediction.repository.PredictionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final PredictionTypeRepository predictionTypeRepository;
    
    @Override
    public void run(String... args) {
        if (predictionTypeRepository.count() == 0) {
            predictionTypeRepository.save(new PredictionType(null, "WINNER", "Ganador del partido", 10));
            predictionTypeRepository.save(new PredictionType(null, "DRAW", "Empate", 15));
            predictionTypeRepository.save(new PredictionType(null, "EXACT_SCORE", "Marcador exacto", 50));
            predictionTypeRepository.save(new PredictionType(null, "FIRST_GOAL_SCORER", "Primer goleador", 30));
            predictionTypeRepository.save(new PredictionType(null, "BOTH_SCORE", "Ambos anotan", 10));
            predictionTypeRepository.save(new PredictionType(null, "OVER_2_5", "Más de 2.5 goles", 10));
            predictionTypeRepository.save(new PredictionType(null, "UNDER_2_5", "Menos de 2.5 goles", 10));
        }
    }
}
```

---

## 11. TESTING

### Crear predicción
```bash
curl -X POST http://localhost:8084/predictions \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{
    "matchId": 1,
    "predictionTypeId": 1,
    "predictionValue": "HOME"
  }'
```

### Obtener predicciones de usuario
```bash
curl http://localhost:8084/predictions/user/1
```

---

## TAREA SIGUIENTE

Una vez implementado el Prediction Service, procede al README-12-scoring-service.md para implementar el Scoring Service.
