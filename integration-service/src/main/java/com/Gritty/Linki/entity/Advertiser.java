package com.Gritty.Linki.entity;


import com.Gritty.Linki.util.IdGenerator;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
  * 광고주 엔티티
  */
 @Entity
 @Table(name = "advertiser")
 @Getter
 @Setter
 @NoArgsConstructor(access = AccessLevel.PROTECTED)
 @AllArgsConstructor
 @Builder

 public class Advertiser {
    
    @Id
      @Column(name = "advertiser_id", length = 25)  private String advertiserId;
    
   @Column(name = "business_number", length = 20, nullable = false)
  private String businessNumber;
    
  @Column(name = "company_name", length = 30, nullable = false)
  private String companyName;

 @Column(name = "user_id", length = 25, nullable = false)
 private String userId;

     // 연관관계 매핑
     @OneToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    // 광고주와 캠페인 다대일 관계. 캠페인 삭제 시 광고주 삭제되지 않음.
    @Builder.Default
    @OneToMany(mappedBy = "advertiser", cascade = CascadeType.ALL)
    private List<Campaign> campaigns = new ArrayList<>();
    
    // 광고주와 리다이렉트 링크 일대다 관계. 리다이렉트 링크 삭제 시 광고주 삭제되지 않음.
    @Builder.Default
     @OneToMany(mappedBy = "advertiser", cascade = CascadeType.ALL)
     private List<RedirectLinks> redirectLinks = new ArrayList<>();



}
