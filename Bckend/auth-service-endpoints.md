# Auth Service - API Endpoints

## POST /auth/register
**Descripción:** Registrar un nuevo usuario

**Request Body:**
| Campo | Tipo | Requerido | Validación |
|-------|------|-----------|------------|
| `email` | String | Sí | Email válido |
| `password` | String | Sí | 8-20 caracteres |
| `firstName` | String | Sí | No vacío |
| `lastName` | String | Sí | No vacío |
| `role` | String | Sí | `STUDENT` o `TEACHER` |
| `studentCode` | String | No | Requerido para estudiantes |

**Response 200:** `AuthResponse`
| Campo | Tipo |
|-------|------|
| `token` | String |
| `type` | String (default: "Bearer") |
| `userId` | Long |
| `email` | String |
| `role` | String |

---

## POST /auth/login
**Descripción:** Autenticar usuario

**Request Body:**
| Campo | Tipo | Requerido |
|-------|------|-----------|
| `email` | String | Sí |
| `password` | String | Sí |

**Response 200:** `AuthResponse` (mismo schema que register)

---

## POST /auth/verify-email
**Descripción:** Verificar email con token

**Request Body:**
| Campo | Tipo | Requerido | Validación |
|-------|------|-----------|------------|
| `email` | String | Sí | Email válido |
| `token` | String | Sí | Exactamente 6 caracteres |

**Response 200:** `Boolean`
