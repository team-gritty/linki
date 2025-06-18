package com.ssg.chatservice.config;

import com.ssg.chatservice.domain.message.dto.ChatMessageDTO;
import com.ssg.chatservice.entity.Message;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Configuration
public class ModelMapperConfig {

    // date 형식을 Instant(Entity) -> LocalDateTime(DTO)로 변환
    @Bean
    public ModelMapper MessageDateMapper(){
        ModelMapper modelMapper = new ModelMapper();

        Converter<Instant, LocalDateTime> instantLocalDateTime =
                ctx -> ctx.getSource() == null? null : ctx.getSource().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        modelMapper.typeMap(Message.class, ChatMessageDTO.class).addMappings(mapper->mapper.using(instantLocalDateTime)
                .map(Message::getMessageDate,ChatMessageDTO::setMessageDate));

        Converter<LocalDateTime, Instant> localDateTimeToInstant  =
                ctx -> ctx.getSource() == null? null : ctx.getSource().atZone(ZoneId.of("Asia/Seoul")).toInstant();
        modelMapper.typeMap(Message.class, ChatMessageDTO.class).addMappings(mapper->mapper.using(localDateTimeToInstant)
                .map(Message::getMessageDate,ChatMessageDTO::setMessageDate));

        return modelMapper;
    }


}
