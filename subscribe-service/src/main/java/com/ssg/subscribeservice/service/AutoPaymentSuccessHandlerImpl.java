package com.ssg.subscribeservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.subscribeservice.dto.UserSubscribeDto;
import com.ssg.subscribeservice.entity.UserSubscribeEntity;
import com.ssg.subscribeservice.kafka.event.AutoPaymentSuccessEvent;
import com.ssg.subscribeservice.repository.UserSubscribeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AutoPaymentSuccessHandlerImpl implements AutoPaymentSuccessHandler {

    private final UserSubscribeRepository userSubscribeRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public UserSubscribeDto handle(AutoPaymentSuccessEvent autoPaymentSuccessEvent) {
        UserSubscribeEntity userSubscribeEntity = userSubscribeRepository.findByUserId(autoPaymentSuccessEvent.userId()).orElseThrow(() ->
                new NoSuchElementException("User with id " + autoPaymentSuccessEvent.userId() + " not found"));

        userSubscribeEntity.setUserSubscribeNextBillingAt(autoPaymentSuccessEvent.nextBillingAt());

        userSubscribeRepository.save(userSubscribeEntity);

        return modelMapper.map(userSubscribeEntity, UserSubscribeDto.class);
    }

}
