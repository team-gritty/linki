package com.ssg.paymentservice.dto.responsedto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayInfluencerEmailNameResponseDto {
    private String userName;
    private String userEmail;
}
