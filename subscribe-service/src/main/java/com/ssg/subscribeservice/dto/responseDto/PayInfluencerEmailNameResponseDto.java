package com.ssg.subscribeservice.dto.responseDto;

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
