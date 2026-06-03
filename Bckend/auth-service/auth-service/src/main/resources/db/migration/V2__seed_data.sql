-- ============================================
-- Tecsup World Cup Challenge 2026
-- Migración V2: Datos semilla
-- ============================================

-- Roles
INSERT INTO roles (name, description) VALUES
('ADMIN', 'Administrador del sistema'),
('STUDENT', 'Estudiante de Tecsup'),
('TEACHER', 'Docente de Tecsup');

-- Avatars por defecto
INSERT INTO avatars (name, image_url) VALUES
('Default Avatar', '/avatars/default.png');

-- Campuses
INSERT INTO campuses (name) VALUES
('Lima - Campus Principal'),
('Arequipa'),
('Huancayo'),
('Trujillo'),
('Cusco'),
('Chiclayo'),
('Iquitos'),
('Tacna');

-- Departments
INSERT INTO departments (name) VALUES
('Ingeniería de Sistemas'),
('Ingeniería de Software'),
('Ingeniería Mecánica'),
('Ingeniería Eléctrica'),
('Ingeniería Electrónica'),
('Ingeniería Industrial'),
('Ingeniería Química'),
('Ingeniería Ambiental'),
('Ingeniería Civil'),
('Administración de Empresas');

-- Careers
INSERT INTO careers (department_id, name) VALUES
(1, 'Ingeniería de Sistemas'),
(2, 'Ingeniería de Software'),
(3, 'Ingeniería Mecánica'),
(4, 'Ingeniería Eléctrica'),
(5, 'Ingeniería Electrónica'),
(6, 'Ingeniería Industrial'),
(7, 'Ingeniería Química'),
(8, 'Ingeniería Ambiental'),
(9, 'Ingeniería Civil'),
(10, 'Administración de Empresas');

-- Prediction Types
INSERT INTO prediction_types (code, name, points) VALUES
('MATCH_WINNER', 'Ganador del Partido', 10),
('EXACT_SCORE', 'Marcador Exacto', 30),
('TOP_SCORER', 'Goleador del Torneo', 50),
('GROUP_WINNER', 'Ganador de Grupo', 20),
('CHAMPION', 'Campeón del Mundial', 100);

-- Leaderboards
INSERT INTO leaderboards (name, type) VALUES
('Ranking General', 'GLOBAL'),
('Ranking por Campus', 'CAMPUS'),
('Ranking por Carrera', 'CAREER'),
('Ranking por Departamento', 'DEPARTMENT');

-- Badges
INSERT INTO badges (name, description, icon) VALUES
('Principiante', 'Realiza tu primera predicción', '/badges/beginner.png'),
('Acierto Perfecto', 'Acierta un marcador exacto', '/badges/perfect.png'),
('Racha de 3', 'Acierta 3 predicciones consecutivas', '/badges/streak3.png'),
('Racha de 5', 'Acierta 5 predicciones consecutivas', '/badges/streak5.png'),
('Racha de 10', 'Acierta 10 predicciones consecutivas', '/badges/streak10.png'),
('Top 10', 'Estar en el top 10 del ranking general', '/badges/top10.png'),
('Top 3', 'Estar en el top 3 del ranking general', '/badges/top3.png'),
('Número 1', 'Ser el número 1 del ranking general', '/badges/number1.png'),
('Experto', 'Realiza 50 predicciones', '/badges/expert.png'),
('Leyenda', 'Realiza 100 predicciones', '/badges/legend.png');

-- Rewards
INSERT INTO rewards (position, title, description) VALUES
(1, 'Campeón Tecsup World Cup', 'Primer puesto del ranking general'),
(2, 'Subcampeón Tecsup World Cup', 'Segundo puesto del ranking general'),
(3, 'Tercer puesto Tecsup World Cup', 'Tercer puesto del ranking general');

-- Achievement Rules
INSERT INTO achievement_rules (badge_id, required_points, required_streak, required_position) VALUES
(1, 0, 0, NULL),
(2, 0, 0, NULL),
(3, 0, 3, NULL),
(4, 0, 5, NULL),
(5, 0, 10, NULL),
(6, 0, 0, 10),
(7, 0, 0, 3),
(8, 0, 0, 1),
(9, 500, 0, NULL),
(10, 1000, 0, NULL);

-- Scoring Rules
INSERT INTO scoring_rules (prediction_type_id, points) VALUES
(1, 10),
(2, 30),
(3, 50),
(4, 20),
(5, 100);
