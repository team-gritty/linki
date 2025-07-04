DROP DATABASE IF EXISTS chatdb;
CREATE DATABASE chatdb;

use chatdb;

# CREATE USER 'chat'@'%' IDENTIFIED BY 'chat1234';
# GRANT ALL PRIVILEGES ON chatdb.* TO 'chat'@'%';
# FLUSH PRIVILEGES;

DROP TABLE IF EXISTS chat;

CREATE TABLE `chat` (`chat_id` varchar(25) NOT NULL,`chat_date`	datetime NOT NULL,`chat_status` Enum('PENDING','ACTIVE','INACTIVE','DELETE') NOT NULL,`proposal_id` VARCHAR(25)	NOT NULL COMMENT '제안서 식별 ID',`nego_status` Enum('PENDING','ACCEPTED','REJECTED','PENDING_SIGN','ONGOING','COMPLETED') NOT NULL);

