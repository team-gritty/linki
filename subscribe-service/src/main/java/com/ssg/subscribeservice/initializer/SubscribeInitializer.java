package com.ssg.subscribeservice.initializer;

import com.ssg.subscribeservice.entity.SubscribeEntity;
import com.ssg.subscribeservice.repository.SubscribeRepository;
import com.ssg.subscribeservice.subsenum.SubscribeCode;
import com.ssg.subscribeservice.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

//subscribe 엔티티에 서버 띄울떄 값 insert
@Component
@RequiredArgsConstructor
public class SubscribeInitializer implements ApplicationRunner {
    private final SubscribeRepository subscribeRepository;
    private final IdGenerator idGenerator;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        insertIfNotExist(idGenerator.subscribeId(), SubscribeCode.InfSub, "influencerSubscribe", 100000);
        insertIfNotExist(idGenerator.subscribeId(), SubscribeCode.adSub, "advertiserSubscribe", 100000);
    }

    private void insertIfNotExist(String id, SubscribeCode code, String name, int amount) {
        Optional<SubscribeEntity> exist = subscribeRepository.findBySubscribeCode(code);
        if (exist.isEmpty()) {
            SubscribeEntity entity = SubscribeEntity.builder()
                    .subscribeId(id)
                    .subscribeCode(code)
                    .subscribeName(name)
                    .subscribeAmount(amount)
                    .build();
            subscribeRepository.save(entity);
        }
    }
}
