package com.prgrms.be.intermark.domain.musical_detail_image.model;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "musical_detail_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MusicalDetailImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "image_url", nullable = false, length = 2000)
    private String imageUrl;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musical_id", referencedColumnName = "id", nullable = false)
    private Musical musical;

    @Builder
    public MusicalDetailImage(Musical musical, String imageUrl) {
        this.musical = musical;
        this.imageUrl = imageUrl;
    }

    public void setMusical(Musical musical) {
        Assert.notNull(musical, "musical cannot be null");

        if (Objects.nonNull(this.musical)) {
            this.musical.getMusicalDetailImages().remove(this);
        }
        this.musical = musical;
        musical.getMusicalDetailImages().add(this);
    }


}
