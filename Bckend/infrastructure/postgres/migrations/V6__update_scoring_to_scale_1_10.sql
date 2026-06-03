-- ============================================
-- Tecsup World Cup Challenge 2026
-- Migración V6: Actualizar sistema de puntuación
-- Nueva escala: 1 a 10 (más simple y balanceada)
-- ============================================

-- Actualizar puntos de tipos de predicción (escala 1-10)
UPDATE prediction_types SET points = 3  WHERE code = 'MATCH_WINNER';   -- Acertar ganador: 3 pts
UPDATE prediction_types SET points = 5  WHERE code = 'EXACT_SCORE';    -- Marcador exacto: 5 pts
UPDATE prediction_types SET points = 7  WHERE code = 'TOP_SCORER';     -- Goleador: 7 pts
UPDATE prediction_types SET points = 4  WHERE code = 'GROUP_WINNER';   -- Ganador de grupo: 4 pts
UPDATE prediction_types SET points = 10 WHERE code = 'CHAMPION';       -- Campeón: 10 pts

-- Actualizar reglas de puntuación (scoring_rules)
UPDATE scoring_rules SET points = 3  WHERE prediction_type_id = (SELECT id FROM prediction_types WHERE code = 'MATCH_WINNER');
UPDATE scoring_rules SET points = 5  WHERE prediction_type_id = (SELECT id FROM prediction_types WHERE code = 'EXACT_SCORE');
UPDATE scoring_rules SET points = 7  WHERE prediction_type_id = (SELECT id FROM prediction_types WHERE code = 'TOP_SCORER');
UPDATE scoring_rules SET points = 4  WHERE prediction_type_id = (SELECT id FROM prediction_types WHERE code = 'GROUP_WINNER');
UPDATE scoring_rules SET points = 10 WHERE prediction_type_id = (SELECT id FROM prediction_types WHERE code = 'CHAMPION');

-- Actualizar reglas de logros (achievement_rules) - bajar umbrales para escala 1-10
UPDATE achievement_rules SET required_points = 10  WHERE badge_id = (SELECT id FROM badges WHERE name = 'Experto');
UPDATE achievement_rules SET required_points = 25  WHERE badge_id = (SELECT id FROM badges WHERE name = 'Leyenda');
