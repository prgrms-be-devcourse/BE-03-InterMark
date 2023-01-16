package com.prgrms.be.intermark.domain.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

import com.prgrms.be.intermark.domain.ticket.Ticket;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users",
        uniqueConstraints = {@UniqueConstraint(name = "social_uk", columnNames = {"social_type", "social_id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(min = 2, max = 25)
    @NotBlank
    @Column(name = "user_name", nullable = false, length = 25) // 구글 서비스에서의 최대 이름 길이 제약 25자 이내
    private String userName;

    @Column(name = "password", nullable = false, length = 72) // bcypt encoding 시 최대 72자
    private String password;

    @Email
    @Column(name = "email", nullable = false, unique = true) // 일단 length 제약 없이 두기
    private String email;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", nullable = false, length = 15)
    private UserRole role;

    @NotNull
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Nullable
    @Column(name = "birth")
    private LocalDate birth;

    @OneToMany(mappedBy = "user")
    private List<Ticket> tickets = new ArrayList<>();

    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(name = "social_type", nullable = false)
    private SocialType socialType;

    @NotNull
    @Column(name = "social_id", nullable = false, length = 64)
    private String socialId;

    @Column(name = "refresh_token", unique = true)
    private String refreshToken;

    @Builder
    public User(String userName, String password, String email, UserRole role, boolean isDeleted, @Nullable LocalDate birth, List<Ticket> tickets, SocialType socialType, String socialId, String refreshToken) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.role = role;
        this.isDeleted = isDeleted;
        this.birth = birth;
        this.tickets = tickets;
        this.socialType = socialType;
        this.socialId = socialId;
        this.refreshToken = refreshToken;
    }

    public void setRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }
    public User setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getUserRoleKey() {
        return role.getKey();
    }
}
