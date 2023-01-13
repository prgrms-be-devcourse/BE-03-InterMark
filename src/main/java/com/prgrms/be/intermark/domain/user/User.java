package com.prgrms.be.intermark.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "social")
    @Enumerated(value = EnumType.STRING)
    private Social social;

    @Column(name = "social_id")
    private String socialId;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "username")
    private String username;

    @Column(name = "role")
    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    @Column(name = "status")
    private Boolean status;

    @Temporal(value = TemporalType.DATE)
    @Column(name = "birth")
    private Date birth;

    @Builder
    public User(Social social, String socialId, String refreshToken, String username, UserRole role, Boolean status, Date birth) {
        this.social = social;
        this.socialId = socialId;
        this.refreshToken = refreshToken;
        this.username = username;
        this.role = role;
        this.status = status;
        this.birth = birth;
    }
}
