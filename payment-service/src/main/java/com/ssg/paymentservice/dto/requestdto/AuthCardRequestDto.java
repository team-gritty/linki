package com.ssg.paymentservice.dto.requestdto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthCardRequestDto {
    private String customerKey;
    private String authKey;
}
