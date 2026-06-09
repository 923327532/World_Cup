# Verificacion completa y roadmap del proyecto World Cup

Fecha de revision: 2026-06-08  
Alcance: revision estatica del codigo, documentacion, Docker, estructura de base de datos y ejecucion parcial de pruebas/build.

## 1. Resumen general

El proyecto tiene una base amplia y bien intencionada: frontend Angular, backend en microservicios Spring Boot, API Gateway, Eureka, Config Service, PostgreSQL, Redis, RabbitMQ, Flyway, Dockerfiles por servicio y documentacion separada por modulo. Sin embargo, no esta listo para entrega final porque las funcionalidades centrales de predicciones, puntuacion por sala, bloqueo por fecha/hora, ranking por sala y pruebas verificables estan incompletas o solo parcialmente conectadas.

El riesgo principal es funcional: el sistema parece tener la arquitectura, pero el flujo critico "usuario entra a sala -> predice partido -> se cierra prediccion -> termina partido -> se calcula puntaje -> se actualiza ranking de sala" no esta implementado de extremo a extremo.

## 2. Tabla de cumplimiento de entregables

| Entregable | Estado | Evidencia encontrada | Problema detectado | Recomendacion |
|---|---|---|---|---|
| Propuesta de arquitectura | Completo | `docs/backend/README-03-arquitectura-general.md`, docs por servicio | Debe alinearse con el estado real del codigo | Actualizar docs con brechas actuales |
| Desarrollo funcional | Parcial | Microservicios y pantallas Angular existentes | Flujo principal no esta cerrado | Priorizar predicciones, scoring y ranking por sala |
| Docker | Parcial | `Bckend/docker-compose.yml`, Dockerfiles backend y frontend | Compose backend no incluye frontend ni nginx balanceador | Agregar frontend y nginx al compose |
| Escalamiento horizontal | Parcial | Eureka + Gateway con `lb://service` | `container_name` y puertos fijos dificultan `--scale`; no hay guia real | Quitar `container_name`, no exponer puertos internos, documentar escala |
| Balanceo de carga | Parcial | Gateway usa discovery/load balancer; existe `Bckend/infrastructure/nginx/nginx.conf` | Nginx no esta conectado al compose | Integrar Nginx o explicar que Gateway balancea servicios registrados |
| Documentacion de pruebas | No implementado | Solo tests `contextLoads`; no hay informe de pruebas | No hay resultados, escenarios ni evidencias | Crear plan y reporte de pruebas |
| Stress testing | No implementado | No se encontraron k6/JMeter/Artillery/Locust/Postman | No hay escenarios ni metricas | Agregar k6 minimo para auth, predicciones y ranking |
| Sistema de puntuacion | Parcial | `ScoreCalculatorService`, `BonusService`, `StreakService`, `V6__update_scoring_to_scale_1_10.sql` | No calcula reglas exacto/ganador/diferencia; no hay bonus 24h; scoring dice que prediction-service lo hace, pero prediction-service no lo hace | Implementar motor jerarquico probado |
| Area visible de participantes y puntaje | Parcial | Pantallas rankings y dashboard; `leaderboard-service` global/campus/career/department | No se verifica ranking por sala | Agregar ranking por room |
| Salas/grupos con invitacion | Parcial | `group-service` tiene rooms, members, invites, bans | No esta conectado a predicciones ni ranking por sala | Agregar `roomId` a predicciones y scoring |
| Documentacion tecnica y uso | Parcial | Muchos README en `docs/` y endpoints md | Falta guia unica de ejecucion/verificacion | Crear README final operativo |
| Seguridad basica | Parcial | PasswordEncoder, JWT, AuthGuard, interceptor | Gateway permite `/api/admin/**` y `/api/groups/**`; CORS `*`; servicios confian en `X-User-Id` | Validar JWT en gateway/servicios y roles |
| Persistencia | Completo | Migraciones PostgreSQL, entidades JPA | Algunas relaciones clave faltan para sala/prediccion | Ajustar modelo para room scoring |
| Validaciones de predicciones | Parcial | DTOs y algunos `@Valid`; update revisa propietario | No valida goles, duplicados, sala ni cierre por hora | Implementar validadores y constraints DB |
| Fechas/horarios de partidos | Parcial | `Match.kickoffTime`, consultas por fecha | No hay bloqueo real; usa `LocalDateTime` sin zona | Usar `Instant`/timezone definida y regla de cierre |

## 3. Tabla de funcionalidades principales

| Funcionalidad | Estado | Evidencia | Riesgo | Accion recomendada |
|---|---|---|---|---|
| Registro | Parcial | `AuthService.register` | Envia OTP por consola; retorna token nulo | Implementar envio real o modo demo documentado |
| Login | Parcial | `AuthService.login`, JWT | Depende de email verificado; no hay `/auth/me` ni logout backend | Agregar endpoints me/logout |
| Proteccion rutas | Parcial | `AuthGuard`, `AuthInterceptor` | `/app` no tiene guard; solo `/admin` | Proteger todo `/app` |
| Roles | Parcial | `Role`, JWT role | Gateway permite admin sin auth | Aplicar autorizacion por rol |
| Perfil/historial | Parcial | score history, prediction history UI | Datos incompletos y sin sala | Completar DTOs |
| Crear/unirse a sala | Parcial | `RoomService.createRoom`, `joinByCode` | Falta control auth fuerte | Validar JWT y membresia |
| Invitar usuarios | Parcial | `RoomInvite` | No se verifica expiracion al responder | Validar expiracion y duplicados |
| Ver participantes | Parcial | `GET /groups/{roomId}/members` | Endpoint publico en gateway | Proteger por membresia |
| Ranking por sala | No implementado | Leaderboard global/campus/career/department | Requisito central ausente | Crear leaderboard por `roomId` |
| Listar partidos | Parcial | `MatchController` por fecha/id | No hay endpoint general `/matches` | Agregar filtros paginados |
| Guardar resultado real | Parcial | Admin manual matches | Duplicidad entre `matches` y `manual_matches` | Definir fuente unica o sincronizacion |
| Crear prediccion | Parcial | `PredictionService.createPrediction` | Sin `roomId`, sin validacion, sin lock, sin duplicado | Redisenar entidad/DTO |
| Editar prediccion | Parcial | `updatePrediction` | `isPredictionLocked` retorna `false` | Implementar cierre por kickoff |
| Calcular puntos | No implementado | Comentario en `calculateAndUpdateScores` | No hay motor real | Implementar servicio de calculo |
| Bonus racha | Parcial | `StreakService`, `BonusService` | No calcula por sala ni secuencia real de partidos | Guardar streak por usuario+sala |
| Bonus anticipacion | No implementado | No hay busqueda de regla 24h | Requisito ausente | Calcular si `createdAt <= kickoff - 24h` |
| IA opcional | No verificable | No se encontro modulo IA real | Opcional | Dejar para fase final |

## 4. Revision especifica del sistema de puntuacion

Estado general: parcial/incompleto.

Reglas solicitadas:

| Regla | Estado | Evidencia | Error o brecha |
|---|---|---|---|
| Exacto 5 pts | Parcial | Migracion V6 actualiza `EXACT_SCORE` a 5 | No hay codigo que compare marcador real vs prediccion |
| Ganador/empate 3 pts | Parcial | V6 actualiza `MATCH_WINNER` a 3 | `DataInitializer` usa codigos distintos (`WINNER`, `EXACT_SCORE`) y puntos antiguos en prediction-service |
| Diferencia de goles 2 pts | No implementado | No se encontro regla/calculo | Falta logica |
| Racha +2 cada 3 aciertos | Parcial | `BonusService.calculateStreakBonus(3)` | No es por sala; usa texto `reason contains correct`; no garantiza partidos consecutivos |
| Anticipacion +1 >24h | No implementado | Sin codigo encontrado | Falta comparar `createdAt` con `kickoffTime` |
| Jerarquica + bonus | No implementado | No hay motor jerarquico | Riesgo de puntajes inconsistentes |

Casos de prueba obligatorios:

| Caso | Prediccion | Resultado | Esperado |
|---|---|---|---|
| Exacto | Francia 2-1 Brasil | Francia 2-1 Brasil | 5 |
| Ganador | Francia 1-0 Brasil | Francia 2-1 Brasil | 3 |
| Diferencia | Francia 3-1 Brasil | Francia 2-0 Brasil | 2 |
| Empate correcto | 1-1 | 2-2 | 3 |
| Fallo total | Francia 1-0 Brasil | Francia 0-2 Brasil | 0 |
| Anticipada | creada 25h antes | cualquier acierto base | base +1 |
| No anticipada | creada 10min antes | cualquier acierto base | solo base |
| Racha | 3 aciertos seguidos por usuario+sala | tercer partido | +2 adicional |
| Duplicado | mismo usuario+room+match | segunda prediccion | error o update controlado |
| Bloqueo | prediccion despues de kickoff | partido iniciado | error |

## 5. Revision de Docker

Archivos encontrados:

- `Bckend/docker-compose.yml`
- Dockerfile por cada microservicio backend
- `world-cup-frontend/Dockerfile`
- `world-cup-frontend/nginx.conf`
- `Bckend/infrastructure/nginx/nginx.conf`
- `Bckend/.env`

Problemas:

- No se encontro `.env.example`.
- El compose del backend no incluye frontend.
- El compose no incluye el Nginx de infraestructura.
- `container_name` en servicios impide escalar replicas con Docker Compose.
- Los puertos de cada servicio se exponen al host; para escala deberian quedar internos y salir por gateway/nginx.

Comandos esperados:

```bash
cd Bckend
docker compose up --build
docker compose down
```

Mejoras necesarias:

- Agregar servicio `frontend`.
- Agregar servicio `nginx` o documentar que `api-gateway` es el unico balanceador.
- Crear `.env.example` sin secretos reales.
- Documentar orden de arranque y healthchecks.

## 6. Escalamiento y balanceo

Existe parcialmente por Spring Cloud Gateway + Eureka (`lb://auth-service`, `lb://prediction-service`, etc.). Esto permite que el gateway distribuya trafico entre instancias registradas.

Falta:

- Compose preparado para replicas.
- Eliminar `container_name`.
- Evitar puertos fijos por replica.
- Prueba documentada con `docker compose up --scale prediction-service=3`.
- Confirmar que los servicios son stateless. JWT ayuda, pero Redis/Rabbit/Postgres deben centralizar estado compartido.

Configuracion minima sugerida:

- Mantener solo `api-gateway` y `frontend/nginx` expuestos al host.
- Quitar `container_name` de servicios escalables.
- Usar `expose` en lugar de `ports` para microservicios internos.
- Ejecutar pruebas con 2 o 3 replicas de `prediction-service`, `scoring-service` y `leaderboard-service`.

## 7. Revision de pruebas

Pruebas encontradas:

- Tests tipo `contextLoads` en backend.
- No se encontraron specs propias en frontend fuera de `node_modules`.
- No se encontraron pruebas de stress.

Ejecucion parcial:

- `scoring-service ./mvnw test`: falla porque intenta conectar a PostgreSQL real.
- `prediction-service ./mvnw test`: falla por el mismo problema.
- `group-service ./mvnw test`: usa H2 pero falla por Mockito/ByteBuddy con Java 25.
- `world-cup-frontend npm run build`: no completo localmente con Node 25; el Dockerfile usa Node 22.

Faltan pruebas obligatorias:

- Unitarias de motor de puntuacion.
- Unitarias de bloqueo y duplicados de prediccion.
- Integracion del flujo sala-prediccion-resultado-score-ranking.
- E2E de usuario.
- Stress con usuarios concurrentes.

Stress testing minimo:

- 100 usuarios registrandose/login.
- 500 usuarios creando predicciones sobre 10 partidos.
- 1000 lecturas concurrentes de ranking.
- Calculo masivo al finalizar un partido con 10 000 predicciones.
- Medir p95, errores, throughput y uso de CPU/memoria.

## 8. Revision de base de datos

Entidades/tablas encontradas:

- Seguridad: `users`, `roles`, `email_verification_tokens`, `audit_logs`
- Organizacion: `campuses`, `departments`, `careers`, `avatars`, perfiles
- Mundial: `tournaments`, `stages`, `groups`, `teams`, `players`, `matches`, `match_events`
- Predicciones: `prediction_types`, `predictions`, `prediction_results`, `user_predictions_lock`
- Scoring: `scoring_rules`, `user_scores`, `score_history`
- Ranking: `leaderboards`, `leaderboard_entries`
- Salas: `rooms`, `room_members`, `room_invites`, `user_reports`, `room_bans`
- Social/gamificacion/notificaciones

Relaciones correctas:

- Usuarios con roles.
- Salas con miembros y restriccion unica `(room_id, user_id)`.
- Partidos con equipos.
- Predicciones con usuario, partido y tipo.

Relaciones faltantes o debiles:

- `predictions` no tiene `room_id`.
- `user_scores` no tiene `room_id`.
- `score_history` no guarda `match_id`, `prediction_id` ni `room_id`.
- `leaderboard_entries` no esta claramente ligado a sala.
- No hay constraint unico para `user_id + room_id + match_id`.
- `kickoff_time` usa timestamp sin zona.

## 9. Revision de API

Endpoints encontrados parcialmente:

- Auth: `POST /auth/register`, `POST /auth/login`
- Groups: `GET/POST/PUT/DELETE /groups`, join por codigo, members, invites, reports, bans
- Matches: `GET /matches/date/{date}`, `GET /matches/{id}`
- Predictions: `POST /predictions`, `PUT /predictions/{id}`, `GET /predictions/user/{userId}`, `GET /predictions/match/{matchId}`
- Scoring: `GET /scoring/user/{userId}`, `GET /scoring/user/{userId}/history`
- Leaderboard: global/campus/career/department
- Admin: matches/audit

Faltantes importantes:

- `GET /auth/me`
- `POST /auth/logout` si se usara invalidacion/token blacklist
- `GET /matches` paginado
- `PATCH /matches/{id}/result` sobre la tabla oficial de partidos
- `GET /rooms/{id}/ranking`
- `GET /rooms/{id}/predictions`
- `POST /scores/calculate/{matchId}` o proceso interno equivalente protegido
- Endpoints de estadisticas por usuario+sala.

Endpoints que deben protegerse:

- Admin completo.
- Groups excepto quizas unirse por codigo.
- Predicciones.
- Scoring por usuario.
- Ranking privado por sala.
- Reports/bans/invites.

Validaciones necesarias:

- `X-User-Id` no debe aceptarse sin validar JWT.
- Goles no negativos y enteros.
- Prediccion antes del cierre.
- Usuario miembro de sala.
- Duplicados por usuario+sala+partido.
- Roles para admin/owner.

## 10. Revision de frontend

Pantallas existentes:

- Landing
- Login, registro, verificacion de email
- Dashboard
- Matches: lista, detalle, vivo
- Predictions: crear, historial
- Rankings: global, campus, carrera, departamento
- Social: chat/reacciones
- Gamification: badges, achievements, rewards
- Admin: dashboard, matches, audit

Pantallas faltantes o incompletas:

- Gestion visible de salas/grupos.
- Ranking por sala.
- Invitaciones/codigo/enlace de sala.
- Historial de predicciones por sala.
- Validacion clara de cierre de prediccion.
- Perfil completo de usuario.

Problemas:

- `/app` no esta protegido con `AuthGuard`; solo `/admin`.
- Algunos servicios hacen fallback a datos mock, lo que puede ocultar fallos reales.
- Create prediction trabaja con tipos como marcador/ganador, pero no queda claro el formato estructurado del marcador.

Mejoras visuales:

- Mostrar estado de cierre: "abierto", "cierra en X", "cerrado".
- Mostrar reglas de puntos junto al formulario.
- Mostrar confirmacion de prediccion por sala.
- Mostrar leaderboard de sala como pantalla principal del grupo.

## 11. Riesgos principales

Alto:

- Puntuacion oficial no implementada de extremo a extremo.
- Predicciones sin `roomId`, sin bloqueo real y sin duplicados.
- Gateway permite rutas admin/groups sin autenticacion.
- No hay ranking por sala.
- Tests criticos no existen o fallan.

Medio:

- Docker no levanta todo el sistema con frontend.
- Escalamiento documentado pero no comprobado.
- Fechas sin zona horaria clara.
- OTP/email no implementado realmente.
- Fallbacks mock pueden dar falsa sensacion de funcionalidad.

Bajo:

- Documentacion extensa pero dispersa.
- Warnings de dependencias/JDK.
- Nginx existe pero no esta integrado.

## 12. Roadmap para terminar

| Fase | Objetivo | Tareas concretas | Prioridad | Responsable sugerido | Tiempo estimado | Resultado esperado |
|---|---|---|---|---|---|---|
| 1 | Correcciones criticas | Implementar `roomId` en predicciones; bloqueo por kickoff; constraint unico; proteger gateway; aplicar AuthGuard a `/app` | Alta | Backend + Frontend | 3-5 dias | Flujo seguro base |
| 1 | Motor de puntuacion | Crear motor jerarquico exacto/ganador/diferencia + bonus; guardar `score_history` por usuario+sala+partido | Alta | Backend | 3-4 dias | Puntaje correcto y auditable |
| 2 | Funcionalidades obligatorias | Pantallas de salas, invitacion, miembros, ranking por sala, historial por sala | Alta | Frontend + Backend | 4-6 dias | Producto usable por participantes |
| 2 | Integracion de resultados | Unificar `matches` y `manual_matches` o sincronizar resultado final hacia scoring | Alta | Backend | 2-3 dias | Calculo automatico al finalizar |
| 3 | Docker/despliegue | Agregar frontend y nginx al compose; `.env.example`; quitar `container_name` escalable | Media | DevOps | 2-3 dias | `docker compose up --build` real |
| 3 | Escalamiento | Probar replicas por gateway; documentar `--scale`; validar servicios stateless | Media | DevOps + Backend | 2 dias | Evidencia de balanceo |
| 4 | Pruebas | Unitarias scoring/prediction; integracion flujo completo; configurar H2/Testcontainers | Alta | QA + Backend | 4-6 dias | Tests automatizados confiables |
| 4 | Stress testing | k6 para login, predicciones, ranking y calculo masivo | Media | QA/DevOps | 2-3 dias | Reporte con metricas |
| 5 | Documentacion final | README unico, guia de ejecucion, endpoints, evidencias de pruebas, arquitectura actualizada | Alta | Lider tecnico | 2 dias | Entregable defendible |
| 6 | IA opcional | Resumen descriptivo de desempeno y tendencias sin prometer resultados reales | Baja | Backend/Frontend | 2-4 dias | Mejora opcional |

## 13. Checklist final para entrega

- [ ] `docker compose up --build` levanta backend, frontend, gateway, DB, Redis y RabbitMQ.
- [ ] Existe `.env.example` sin secretos.
- [ ] Login/registro/verificacion funcionan o el modo demo esta documentado.
- [ ] `/app` y `/admin` estan protegidos.
- [ ] Gateway valida JWT y roles.
- [ ] Usuario puede crear sala.
- [ ] Usuario puede unirse por codigo/invitacion.
- [ ] Se muestran miembros de la sala.
- [ ] Usuario puede crear/editar prediccion antes del cierre.
- [ ] No puede predecir despues del kickoff.
- [ ] No puede duplicar prediccion en la misma sala y partido.
- [ ] Se calcula exacto=5, ganador/empate=3, diferencia=2.
- [ ] Bonus racha +2 por cada 3 aciertos consecutivos por usuario+sala.
- [ ] Bonus anticipacion +1 si predijo mas de 24h antes.
- [ ] Ranking por sala ordena puntajes acumulados.
- [ ] Historial muestra puntos por partido.
- [ ] Hay pruebas unitarias para scoring.
- [ ] Hay pruebas de integracion del flujo principal.
- [ ] Hay reporte de stress testing.
- [ ] La documentacion final explica arquitectura, uso, endpoints y despliegue.

## 14. Conclusion

Estado del proyecto: incompleto para una entrega final exigente.

No esta en cero: tiene arquitectura, servicios, entidades, pantallas, Dockerfiles y documentacion. Pero los requisitos centrales de la aplicacion educativa de predicciones todavia no estan garantizados. Para presentarlo con seguridad, primero deben cerrarse scoring, predicciones por sala, ranking por sala, seguridad del gateway y pruebas.

## 15. Diez acciones mas importantes

1. Implementar `roomId` en `Prediction`, `ScoreHistory`, `UserScore` o modelo equivalente.
2. Crear constraint unico `user_id + room_id + match_id`.
3. Implementar bloqueo real por `kickoffTime` y ventana de cierre.
4. Crear motor de puntuacion jerarquico: exacto 5, ganador/empate 3, diferencia 2.
5. Implementar bonus +1 por anticipacion mayor a 24 horas.
6. Implementar racha +2 por cada 3 aciertos consecutivos por usuario y sala.
7. Crear ranking por sala y endpoint `GET /groups/{roomId}/ranking`.
8. Proteger gateway y servicios: no confiar en `X-User-Id` sin JWT.
9. Agregar pruebas unitarias/integracion para predicciones y scoring.
10. Completar Docker Compose con frontend, nginx opcional, `.env.example` y evidencia de stress testing.
