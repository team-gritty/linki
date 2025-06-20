package com.ssg.chatservice.domain.message.repository;

import com.ssg.chatservice.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message,String> {
    //채팅방 아이디로 메세지 조회
    List<Message> findByChatId(String chatId);
    // 채팅방 내 가장 마지막 메시지 1건
    Message findTop1ByChatIdOrderByMessageDateDesc(String chatId);
}
