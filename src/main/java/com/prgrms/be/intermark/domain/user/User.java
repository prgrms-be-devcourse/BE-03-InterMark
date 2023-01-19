package com.prgrms.be.intermark.domain.user;

import com.prgrms.be.intermark.domain.ticket.model.Ticket;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user",
        uniqueConstraints = {@UniqueConstraint(name = "social_uk", columnNames = {"social_type", "social_id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(name = "email", nullable = false, unique = true) // 일단 length 제약 없이 두기
    private String email;

    @Length(min = 2, max = 25)
    @NotBlank
    @Column(name = "nickname", nullable = false, length = 25)
    private String nickname;

    @NotBlank
    @Enumerated(value = EnumType.STRING)
    @Column(name = "social", nullable = false)
    private SocialType social;

    @NotBlank
    @Column(name = "social_id", nullable = false, length = 64)
    private String socialId;

    @NotBlank
    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", nullable = false, length = 15)
    private UserRole role;

    @NotBlank
    @Column(name = "refresh_token", nullable = false, unique = true)
    private String refreshToken;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Nullable
    @Column(name = "birth")
    private LocalDate birth;

    @OneToMany(mappedBy = "user")
    private List<Ticket> tickets = new ArrayList<>();

    @Builder
    public User(SocialType social, String socialId, String nickname, UserRole role,  String email) {
        this.social = social;
        this.socialId = socialId;
        this.nickname = nickname;
        this.role = role;
        this.isDeleted = false;
        this.email = email;
    }

    public void setRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }

    public void setNickname(String nickname){
        this.nickname = nickname;
    }
    public String getUserRoleKey() {
        return role.getKey();
    }
}
