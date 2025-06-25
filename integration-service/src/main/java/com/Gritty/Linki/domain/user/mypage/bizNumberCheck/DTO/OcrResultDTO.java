package com.Gritty.Linki.domain.user.mypage.bizNumberCheck.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OcrResultDTO {
    private String ocrNumber;
    private String companyName;
}
