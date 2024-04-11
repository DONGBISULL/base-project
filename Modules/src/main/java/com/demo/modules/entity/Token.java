package com.demo.modules.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "token")
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
//
public class Token extends BaseEntity {
    public Token() {
    }

    @Builder
    public Token(Member member, String refreshToken, Date expirationDate) {
        this.member = member;
        this.refreshToken = refreshToken;
        this.expirationDate = expirationDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // 구분을 위한 키값

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "expiration_date")
    private Date expirationDate;


}
