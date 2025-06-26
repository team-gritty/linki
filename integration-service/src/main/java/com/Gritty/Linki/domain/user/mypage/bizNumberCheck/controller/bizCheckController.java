package com.Gritty.Linki.domain.user.mypage.bizNumberCheck.controller;

import com.Gritty.Linki.domain.user.mypage.bizNumberCheck.DTO.RequestDTO;
import com.Gritty.Linki.domain.user.mypage.bizNumberCheck.DTO.ResponseDTO;
import com.Gritty.Linki.domain.user.mypage.bizNumberCheck.service.BizCheckService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Log4j2
@CrossOrigin(origins = "*")
public class bizCheckController {

    private final BizCheckService bizCheckService;

    @PostMapping("/v1/api/user/bizCheck")
    public ResponseEntity<ResponseDTO> bizCheck(RequestDTO requestDTO) throws IOException {
        log.info(requestDTO);

        MultipartFile file = requestDTO.getFile();
        log.info(file.getOriginalFilename());
        log.info(file.getSize());
        log.info(file.getContentType());

        ResponseDTO responseDTO = bizCheckService.check(requestDTO);

        return ResponseEntity.ok(responseDTO);
    }
}
