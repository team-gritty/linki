DROP DATABASE IF EXISTS chatdb;
CREATE DATABASE chatdb;

use chatdb;

# CREATE USER 'chat'@'%' IDENTIFIED BY 'chat1234';
# GRANT ALL PRIVILEGES ON chatdb.* TO 'chat'@'%';
# FLUSH PRIVILEGES;

DROP TABLE IF EXISTS chat;

CREATE TABLE `chat` (
                        `chat_id`	varchar(25)	NOT NULL,
                        `chat_date`	datetime	NOT NULL,
                        `chat_status`	Enum('PENDING','ACTIVE','INACTIVE','DELETE')	NOT NULL	COMMENT '대기/활성/비활성/삭제',
                        `proposal_id`	VARCHAR(25)	NOT NULL	COMMENT '제안서 식별 ID',
                        `nego_status` Enum('PENDING','ACCEPTED','REJECTED','PENDING_SIGN','ONGOING','COMPLETED') NOT NULL COMMENT'제안서 대기/수락/거절/계약서 생성 후 서명 대기/서명 완료 후 계약 이행 중 / 광고 이행 및 정산 완료 상태 '
);

