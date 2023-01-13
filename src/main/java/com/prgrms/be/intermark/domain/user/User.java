package com.prgrms.be.intermark.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "user",
        uniqueConstraints = {@UniqueConstraint(name = "social_uk", columnNames = {"social", "social_id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(name = "social", nullable = false)
    private Social social;

    @NotNull
    @Column(name = "social_id", nullable = false, length = 64)
    private String socialId;

    @NotNull
    @Column(name = "refresh_token", nullable = false, unique = true)
    private String refreshToken;

    @Length(min = 2, max = 20)
    @NotBlank
    @Column(name = "username", nullable = false, unique = true, length = 20)
    private String username;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", nullable = false, length = 15)
    private UserRole role;

    @NotNull
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Nullable
    @Temporal(value = TemporalType.DATE)
    @Column(name = "birth")
    private Date birth;

    @Builder
    public User(Social social, String socialId, String refreshToken, String username, UserRole role, boolean isDeleted, Date birth) {
        this.social = social;
        this.socialId = socialId;
        this.refreshToken = refreshToken;
        this.username = username;
        this.role = role;
        this.isDeleted = isDeleted;
        this.birth = birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }
}
