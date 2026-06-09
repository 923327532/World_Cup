# Organization Service - API Endpoints

## Base URL
```
/teacher-profiles
```

## TeacherProfileController

### Crear perfil de profesor
```
POST /teacher-profiles
```
- **Body:** `TeacherProfileDTO`
- **Response:** `TeacherProfileDTO`

### Obtener perfil de profesor por usuario
```
GET /teacher-profiles/user/{userId}
```
- **Response:** `TeacherProfileDTO`

---

## Base URL
```
/student-profiles
```

## StudentProfileController

### Crear perfil de estudiante
```
POST /student-profiles
```
- **Body:** `StudentProfileDTO`
- **Response:** `StudentProfileDTO`

### Obtener perfil de estudiante por usuario
```
GET /student-profiles/user/{userId}
```
- **Response:** `StudentProfileDTO`

---

## Base URL
```
/departments
```

## DepartmentController

### Obtener todos los departamentos
```
GET /departments
```
- **Response:** `List<DepartmentDTO>`

### Obtener departamento por ID
```
GET /departments/{id}
```
- **Response:** `DepartmentDTO`

---

## Base URL
```
/careers
```

## CareerController

### Obtener todas las carreras
```
GET /careers
```
- **Response:** `List<CareerDTO>`

### Obtener carreras por departamento
```
GET /careers/department/{departmentId}
```
- **Response:** `List<CareerDTO>`

---

## Base URL
```
/campuses
```

## CampusController

### Obtener todos los campus
```
GET /campuses
```
- **Response:** `List<CampusDTO>`

### Obtener campus por ID
```
GET /campuses/{id}
```
- **Response:** `CampusDTO`

---

## Base URL
```
/avatars
```

## AvatarController

### Obtener todos los avatares
```
GET /avatars
```
- **Response:** `List<AvatarDTO>`

### Obtener avatar por ID
```
GET /avatars/{id}
```
- **Response:** `AvatarDTO`
