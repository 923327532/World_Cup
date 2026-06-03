-- ============================================
-- Tecsup World Cup Challenge 2026
-- Migración V5: Seed data for World Cup 2026 matches
-- Data from readmi.final.md - MANUAL source type
-- ============================================

-- Insert tournament
INSERT INTO tournaments (name, year, start_date, end_date)
SELECT 'FIFA World Cup 2026', 2026, '2026-06-11', '2026-07-19'
WHERE NOT EXISTS (SELECT 1 FROM tournaments WHERE year = 2026);

-- Insert stages
INSERT INTO stages (tournament_id, name)
SELECT t.id, s.name
FROM tournaments t
CROSS JOIN (
    VALUES ('Group Stage'), ('Round of 32'), ('Round of 16'), ('Quarter-finals'), ('Semi-finals'), ('Third Place'), ('Final')
) AS s(name)
WHERE t.year = 2026
AND NOT EXISTS (SELECT 1 FROM stages st WHERE st.tournament_id = t.id AND st.name = s.name);

-- Insert groups (A through L)
INSERT INTO "groups" (tournament_id, name)
SELECT t.id, g.name
FROM tournaments t
CROSS JOIN (
    VALUES ('A'), ('B'), ('C'), ('D'), ('E'), ('F'), ('G'), ('H'), ('I'), ('J'), ('K'), ('L')
) AS g(name)
WHERE t.year = 2026
AND NOT EXISTS (SELECT 1 FROM "groups" gr WHERE gr.tournament_id = t.id AND gr.name = g.name);

-- Insert teams
INSERT INTO teams (name, code) VALUES
('Mexico', 'MEX'),
('South Africa', 'RSA'),
('South Korea', 'KOR'),
('Czech Republic', 'CZE'),
('Canada', 'CAN'),
('Bosnia and Herzegovina', 'BIH'),
('Qatar', 'QAT'),
('Switzerland', 'SUI'),
('Brazil', 'BRA'),
('Morocco', 'MAR'),
('Haiti', 'HAI'),
('Scotland', 'SCO'),
('United States', 'USA'),
('Paraguay', 'PAR'),
('Australia', 'AUS'),
('Turkey', 'TUR'),
('Germany', 'GER'),
('Curacao', 'CUR'),
('Ivory Coast', 'CIV'),
('Ecuador', 'ECU'),
('Netherlands', 'NED'),
('Japan', 'JPN'),
('Sweden', 'SWE'),
('Tunisia', 'TUN'),
('Belgium', 'BEL'),
('Egypt', 'EGY'),
('Iran', 'IRN'),
('New Zealand', 'NZL'),
('Spain', 'ESP'),
('Cape Verde', 'CPV'),
('Saudi Arabia', 'KSA'),
('Uruguay', 'URU'),
('France', 'FRA'),
('Senegal', 'SEN'),
('Iraq', 'IRQ'),
('Norway', 'NOR'),
('Argentina', 'ARG'),
('Algeria', 'ALG'),
('Austria', 'AUT'),
('Jordan', 'JOR'),
('Portugal', 'POR'),
('DR Congo', 'COD'),
('Uzbekistan', 'UZB'),
('Colombia', 'COL'),
('England', 'ENG'),
('Croatia', 'CRO'),
('Ghana', 'GHA'),
('Panama', 'PAN')
ON CONFLICT DO NOTHING;

-- ============================================
-- FASE DE GRUPOS (72 partidos)
-- ============================================
INSERT INTO manual_matches (home_team, away_team, start_time, status, source_type, created_by, venue, stage, group_name, home_score, away_score)
VALUES

-- Group A
('Mexico', 'South Africa', '2026-06-11 15:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Estadio Ciudad de Mexico', 'Group Stage', 'A', 0, 0),
('South Korea', 'Czech Republic', '2026-06-11 22:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Estadio Guadalajara', 'Group Stage', 'A', 0, 0),
('Czech Republic', 'South Africa', '2026-06-18 12:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Atlanta', 'Group Stage', 'A', 0, 0),
('Mexico', 'South Korea', '2026-06-18 21:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Estadio Guadalajara', 'Group Stage', 'A', 0, 0),
('Czech Republic', 'Mexico', '2026-06-24 21:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Estadio Ciudad de Mexico', 'Group Stage', 'A', 0, 0),
('South Africa', 'South Korea', '2026-06-24 21:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Monterrey', 'Group Stage', 'A', 0, 0),

-- Group B
('Canada', 'Bosnia and Herzegovina', '2026-06-12 15:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Toronto', 'Group Stage', 'B', 0, 0),
('Qatar', 'Switzerland', '2026-06-13 15:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Bahia de San Francisco', 'Group Stage', 'B', 0, 0),
('Switzerland', 'Bosnia and Herzegovina', '2026-06-18 15:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Los Angeles', 'Group Stage', 'B', 0, 0),
('Canada', 'Qatar', '2026-06-18 18:00:00', 'SCHEDULED', 'MANUAL', 'system', 'BC Place Vancouver', 'Group Stage', 'B', 0, 0),
('Switzerland', 'Canada', '2026-06-24 15:00:00', 'SCHEDULED', 'MANUAL', 'system', 'BC Place Vancouver', 'Group Stage', 'B', 0, 0),
('Bosnia and Herzegovina', 'Qatar', '2026-06-24 15:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Seattle', 'Group Stage', 'B', 0, 0),

-- Group C
('Brazil', 'Morocco', '2026-06-13 18:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Nueva York Nueva Jersey', 'Group Stage', 'C', 0, 0),
('Haiti', 'Scotland', '2026-06-13 21:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Boston', 'Group Stage', 'C', 0, 0),
('Scotland', 'Morocco', '2026-06-19 18:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Boston', 'Group Stage', 'C', 0, 0),
('Brazil', 'Haiti', '2026-06-19 21:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Filadelfia', 'Group Stage', 'C', 0, 0),
('Scotland', 'Brazil', '2026-06-24 18:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Miami', 'Group Stage', 'C', 0, 0),
('Morocco', 'Haiti', '2026-06-24 18:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Atlanta', 'Group Stage', 'C', 0, 0),

-- Group D
('United States', 'Paraguay', '2026-06-12 21:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Los Angeles', 'Group Stage', 'D', 0, 0),
('Australia', 'Turkey', '2026-06-14 00:00:00', 'SCHEDULED', 'MANUAL', 'system', 'BC Place Vancouver', 'Group Stage', 'D', 0, 0),
('United States', 'Australia', '2026-06-19 15:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Seattle', 'Group Stage', 'D', 0, 0),
('Turkey', 'Paraguay', '2026-06-20 00:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Bahia de San Francisco', 'Group Stage', 'D', 0, 0),
('Turkey', 'United States', '2026-06-25 22:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Los Angeles', 'Group Stage', 'D', 0, 0),
('Paraguay', 'Australia', '2026-06-25 22:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Bahia de San Francisco', 'Group Stage', 'D', 0, 0),

-- Group E
('Germany', 'Curacao', '2026-06-14 13:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Houston', 'Group Stage', 'E', 0, 0),
('Ivory Coast', 'Ecuador', '2026-06-14 19:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Filadelfia', 'Group Stage', 'E', 0, 0),
('Germany', 'Ivory Coast', '2026-06-20 16:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Toronto', 'Group Stage', 'E', 0, 0),
('Ecuador', 'Curacao', '2026-06-20 22:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Kansas City', 'Group Stage', 'E', 0, 0),
('Curacao', 'Ivory Coast', '2026-06-25 16:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Filadelfia', 'Group Stage', 'E', 0, 0),
('Ecuador', 'Germany', '2026-06-25 16:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Nueva York Nueva Jersey', 'Group Stage', 'E', 0, 0),

-- Group F
('Netherlands', 'Japan', '2026-06-14 16:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Dallas', 'Group Stage', 'F', 0, 0),
('Sweden', 'Tunisia', '2026-06-14 22:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Monterrey', 'Group Stage', 'F', 0, 0),
('Netherlands', 'Sweden', '2026-06-20 13:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Houston', 'Group Stage', 'F', 0, 0),
('Tunisia', 'Japan', '2026-06-21 00:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Monterrey', 'Group Stage', 'F', 0, 0),
('Japan', 'Sweden', '2026-06-25 19:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Dallas', 'Group Stage', 'F', 0, 0),
('Tunisia', 'Netherlands', '2026-06-25 19:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Kansas City', 'Group Stage', 'F', 0, 0),

-- Group G
('Belgium', 'Egypt', '2026-06-15 15:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Seattle', 'Group Stage', 'G', 0, 0),
('Iran', 'New Zealand', '2026-06-15 21:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Los Angeles', 'Group Stage', 'G', 0, 0),
('Belgium', 'Iran', '2026-06-21 15:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Los Angeles', 'Group Stage', 'G', 0, 0),
('New Zealand', 'Egypt', '2026-06-21 21:00:00', 'SCHEDULED', 'MANUAL', 'system', 'BC Place Vancouver', 'Group Stage', 'G', 0, 0),
('Egypt', 'Iran', '2026-06-26 23:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Seattle', 'Group Stage', 'G', 0, 0),
('New Zealand', 'Belgium', '2026-06-26 23:00:00', 'SCHEDULED', 'MANUAL', 'system', 'BC Place Vancouver', 'Group Stage', 'G', 0, 0),

-- Group H
('Spain', 'Cape Verde', '2026-06-15 12:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Atlanta', 'Group Stage', 'H', 0, 0),
('Saudi Arabia', 'Uruguay', '2026-06-15 18:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Miami', 'Group Stage', 'H', 0, 0),
('Spain', 'Saudi Arabia', '2026-06-21 12:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Atlanta', 'Group Stage', 'H', 0, 0),
('Uruguay', 'Cape Verde', '2026-06-21 18:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Miami', 'Group Stage', 'H', 0, 0),
('Cape Verde', 'Saudi Arabia', '2026-06-26 20:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Houston', 'Group Stage', 'H', 0, 0),
('Uruguay', 'Spain', '2026-06-26 20:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Guadalajara', 'Group Stage', 'H', 0, 0),

-- Group I
('France', 'Senegal', '2026-06-16 15:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Nueva York Nueva Jersey', 'Group Stage', 'I', 0, 0),
('Iraq', 'Norway', '2026-06-16 18:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Boston', 'Group Stage', 'I', 0, 0),
('France', 'Iraq', '2026-06-22 17:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Filadelfia', 'Group Stage', 'I', 0, 0),
('Norway', 'Senegal', '2026-06-22 20:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Nueva York Nueva Jersey', 'Group Stage', 'I', 0, 0),
('Norway', 'France', '2026-06-26 15:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Boston', 'Group Stage', 'I', 0, 0),
('Senegal', 'Iraq', '2026-06-26 15:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Toronto', 'Group Stage', 'I', 0, 0),

-- Group J
('Argentina', 'Algeria', '2026-06-16 21:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Kansas City', 'Group Stage', 'J', 0, 0),
('Austria', 'Jordan', '2026-06-17 00:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Bahia de San Francisco', 'Group Stage', 'J', 0, 0),
('Argentina', 'Austria', '2026-06-22 13:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Dallas', 'Group Stage', 'J', 0, 0),
('Jordan', 'Algeria', '2026-06-22 23:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Bahia de San Francisco', 'Group Stage', 'J', 0, 0),
('Algeria', 'Austria', '2026-06-27 22:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Kansas City', 'Group Stage', 'J', 0, 0),
('Jordan', 'Argentina', '2026-06-27 22:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Dallas', 'Group Stage', 'J', 0, 0),

-- Group K
('Portugal', 'DR Congo', '2026-06-17 13:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Houston', 'Group Stage', 'K', 0, 0),
('Uzbekistan', 'Colombia', '2026-06-17 22:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Ciudad de Mexico', 'Group Stage', 'K', 0, 0),
('Portugal', 'Uzbekistan', '2026-06-23 13:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Houston', 'Group Stage', 'K', 0, 0),
('Colombia', 'DR Congo', '2026-06-23 22:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Guadalajara', 'Group Stage', 'K', 0, 0),
('Colombia', 'Portugal', '2026-06-27 19:30:00', 'SCHEDULED', 'MANUAL', 'system', 'Miami', 'Group Stage', 'K', 0, 0),
('DR Congo', 'Uzbekistan', '2026-06-27 19:30:00', 'SCHEDULED', 'MANUAL', 'system', 'Atlanta', 'Group Stage', 'K', 0, 0),

-- Group L
('England', 'Croatia', '2026-06-17 16:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Dallas', 'Group Stage', 'L', 0, 0),
('Ghana', 'Panama', '2026-06-17 19:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Toronto', 'Group Stage', 'L', 0, 0),
('England', 'Ghana', '2026-06-23 16:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Boston', 'Group Stage', 'L', 0, 0),
('Panama', 'Croatia', '2026-06-23 19:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Toronto', 'Group Stage', 'L', 0, 0),
('Panama', 'England', '2026-06-27 17:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Nueva York Nueva Jersey', 'Group Stage', 'L', 0, 0),
('Croatia', 'Ghana', '2026-06-27 17:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Filadelfia', 'Group Stage', 'L', 0, 0);

-- ============================================
-- DIECISEISAVOS DE FINAL (Round of 32)
-- P73 a P88 - Fechas y estadios definidos
-- Los enfrentamientos (TBD) los actualiza el admin
-- cuando terminan los grupos
-- ============================================
INSERT INTO manual_matches (home_team, away_team, start_time, status, source_type, created_by, venue, stage, group_name, home_score, away_score)
VALUES
('TBD', 'TBD', '2026-06-28 14:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Estadio Los Angeles', 'Round of 32', NULL, 0, 0),
('TBD', 'TBD', '2026-06-29 12:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Houston', 'Round of 32', NULL, 0, 0),
('TBD', 'TBD', '2026-06-29 15:30:00', 'SCHEDULED', 'MANUAL', 'system', 'Boston', 'Round of 32', NULL, 0, 0),
('TBD', 'TBD', '2026-06-29 20:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Monterrey', 'Round of 32', NULL, 0, 0),
('TBD', 'TBD', '2026-06-30 12:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Dallas', 'Round of 32', NULL, 0, 0),
('TBD', 'TBD', '2026-06-30 16:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Nueva York Nueva Jersey', 'Round of 32', NULL, 0, 0),
('TBD', 'TBD', '2026-06-30 20:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Estadio Ciudad de Mexico', 'Round of 32', NULL, 0, 0),
('TBD', 'TBD', '2026-07-01 11:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Atlanta', 'Round of 32', NULL, 0, 0),
('TBD', 'TBD', '2026-07-01 15:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Seattle', 'Round of 32', NULL, 0, 0),
('TBD', 'TBD', '2026-07-01 19:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Bahia de San Francisco', 'Round of 32', NULL, 0, 0),
('TBD', 'TBD', '2026-07-02 14:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Estadio Los Angeles', 'Round of 32', NULL, 0, 0),
('TBD', 'TBD', '2026-07-02 18:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Toronto', 'Round of 32', NULL, 0, 0),
('TBD', 'TBD', '2026-07-02 22:00:00', 'SCHEDULED', 'MANUAL', 'system', 'BC Place Vancouver', 'Round of 32', NULL, 0, 0),
('TBD', 'TBD', '2026-07-03 13:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Dallas', 'Round of 32', NULL, 0, 0),
('TBD', 'TBD', '2026-07-03 17:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Miami', 'Round of 32', NULL, 0, 0),
('TBD', 'TBD', '2026-07-03 20:30:00', 'SCHEDULED', 'MANUAL', 'system', 'Kansas City', 'Round of 32', NULL, 0, 0);

-- ============================================
-- OCTAVOS DE FINAL (Round of 16)
-- P89 a P96 - Fechas y estadios definidos
-- Los enfrentamientos los actualiza el admin
-- cuando terminan los dieciseisavos
-- ============================================
INSERT INTO manual_matches (home_team, away_team, start_time, status, source_type, created_by, venue, stage, group_name, home_score, away_score)
VALUES
('TBD', 'TBD', '2026-07-04 12:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Houston', 'Round of 16', NULL, 0, 0),
('TBD', 'TBD', '2026-07-04 16:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Filadelfia', 'Round of 16', NULL, 0, 0),
('TBD', 'TBD', '2026-07-05 15:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Nueva York Nueva Jersey', 'Round of 16', NULL, 0, 0),
('TBD', 'TBD', '2026-07-05 19:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Estadio Ciudad de Mexico', 'Round of 16', NULL, 0, 0),
('TBD', 'TBD', '2026-07-06 14:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Dallas', 'Round of 16', NULL, 0, 0),
('TBD', 'TBD', '2026-07-06 19:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Seattle', 'Round of 16', NULL, 0, 0),
('TBD', 'TBD', '2026-07-07 11:00:00', 'SCHEDULED', 'MANUAL', 'system', 'Atlanta', 'Round of 16', NULL, 0, 0),
('TBD', 'TBD', '2026-07-07 15:00:00', 'SCHEDULED', 'MANUAL', 'system', 'BC Place Vancouver', 'Round of 16', NULL, 0, 0);

-- ============================================
-- CUARTOS DE FINAL (Quarter-finals)
-- P97 a P100 - Fechas definidas
-- Los enfrentamientos los actualiza el admin
-- cuando terminan los octavos
-- ============================================
INSERT INTO manual_matches (home_team, away_team, start_time, status, source_type, created_by, venue, stage, group_name, home_score, away_score)
VALUES
('TBD', 'TBD', '2026-07-09 15:00:00', 'SCHEDULED', 'MANUAL', 'system', 'TBD', 'Quarter-finals', NULL, 0, 0),
('TBD', 'TBD', '2026-07-10 14:00:00', 'SCHEDULED', 'MANUAL', 'system', 'TBD', 'Quarter-finals', NULL, 0, 0),
('TBD', 'TBD', '2026-07-11 16:00:00', 'SCHEDULED', 'MANUAL', 'system', 'TBD', 'Quarter-finals', NULL, 0, 0),
('TBD', 'TBD', '2026-07-11 20:00:00', 'SCHEDULED', 'MANUAL', 'system', 'TBD', 'Quarter-finals', NULL, 0, 0);

-- ============================================
-- SEMIFINALES (Semi-finals)
-- P101 y P102 - Fechas definidas
-- ============================================
INSERT INTO manual_matches (home_team, away_team, start_time, status, source_type, created_by, venue, stage, group_name, home_score, away_score)
VALUES
('TBD', 'TBD', '2026-07-14 14:00:00', 'SCHEDULED', 'MANUAL', 'system', 'TBD', 'Semi-finals', NULL, 0, 0),
('TBD', 'TBD', '2026-07-15 14:00:00', 'SCHEDULED', 'MANUAL', 'system', 'TBD', 'Semi-finals', NULL, 0, 0);

-- ============================================
-- TERCER PUESTO (Third Place)
-- P103
-- ============================================
INSERT INTO manual_matches (home_team, away_team, start_time, status, source_type, created_by, venue, stage, group_name, home_score, away_score)
VALUES
('TBD', 'TBD', '2026-07-18 16:00:00', 'SCHEDULED', 'MANUAL', 'system', 'TBD', 'Third Place', NULL, 0, 0);

-- ============================================
-- FINAL
-- P104
-- ============================================
INSERT INTO manual_matches (home_team, away_team, start_time, status, source_type, created_by, venue, stage, group_name, home_score, away_score)
VALUES
('TBD', 'TBD', '2026-07-19 14:00:00', 'SCHEDULED', 'MANUAL', 'system', 'TBD', 'Final', NULL, 0, 0);
