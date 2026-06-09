# Prediction Service - API Endpoints

## Base URL
```
/predictions
```

## PredictionController

### Crear predicción
```
POST /predictions
```
- **Headers:** `X-User-Id`
- **Body:** `CreatePredictionRequest` (matchId, predictionTypeId, predictionValue)
- **Response:** `PredictionDTO`

### Actualizar predicción
```
PUT /predictions/{id}
```
- **Headers:** `X-User-Id`
- **Body:** `String newValue`
- **Response:** `PredictionDTO`

### Obtener predicciones de un usuario
```
GET /predictions/user/{userId}
```
- **Response:** `List<PredictionDTO>`

### Obtener predicciones de un partido
```
GET /predictions/match/{matchId}
```
- **Response:** `List<PredictionDTO>`

---

## Base URL
```
/prediction-types
```

## PredictionTypeController

### Obtener todos los tipos de predicción
```
GET /prediction-types
```
- **Response:** `List<PredictionTypeDTO>`
