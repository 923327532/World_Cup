# README 09: Organization Service - Información Institucional

## PROMPT PARA EL EQUIPO DE DESARROLLO

Como desarrollador backend, tu tarea es implementar el Organization Service responsable de gestionar la información institucional de Tecsup (sedes, departamentos, carreras, perfiles de estudiantes y docentes).

---

## CONFIGURACIÓN SPRING INITIALIZR

### Project
- **Type**: Maven
- **Language**: Java
- **Spring Boot**: 3.5.x

### Project Metadata
- **Group**: pe.tecsup.worldcup
- **Artifact**: organization-service
- **Name**: organization-service
- **Package name**: pe.tecsup.worldcup.organization
- **Packaging**: Jar
- **Java**: 17

### Dependencies
- **Spring Web**
- **Spring Data JPA**
- **PostgreSQL**
- **Validation**
- **OpenFeign**
- **Eureka Discovery Client**
- **Lombok**

### Generar en Spring Initializr
```
https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.5.x&packaging=jar&jvmVersion=17&groupId=pe.tecsup.worldcup&artifactId=organization-service&name=organization-service&packageName=pe.tecsup.worldcup.organization&dependencies=web,data-jpa,postgresql,validation,openfeign,eureka-client,lombok
```

---

## 1. ESTRUCTURA DEL PROYECTO

```
organization-service
│
├── controller
│   ├── CampusController
│   ├── DepartmentController
│   ├── CareerController
│   ├── AvatarController
│   ├── StudentProfileController
│   └── TeacherProfileController
│
├── service
│   ├── CampusService
│   ├── DepartmentService
│   ├── CareerService
│   ├── AvatarService
│   ├── StudentProfileService
│   └── TeacherProfileService
│
├── repository
│   ├── CampusRepository
│   ├── DepartmentRepository
│   ├── CareerRepository
│   ├── AvatarRepository
│   ├── StudentProfileRepository
│   └── TeacherProfileRepository
│
├── entity
│   ├── Campus
│   ├── Department
│   ├── Career
│   ├── Avatar
│   ├── StudentProfile
│   └── TeacherProfile
│
├── dto
│   ├── CampusDTO
│   ├── DepartmentDTO
│   ├── CareerDTO
│   ├── AvatarDTO
│   ├── StudentProfileDTO
│   └── TeacherProfileDTO
│
├── resources
│   ├── application.yml
│   └── application-dev.yml
│
└── OrganizationApplication.java
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

### entity/Campus.java
```java
package com.tecsup.organization.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "campuses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Campus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
}
```

### entity/Department.java
```java
package com.tecsup.organization.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "departments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String name;
}
```

### entity/Career.java
```java
package com.tecsup.organization.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "careers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Career {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
    
    @Column(nullable = false, length = 200)
    private String name;
}
```

### entity/Avatar.java
```java
package com.tecsup.organization.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "avatars")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Avatar {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 100)
    private String name;
    
    @Column(length = 500)
    private String imageUrl;
}
```

### entity/StudentProfile.java
```java
package com.tecsup.organization.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "student_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private Long userId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campus_id", nullable = false)
    private Campus campus;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_id", nullable = false)
    private Career career;
    
    @Column(nullable = false, unique = true, length = 30)
    private String studentCode;
    
    @Column(length = 20)
    private String cycle;
}
```

### entity/TeacherProfile.java
```java
package com.tecsup.organization.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "teacher_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private Long userId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campus_id", nullable = false)
    private Campus campus;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}
```

---

## 4. DTOS

### dto/CampusDTO.java
```java
package com.tecsup.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampusDTO {
    private Long id;
    private String name;
}
```

### dto/DepartmentDTO.java
```java
package com.tecsup.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {
    private Long id;
    private String name;
}
```

### dto/CareerDTO.java
```java
package com.tecsup.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CareerDTO {
    private Long id;
    private Long departmentId;
    private String departmentName;
    private String name;
}
```

### dto/StudentProfileDTO.java
```java
package com.tecsup.organization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileDTO {
    private Long id;
    private Long userId;
    
    @NotNull
    private Long campusId;
    
    @NotNull
    private Long careerId;
    
    @NotBlank
    private String studentCode;
    
    private String cycle;
}
```

### dto/TeacherProfileDTO.java
```java
package com.tecsup.organization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherProfileDTO {
    private Long id;
    private Long userId;
    
    @NotNull
    private Long campusId;
    
    @NotNull
    private Long departmentId;
}
```

---

## 5. REPOSITORIES

### repository/CampusRepository.java
```java
package com.tecsup.organization.repository;

import com.tecsup.organization.entity.Campus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampusRepository extends JpaRepository<Campus, Long> {
    List<Campus> findAll();
}
```

### repository/DepartmentRepository.java
```java
package com.tecsup.organization.repository;

import com.tecsup.organization.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findAll();
}
```

### repository/CareerRepository.java
```java
package com.tecsup.organization.repository;

import com.tecsup.organization.entity.Career;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerRepository extends JpaRepository<Career, Long> {
    List<Career> findByDepartmentId(Long departmentId);
    List<Career> findAll();
}
```

### repository/StudentProfileRepository.java
```java
package com.tecsup.organization.repository;

import com.tecsup.organization.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByUserId(Long userId);
    Optional<StudentProfile> findByStudentCode(String studentCode);
}
```

### repository/TeacherProfileRepository.java
```java
package com.tecsup.organization.repository;

import com.tecsup.organization.entity.TeacherProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherProfileRepository extends JpaRepository<TeacherProfile, Long> {
    Optional<TeacherProfile> findByUserId(Long userId);
}
```

---

## 6. SERVICES

### service/CampusService.java
```java
package com.tecsup.organization.service;

import com.tecsup.organization.dto.CampusDTO;
import com.tecsup.organization.entity.Campus;
import com.tecsup.organization.repository.CampusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampusService {
    
    private final CampusRepository campusRepository;
    
    public List<CampusDTO> getAllCampuses() {
        return campusRepository.findAll().stream()
            .map(this::toDTO)
            .toList();
    }
    
    public CampusDTO getCampusById(Long id) {
        Campus campus = campusRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Campus not found"));
        return toDTO(campus);
    }
    
    private CampusDTO toDTO(Campus campus) {
        return new CampusDTO(campus.getId(), campus.getName());
    }
}
```

### service/CareerService.java
```java
package com.tecsup.organization.service;

import com.tecsup.organization.dto.CareerDTO;
import com.tecsup.organization.entity.Career;
import com.tecsup.organization.repository.CareerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CareerService {
    
    private final CareerRepository careerRepository;
    
    public List<CareerDTO> getAllCareers() {
        return careerRepository.findAll().stream()
            .map(this::toDTO)
            .toList();
    }
    
    public List<CareerDTO> getCareersByDepartment(Long departmentId) {
        return careerRepository.findByDepartmentId(departmentId).stream()
            .map(this::toDTO)
            .toList();
    }
    
    private CareerDTO toDTO(Career career) {
        return new CareerDTO(
            career.getId(),
            career.getDepartment().getId(),
            career.getDepartment().getName(),
            career.getName()
        );
    }
}
```

### service/StudentProfileService.java
```java
package com.tecsup.organization.service;

import com.tecsup.organization.dto.StudentProfileDTO;
import com.tecsup.organization.entity.StudentProfile;
import com.tecsup.organization.repository.CampusRepository;
import com.tecsup.organization.repository.CareerRepository;
import com.tecsup.organization.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentProfileService {
    
    private final StudentProfileRepository studentProfileRepository;
    private final CampusRepository campusRepository;
    private final CareerRepository careerRepository;
    
    public StudentProfileDTO createProfile(StudentProfileDTO dto) {
        StudentProfile profile = new StudentProfile();
        profile.setUserId(dto.getUserId());
        profile.setCampus(campusRepository.findById(dto.getCampusId())
            .orElseThrow(() -> new RuntimeException("Campus not found")));
        profile.setCareer(careerRepository.findById(dto.getCareerId())
            .orElseThrow(() -> new RuntimeException("Career not found")));
        profile.setStudentCode(dto.getStudentCode());
        profile.setCycle(dto.getCycle());
        
        profile = studentProfileRepository.save(profile);
        return toDTO(profile);
    }
    
    public StudentProfileDTO getProfileByUserId(Long userId) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));
        return toDTO(profile);
    }
    
    private StudentProfileDTO toDTO(StudentProfile profile) {
        return new StudentProfileDTO(
            profile.getId(),
            profile.getUserId(),
            profile.getCampus().getId(),
            profile.getCareer().getId(),
            profile.getStudentCode(),
            profile.getCycle()
        );
    }
}
```

---

## 7. CONTROLLERS

### controller/CampusController.java
```java
package com.tecsup.organization.controller;

import com.tecsup.organization.dto.CampusDTO;
import com.tecsup.organization.service.CampusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/campuses")
@RequiredArgsConstructor
public class CampusController {
    
    private final CampusService campusService;
    
    @GetMapping
    public ResponseEntity<List<CampusDTO>> getAllCampuses() {
        return ResponseEntity.ok(campusService.getAllCampuses());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CampusDTO> getCampusById(@PathVariable Long id) {
        return ResponseEntity.ok(campusService.getCampusById(id));
    }
}
```

### controller/CareerController.java
```java
package com.tecsup.organization.controller;

import com.tecsup.organization.dto.CareerDTO;
import com.tecsup.organization.service.CareerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/careers")
@RequiredArgsConstructor
public class CareerController {
    
    private final CareerService careerService;
    
    @GetMapping
    public ResponseEntity<List<CareerDTO>> getAllCareers() {
        return ResponseEntity.ok(careerService.getAllCareers());
    }
    
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<CareerDTO>> getCareersByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(careerService.getCareersByDepartment(departmentId));
    }
}
```

### controller/StudentProfileController.java
```java
package com.tecsup.organization.controller;

import com.tecsup.organization.dto.StudentProfileDTO;
import com.tecsup.organization.service.StudentProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student-profiles")
@RequiredArgsConstructor
public class StudentProfileController {
    
    private final StudentProfileService studentProfileService;
    
    @PostMapping
    public ResponseEntity<StudentProfileDTO> createProfile(@RequestBody StudentProfileDTO dto) {
        return ResponseEntity.ok(studentProfileService.createProfile(dto));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<StudentProfileDTO> getProfileByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(studentProfileService.getProfileByUserId(userId));
    }
}
```

---

## 8. MAIN APPLICATION

### OrganizationApplication.java
```java
package com.tecsup.organization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class OrganizationApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrganizationApplication.class, args);
    }
}
```

---

## 9. SEED DATA

### DataInitializer.java
```java
package com.tecsup.organization.config;

import com.tecsup.organization.entity.Campus;
import com.tecsup.organization.entity.Department;
import com.tecsup.organization.repository.CampusRepository;
import com.tecsup.organization.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final CampusRepository campusRepository;
    private final DepartmentRepository departmentRepository;
    
    @Override
    public void run(String... args) {
        if (campusRepository.count() == 0) {
            campusRepository.save(new Campus(null, "Lima"));
            campusRepository.save(new Campus(null, "Arequipa"));
            campusRepository.save(new Campus(null, "Trujillo"));
        }
        
        if (departmentRepository.count() == 0) {
            departmentRepository.save(new Department(null, "Tecnología Digital"));
            departmentRepository.save(new Department(null, "Mecánica y Aviación"));
            departmentRepository.save(new Department(null, "Electricidad y Electrónica"));
            departmentRepository.save(new Department(null, "Mecatrónica"));
            departmentRepository.save(new Department(null, "Minería, Procesos Químicos y Metalúrgicos"));
            departmentRepository.save(new Department(null, "Gestión y Producción"));
            departmentRepository.save(new Department(null, "Seguridad y Salud en el Trabajo"));
            departmentRepository.save(new Department(null, "Tecnología Agrícola"));
        }
    }
}
```

---

## 10. TESTING

### Obtener todas las sedes
```bash
curl http://localhost:8082/campuses
```

### Obtener carreras por departamento
```bash
curl http://localhost:8082/careers/department/1
```

### Crear perfil de estudiante
```bash
curl -X POST http://localhost:8082/student-profiles \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "campusId": 1,
    "careerId": 1,
    "studentCode": "2023001",
    "cycle": "1"
  }'
```

---

## TAREA SIGUIENTE

Una vez implementado el Organization Service, procede al README-10-worldcup-service.md para implementar el World Cup Service.
