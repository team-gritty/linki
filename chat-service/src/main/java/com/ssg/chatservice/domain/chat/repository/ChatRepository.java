package com.ssg.chatservice.domain.chat.repository;

import com.ssg.chatservice.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat,String> {
}
