# Auth Service - API Endpoints

## Base URL
```
/auth
```

## Endpoints

### Registro de usuario
```
POST /auth/register
```
- **Body:** `RegisterRequest` (email, password, firstName, lastName, role, studentCode)
- **Response:** `AuthResponse` (token, type, userId, email, role)

### Login
```
POST /auth/login
```
- **Body:** `LoginRequest` (email, password)
- **Response:** `AuthResponse` (token, type, userId, email, role)

### Verificar email
```
POST /auth/verify-email
```
- **Body:** `VerifyEmailRequest` (email, token)
- **Response:** `Boolean`
