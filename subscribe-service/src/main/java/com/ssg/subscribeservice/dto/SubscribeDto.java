package com.ssg.subscribeservice.dto;

import com.ssg.subscribeservice.subsenum.SubscribeCode;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscribeDto {
    private String subscribeId;
    private SubscribeCode subscribeCode;
    private Integer subscribeAmount;
    private LocalDate subscribeChangedAt;
    private String subscribeName;
}
