package com.ssg.chatservice.domain.chat.repository;

import com.ssg.chatservice.entity.Chat;
import com.ssg.chatservice.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat,String> {
    //제안서 아이디로 객체 찾기
    Optional<Chat> findByProposalId(String proposalId);
    //제안서 아이디로 채팅방 존재 여부 확인
    boolean existsByProposalId(String proposalId);
    //(캠페인에 해당하는 여러 채팅방 조회) 제안서 아이디 리스트로 채팅방 리스트 조회
    List<Chat> findByProposalIdIn(List<String> proposalIds);
}
