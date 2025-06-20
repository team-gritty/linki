package com.Gritty.Linki.domain.oAuth.account.service;

import com.Gritty.Linki.domain.oAuth.dto.JoinDTO;
import com.Gritty.Linki.entity.User;

public interface AccountService {

    void save(JoinDTO joinDTO);

    User find(String userLoginId, String userLoginPw);

    User find(String userLoginId);
}
