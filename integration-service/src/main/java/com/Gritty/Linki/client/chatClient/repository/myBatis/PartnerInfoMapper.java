package com.Gritty.Linki.client.chatClient.repository.myBatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PartnerInfoMapper {
    String findPartnerId(@Param("proposalId") String proposalId, @Param("loginUserId") String loginUserId);
}