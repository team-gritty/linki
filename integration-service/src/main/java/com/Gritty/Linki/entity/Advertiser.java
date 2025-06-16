package com.Gritty.Linki.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
 public class Advertiser {
    
    @Id
      @Column(name = "advertiser_id", length = 25)  private String advertiserId;
    
   @Column(name = "business_number", length = 20, nullable = false)
  private String businessNumber;
    
  @Column(name = "company_name", length = 30, nullable = false)
  private String companyName;
    
     // 연관관계 매핑
     @OneToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // 광고주와 캠페인 다대일 관계. 캠페인 삭제 시 광고주 삭제되지 않음.
    @OneToMany(mappedBy = "advertiser", cascade = CascadeType.ALL)
    private List<Campaign> campaigns = new ArrayList<>();
    
    // 광고주와 리다이렉트 링크 일대다 관계. 리다이렉트 링크 삭제 시 광고주 삭제되지 않음.
     @OneToMany(mappedBy = "advertiser", cascade = CascadeType.ALL)
     private List<RedirectLinks> redirectLinks = new ArrayList<>();
    

}
