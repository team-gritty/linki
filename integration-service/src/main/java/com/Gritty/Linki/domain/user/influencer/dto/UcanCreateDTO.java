package com.Gritty.Linki.domain.user.influencer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UcanCreateDTO {

    private String documentName;
    private String customValue;
    private String participant1Name;
    private String participant1Email;

    private String participant2Name;
    private String participant2Email;
    private String advertiserName;
    private String influencerName;

    private String contractStartDateYear;
    private String contractStartDateMonth;
    private String contractStartDateDay;

    private String contractEndDateYear;
    private String contractEndDateMonth;
    private String contractEndDateDay;

    private String writtenDateYear;
    private String writtenDateMonth;
    private String writtenDateDay;

    private String businessNumber;
    private String advertiserAddress;
    private String influencerAddress;
    private String adDeliveryUrl;
    private String contractSpecialTerms;

    private String influencerChannelUrl;
}
