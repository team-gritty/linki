package com.Gritty.Linki.domain.user.mypage.bizNumberCheck.service;


import com.Gritty.Linki.domain.user.mypage.bizNumberCheck.DTO.OcrResultDTO;
import com.Gritty.Linki.domain.user.mypage.bizNumberCheck.DTO.RequestDTO;

import java.io.IOException;

public interface OcrService {
    OcrResultDTO performOcr(RequestDTO requestDTO) throws IOException;
}
