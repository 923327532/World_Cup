# Organization Service - API Endpoints

## TeacherProfileController

### POST /teacher-profiles
Crear perfil de profesor

**Request Body:** `TeacherProfileDTO`

**Response 200:** `TeacherProfileDTO`

### GET /teacher-profiles/user/{userId}
Obtener perfil de profesor por usuario

**Response 200:** `TeacherProfileDTO`

---

## StudentProfileController

### POST /student-profiles
Crear perfil de estudiante

**Request Body:** `StudentProfileDTO`

**Response 200:** `StudentProfileDTO`

### GET /student-profiles/user/{userId}
Obtener perfil de estudiante por usuario

**Response 200:** `StudentProfileDTO`

---

## DepartmentController

### GET /departments
Obtener todos los departamentos

**Response 200:** `List<DepartmentDTO>`

### GET /departments/{id}
Obtener departamento por ID

**Response 200:** `DepartmentDTO`

---

## CareerController

### GET /careers
Obtener todas las carreras

**Response 200:** `List<CareerDTO>`

### GET /careers/department/{departmentId}
Obtener carreras por departamento

**Response 200:** `List<CareerDTO>`

---

## CampusController

### GET /campuses
Obtener todos los campus

**Response 200:** `List<CampusDTO>`

### GET /campuses/{id}
Obtener campus por ID

**Response 200:** `CampusDTO`

---

## AvatarController

### GET /avatars
Obtener todos los avatares

**Response 200:** `List<AvatarDTO>`

### GET /avatars/{id}
Obtener avatar por ID

**Response 200:** `AvatarDTO`
