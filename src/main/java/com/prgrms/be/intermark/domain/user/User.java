package com.prgrms.be.intermark.domain.user;

import com.prgrms.be.intermark.domain.ticket.model.Ticket;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user",
        uniqueConstraints = {@UniqueConstraint(name = "social_uk", columnNames = {"social", "social_id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Enumerated(value = EnumType.STRING)
    @Column(name = "social", nullable = false)
    private Social social;

    @NotBlank
    @Column(name = "social_id", nullable = false, length = 64)
    private String socialId;

    @NotBlank
    @Column(name = "refresh_token", nullable = false, unique = true)
    private String refreshToken;

    @Length(min = 2, max = 20)
    @NotBlank
    @Column(name = "nickname", nullable = false, unique = true, length = 20)
    private String nickname;

    @NotBlank
    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", nullable = false, length = 15)
    private UserRole role;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Nullable
    @Column(name = "birth")
    private LocalDate birth;

    @OneToMany(mappedBy = "user")
    private List<Ticket> tickets = new ArrayList<>();

    @Builder
    public User(Social social, String socialId, String refreshToken, String nickname, UserRole role, boolean isDeleted, LocalDate birth) {
        this.social = social;
        this.socialId = socialId;
        this.refreshToken = refreshToken;
        this.nickname = nickname;
        this.role = role;
        this.isDeleted = isDeleted;
        this.birth = birth;
    }

    public void setBirth(LocalDate birth) {
        this.birth = birth;
    }
}
