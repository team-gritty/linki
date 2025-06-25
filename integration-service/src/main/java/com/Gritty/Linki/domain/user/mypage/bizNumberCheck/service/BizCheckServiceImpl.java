package com.Gritty.Linki.domain.user.mypage.bizNumberCheck.service;

import com.Gritty.Linki.domain.user.mypage.bizNumberCheck.DTO.RequestDTO;
import com.Gritty.Linki.domain.user.mypage.bizNumberCheck.DTO.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Log4j2
public class BizCheckServiceImpl implements BizCheckService {


    private final NtsBizCheckService ntsBizCheckService;
    private final OcrService ocrService;
    private final AiBizCheckService aiBizCheckService;

    @Override
    public ResponseDTO check(RequestDTO requestDTO) throws IOException {
        ResponseDTO responseDTO = new ResponseDTO();

        responseDTO.setBusinessNumber(requestDTO.getBusinessNumber());

        // 1. 국세청 사업자 검증
        if (!ntsBizCheckService.check(requestDTO)) {
            log.info("사업자등록증체크완료");
            responseDTO.setValid(false);
            responseDTO.setMessage("국세청 계속사업자 검증 실패");
            responseDTO.setOcrNumber("국세청 사업자 검증 실패로 OCR 추출 안함");
            return responseDTO;
        }

        // 2. OCR 사업자 번호 추출
        responseDTO.setOcrNumber(ocrService.performOcr(requestDTO));

        log.info(responseDTO.toString());
        log.info("OCR사업자번호추출");


        // 3. GPT 사업자 번호 일치 검증
        String check = "유저 입력 사업자 번호 : " + requestDTO.getBusinessNumber() + "OCR 사업자 번호 : " + responseDTO.getOcrNumber();
        String result = aiBizCheckService.check("bizNumber.json",check);

        if (result.equals("true")) {
            responseDTO.setValid(true);
        }else {
            responseDTO.setValid(false);
            responseDTO.setMessage("AI 사업자 번호 매칭 실패");
        }





        return responseDTO;
    }
}
