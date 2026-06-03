# README 14: Social Service - Comentarios y Comunidad

## PROMPT PARA EL EQUIPO DE DESARROLLO

Como desarrollador backend, tu tarea es implementar el Social Service responsable de gestionar los comentarios en vivo, reacciones y chat de los partidos.

---

## CONFIGURACIÓN SPRING INITIALIZR

### Project
- **Type**: Maven
- **Language**: Java
- **Spring Boot**: 3.5.x

### Project Metadata
- **Group**: pe.tecsup.worldcup
- **Artifact**: social-service
- **Name**: social-service
- **Package name**: pe.tecsup.worldcup.social
- **Packaging**: Jar
- **Java**: 17

### Dependencies
- **Spring Web**
- **Spring Data JPA**
- **WebSocket**
- **PostgreSQL**
- **RabbitMQ (Spring AMQP)**
- **Eureka Discovery Client**
- **Lombok**

### Generar en Spring Initializr
```
https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.5.x&packaging=jar&jvmVersion=17&groupId=pe.tecsup.worldcup&artifactId=social-service&name=social-service&packageName=pe.tecsup.worldcup.social&dependencies=web,data-jpa,websocket,postgresql,amqp,eureka-client,lombok
```

---

## 1. ESTRUCTURA DEL PROYECTO

```
social-service
│
├── controller
│   ├── CommentController
│   └── ReactionController
│
├── service
│   ├── CommentService
│   └── ReactionService
│
├── repository
│   ├── CommentRepository
│   └── CommentReactionRepository
│
├── entity
│   ├── Comment
│   └── CommentReaction
│
├── dto
│   ├── CommentDTO
│   ├── CreateCommentRequest
│   ├── ReactionDTO
│   └── CreateReactionRequest
│
├── websocket
│   ├── WebSocketConfig
│   ├── ChatHandler
│   └── ChatPublisher
│
├── listener
│   └── MatchEventListener
│
├── resources
│   ├── application.yml
│   └── application-dev.yml
│
└── SocialApplication.java
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

    <!-- Spring Boot Starter WebSocket -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
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

### entity/Comment.java
```java
package com.tecsup.social.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;
    
    @Column(name = "user_id")
    private Long userId;
    
    private String content;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
```

### entity/CommentReaction.java
```java
package com.tecsup.social.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment_reactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentReaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(length = 20)
    private String reaction;
}
```

---

## 4. WEBSOCKET CONFIG

### websocket/WebSocketConfig.java
```java
package com.tecsup.social.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Value("${websocket.allowed-origins:*}")
    private String allowedOrigins;
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")
            .setAllowedOrigins(allowedOrigins)
            .withSockJS();
    }
}
```

### websocket/ChatHandler.java
```java
package com.tecsup.social.websocket;

import com.tecsup.social.dto.CommentDTO;
import com.tecsup.social.entity.Comment;
import com.tecsup.social.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatHandler {
    
    private final CommentService commentService;
    
    @MessageMapping("/chat/{matchId}")
    @SendTo("/topic/chat/{matchId}")
    public CommentDTO sendMessage(@DestinationVariable Long matchId, CommentDTO comment) {
        Comment saved = commentService.createComment(comment.getUserId(), matchId, comment.getContent());
        return commentService.toDTO(saved);
    }
}
```

---

## 5. SERVICES

### service/CommentService.java
```java
package com.tecsup.social.service;

import com.tecsup.social.dto.CommentDTO;
import com.tecsup.social.entity.Comment;
import com.tecsup.social.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentRepository commentRepository;
    
    public Comment createComment(Long userId, Long matchId, String content) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setMatch(new Match(matchId));
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        
        return commentRepository.save(comment);
    }
    
    public List<CommentDTO> getMatchComments(Long matchId) {
        return commentRepository.findByMatchIdOrderByCreatedAtDesc(matchId).stream()
            .map(this::toDTO)
            .toList();
    }
    
    public CommentDTO toDTO(Comment comment) {
        return new CommentDTO(
            comment.getId(),
            comment.getUserId(),
            comment.getMatch().getId(),
            comment.getContent(),
            comment.getCreatedAt()
        );
    }
}
```

### service/ReactionService.java
```java
package com.tecsup.social.service;

import com.tecsup.social.dto.ReactionDTO;
import com.tecsup.social.entity.Comment;
import com.tecsup.social.entity.CommentReaction;
import com.tecsup.social.repository.CommentReactionRepository;
import com.tecsup.social.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReactionService {
    
    private final CommentReactionRepository reactionRepository;
    private final CommentRepository commentRepository;
    
    public CommentReaction addReaction(Long userId, Long commentId, String reaction) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        // Check if user already reacted
        reactionRepository.findByCommentIdAndUserId(commentId, userId)
            .ifPresent(existing -> {
                reactionRepository.delete(existing);
            });
        
        CommentReaction newReaction = new CommentReaction();
        newReaction.setComment(comment);
        newReaction.setUserId(userId);
        newReaction.setReaction(reaction);
        
        return reactionRepository.save(newReaction);
    }
    
    public void removeReaction(Long userId, Long commentId) {
        reactionRepository.findByCommentIdAndUserId(commentId, userId)
            .ifPresent(reactionRepository::delete);
    }
}
```

---

## 6. CONTROLLERS

### controller/CommentController.java
```java
package com.tecsup.social.controller;

import com.tecsup.social.dto.CommentDTO;
import com.tecsup.social.dto.CreateCommentRequest;
import com.tecsup.social.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    
    @PostMapping
    public ResponseEntity<CommentDTO> createComment(
        @RequestHeader("X-User-Id") Long userId,
        @RequestBody CreateCommentRequest request
    ) {
        Comment comment = commentService.createComment(userId, request.getMatchId(), request.getContent());
        return ResponseEntity.ok(commentService.toDTO(comment));
    }
    
    @GetMapping("/match/{matchId}")
    public ResponseEntity<List<CommentDTO>> getMatchComments(@PathVariable Long matchId) {
        return ResponseEntity.ok(commentService.getMatchComments(matchId));
    }
}
```

### controller/ReactionController.java
```java
package com.tecsup.social.controller;

import com.tecsup.social.dto.CreateReactionRequest;
import com.tecsup.social.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reactions")
@RequiredArgsConstructor
public class ReactionController {
    
    private final ReactionService reactionService;
    
    @PostMapping
    public ResponseEntity<Void> addReaction(
        @RequestHeader("X-User-Id") Long userId,
        @RequestBody CreateReactionRequest request
    ) {
        reactionService.addReaction(userId, request.getCommentId(), request.getReaction());
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> removeReaction(
        @RequestHeader("X-User-Id") Long userId,
        @PathVariable Long commentId
    ) {
        reactionService.removeReaction(userId, commentId);
        return ResponseEntity.ok().build();
    }
}
```

---

## 7. REPOSITORIES

### repository/CommentRepository.java
```java
package com.tecsup.social.repository;

import com.tecsup.social.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByMatchIdOrderByCreatedAtDesc(Long matchId);
}
```

### repository/CommentReactionRepository.java
```java
package com.tecsup.social.repository;

import com.tecsup.social.entity.CommentReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {
    Optional<CommentReaction> findByCommentIdAndUserId(Long commentId, Long userId);
}
```

---

## 8. MAIN APPLICATION

### SocialApplication.java
```java
package com.tecsup.social;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SocialApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialApplication.class, args);
    }
}
```

---

## 9. TESTING

### Crear comentario
```bash
curl -X POST http://localhost:8087/comments \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{
    "matchId": 1,
    "content": "Golazo!"
  }'
```

### Obtener comentarios de partido
```bash
curl http://localhost:8087/comments/match/1
```

### Conectar via WebSocket
```javascript
// En el frontend
const socket = new SockJS('http://localhost:8087/ws/chat');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    stompClient.subscribe('/topic/chat/1', function(message) {
        console.log(message.body);
    });
    
    stompClient.send('/app/chat/1', {}, JSON.stringify({
        userId: 1,
        content: 'Hola desde el chat'
    }));
});
```

---

## TAREA SIGUIENTE

Una vez implementado el Social Service, procede al README-15-gamification-notification.md para implementar los servicios de Gamification y Notification.
