package com.ssg.chatservice.domain.message.repository;

import com.ssg.chatservice.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message,String> {
    List<Message> findByChatId(String chatId);
}
