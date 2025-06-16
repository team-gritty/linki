package com.Gritty.Linki.domain.oAuth.signUp.service;

import com.Gritty.Linki.domain.oAuth.dto.JoinDTO;

public interface AccountService {

    void save(JoinDTO joinDTO);
}
