package com.ssg.subscribeservice.entity;

import com.ssg.subscribeservice.subsenum.SubscribeCode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Table(name = "subscribe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscribeEntity {

    @Id
    @Column(name = "subscribe_id", length = 25)
    private String subscribeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscribe_code", nullable = false, length = 10)
    private SubscribeCode subscribeCode;

    @Column(name = "subscribe_amount", nullable = false)
    private Integer subscribeAmount;

    @CreationTimestamp
    @Column(name = "subscribe_changed_at", nullable = false)
    private LocalDate subscribeChangedAt;

    @Column(name = "subscribe_name", length = 25, nullable = false)
    private String subscribeName;
}
