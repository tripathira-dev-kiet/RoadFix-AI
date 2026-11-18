-- =====================================================================
-- RoadFix AI - Smart Municipal Road Hazard Platform Database Schema
-- MCA Project, KIET Group of Institutions
-- Database: roadfix_db (XAMPP / MySQL / MariaDB 10.4+)
-- =====================================================================

CREATE DATABASE IF NOT EXISTS roadfix_db;
USE roadfix_db;

-- 1. Users Table (Citizens, Municipal Staff, Admins)
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) NOT NULL,
  `email` varchar(120) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `role` varchar(50) NOT NULL DEFAULT 'CITIZEN',
  `department_name` varchar(100) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Issues / Complaints Table (Road Hazards with AI Vision Assessment)
CREATE TABLE IF NOT EXISTS `issues` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tracking_number` varchar(32) NOT NULL,
  `title` varchar(200) NOT NULL,
  `description` text NOT NULL,
  `category` varchar(100) NOT NULL,
  `status` varchar(50) NOT NULL DEFAULT 'SUBMITTED',
  `priority` varchar(50) NOT NULL DEFAULT 'MEDIUM',
  `severity_score` int(11) NOT NULL DEFAULT 50,
  `latitude` double NOT NULL DEFAULT 28.6139,
  `longitude` double NOT NULL DEFAULT 77.2090,
  `address` varchar(255) NOT NULL,
  `photo_url` longtext DEFAULT NULL,
  `ai_detected_category` varchar(100) DEFAULT NULL,
  `ai_confidence` double DEFAULT NULL,
  `ai_hazard_notes` text DEFAULT NULL,
  `is_duplicate` tinyint(1) DEFAULT 0,
  `duplicate_of_id` bigint(20) DEFAULT NULL,
  `assigned_department` varchar(100) DEFAULT 'Pending Assignment',
  `citizen_id` bigint(20) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `tracking_number` (`tracking_number`),
  KEY `citizen_id` (`citizen_id`),
  CONSTRAINT `issues_ibfk_1` FOREIGN KEY (`citizen_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Feedbacks Table (5-Star Citizen Ratings & Service Feedback)
CREATE TABLE IF NOT EXISTS `feedbacks` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `issue_id` bigint(20) DEFAULT NULL,
  `tracking_number` varchar(50) DEFAULT NULL,
  `citizen_id` bigint(20) DEFAULT NULL,
  `citizen_name` varchar(150) DEFAULT NULL,
  `citizen_email` varchar(150) DEFAULT NULL,
  `rating` int(11) NOT NULL CHECK (`rating` between 1 and 5),
  `speed_rating` int(11) NOT NULL DEFAULT 5,
  `comment` text DEFAULT NULL,
  `submitted_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `issue_id` (`issue_id`),
  KEY `citizen_id` (`citizen_id`),
  CONSTRAINT `feedbacks_ibfk_1` FOREIGN KEY (`issue_id`) REFERENCES `issues` (`id`) ON DELETE SET NULL,
  CONSTRAINT `feedbacks_ibfk_2` FOREIGN KEY (`citizen_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Work Proofs Table (Field Staff Photographic Completion Proofs)
CREATE TABLE IF NOT EXISTS `work_proofs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `issue_id` bigint(20) NOT NULL,
  `officer_name` varchar(120) NOT NULL,
  `department_name` varchar(100) NOT NULL,
  `before_photo_url` longtext DEFAULT NULL,
  `after_photo_url` longtext NOT NULL,
  `completion_notes` text NOT NULL,
  `materials_used` varchar(255) DEFAULT NULL,
  `gps_latitude` double DEFAULT NULL,
  `gps_longitude` double DEFAULT NULL,
  `resolved_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `issue_id` (`issue_id`),
  CONSTRAINT `work_proofs_ibfk_1` FOREIGN KEY (`issue_id`) REFERENCES `issues` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -- Initial Municipal Seed Data
-- INSERT INTO `users` (`id`, `full_name`, `email`, `password`, `phone`, `role`, `department_name`, `created_at`) VALUES
-- (1, 'Aarav Sharma', 'citizen@roadfix.ai', 'citizen123', '+91 9811002233', 'CITIZEN', NULL, '2026-11-01 10:00:00'),
-- (2, 'Chief Commissioner Mehta', 'admin@roadfix.ai', 'admin123', '+91 9822334455', 'ADMIN', 'Administration', '2026-11-01 10:00:00'),
-- (3, 'Inspector Rajesh Kumar', 'dept.roads@roadfix.ai', 'dept123', '+91 9833445566', 'DEPARTMENT_OFFICER', 'Roads & Infrastructure', '2026-11-01 10:00:00'),
-- (4, 'Officer Sunita Verma', 'dept.sanitation@roadfix.ai', 'dept123', '+91 9844556677', 'DEPARTMENT_OFFICER', 'Sanitation & Waste', '2026-11-01 10:00:00'),
-- (5, 'Engineer Vikram Rao', 'dept.electrical@roadfix.ai', 'dept123', '+91 9855667788', 'DEPARTMENT_OFFICER', 'Electrical & Lighting', '2026-11-01 10:00:00')
-- ON DUPLICATE KEY UPDATE `full_name`=VALUES(`full_name`);

-- INSERT INTO `issues` (`id`, `tracking_number`, `title`, `description`, `category`, `status`, `priority`, `severity_score`, `latitude`, `longitude`, `address`, `photo_url`, `ai_detected_category`, `ai_confidence`, `ai_hazard_notes`, `assigned_department`, `citizen_id`) VALUES
-- (8, 'RF-101', 'Deep Pothole on MG Road', 'Large crater causing severe vehicle skidding and slowdown.', 'POTHOLE', 'RESOLVED', 'HIGH', 85, 28.6139, 77.2090, 'MG Road near Metro Pillar 42', 'https://images.unsplash.com/photo-1515162816999-a0c47dc192f7?w=800', 'Road Surface Pothole (Depth: ~14 cm)', 96.4, 'High Hazard: Severe two-wheeler skidding risk. Cold-mix asphalt patch required.', 'Roads & Infrastructure', 1),
-- (9, 'RF-102', 'Blocked Stormwater Drain Overflowing', 'Rainwater drain clogged with plastic debris and overflowing onto footpath.', 'DRAINAGE', 'IN_PROGRESS', 'MEDIUM', 60, 28.6145, 77.2100, 'Commercial Street Crossing', 'https://images.unsplash.com/photo-1544717305-2782549b5136?w=800', 'Stormwater Drainage Blockage', 94.8, 'Environmental Hazard: Foul runoff water spilling on sidewalk. Desilting required.', 'Sanitation & Waste', 1),
-- (10, 'RF-103', 'Streetlight Non-Functional', 'Streetlight has been off for 2 nights.', 'STREETLIGHT', 'ASSIGNED', 'LOW', 40, 28.6150, 77.2110, '100ft Ring Road, Junction 4', 'https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=800', 'Luminaire Non-Functional / Power Fault', 92.1, 'Night Transit Hazard: Low visibility zone increasing accident probability.', 'Electrical & Lighting', 1)
-- ON DUPLICATE KEY UPDATE `title`=VALUES(`title`);
