package com.prgrms.be.intermark.domain.actor;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Entity
@Table(name = "actor")
@NoArgsConstructor
@Getter
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @NotNull
    @Temporal(value = TemporalType.DATE)
    @Column(name = "birth", nullable = false)
    private Date birth;

    @NotNull
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    @NotNull
    @Column(name = "image_url", nullable = false, length = 2000)
    private String imageUrl;

    @Builder
    public Actor(String name, Date birth, Gender gender, String imageUrl) {
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.imageUrl = imageUrl;
    }
}
