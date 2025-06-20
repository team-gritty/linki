package com.Gritty.Linki.domain.user.influencer.dto;

import com.Gritty.Linki.vo.enums.ContractStatus;
import lombok.*;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractDTO {
private String contractId;
private String contractTitle;
private String documentId;
private ContractStatus contractStatus;
private LocalDate contractStartDate;
private LocalDate contractEndDate;
private BigDecimal contractAmount;
private LocalDateTime contractCreatedAt;
private LocalDateTime contractCompletedAt;
private LocalDate contractPaymentDate;
private String contractSpecialTerms;
private String pdfFilePath;
private Boolean adDeliveryStatus;
private String eventType;
private String documentName;
private String proposalId;

}
