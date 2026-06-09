# Plan para continuar y terminar el proyecto World Cup

Fecha: 2026-06-08  
Objetivo: dejar una guia practica para revisar backend, frontend y completar el proyecto hasta una entrega defendible.

## 1. Estado actual resumido

El proyecto tiene una buena base tecnica: frontend Angular, backend con microservicios Spring Boot, API Gateway, Eureka, Config Service, PostgreSQL, Redis, RabbitMQ, Flyway, Dockerfiles y documentacion por modulo.

Pero todavia no esta listo como producto final. El problema principal no es la cantidad de archivos, sino que el flujo mas importante de la aplicacion no esta completo:

```text
Usuario se registra
-> crea o entra a una sala
-> ve partidos
-> registra prediccion antes del partido
-> termina el partido
-> se calcula puntaje
-> se actualiza ranking de la sala
-> usuario ve historial y posicion
```

Actualmente ese flujo existe por partes, pero no esta conectado de extremo a extremo.

## 2. Verificacion del backend

### 2.1 Lo que esta bien encaminado

- Hay arquitectura de microservicios.
- Existe API Gateway.
- Existe Discovery Service con Eureka.
- Existe Config Service.
- Existe PostgreSQL con migraciones Flyway.
- Existe Redis.
- Existe RabbitMQ.
- Hay servicios separados para auth, organization, worldcup, prediction, scoring, leaderboard, social, gamification, notification, admin y group.
- Hay entidades importantes: users, roles, matches, teams, predictions, user_scores, score_history, rooms, room_members, room_invites.
- Hay Dockerfile para cada servicio.
- Hay documentacion tecnica por servicio.

### 2.2 Problemas criticos del backend

| Area | Problema | Riesgo | Prioridad |
|---|---|---|---|
| Predicciones | `Prediction` no tiene `roomId` | No se puede calcular ranking por sala | Alta |
| Predicciones | No hay bloqueo real por inicio del partido | Usuarios podrian predecir tarde | Alta |
| Predicciones | No evita duplicados por usuario, sala y partido | Puntajes duplicados o injustos | Alta |
| Scoring | No calcula exacto, ganador, empate, diferencia | Regla principal incompleta | Alta |
| Scoring | No existe bonus por prediccion anticipada | Requisito no cumplido | Alta |
| Scoring | Racha no es por sala ni por partidos consecutivos reales | Bonus incorrecto | Alta |
| Ranking | No existe ranking por sala | Requisito central ausente | Alta |
| Seguridad | Gateway permite `/api/admin/**` y `/api/groups/**` sin autenticacion | Riesgo alto | Alta |
| Seguridad | Servicios confian en `X-User-Id` enviado por frontend | Un usuario podria suplantar otro | Alta |
| Tests | Solo hay `contextLoads` y algunos fallan | No hay garantia de calidad | Alta |
| Docker | Compose no incluye frontend ni nginx balanceador | No levanta todo con un solo comando | Media |

### 2.3 Backend: orden recomendado de correccion

1. Corregir modelo de predicciones.
2. Corregir seguridad del Gateway.
3. Implementar bloqueo de predicciones.
4. Implementar motor de puntuacion.
5. Implementar ranking por sala.
6. Crear pruebas unitarias e integracion.
7. Arreglar Docker Compose completo.

## 3. Verificacion del frontend

### 3.1 Lo que esta bien encaminado

- Existe frontend Angular.
- Hay rutas para landing, auth, dashboard, matches, predictions, rankings, social, gamification y admin.
- Hay servicios API separados por modulo.
- Hay interceptor para enviar token y `X-User-Id`.
- Hay guard de autenticacion.
- Hay pantallas para login, registro, partidos, predicciones, ranking global, ranking por campus/carrera/departamento y admin.
- Hay estilos y estructura visual avanzada.

### 3.2 Problemas criticos del frontend

| Area | Problema | Riesgo | Prioridad |
|---|---|---|---|
| Rutas | `/app` no esta protegido con `AuthGuard` | Usuario no autenticado podria entrar | Alta |
| Salas | No se ve una pantalla completa para gestionar salas | Requisito central incompleto | Alta |
| Ranking | No hay ranking por sala visible | Requisito central incompleto | Alta |
| Predicciones | Formulario no esta ligado a sala | No sirve para competencia por grupo | Alta |
| Predicciones | No muestra claramente cierre por horario | Mala experiencia y riesgo funcional | Alta |
| API | Muchos servicios usan fallback mock con `catchError` | Puede ocultar errores reales | Media |
| Auth | Login/register pueden simular respuestas si falla backend | Puede dar falsa sensacion de funcionamiento | Media |
| Validacion | Falta validacion fuerte de goles y estado del partido | Datos invalidos | Alta |

### 3.3 Frontend: orden recomendado de correccion

1. Proteger rutas privadas con `AuthGuard`.
2. Crear seccion de salas/grupos.
3. Crear detalle de sala: miembros, codigo de invitacion, partidos, ranking.
4. Modificar predicciones para elegir o recibir `roomId`.
5. Mostrar estado de cierre de cada partido.
6. Mostrar ranking por sala.
7. Reducir mocks en modo produccion.
8. Agregar pruebas de componentes principales.

## 4. Modelo minimo que falta

Para que el sistema funcione correctamente por salas, se recomienda ajustar el modelo asi:

### 4.1 Tabla `predictions`

Debe incluir:

- `id`
- `user_id`
- `room_id`
- `match_id`
- `home_score`
- `away_score`
- `created_at`
- `updated_at`
- `locked`

Constraint recomendado:

```sql
UNIQUE (user_id, room_id, match_id)
```

Esto evita que el mismo usuario prediga dos veces el mismo partido en la misma sala.

### 4.2 Tabla `score_history`

Debe incluir:

- `id`
- `user_id`
- `room_id`
- `match_id`
- `prediction_id`
- `base_points`
- `streak_bonus`
- `early_bonus`
- `total_points`
- `reason`
- `created_at`

### 4.3 Tabla o vista para ranking por sala

Puede resolverse con consulta sobre `score_history`:

```sql
SELECT
  user_id,
  room_id,
  SUM(total_points) AS total_points
FROM score_history
WHERE room_id = :roomId
GROUP BY user_id, room_id
ORDER BY total_points DESC;
```

O con una tabla persistida:

- `room_scores`
- `user_id`
- `room_id`
- `total_points`
- `correct_exact`
- `correct_winner`
- `current_streak`

## 5. Reglas de puntuacion a implementar

La logica debe ser jerarquica:

1. Si acierta marcador exacto: 5 puntos.
2. Si no acierta exacto, pero acierta ganador o empate: 3 puntos.
3. Si no acierta exacto, pero acierta diferencia de goles con el mismo ganador: 2 puntos.
4. Luego se suman bonus:
   - +2 por cada 3 partidos consecutivos acertados al menos en ganador/empate.
   - +1 si predijo mas de 24 horas antes del inicio del partido.

Ejemplos:

| Prediccion | Resultado | Base | Bonus | Total |
|---|---|---:|---:|---:|
| Francia 2-1 Brasil | Francia 2-1 Brasil | 5 | 0 | 5 |
| Francia 1-0 Brasil | Francia 2-1 Brasil | 3 | 0 | 3 |
| Francia 3-1 Brasil | Francia 2-0 Brasil | 2 | 0 | 2 |
| Francia 2-1 Brasil, creada 25h antes | Francia 2-1 Brasil | 5 | 1 | 6 |
| Tercer acierto seguido en sala | Cualquier acierto de ganador/empate | base | 2 | base + 2 |

## 6. Endpoints minimos a completar

### Auth

- `POST /auth/register`
- `POST /auth/login`
- `GET /auth/me`
- `POST /auth/logout` opcional si se invalida token

### Salas

- `POST /groups`
- `GET /groups`
- `GET /groups/{id}`
- `POST /groups/join/{inviteCode}`
- `GET /groups/{id}/members`
- `POST /groups/{id}/invites`
- `GET /groups/{id}/ranking`

### Partidos

- `GET /matches`
- `GET /matches/date/{date}`
- `GET /matches/{id}`
- `PATCH /matches/{id}/result`

### Predicciones

- `POST /predictions`
- `PUT /predictions/{id}`
- `GET /predictions/user/{userId}`
- `GET /predictions/match/{matchId}`
- `GET /groups/{roomId}/predictions`

### Puntajes

- `POST /scoring/matches/{matchId}/calculate`
- `GET /scoring/user/{userId}`
- `GET /scoring/user/{userId}/history`
- `GET /scoring/rooms/{roomId}/ranking`

## 7. Seguridad minima obligatoria

Antes de entregar:

- El Gateway no debe dejar admin publico.
- El Gateway no debe dejar groups publico completo.
- El backend no debe confiar ciegamente en `X-User-Id`.
- El usuario autenticado debe obtenerse desde el JWT.
- Las acciones admin deben validar rol ADMIN.
- Las acciones de sala deben validar membresia.
- Las acciones owner/admin de sala deben validar rol dentro de la sala.
- CORS debe restringirse a dominios esperados.
- Los secretos deben ir en `.env`, no en codigo.
- Debe existir `.env.example` sin secretos reales.

## 8. Docker y ejecucion local

### 8.1 Objetivo

El proyecto deberia poder ejecutarse con:

```bash
cd Bckend
docker compose up --build
```

Y detenerse con:

```bash
docker compose down
```

### 8.2 Pendientes Docker

- Agregar frontend al `docker-compose.yml`.
- Agregar Nginx si se quiere balanceo externo.
- Crear `.env.example`.
- Quitar `container_name` en servicios que se quieran escalar.
- Evitar exponer todos los microservicios al host.
- Exponer principalmente frontend y API Gateway.

### 8.3 Escalamiento minimo

Despues de quitar `container_name`, probar:

```bash
docker compose up --scale prediction-service=3 --scale scoring-service=2
```

Luego verificar que Eureka registre las instancias y que Gateway distribuya trafico.

## 9. Pruebas necesarias

### 9.1 Unitarias backend

Obligatorias:

- Calculo exacto: 5 puntos.
- Ganador correcto: 3 puntos.
- Empate correcto: 3 puntos.
- Diferencia correcta: 2 puntos.
- Fallo total: 0 puntos.
- Bonus anticipacion: +1.
- Bonus racha: +2.
- Prediccion duplicada: error.
- Prediccion despues del inicio: error.

### 9.2 Integracion backend

Flujo obligatorio:

1. Registrar usuario.
2. Crear sala.
3. Unirse a sala.
4. Crear partido.
5. Crear prediccion.
6. Finalizar partido.
7. Calcular puntaje.
8. Consultar ranking de sala.
9. Consultar historial.

### 9.3 Frontend

Pruebas recomendadas:

- Login correcto.
- Ruta privada bloquea usuario no autenticado.
- Crear sala.
- Unirse por codigo.
- Crear prediccion.
- Boton de prediccion se bloquea si partido ya empezo.
- Ranking por sala muestra orden correcto.

### 9.4 Stress testing

Herramienta sugerida: k6.

Escenarios:

- 100 usuarios haciendo login.
- 500 usuarios creando predicciones.
- 1000 consultas de ranking.
- 10 000 predicciones calculadas al cerrar partido.

Metricas a reportar:

- Duracion.
- Usuarios virtuales.
- Tiempo promedio.
- p95.
- Errores.
- Throughput.
- Conclusiones.

## 10. Roadmap recomendado

### Fase 1: Correcciones criticas

Objetivo: que el flujo principal sea correcto.

Tareas:

- Agregar `roomId` a predicciones.
- Agregar constraint unico usuario+sala+partido.
- Implementar bloqueo por fecha/hora.
- Implementar motor de puntuacion.
- Implementar ranking por sala.
- Proteger Gateway.

Prioridad: Alta  
Responsable: Backend  
Tiempo estimado: 5 a 7 dias  
Resultado esperado: flujo central funcionando.

### Fase 2: Completar frontend obligatorio

Objetivo: que el usuario pueda usar salas y predicciones desde la UI.

Tareas:

- Proteger `/app` con guard.
- Crear modulo/pantallas de salas.
- Crear detalle de sala.
- Integrar predicciones con sala.
- Mostrar ranking por sala.
- Mostrar historial por sala.

Prioridad: Alta  
Responsable: Frontend  
Tiempo estimado: 4 a 6 dias  
Resultado esperado: experiencia principal completa.

### Fase 3: Docker y despliegue

Objetivo: levantar todo con un solo comando.

Tareas:

- Agregar frontend al compose.
- Crear `.env.example`.
- Revisar puertos y healthchecks.
- Quitar `container_name` para servicios escalables.
- Documentar `docker compose up --build`.

Prioridad: Media  
Responsable: DevOps/backend  
Tiempo estimado: 2 a 3 dias  
Resultado esperado: entorno reproducible.

### Fase 4: Pruebas

Objetivo: demostrar que el sistema funciona.

Tareas:

- Crear unit tests de scoring.
- Crear unit tests de predicciones.
- Crear integracion del flujo completo.
- Crear pruebas frontend basicas.
- Crear stress test con k6.

Prioridad: Alta  
Responsable: QA/backend/frontend  
Tiempo estimado: 5 a 7 dias  
Resultado esperado: evidencia objetiva de calidad.

### Fase 5: Documentacion final

Objetivo: entregar el proyecto con instrucciones claras.

Tareas:

- README final.
- Arquitectura actualizada.
- Endpoints principales.
- Guia de ejecucion local.
- Guia de Docker.
- Evidencia de pruebas.
- Limitaciones conocidas.

Prioridad: Alta  
Responsable: Lider tecnico  
Tiempo estimado: 2 dias  
Resultado esperado: entrega defendible.

### Fase 6: Mejoras opcionales

Objetivo: agregar valor si queda tiempo.

Tareas:

- Estadisticas descriptivas.
- Comentarios automaticos de rendimiento.
- Comparacion entre participantes.
- Badges por hitos.
- Mejoras visuales.

Prioridad: Baja  
Responsable: Fullstack  
Tiempo estimado: 3 a 5 dias  
Resultado esperado: producto mas atractivo.

## 11. Checklist para saber si ya esta listo

- [ ] El usuario se registra.
- [ ] El usuario inicia sesion.
- [ ] Las rutas privadas estan protegidas.
- [ ] El usuario crea una sala.
- [ ] Otro usuario se une por codigo o invitacion.
- [ ] Se ven los participantes de la sala.
- [ ] Se listan partidos.
- [ ] El usuario predice un partido dentro de una sala.
- [ ] No se permite predecir despues del inicio.
- [ ] No se permite duplicar prediccion en la misma sala.
- [ ] El admin registra resultado real.
- [ ] El sistema calcula puntos automaticamente.
- [ ] Exacto da 5 puntos.
- [ ] Ganador o empate correcto da 3 puntos.
- [ ] Diferencia correcta da 2 puntos.
- [ ] Prediccion anticipada suma +1.
- [ ] Racha de 3 aciertos suma +2.
- [ ] Ranking por sala se actualiza.
- [ ] Historial de puntos se guarda.
- [ ] Docker levanta todo.
- [ ] Existen pruebas unitarias.
- [ ] Existen pruebas de integracion.
- [ ] Existe reporte de stress testing.
- [ ] Existe README final de ejecucion.

## 12. Prioridad absoluta

Si hay poco tiempo, enfocar el trabajo en este orden:

1. Predicciones por sala.
2. Bloqueo de predicciones por hora del partido.
3. Calculo de puntos correcto.
4. Ranking por sala.
5. Seguridad del Gateway.
6. Pantallas de sala en frontend.
7. Pruebas de scoring.
8. Docker Compose completo.
9. Documentacion de ejecucion.
10. Stress testing basico.

## 13. Conclusion

El backend y el frontend tienen una base suficiente para terminar el proyecto, pero aun no se puede considerar que esten bien completos. El backend necesita cerrar la logica de negocio principal y seguridad. El frontend necesita mostrar y conectar correctamente salas, predicciones y ranking por sala.

Estado recomendado: casi encaminado, pero incompleto. Con las fases anteriores, el proyecto puede pasar de una arquitectura parcialmente implementada a una aplicacion funcional y presentable.
