package com.Gritty.Linki.user.influencer.campaign.repository;

import com.Gritty.Linki.domain.user.influencer.campaign.repository.jpa.InfluencerUtilRepository;
import com.Gritty.Linki.entity.Influencer;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaRepositories(
        basePackages = {
                "com.Gritty.Linki.domain.user.influencer.campaign.repository.jpa",
        }
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class InfluencerUtilRepositoryTest {

    @Autowired
    private InfluencerUtilRepository influencerRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("findByUser_UserId() 로 userId 에 해당하는 Influencer 를 잘 조회한다")
    void testFindByUserUserId() {
        // given: 테스트 DB에 user.userId="USER0001" 인 Influencer, User 데이터가 미리 INSERT 되어 있어야 합니다.
        String userId = "USER0001";

        // when
        Optional<Influencer> opt = influencerRepository.findByUser_UserId(userId);

        // then
        assertThat(opt).as("userId=%s 인 Influencer 존재 여부", userId).isPresent();
        Influencer inf = opt.get();

        // 출력해 보기
        System.out.println("▶ InfluencerId : " + inf.getInfluencerId());
        System.out.println("▶ UserId       : " + inf.getUserId());
        System.out.println("▶ UserName     : " + inf.getInfluencerName());

        // 영속성 컨텍스트 초기화 후에도 LazyInitializationException 이 발생하지 않는지
        String before = inf.getInfluencerName();
        em.flush();
        em.clear();
        String after = inf.getInfluencerName();
        assertThat(after).isEqualTo(before);
    }
}