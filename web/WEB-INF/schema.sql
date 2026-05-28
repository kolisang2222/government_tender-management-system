/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  kolisang
 * Created: Apr 20, 2026
 */

-- ============================================================
-- PROCUREGOV DATABASE SCHEMA
-- Ministry of Public Works, Kingdom of Lesotho
-- Generated from actual database dump - April 20, 2026
-- ============================================================

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

DROP TABLE IF EXISTS documents;
DROP TABLE IF EXISTS evaluation_scores;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS bids;
DROP TABLE IF EXISTS tender_evaluators;
DROP TABLE IF EXISTS tenders;
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS users;

DROP PROCEDURE IF EXISTS CalculateBidRankings;
DROP PROCEDURE IF EXISTS CloseExpiredTenders;
DROP TRIGGER IF EXISTS after_tender_awarded;

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `password_hash` varchar(64) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `address` text DEFAULT NULL,
  `contact_number` varchar(20) DEFAULT NULL,
  `role` enum('SUPPLIER','PROCUREMENT_OFFICER','EVALUATION_COMMITTEE') NOT NULL,
  `registration_number` varchar(20) DEFAULT NULL,
  `failed_attempts` int(11) DEFAULT 0,
  `locked_until` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `registration_number` (`registration_number`),
  KEY `idx_email` (`email`),
  KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `tenders` (
  `tender_id` int(11) NOT NULL AUTO_INCREMENT,
  `reference_number` varchar(20) NOT NULL,
  `title` varchar(200) NOT NULL,
  `category` enum('CONSTRUCTION','ROADS','ELECTRICAL','PLUMBING','GENERAL_SERVICES') NOT NULL,
  `description` text DEFAULT NULL,
  `estimated_value` decimal(15,2) DEFAULT NULL,
  `closing_datetime` datetime NOT NULL,
  `status` enum('DRAFT','OPEN','CLOSED','UNDER_EVALUATION','EVALUATED','AWARDED') DEFAULT 'DRAFT',
  `tender_notice_path` varchar(500) DEFAULT NULL,
  `created_by` int(11) NOT NULL,
  `award_justification` text DEFAULT NULL,
  `awarded_bid_id` int(11) DEFAULT NULL,
  `awarded_value` decimal(15,2) DEFAULT NULL,
  `award_date` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`tender_id`),
  UNIQUE KEY `reference_number` (`reference_number`),
  KEY `created_by` (`created_by`),
  KEY `idx_reference` (`reference_number`),
  KEY `idx_status` (`status`),
  KEY `idx_category` (`category`),
  KEY `idx_closing` (`closing_datetime`),
  CONSTRAINT `tenders_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `bids` (
  `bid_id` int(11) NOT NULL AUTO_INCREMENT,
  `tender_id` int(11) NOT NULL,
  `supplier_id` int(11) NOT NULL,
  `bid_amount` decimal(15,2) NOT NULL,
  `technical_compliance_statement` text DEFAULT NULL,
  `proposed_timeline_days` int(11) NOT NULL,
  `supporting_document_path` varchar(500) DEFAULT NULL,
  `submitted_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`bid_id`),
  UNIQUE KEY `unique_supplier_tender` (`tender_id`,`supplier_id`),
  KEY `idx_tender` (`tender_id`),
  KEY `idx_supplier` (`supplier_id`),
  CONSTRAINT `bids_ibfk_1` FOREIGN KEY (`tender_id`) REFERENCES `tenders` (`tender_id`) ON DELETE CASCADE,
  CONSTRAINT `bids_ibfk_2` FOREIGN KEY (`supplier_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `evaluation_scores` (
  `score_id` int(11) NOT NULL AUTO_INCREMENT,
  `bid_id` int(11) NOT NULL,
  `evaluator_id` int(11) NOT NULL,
  `technical_compliance_score` decimal(5,2) DEFAULT NULL,
  `price_score` decimal(5,2) DEFAULT NULL,
  `timeline_score` decimal(5,2) DEFAULT NULL,
  `weighted_total` decimal(5,2) DEFAULT NULL,
  `submitted_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`score_id`),
  UNIQUE KEY `unique_evaluator_bid` (`bid_id`,`evaluator_id`),
  KEY `idx_bid` (`bid_id`),
  KEY `idx_evaluator` (`evaluator_id`),
  CONSTRAINT `evaluation_scores_ibfk_1` FOREIGN KEY (`bid_id`) REFERENCES `bids` (`bid_id`) ON DELETE CASCADE,
  CONSTRAINT `evaluation_scores_ibfk_2` FOREIGN KEY (`evaluator_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `tender_evaluators` (
  `tender_evaluator_id` int(11) NOT NULL AUTO_INCREMENT,
  `tender_id` int(11) NOT NULL,
  `evaluator_id` int(11) NOT NULL,
  `assigned_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`tender_evaluator_id`),
  UNIQUE KEY `unique_tender_evaluator` (`tender_id`,`evaluator_id`),
  KEY `idx_tender` (`tender_id`),
  KEY `idx_evaluator` (`evaluator_id`),
  CONSTRAINT `tender_evaluators_ibfk_1` FOREIGN KEY (`tender_id`) REFERENCES `tenders` (`tender_id`) ON DELETE CASCADE,
  CONSTRAINT `tender_evaluators_ibfk_2` FOREIGN KEY (`evaluator_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `documents` (
  `document_id` int(11) NOT NULL AUTO_INCREMENT,
  `tender_id` int(11) DEFAULT NULL,
  `bid_id` int(11) DEFAULT NULL,
  `document_type` enum('TENDER_NOTICE','BID_SUPPORTING','AWARD_NOTICE') NOT NULL,
  `file_name` varchar(255) NOT NULL,
  `file_path` varchar(500) NOT NULL,
  `file_size` bigint(20) DEFAULT NULL,
  `mime_type` varchar(100) DEFAULT NULL,
  `uploaded_by` int(11) DEFAULT NULL,
  `uploaded_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`document_id`),
  KEY `uploaded_by` (`uploaded_by`),
  KEY `idx_tender` (`tender_id`),
  KEY `idx_bid` (`bid_id`),
  KEY `idx_type` (`document_type`),
  CONSTRAINT `documents_ibfk_1` FOREIGN KEY (`tender_id`) REFERENCES `tenders` (`tender_id`) ON DELETE CASCADE,
  CONSTRAINT `documents_ibfk_2` FOREIGN KEY (`bid_id`) REFERENCES `bids` (`bid_id`) ON DELETE CASCADE,
  CONSTRAINT `documents_ibfk_3` FOREIGN KEY (`uploaded_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `notifications` (
  `notification_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `tender_id` int(11) DEFAULT NULL,
  `type` enum('TENDER_AWARDED','BID_RECEIVED','EVALUATION_COMPLETE','TENDER_CLOSED') NOT NULL,
  `subject` varchar(200) DEFAULT NULL,
  `message` text DEFAULT NULL,
  `is_read` tinyint(1) DEFAULT 0,
  `sent_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `read_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`notification_id`),
  KEY `tender_id` (`tender_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_read` (`is_read`),
  KEY `idx_sent` (`sent_at`),
  CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `notifications_ibfk_2` FOREIGN KEY (`tender_id`) REFERENCES `tenders` (`tender_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `audit_logs` (
  `log_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `action` varchar(50) NOT NULL,
  `entity_type` varchar(50) DEFAULT NULL,
  `entity_id` int(11) DEFAULT NULL,
  `details` text DEFAULT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`log_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_action` (`action`),
  KEY `idx_created` (`created_at`),
  CONSTRAINT `audit_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `users` (`user_id`, `email`, `password_hash`, `full_name`, `address`, `contact_number`, `role`, `registration_number`, `failed_attempts`, `locked_until`, `created_at`, `updated_at`) VALUES
(1, 'itumeleng.thulo@bothouniversity.com', 'e2a625234913a3e63bf3a8353b800988f11895c7739780ecb484936f6f1b8473', 'Maellen Finances', 'plot12 \r\nMaseru Ha Abia\r\nLesotho', '+26657772442', 'SUPPLIER', 'SUP-2026-0001', 5, '2026-04-12 15:53:24', '2026-04-12 00:59:09', '2026-04-12 17:23:24'),
(2, 'thulo@gmail.com', '42039dab7430d2c8f89add07355c7bb97061c78b22009157cd2fdfc88828812a', 'Thulo (SGK)', 'zone6 \r\nKhubeluu\r\nmaseru', '+266 62626912', 'SUPPLIER', 'SUP-2026-0002', 0, NULL, '2026-04-12 08:19:42', '2026-04-17 12:37:10'),
(4, 'kolisang.phatela@bothouniversity.com', '2348122be7a19c218fb6939d8618894d47092b66ac66beb04d5941331e7fc1c5', 'Kolisang Phatela', 'Ministry of Public Works, Maseru, Lesotho', '+266 2232 1000', 'PROCUREMENT_OFFICER', NULL, 0, NULL, '2026-04-12 18:01:35', '2026-04-18 13:11:59'),
(5, 'seboko.faso@gmail.com', '330a37a757b889f4ee46b1049704b3dc43e2b2f01cae5d89775bffbab0c73737', 'Seboko Faso', 'Ministry of Public Works, Maseru, Lesotho', '+266 2232 1000', 'EVALUATION_COMMITTEE', NULL, 0, NULL, '2026-04-12 18:07:34', '2026-04-17 20:33:22'),
(6, 'mphutlane.moeti@gmail.com', '9ea36df50633350789b275a31efbb0c0d5ecb7e791f70e00ede562a55f2b2ebc', 'Phatela Infrastructure Solutions', 'Plot 452, \r\nKingsway Road,\r\n Maseru 100, Lesotho', '+266 63328592', 'SUPPLIER', 'SUP-2026-0003', 0, NULL, '2026-04-17 21:12:50', '2026-04-17 21:12:50'),
(7, 'mphutlanemoeti@gmail.com', '9ea36df50633350789b275a31efbb0c0d5ecb7e791f70e00ede562a55f2b2ebc', 'KA QABANE KA MOO', 'qabane ha phalo\r\nsekitsing \r\nMohale \'s hoek', '+266 50626677', 'SUPPLIER', 'SUP-2026-0004', 0, NULL, '2026-04-18 13:46:43', '2026-04-18 14:02:35'),
(8, 'phatelakolisang86@gmail.com', '9ea36df50633350789b275a31efbb0c0d5ecb7e791f70e00ede562a55f2b2ebc', 'Parts Computer Systems', 'maseru \r\nkhubelu \r\nlesotho', '+266 50626319', 'SUPPLIER', 'SUP-2026-0005', 0, NULL, '2026-04-18 15:28:23', '2026-04-18 15:28:23');

-- ============================================================
-- SEED DATA - TENDERS (Actual data from your database)
-- ============================================================
INSERT INTO `tenders` (`tender_id`, `reference_number`, `title`, `category`, `description`, `estimated_value`, `closing_datetime`, `status`, `tender_notice_path`, `created_by`, `award_justification`, `awarded_bid_id`, `awarded_value`, `award_date`, `created_at`, `updated_at`) VALUES
(1, 'MPW-2026-0001', 'Construction of Maseru District Hospital Access Road', 'ROADS', 'The Ministry of Public Works invites sealed bids from eligible and qualified contractors for the construction of an access road to the new Maseru District Hospital. The scope includes site clearance, earthworks and grading over 2.8 km stretch, installation of drainage culverts at 6 locations, asphalt paving width of 7.5m, road markings and signage, sidewalks on both sides width of 1.5m each, and street lighting installation. Bidders must have minimum 5 years experience in road construction and provide proof of similar completed projects.', 2500000.00, '2026-04-30 10:00:00', 'OPEN', 'C:\\kolisangphatela2334120\\uploads\\tender-notices\\tenderr_1776321075451_01cf7b53.pdf', 4, NULL, NULL, NULL, NULL, '2026-04-16 06:31:19', '2026-04-16 06:31:19'),
(2, 'MPW-2026-0002', 'Proposed Construction of New Office Block and External Works at the Ministry of Public Works Regional Headquarters.', 'CONSTRUCTION', 'The scope of work includes the construction of a three-story reinforced concrete office building (approx. 1,500 sqm), including masonry walling, roofing, electrical installations, and plumbing. External works involve paved parking for 50 vehicles, perimeter fencing, and landscaping. Bidders must be registered with the relevant construction boards and provide a valid tax clearance certificate.', 56665656.00, '2026-04-30 10:00:00', 'OPEN', 'C:\\kolisangphatela2334120\\uploads\\tender-notices\\tenderr_1776355131673_382bd739.pdf', 4, NULL, NULL, NULL, NULL, '2026-04-16 15:58:52', '2026-04-16 15:58:52'),
(3, 'MPW-2026-0003', 'Routine Maintenance and Rehabilitation of the A1 North Main Road (Segment B)', 'GENERAL_SERVICES', 'The project involves the rehabilitation of a 15km stretch of asphalt road. The scope includes pothole patching, resurfacing, clearing of drainage systems, and installation of new road signage and cat-eyes. Contractors must provide proof of heavy machinery ownership and a minimum of 5 years of experience in civil works', 5667788.97, '2026-04-25 16:00:00', 'OPEN', 'C:\\kolisangphatela2334120\\uploads\\tender-notices\\tenderr_1776460813363_0f8433ef.pdf', 4, NULL, NULL, NULL, NULL, '2026-04-17 21:20:13', '2026-04-18 13:58:53'),
(4, 'MPW-2026-0004', 'Pulihali Dam Construction', 'CONSTRUCTION', 'the bigger dam is being build in Mokhotlong Lesotho, to improve the water supply in Lesotho, and provide jobs for Basotho People', 889997.99, '2026-05-01 23:00:00', 'OPEN', 'C:\\kolisangphatela2334120\\uploads\\tender-notices\\tenderr_1776525429301_9edf229b.pdf', 4, NULL, NULL, NULL, NULL, '2026-04-18 15:17:09', '2026-04-18 15:17:09');

-- ============================================================
-- SEED DATA - BIDS (Actual data from your database)
-- ============================================================
INSERT INTO `bids` (`bid_id`, `tender_id`, `supplier_id`, `bid_amount`, `technical_compliance_statement`, `proposed_timeline_days`, `supporting_document_path`, `submitted_at`, `updated_at`) VALUES
(1, 2, 2, 500000.02, 're sebetsana le mebila ka tsela e nepahetseng haholo', 249, 'C:\\kolisangphatela2334120\\uploads\\bid-documents\\bid_2_2_1776449151305_deb3ea95.docx', '2026-04-17 16:05:51', '2026-04-17 18:05:51'),
(2, 3, 7, 78887787.00, 'ke sebetsa ka thata haholo ho bona hore mosebetsi waka o phethahala ka ho tlala', 20, 'C:\\kolisangphatela2334120\\uploads\\bid-documents\\bid_3_7_1776521064251_6de1f8b1.pdf', '2026-04-18 12:04:24', '2026-04-18 14:04:24');

-- ============================================================
-- STORED PROCEDURES
-- ============================================================
DELIMITER $$

CREATE PROCEDURE `CalculateBidRankings` (IN `p_tender_id` INT)
BEGIN
    SELECT 
        b.bid_id,
        b.bid_amount,
        u.full_name AS supplier_name,
        AVG(es.weighted_total) AS final_score,
        RANK() OVER (ORDER BY AVG(es.weighted_total) DESC) AS ranking
    FROM bids b
    JOIN users u ON b.supplier_id = u.user_id
    LEFT JOIN evaluation_scores es ON b.bid_id = es.bid_id
    WHERE b.tender_id = p_tender_id
    GROUP BY b.bid_id, b.bid_amount, u.full_name
    ORDER BY final_score DESC;
END$$

CREATE PROCEDURE `CloseExpiredTenders` ()
BEGIN
    UPDATE tenders 
    SET status = 'CLOSED' 
    WHERE status = 'OPEN' 
      AND closing_datetime < NOW();
END$$

DELIMITER ;

-- ============================================================
-- TRIGGERS
-- ============================================================
DELIMITER $$

CREATE TRIGGER `after_tender_awarded` AFTER UPDATE ON `tenders`
FOR EACH ROW
BEGIN
    IF NEW.status = 'AWARDED' AND OLD.status != 'AWARDED' THEN
        INSERT INTO notifications (user_id, tender_id, type, subject, message)
        SELECT DISTINCT b.supplier_id, NEW.tender_id, 'TENDER_AWARDED',
               CONCAT('Tender ', NEW.reference_number, ' Awarded'),
               CASE 
                   WHEN b.bid_id = NEW.awarded_bid_id THEN 'Congratulations! Your bid has been selected.'
                   ELSE 'Thank you for your bid. The tender has been awarded to another supplier.'
               END
        FROM bids b
        WHERE b.tender_id = NEW.tender_id;
    END IF;
END$$

DELIMITER ;

-- ============================================================
-- RESET AUTO_INCREMENT VALUES
-- ============================================================
ALTER TABLE `users` AUTO_INCREMENT = 9;
ALTER TABLE `tenders` AUTO_INCREMENT = 5;
ALTER TABLE `bids` AUTO_INCREMENT = 3;
ALTER TABLE `evaluation_scores` AUTO_INCREMENT = 1;
ALTER TABLE `tender_evaluators` AUTO_INCREMENT = 1;
ALTER TABLE `documents` AUTO_INCREMENT = 1;
ALTER TABLE `notifications` AUTO_INCREMENT = 1;
ALTER TABLE `audit_logs` AUTO_INCREMENT = 1;

COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;