package com.ssg.chatservice.domain.kafka.enums;

import com.ssg.chatservice.domain.chat.enums.NegoStatus;
import com.ssg.chatservice.domain.chat.service.ChatService;
import lombok.Getter;

@Getter
public enum EventType {
    PROPOSAL_CREATE("제안서 승인요청",NegoStatus.PENDING){
        @Override
        public void handle(ChatService chatService,String proposalId){
            chatService.createRoom(proposalId);
        }
    },
    PROPOSAL_ACTIVE("제안서를 승인",NegoStatus.ACCEPTED){
        @Override
        public void handle(ChatService chatService,String proposalId){
            chatService.activateRoomByProposal(proposalId);
        }
    },
    PROPOSAL_MODIFY("제안서를 수정"){
        @Override
        public void handle(ChatService chatService,String proposalId){
            //알림만 발송
        }
    },
    PROPOSAL_REJECT("제안서를 거절",NegoStatus.REJECTED){
        @Override
        public void handle(ChatService chatService,String proposalId){
            chatService.softDeleteChat(proposalId);
        }
    },
    PROPOSAL_DELETE("제안서를 삭제"){
        @Override
        public void handle(ChatService chatService,String proposalId){
            chatService.softDeleteChat(proposalId);
        }
    },
    CONTRACT_CREATE("계약서를 생성",NegoStatus.PENDING_SIGN){
        @Override
        public void handle(ChatService chatService,String proposalId){
            //알림만 발송
        }
    },
    CONTRACT_SIGN("계약서 서명을 완료",NegoStatus.ONGOING){
        @Override
        public void handle(ChatService chatService,String proposalId){
            //알림만 발송
        }
    },
    CONTRACT_COMPLETED("정산을 완료",NegoStatus.COMPLETED){
        @Override
        public void handle(ChatService chatService,String proposalId){
            chatService.inactiveChat(proposalId);
        }
    };


    private final String label;

    private final NegoStatus negoStatus;

    EventType(String label, NegoStatus negoStatus) {
        this.label = label;
        this.negoStatus = negoStatus;
    }
    EventType(String label) {
        this.label = label;
        this.negoStatus = null;
    }

    public abstract void handle(ChatService chatService, String proposalId);

}
