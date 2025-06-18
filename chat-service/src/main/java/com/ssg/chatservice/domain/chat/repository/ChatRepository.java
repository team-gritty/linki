package com.ssg.chatservice.domain.chat.repository;

import com.ssg.chatservice.entity.Chat;
import com.ssg.chatservice.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat,String> {
    //제안서 아이디로 객체 찾기
    Chat findByProposalId(String proposalId);
    //제안서 아이디로 채팅방 존재 여부 확인
    boolean existsByProposalId(String propodalId);

}
