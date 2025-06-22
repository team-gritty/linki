package com.ssg.chatservice.config;

import com.ssg.chatservice.domain.message.dto.ChatMessageDTO;
import com.ssg.chatservice.entity.Message;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ssg.chatservice.domain.message.dto.respone.ChatMessageResponeDTO;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Configuration
public class ModelMapperConfig {

    // date 형식을 Instant(Entity) -> LocalDateTime(DTO)로 변환
    @Bean
    public ModelMapper MessageDateMapper(){
        ModelMapper modelMapper = new ModelMapper();

        // Entity → DTO: Instant → LocalDateTime
        Converter<Instant, LocalDateTime> instantToLocalDateTime =
                ctx -> ctx.getSource() == null ? null :
                        ctx.getSource().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime();

        // Message → ChatMessageDTO
        modelMapper.typeMap(Message.class, ChatMessageDTO.class)
                .addMappings(mapper -> mapper.using(instantToLocalDateTime)
                        .map(Message::getMessageDate, ChatMessageDTO::setMessageDate));

        // Message → ChatMessageResponeDTO
        modelMapper.typeMap(Message.class, ChatMessageResponeDTO.class)
                .addMappings(mapper -> mapper.using(instantToLocalDateTime)
                        .map(Message::getMessageDate, ChatMessageResponeDTO::setMessageDate));

        // DTO → Entity: LocalDateTime → Instant
        Converter<LocalDateTime, Instant> localDateTimeToInstant =
                ctx -> ctx.getSource() == null ? null :
                        ctx.getSource().atZone(ZoneId.of("Asia/Seoul")).toInstant();

        modelMapper.typeMap(ChatMessageDTO.class, Message.class)
                .addMappings(mapper -> mapper.using(localDateTimeToInstant)
                        .map(ChatMessageDTO::getMessageDate, Message::setMessageDate));

        return modelMapper;
    }

}
