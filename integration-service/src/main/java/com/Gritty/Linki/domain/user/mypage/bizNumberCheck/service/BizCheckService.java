package com.Gritty.Linki.domain.user.mypage.bizNumberCheck.service;

import com.Gritty.Linki.domain.user.mypage.bizNumberCheck.DTO.RequestDTO;
import com.Gritty.Linki.domain.user.mypage.bizNumberCheck.DTO.ResponseDTO;

import java.io.IOException;

public interface BizCheckService {
    ResponseDTO check(RequestDTO requestDTO) throws IOException;
}
