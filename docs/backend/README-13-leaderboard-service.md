# README 13: Leaderboard Service - Rankings

## PROMPT PARA EL EQUIPO DE DESARROLLO

Como desarrollador backend, tu tarea es implementar el Leaderboard Service responsable de gestionar todos los rankings del sistema (global, por sede, por departamento, por carrera, etc.).

---

## CONFIGURACIÓN SPRING INITIALIZR

### Project
- **Type**: Maven
- **Language**: Java
- **Spring Boot**: 3.5.x

### Project Metadata
- **Group**: pe.tecsup.worldcup
- **Artifact**: leaderboard-service
- **Name**: leaderboard-service
- **Package name**: pe.tecsup.worldcup.leaderboard
- **Packaging**: Jar
- **Java**: 17

### Dependencies
- **Spring Web**
- **Spring Data JPA**
- **PostgreSQL**
- **Redis (Spring Data Redis)**
- **RabbitMQ (Spring AMQP)**
- **Eureka Discovery Client**
- **Lombok**

### Generar en Spring Initializr
```
https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.5.x&packaging=jar&jvmVersion=17&groupId=pe.tecsup.worldcup&artifactId=leaderboard-service&name=leaderboard-service&packageName=pe.tecsup.worldcup.leaderboard&dependencies=web,data-jpa,postgresql,data-redis,amqp,eureka-client,lombok
```

---

## 1. ESTRUCTURA DEL PROYECTO

```
leaderboard-service
│
├── controller
│   └── LeaderboardController
│
├── service
│   ├── RankingService
│   ├── CampusRankingService
│   ├── CareerRankingService
│   ├── DepartmentRankingService
│   └── LeaderboardCacheService
│
├── repository
│   ├── LeaderboardRepository
│   └── LeaderboardEntryRepository
│
├── entity
│   ├── Leaderboard
│   └── LeaderboardEntry
│
├── dto
│   ├── LeaderboardDTO
│   ├── LeaderboardEntryDTO
│   └── RankingRequest
│
├── listener
│   └── ScoreUpdateListener
│
├── resources
│   ├── application.yml
│   └── application-dev.yml
│
└── LeaderboardApplication.java
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

### entity/Leaderboard.java
```java
package com.tecsup.leaderboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "leaderboards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Leaderboard {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @Column(length = 50)
    private String type;
}
```

### entity/LeaderboardEntry.java
```java
package com.tecsup.leaderboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "leaderboard_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leaderboard_id", nullable = false)
    private Leaderboard leaderboard;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "ranking_position")
    private Integer rankingPosition;
    
    private Long points;
}
```

---

## 4. SERVICES

### service/RankingService.java
```java
package com.tecsup.leaderboard.service;

import com.tecsup.leaderboard.dto.LeaderboardEntryDTO;
import com.tecsup.leaderboard.entity.Leaderboard;
import com.tecsup.leaderboard.entity.LeaderboardEntry;
import com.tecsup.leaderboard.repository.LeaderboardEntryRepository;
import com.tecsup.leaderboard.repository.LeaderboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {
    
    private final LeaderboardRepository leaderboardRepository;
    private final LeaderboardEntryRepository entryRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    
    public List<LeaderboardEntryDTO> getGlobalRanking(int limit) {
        String cacheKey = "ranking:global:" + limit;
        
        @SuppressWarnings("unchecked")
        List<LeaderboardEntryDTO> cached = (List<LeaderboardEntryDTO>) redisTemplate.opsForValue().get(cacheKey);
        
        if (cached != null) {
            return cached;
        }
        
        Leaderboard globalLeaderboard = leaderboardRepository.findByType("GLOBAL")
            .orElseThrow(() -> new RuntimeException("Global leaderboard not found"));
        
        List<LeaderboardEntry> entries = entryRepository
            .findByLeaderboardIdOrderByPointsDesc(globalLeaderboard.getId())
            .stream()
            .limit(limit)
            .toList();
        
        List<LeaderboardEntryDTO> result = entries.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        
        redisTemplate.opsForValue().set(cacheKey, result, 5, TimeUnit.MINUTES);
        
        return result;
    }
    
    public void updateUserRanking(Long userId, Long points) {
        // Update all leaderboards where user appears
        List<Leaderboard> leaderboards = leaderboardRepository.findAll();
        
        leaderboards.forEach(leaderboard -> {
            LeaderboardEntry entry = entryRepository
                .findByLeaderboardIdAndUserId(leaderboard.getId(), userId)
                .orElse(new LeaderboardEntry());
            
            entry.setLeaderboard(leaderboard);
            entry.setUserId(userId);
            entry.setPoints(points);
            
            entryRepository.save(entry);
        });
        
        // Recalculate positions
        recalculatePositions();
        
        // Clear cache
        clearCache();
    }
    
    private void recalculatePositions() {
        List<Leaderboard> leaderboards = leaderboardRepository.findAll();
        
        leaderboards.forEach(leaderboard -> {
            List<LeaderboardEntry> entries = entryRepository
                .findByLeaderboardIdOrderByPointsDesc(leaderboard.getId());
            
            for (int i = 0; i < entries.size(); i++) {
                entries.get(i).setRankingPosition(i + 1);
                entryRepository.save(entries.get(i));
            }
        });
    }
    
    private void clearCache() {
        // Clear all ranking caches
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }
    
    private LeaderboardEntryDTO toDTO(LeaderboardEntry entry) {
        return new LeaderboardEntryDTO(
            entry.getId(),
            entry.getUserId(),
            entry.getRankingPosition(),
            entry.getPoints()
        );
    }
}
```

### service/CampusRankingService.java
```java
package com.tecsup.leaderboard.service;

import com.tecsup.leaderboard.dto.LeaderboardEntryDTO;
import com.tecsup.leaderboard.entity.Leaderboard;
import com.tecsup.leaderboard.entity.LeaderboardEntry;
import com.tecsup.leaderboard.repository.LeaderboardEntryRepository;
import com.tecsup.leaderboard.repository.LeaderboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampusRankingService {
    
    private final LeaderboardRepository leaderboardRepository;
    private final LeaderboardEntryRepository entryRepository;
    
    public List<LeaderboardEntryDTO> getCampusRanking(Long campusId, int limit) {
        Leaderboard campusLeaderboard = leaderboardRepository
            .findByTypeAndName("CAMPUS", "Campus " + campusId)
            .orElseThrow(() -> new RuntimeException("Campus leaderboard not found"));
        
        List<LeaderboardEntry> entries = entryRepository
            .findByLeaderboardIdOrderByPointsDesc(campusLeaderboard.getId())
            .stream()
            .limit(limit)
            .toList();
        
        return entries.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    private LeaderboardEntryDTO toDTO(LeaderboardEntry entry) {
        return new LeaderboardEntryDTO(
            entry.getId(),
            entry.getUserId(),
            entry.getRankingPosition(),
            entry.getPoints()
        );
    }
}
```

---

## 5. LISTENER

### listener/ScoreUpdateListener.java
```java
package com.tecsup.leaderboard.listener;

import com.tecsup.leaderboard.service.RankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScoreUpdateListener {
    
    private final RankingService rankingService;
    
    @RabbitListener(queues = "leaderboard.updated")
    public void handleScoreUpdated(Long userId) {
        log.info("Updating ranking for user: {}", userId);
        // Get user score and update ranking
        // rankingService.updateUserRanking(userId, score);
    }
}
```

---

## 6. CONTROLLERS

### controller/LeaderboardController.java
```java
package com.tecsup.leaderboard.controller;

import com.tecsup.leaderboard.dto.LeaderboardEntryDTO;
import com.tecsup.leaderboard.service.CampusRankingService;
import com.tecsup.leaderboard.service.CareerRankingService;
import com.tecsup.leaderboard.service.DepartmentRankingService;
import com.tecsup.leaderboard.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {
    
    private final RankingService rankingService;
    private final CampusRankingService campusRankingService;
    private final CareerRankingService careerRankingService;
    private final DepartmentRankingService departmentRankingService;
    
    @GetMapping("/global")
    public ResponseEntity<List<LeaderboardEntryDTO>> getGlobalRanking(
        @RequestParam(defaultValue = "100") int limit
    ) {
        return ResponseEntity.ok(rankingService.getGlobalRanking(limit));
    }
    
    @GetMapping("/campus/{campusId}")
    public ResponseEntity<List<LeaderboardEntryDTO>> getCampusRanking(
        @PathVariable Long campusId,
        @RequestParam(defaultValue = "50") int limit
    ) {
        return ResponseEntity.ok(campusRankingService.getCampusRanking(campusId, limit));
    }
    
    @GetMapping("/career/{careerId}")
    public ResponseEntity<List<LeaderboardEntryDTO>> getCareerRanking(
        @PathVariable Long careerId,
        @RequestParam(defaultValue = "50") int limit
    ) {
        return ResponseEntity.ok(careerRankingService.getCareerRanking(careerId, limit));
    }
    
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<LeaderboardEntryDTO>> getDepartmentRanking(
        @PathVariable Long departmentId,
        @RequestParam(defaultValue = "50") int limit
    ) {
        return ResponseEntity.ok(departmentRankingService.getDepartmentRanking(departmentId, limit));
    }
}
```

---

## 7. REPOSITORIES

### repository/LeaderboardRepository.java
```java
package com.tecsup.leaderboard.repository;

import com.tecsup.leaderboard.entity.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long> {
    Optional<Leaderboard> findByType(String type);
    Optional<Leaderboard> findByTypeAndName(String type, String name);
}
```

### repository/LeaderboardEntryRepository.java
```java
package com.tecsup.leaderboard.repository;

import com.tecsup.leaderboard.entity.LeaderboardEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaderboardEntryRepository extends JpaRepository<LeaderboardEntry, Long> {
    List<LeaderboardEntry> findByLeaderboardIdOrderByPointsDesc(Long leaderboardId);
    Optional<LeaderboardEntry> findByLeaderboardIdAndUserId(Long leaderboardId, Long userId);
}
```

---

## 8. MAIN APPLICATION

### LeaderboardApplication.java
```java
package com.tecsup.leaderboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class LeaderboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeaderboardApplication.class, args);
    }
}
```

---

## 9. SEED DATA

### DataInitializer.java
```java
package com.tecsup.leaderboard.config;

import com.tecsup.leaderboard.entity.Leaderboard;
import com.tecsup.leaderboard.repository.LeaderboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final LeaderboardRepository leaderboardRepository;
    
    @Override
    public void run(String... args) {
        if (leaderboardRepository.count() == 0) {
            leaderboardRepository.save(new Leaderboard(null, "Global Ranking", "GLOBAL"));
            leaderboardRepository.save(new Leaderboard(null, "Lima Campus", "CAMPUS"));
            leaderboardRepository.save(new Leaderboard(null, "Arequipa Campus", "CAMPUS"));
            leaderboardRepository.save(new Leaderboard(null, "Trujillo Campus", "CAMPUS"));
            leaderboardRepository.save(new Leaderboard(null, "Students Ranking", "STUDENT"));
            leaderboardRepository.save(new Leaderboard(null, "Teachers Ranking", "TEACHER"));
        }
    }
}
```

---

## 10. TESTING

### Obtener ranking global
```bash
curl http://localhost:8086/leaderboard/global?limit=10
```

### Obtener ranking por sede
```bash
curl http://localhost:8086/leaderboard/campus/1?limit=10
```

---

## TAREA SIGUIENTE

Una vez implementado el Leaderboard Service, procede al README-14-social-service.md para implementar el Social Service.
