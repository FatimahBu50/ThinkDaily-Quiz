-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 10, 2025 at 02:24 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

USE thinkdaily;


SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `thinkdaily`
--

-- --------------------------------------------------------

--
-- Table structure for table `accounts`
--

create TABLE `accounts` (
  `account_id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` enum('PLAYER','ADMIN') NOT NULL,
  `security_question1` varchar(255) NOT NULL,
  `security_ans1` varchar(255) NOT NULL,
  `security_question2` varchar(255) NOT NULL,
  `security_ans2` varchar(255) NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `accounts`
--

INSERT INTO `accounts`
(`account_id`, `username`, `password_hash`, `role`,
 `security_question1`, `security_ans1`,
 `security_question2`, `security_ans2`, `is_active`)
VALUES
(1, 'admin_1',
 SHA2('passK1', 256),
 'ADMIN',
 'What is your favorite color?', 'red',
 'What is your pet''s name?', 'cat',
 1),

(2, 'admin_2',
 SHA2('passK2', 256),
 'ADMIN',
 'What is your favorite color?', 'blue',
 'What city were you born in?', 'dammam',
 1),

(3, 'admin_3',
 SHA2('passK3', 256),
 'ADMIN',
 'What is your favorite color?', 'green',
 'What is your mother''s name?', 'fatimah',
 1);



-- --------------------------------------------------------

--
-- Table structure for table `admins`
--

CREATE TABLE `admins` (
  `admin_id` int(11) NOT NULL,
  `account_id` int(11) NOT NULL,
  `full_name` varchar(80) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Table structure for table `answers`
--

CREATE TABLE `answers` (
  `answer_id` int(11) NOT NULL,
  `question_id` int(11) NOT NULL,
  `answer_text` text NOT NULL,
  `is_correct` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `answers`
--

INSERT INTO `answers` (`answer_id`, `question_id`, `answer_text`, `is_correct`) VALUES
(1, 1, '24', 1),
(2, 1, '28', 0),
(3, 1, '30', 0),
(4, 2, 'H2O', 1),
(5, 2, 'O2', 0),
(6, 2, 'H2', 0);

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `category_id` int(11) NOT NULL,
  `category_name` varchar(60) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`category_id`, `category_name`) VALUES
(1, 'Math'),
(2, 'Science'),(3,'Random'),(4,'History'),(5,'Technology');

-- --------------------------------------------------------

--
-- Table structure for table `extended_sessions`
--

CREATE TABLE `extended_sessions` (
  `session_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `category_id` int(11) DEFAULT NULL,
  `difficulty` enum('EASY','MEDIUM','HARD') NOT NULL,
  `requested_question_count` int(11) NOT NULL,
  `duration` int(11) NOT NULL,
  `total_points_earned` int(11) NOT NULL DEFAULT 0,
  `was_completed` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `extended_sessions`
--

INSERT INTO `extended_sessions` (`session_id`, `user_id`, `category_id`, `difficulty`, `requested_question_count`, `duration`, `total_points_earned`, `was_completed`) VALUES
(1, 1, 1, 'MEDIUM', 5, 0, 0, 0),
(2, 2, 2, 'MEDIUM', 5, 4, 10, 1),
(3, 2, NULL, 'MEDIUM', 3, 0, 0, 0),
(4, 2, NULL, 'MEDIUM', 5, 0, 0, 0),
(5, 2, NULL, 'MEDIUM', 5, 0, 0, 0),
(6, 2, 2, 'MEDIUM', 5, 0, 0, 0),
(7, 2, NULL, 'MEDIUM', 4, 0, 0, 0),
(8, 2, 2, 'MEDIUM', 6, 0, 0, 0),
(9, 2, 2, 'MEDIUM', 5, 11, 10, 1);

-- --------------------------------------------------------

--
-- Table structure for table `leaderboard_all_time`
--

CREATE TABLE `leaderboard_all_time` (
  `user_id` int(11) NOT NULL,
  `points_all_time` int(11) NOT NULL,
  `current_level` int(11) NOT NULL,
  `rank_position` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `leaderboard_daily`
--

CREATE TABLE `leaderboard_daily` (
  `user_id` int(11) NOT NULL,
  `points_today` int(11) NOT NULL,
  `rank_position` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `level_rules`
--

CREATE TABLE `level_rules` (
  `level_number` int(11) NOT NULL,
  `min_points_required` int(11) NOT NULL,
  `max_points_exclusive` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `questions`
--

CREATE TABLE `questions` (
  `question_id` int(11) NOT NULL,
  `category_id` int(11) NOT NULL,
  `question_text` text NOT NULL,
  `difficulty` enum('EASY','MEDIUM','HARD') NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `times_answered` int(11) NOT NULL DEFAULT 0,
  `times_answered_correctly` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `questions`
--

INSERT INTO `questions` (`question_id`, `category_id`, `question_text`, `difficulty`, `is_active`, `times_answered`, `times_answered_correctly`) VALUES
(1, 1, '1.What is 15 + 9?', 'HARD', 1, 0, 0),
(2, 2, 'What is the chemical symbol for water?', 'EASY', 1, 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `quiz_config`
--

CREATE TABLE `quiz_config` (
  `config_id` int(11) NOT NULL,
  `daily_question_count` int(11) NOT NULL DEFAULT 3,
  `daily_countdown_hours` int(11) NOT NULL DEFAULT 24,
  `extended_min_questions` int(11) NOT NULL DEFAULT 3,
  `extended_max_questions` int(11) NOT NULL DEFAULT 20
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `account_id` int(11) NOT NULL,
  `user_name` varchar(60) NOT NULL,
  `bio` varchar(200) DEFAULT NULL,
  `current_level` int(11) NOT NULL DEFAULT 1,
  `current_points_total` int(11) NOT NULL DEFAULT 0,
  `current_icon_path` varchar(120) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;



-- --------------------------------------------------------

--
-- Table structure for table `user_daily_stats`
--

CREATE TABLE `user_daily_stats` (
  `user_id` int(11) NOT NULL,
  `quiz_date` date NOT NULL,
  `daily_category_id` int(11) NOT NULL,
  `is_completed` tinyint(1) NOT NULL,
  `points_earned_today` int(11) NOT NULL DEFAULT 0,
  `next_quiz_available_at` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user_responses`
--

CREATE TABLE `user_responses` (
  `response_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `question_id` int(11) NOT NULL,
  `selected_answer_id` int(11) NOT NULL,
  `is_correct` tinyint(1) NOT NULL,
  `points_awarded` int(11) NOT NULL,
  `answered_at` datetime NOT NULL,
  `mode` enum('DAILY','EXTENDED') NOT NULL,
  `session_id` int(11) DEFAULT NULL,
  `quiz_date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_responses`
--

INSERT INTO `user_responses` (`response_id`, `user_id`, `question_id`, `selected_answer_id`, `is_correct`, `points_awarded`, `answered_at`, `mode`, `session_id`, `quiz_date`) VALUES
(1, 2, 2, 4, 1, 10, '2025-12-09 23:50:31', 'EXTENDED', 2, '2025-12-10'),
(2, 2, 2, 4, 1, 10, '2025-12-10 00:02:57', 'DAILY', NULL, '2025-12-10'),
(3, 2, 1, 2, 0, 0, '2025-12-10 00:02:57', 'DAILY', NULL, '2025-12-10'),
(4, 2, 2, 4, 1, 10, '2025-12-10 00:29:27', 'EXTENDED', 9, '2025-12-10'),
(5, 2, 2, 4, 1, 10, '2025-12-10 00:30:28', 'DAILY', NULL, '2025-12-10'),
(6, 2, 1, 1, 1, 10, '2025-12-10 00:30:28', 'DAILY', NULL, '2025-12-10');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `accounts`
--
ALTER TABLE `accounts`
  ADD PRIMARY KEY (`account_id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `admins`
--
ALTER TABLE `admins`
  ADD PRIMARY KEY (`admin_id`),
  ADD KEY `account_id` (`account_id`);

--
-- Indexes for table `answers`
--
ALTER TABLE `answers`
  ADD PRIMARY KEY (`answer_id`),
  ADD KEY `question_id` (`question_id`);

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`category_id`),
  ADD UNIQUE KEY `category_name` (`category_name`);

--
-- Indexes for table `extended_sessions`
--
ALTER TABLE `extended_sessions`
  ADD PRIMARY KEY (`session_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `category_id` (`category_id`);

--
-- Indexes for table `leaderboard_all_time`
--
ALTER TABLE `leaderboard_all_time`
  ADD PRIMARY KEY (`user_id`);

--
-- Indexes for table `leaderboard_daily`
--
ALTER TABLE `leaderboard_daily`
  ADD PRIMARY KEY (`user_id`);

--
-- Indexes for table `level_rules`
--
ALTER TABLE `level_rules`
  ADD PRIMARY KEY (`level_number`);

--
-- Indexes for table `questions`
--
ALTER TABLE `questions`
  ADD PRIMARY KEY (`question_id`),
  ADD KEY `category_id` (`category_id`);

--
-- Indexes for table `quiz_config`
--
ALTER TABLE `quiz_config`
  ADD PRIMARY KEY (`config_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD KEY `account_id` (`account_id`);

--
-- Indexes for table `user_daily_stats`
--
ALTER TABLE `user_daily_stats`
  ADD PRIMARY KEY (`user_id`,`quiz_date`),
  ADD KEY `daily_category_id` (`daily_category_id`);

--
-- Indexes for table `user_responses`
--
ALTER TABLE `user_responses`
  ADD PRIMARY KEY (`response_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `question_id` (`question_id`),
  ADD KEY `selected_answer_id` (`selected_answer_id`);


START TRANSACTION;

-- Make sure categories exist (won't duplicate because category_name is UNIQUE)
INSERT IGNORE INTO categories (category_id, category_name) VALUES
(1,'Math'),(2,'Science'),(3,'Random'),(4,'History'),(5,'Technology');

-- =========================================================
-- MATH (category_id = 1) 20 questions
-- =========================================================
INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (1,'What is 18 + 27?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'45',1),(@q,'55',0),(@q,'35',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (1,'What is 64 ÷ 8?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'8',1),(@q,'6',0),(@q,'9',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (1,'What is 12 × 7?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'84',1),(@q,'72',0),(@q,'94',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (1,'What is 90 − 36?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'54',1),(@q,'64',0),(@q,'44',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (1,'What is 5^2 (5 squared)?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'25',1),(@q,'10',0),(@q,'15',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (1,'What is 7^3 (7 cubed)?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'343',1),(@q,'312',0),(@q,'373',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (1,'What is the value of π (pi) rounded to 2 decimal places?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'3.14',1),(@q,'3.41',0),(@q,'3.04',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (1,'What is 25% of 200?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'50',1),(@q,'25',0),(@q,'75',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (1,'If x = 6, what is 2x + 5?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'17',1),(@q,'15',0),(@q,'19',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (1,'What is the perimeter of a square with side length 9?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'36',1),(@q,'18',0),(@q,'81',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (1,'What is the area of a rectangle with length 8 and width 5?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'40',1),(@q,'13',0),(@q,'30',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (1,'What is the greatest common divisor (GCD) of 12 and 18?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'6',1),(@q,'3',0),(@q,'12',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (1,'What is the least common multiple (LCM) of 4 and 6?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'12',1),(@q,'24',0),(@q,'10',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (1,'What is 0.5 as a fraction in simplest form?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'1/2',1),(@q,'2/1',0),(@q,'1/5',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (1,'Solve: 3x = 27. What is x?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'9',1),(@q,'8',0),(@q,'10',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (1,'What is 11 × 11?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'121',1),(@q,'111',0),(@q,'131',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (1,'What is 144 ÷ 12?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'12',1),(@q,'11',0),(@q,'14',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (1,'If a triangle has angles 50° and 60°, what is the third angle?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'70°',1),(@q,'80°',0),(@q,'90°',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (1,'What is the mean (average) of 4, 8, and 10?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'7.33',1),(@q,'7',0),(@q,'8',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (1,'What is 2^6?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'64',1),(@q,'32',0),(@q,'128',0);


-- =========================================================
-- SCIENCE (category_id = 2) 20 questions
-- =========================================================
INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (2,'What gas do plants absorb from the atmosphere?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Carbon dioxide (CO2)',1),(@q,'Oxygen (O2)',0),(@q,'Nitrogen (N2)',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (2,'What is the chemical symbol for sodium?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Na',1),(@q,'So',0),(@q,'S',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (2,'What organ pumps blood through the body?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Heart',1),(@q,'Lungs',0),(@q,'Kidney',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (2,'Which planet is known as the Red Planet?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Mars',1),(@q,'Venus',0),(@q,'Jupiter',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (2,'What is the basic unit of life?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Cell',1),(@q,'Atom',0),(@q,'Tissue',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (2,'What part of the cell contains DNA in eukaryotes?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Nucleus',1),(@q,'Cell membrane',0),(@q,'Cytoplasm',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (2,'What is the boiling point of water at sea level (°C)?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'100',1),(@q,'90',0),(@q,'110',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (2,'Which blood type is known as the universal donor (for red blood cells)?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'O negative',1),(@q,'AB positive',0),(@q,'A positive',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (2,'What force pulls objects toward Earth?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Gravity',1),(@q,'Friction',0),(@q,'Magnetism',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (2,'What is the largest organ in the human body?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Skin',1),(@q,'Liver',0),(@q,'Heart',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (2,'Which vitamin is produced in the skin when exposed to sunlight?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Vitamin D',1),(@q,'Vitamin C',0),(@q,'Vitamin B12',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (2,'What is the process by which plants make food using sunlight?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Photosynthesis',1),(@q,'Respiration',0),(@q,'Fermentation',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (2,'What is the closest star to Earth?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'The Sun',1),(@q,'Polaris',0),(@q,'Sirius',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (2,'Which layer of Earth is liquid?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Outer core',1),(@q,'Crust',0),(@q,'Inner core',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (2,'What instrument is used to measure temperature?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Thermometer',1),(@q,'Barometer',0),(@q,'Hygrometer',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (2,'Which particle has a negative charge?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Electron',1),(@q,'Proton',0),(@q,'Neutron',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (2,'What is the main gas in Earth’s atmosphere?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Nitrogen',1),(@q,'Oxygen',0),(@q,'Carbon dioxide',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (2,'What type of energy does a moving object have?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Kinetic energy',1),(@q,'Potential energy',0),(@q,'Thermal energy',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (2,'What is the pH of a neutral solution?', 'HARD', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'7',1),(@q,'0',0),(@q,'14',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (2,'What is the name of the process where water vapor becomes liquid?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Condensation',1),(@q,'Evaporation',0),(@q,'Sublimation',0);


-- =========================================================
-- HISTORY (category_id = 4) 20 questions
-- =========================================================
INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (4,'Which ancient civilization built the pyramids of Giza?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Ancient Egyptians',1),(@q,'Romans',0),(@q,'Vikings',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (4,'The Renaissance began in which country?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Italy',1),(@q,'China',0),(@q,'Mexico',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (4,'Which city was historically known as Constantinople?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Istanbul',1),(@q,'Athens',0),(@q,'Rome',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (4,'Which war was fought between the North and South regions of the United States?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'The American Civil War',1),(@q,'World War I',0),(@q,'The Cold War',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (4,'What was the primary writing material used in ancient Egypt?', 'HARD', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Papyrus',1),(@q,'Silk',0),(@q,'Plastic',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (4,'Who was the first President of the United States?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'George Washington',1),(@q,'Abraham Lincoln',0),(@q,'Thomas Edison',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (4,'The Great Wall is located in which country?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'China',1),(@q,'India',0),(@q,'Japan',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (4,'Which empire was ruled by Julius Caesar (as a key leader)?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Roman Republic/Rome',1),(@q,'Ottoman Empire',0),(@q,'Mongol Empire',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (4,'In which century did World War I begin?', 'HARD', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'20th century',1),(@q,'18th century',0),(@q,'16th century',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (4,'What is the name of the trade route that connected Asia and Europe for centuries?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'The Silk Road',1),(@q,'The Amber Road',0),(@q,'The Spice River',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (4,'Which civilization is known for the city of Machu Picchu?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Inca',1),(@q,'Aztec',0),(@q,'Greek',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (4,'Who was known as the “Maid of Orléans”?', 'HARD', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Joan of Arc',1),(@q,'Cleopatra',0),(@q,'Queen Victoria',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (4,'The “Industrial Revolution” started first in which country?', 'HARD', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Britain (UK)',1),(@q,'Brazil',0),(@q,'Russia',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (4,'What was the main language of the ancient Roman Empire?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Latin',1),(@q,'Greek',0),(@q,'Arabic',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (4,'Which continent did the ancient civilization of Mesopotamia belong to?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Asia',1),(@q,'South America',0),(@q,'Australia',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (4,'What invention is Johannes Gutenberg famous for?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Printing press',1),(@q,'Telephone',0),(@q,'Steam engine',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (4,'Which ancient Greek city-state is known for its military discipline?', 'HARD', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Sparta',1),(@q,'Athens',0),(@q,'Corinth',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (4,'What was the name of the ship on which the Pilgrims traveled to North America in 1620?', 'HARD', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Mayflower',1),(@q,'Santa Maria',0),(@q,'Endeavour',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (4,'The Cold War was mainly a rivalry between which two countries?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'USA and USSR',1),(@q,'China and Japan',0),(@q,'France and Spain',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (4,'Which ancient civilization created cuneiform writing?', 'HARD', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Sumerians',1),(@q,'Vikings',0),(@q,'Incas',0);


-- =========================================================
-- TECHNOLOGY (category_id = 5) 20 questions
-- =========================================================
INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (5,'What does CPU stand for?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Central Processing Unit',1),(@q,'Computer Program Utility',0),(@q,'Central Power Unit',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (5,'Which device is used to display output visually from a computer?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Monitor',1),(@q,'Router',0),(@q,'Microphone',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (5,'What does “HTTP” stand for?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'HyperText Transfer Protocol',1),(@q,'High Transfer Text Program',0),(@q,'Hyperlink Transmission Process',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (5,'Which programming language is primarily used for styling web pages?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'CSS',1),(@q,'SQL',0),(@q,'HTML',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (5,'What is the name of the global network that connects computers worldwide?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Internet',1),(@q,'Intranet',0),(@q,'Ethernet',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (5,'In databases, what does SQL stand for?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Structured Query Language',1),(@q,'Simple Question Language',0),(@q,'System Queue Logic',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (5,'Which data structure works on a “First In, First Out” basis?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Queue',1),(@q,'Stack',0),(@q,'Tree',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (5,'Which data structure works on a “Last In, First Out” basis?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Stack',1),(@q,'Queue',0),(@q,'Graph',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (5,'What does RAM stand for?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Random Access Memory',1),(@q,'Read Access Memory',0),(@q,'Rapid Action Module',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (5,'Which protocol is commonly used to securely browse websites?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'HTTPS',1),(@q,'FTP',0),(@q,'SMTP',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (5,'What is “open-source” software?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Software with publicly available source code',1),(@q,'Software that cannot be modified',0),(@q,'Software that only works offline',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (5,'What is the main purpose of an operating system?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Manage hardware and run applications',1),(@q,'Only browse the web',0),(@q,'Only store files',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (5,'Which one is a version control system?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Git',1),(@q,'Wi-Fi',0),(@q,'Excel',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (5,'What is a firewall mainly used for?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Network security and traffic filtering',1),(@q,'Increase screen brightness',0),(@q,'Speed up typing',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (5,'Which part of a URL usually identifies the domain name?', 'HARD', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'The main host/domain (e.g., example.com)',1),(@q,'The protocol (http/https)',0),(@q,'The page fragment (#...)',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (5,'What does “AI” stand for?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Artificial Intelligence',1),(@q,'Automatic Internet',0),(@q,'Advanced Interface',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (5,'What is the purpose of encryption?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Protect data by making it unreadable without a key',1),(@q,'Make files larger',0),(@q,'Delete viruses',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (5,'Which is an example of cloud storage?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Google Drive',1),(@q,'USB cable',0),(@q,'CPU cache',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (5,'What is a database primary key used for?', 'MEDIUM', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Uniquely identify each row',1),(@q,'Store passwords',0),(@q,'Duplicate rows faster',0);

INSERT INTO questions (category_id, question_text, difficulty, is_active) VALUES (5,'What does “GUI” stand for?', 'EASY', 1);
SET @q := LAST_INSERT_ID();
INSERT INTO answers (question_id, answer_text, is_correct) VALUES (@q,'Graphical User Interface',1),(@q,'General User Internet',0),(@q,'Graphic Utility Index',0);


COMMIT;

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `accounts`
--
ALTER TABLE `accounts`
  MODIFY `account_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `admins`
--
ALTER TABLE `admins`
  MODIFY `admin_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `answers`
--
ALTER TABLE `answers`
  MODIFY `answer_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `category_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `extended_sessions`
--
ALTER TABLE `extended_sessions`
  MODIFY `session_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `questions`
--
ALTER TABLE `questions`
  MODIFY `question_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `quiz_config`
--
ALTER TABLE `quiz_config`
  MODIFY `config_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `user_responses`
--
ALTER TABLE `user_responses`
  MODIFY `response_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `admins`
--
ALTER TABLE `admins`
  ADD CONSTRAINT `admins_ibfk_1` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`);

--
-- Constraints for table `answers`
--
ALTER TABLE `answers`
  ADD CONSTRAINT `answers_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `questions` (`question_id`);

--
-- Constraints for table `extended_sessions`
--
ALTER TABLE `extended_sessions`
  ADD CONSTRAINT `extended_sessions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `extended_sessions_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`);

--
-- Constraints for table `leaderboard_all_time`
--
ALTER TABLE `leaderboard_all_time`
  ADD CONSTRAINT `leaderboard_all_time_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);


ALTER TABLE leaderboard_daily
  ADD COLUMN quiz_date DATE NOT NULL DEFAULT (CURDATE()),
  DROP PRIMARY KEY,
  ADD PRIMARY KEY (user_id, quiz_date);
--
-- Constraints for table `leaderboard_daily`
--
ALTER TABLE `leaderboard_daily`
  ADD CONSTRAINT `leaderboard_daily_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `questions`
--
ALTER TABLE `questions`
  ADD CONSTRAINT `questions_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`);

--
-- Constraints for table `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_ibfk_1` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`);

--
-- Constraints for table `user_daily_stats`
--
ALTER TABLE `user_daily_stats`
  ADD CONSTRAINT `user_daily_stats_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `user_daily_stats_ibfk_2` FOREIGN KEY (`daily_category_id`) REFERENCES `categories` (`category_id`);

--
-- Constraints for table `user_responses`
--
ALTER TABLE `user_responses`
  ADD CONSTRAINT `user_responses_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `user_responses_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `questions` (`question_id`),
  ADD CONSTRAINT `user_responses_ibfk_3` FOREIGN KEY (`selected_answer_id`) REFERENCES `answers` (`answer_id`);
COMMIT;

--
-- Constraints for  deleting
--
ALTER TABLE user_responses
DROP FOREIGN KEY user_responses_ibfk_3;

ALTER TABLE user_responses
ADD CONSTRAINT user_responses_ibfk_3
FOREIGN KEY (selected_answer_id)
REFERENCES answers (answer_id)
ON DELETE CASCADE
ON UPDATE CASCADE;


ALTER TABLE extended_sessions
  DROP FOREIGN KEY extended_sessions_ibfk_1;

ALTER TABLE extended_sessions
  ADD CONSTRAINT extended_sessions_ibfk_1
  FOREIGN KEY (user_id) REFERENCES users(user_id)
  ON DELETE CASCADE;


ALTER TABLE leaderboard_all_time
  DROP FOREIGN KEY leaderboard_all_time_ibfk_1;

ALTER TABLE leaderboard_all_time
  ADD CONSTRAINT leaderboard_all_time_ibfk_1
  FOREIGN KEY (user_id) REFERENCES users(user_id)
  ON DELETE CASCADE;


ALTER TABLE leaderboard_daily
  DROP FOREIGN KEY leaderboard_daily_ibfk_1;

ALTER TABLE leaderboard_daily
  ADD CONSTRAINT leaderboard_daily_ibfk_1
  FOREIGN KEY (user_id) REFERENCES users(user_id)
  ON DELETE CASCADE;


ALTER TABLE user_daily_stats
  DROP FOREIGN KEY user_daily_stats_ibfk_1;

ALTER TABLE user_daily_stats
  ADD CONSTRAINT user_daily_stats_ibfk_1
  FOREIGN KEY (user_id) REFERENCES users(user_id)
  ON DELETE CASCADE;


ALTER TABLE user_responses
  DROP FOREIGN KEY user_responses_ibfk_1;

ALTER TABLE user_responses
  ADD CONSTRAINT user_responses_ibfk_1
  FOREIGN KEY (user_id) REFERENCES users(user_id)
  ON DELETE CASCADE;
  ALTER TABLE users
  DROP FOREIGN KEY users_ibfk_1;

ALTER TABLE users
  ADD CONSTRAINT users_ibfk_1
  FOREIGN KEY (account_id) REFERENCES accounts(account_id)
  ON DELETE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
