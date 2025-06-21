package com.Gritty.Linki.config.security;

import com.Gritty.Linki.client.chatClient.dto.InterfaceChatInfoDto;
import com.Gritty.Linki.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
        Boolean existsByUserLoginId(String userLoginId);
        Optional<User> findByUserLoginId(String userLoginId);
        @Query("SELECT u.userId FROM User u WHERE u.userLoginId = :userLoginId")
        Optional<String> findUserIdByUserLoginId(@Param("userLoginId") String userLoginId);

        //유저 아이디로 인플루언서의 chatInfo 조회
        @Query(value = """
           select u.user_id AS userId,
                   u.user_login_id AS userLoginId,
                   p.proposal_id AS proposalId
           from user u
           join influencer i on u.user_id = i.user_id
           join proposal p on i.influencer_id = p.influencer_id
           where u.user_id = :userId
    """,nativeQuery = true)
        List<InterfaceChatInfoDto> findInfluencerChatInfoByUserId(@Param("userId") String userId);

        //유저 아이디로 광고주의 chatInfo 조회
        @Query(value = """
            select u.user_id AS userId,
                   u.user_login_id AS userLoginId,
                   p.proposal_id AS proposalId
            from user u
                     join advertiser a on u.user_id = a.user_id
                     join campaign c on a.advertiser_id = c.advertiser_id
                     join proposal p on c.campaign_id = p.campaign_id
            where u.user_id = :userId
            """,nativeQuery = true)
        List<InterfaceChatInfoDto> findAdvertiserChatInfoByUserId(@Param("userId") String userId);

}
