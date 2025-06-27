package com.Gritty.Linki.domain.user.mypage.bizNumberCheck.DTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RequestDTO {
    private String businessNumber;
    private MultipartFile file;
}
