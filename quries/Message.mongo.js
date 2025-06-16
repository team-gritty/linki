// ======================================================
// 📌 MongoDB 초기 데이터 자동 생성 스크립트
// ✅ 사용법:
//   1. 이 파일을 mongosh에서 실행
//      load("C:/경로/파일명.mongo.js")
//   2. 실행 내용:
//      - linki 유저 생성 (linkidb dbOwner 권한)
//      - linkidb 전환
//      - 메시지 5000건 더미 데이터 생성 및 삽입
// ======================================================

use admin;

db.createUser({
    user: "linki",
    pwd:  "linki1234",
    roles: [
        { role: "dbOwner", db: "linkidb" }
    ]
});

use linkidb;

let messages = [];

for (let seq = 0; seq < 5000; seq++) {
    const baseDate = ISODate("2024-01-01T00:00:00Z");
    const offset = Math.floor(Math.random() * 365);
    const randomDate = new Date(baseDate.getTime() + offset * 24 * 60 * 60 * 1000); // ms 단위

    messages.push({
        _id: "MSG" + seq.toString().padStart(4, '0'),
        chatroom_id: "CHAT" + Math.floor(seq / 5).toString().padStart(4, '0'),
        message_sender_id: "USER" + Math.floor(Math.random() * 1500).toString().padStart(4, '0'),
        message_content: "메시지 내용" + seq,
        message_type: ["TEXT", "IMAGE", "FILE"][Math.floor(Math.random() * 3)],
        message_date: randomDate,
        message_read: Math.random() < 0.5
    });
}

db.message.insertMany(messages);