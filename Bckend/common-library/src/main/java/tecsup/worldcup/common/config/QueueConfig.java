package tecsup.worldcup.common.config;

public class QueueConfig {

    private QueueConfig() {}

    // Colas de Predicciones
    public static final String PREDICTION_CREATED_QUEUE = "prediction.created";
    public static final String PREDICTION_SCORED_QUEUE = "prediction.scored";

    // Colas de Partidos
    public static final String MATCH_FINISHED_QUEUE = "match.finished";
    public static final String MATCH_GOAL_QUEUE = "match.goal";

    // Colas de Rankings
    public static final String LEADERBOARD_UPDATED_QUEUE = "leaderboard.updated";

    // Colas de Gamificación
    public static final String BADGE_UNLOCKED_QUEUE = "badge.unlocked";

    // Colas de Notificaciones
    public static final String NOTIFICATION_QUEUE = "notification.send";

    // Intercambios (Exchanges)
    public static final String PREDICTION_EXCHANGE = "prediction.exchange";
    public static final String MATCH_EXCHANGE = "match.exchange";
    public static final String RANKING_EXCHANGE = "ranking.exchange";
    public static final String GAMIFICATION_EXCHANGE = "gamification.exchange";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";

    // Routing Keys
    public static final String PREDICTION_CREATED_ROUTING_KEY = "prediction.created";
    public static final String PREDICTION_SCORED_ROUTING_KEY = "prediction.scored";
    public static final String MATCH_FINISHED_ROUTING_KEY = "match.finished";
    public static final String BADGE_UNLOCKED_ROUTING_KEY = "badge.unlocked";
    public static final String NOTIFICATION_SEND_ROUTING_KEY = "notification.send";
}