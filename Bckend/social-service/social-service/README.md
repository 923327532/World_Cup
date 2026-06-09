# Social Service - API Endpoints

## Base URL
```
/comments
```

## CommentController

### Crear comentario
```
POST /comments
```
- **Headers:** `X-User-Id`
- **Body:** `CreateCommentRequest` (matchId, content)
- **Response:** `CommentDTO`

### Obtener comentarios de un partido
```
GET /comments/match/{matchId}
```
- **Response:** `List<CommentDTO>`

---

## Base URL
```
/reactions
```

## ReactionController

### Agregar reacción a un comentario
```
POST /reactions
```
- **Headers:** `X-User-Id`
- **Body:** `CreateReactionRequest` (commentId, reaction)
- **Response:** 200 OK

### Eliminar reacción de un comentario
```
DELETE /reactions/{commentId}
```
- **Headers:** `X-User-Id`
- **Response:** 200 OK
