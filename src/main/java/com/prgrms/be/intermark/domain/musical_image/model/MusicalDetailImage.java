package com.prgrms.be.intermark.domain.musical_image.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.util.Assert;

import com.prgrms.be.intermark.domain.musical.model.Musical;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "musical_detail_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MusicalDetailImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name="original_file_name", nullable = false)
    private String originalFileName;

    @NotBlank
    @Column(name = "image_url", nullable = false, length = 2000)
    private String imageUrl;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musical_id", referencedColumnName = "id", nullable = false)
    private Musical musical;

    @Builder
    public MusicalDetailImage(Musical musical, String originalFileName, String imageUrl) {
        this.musical = musical;
        this.originalFileName = originalFileName;
        this.imageUrl = imageUrl;
    }

    public void setMusical(Musical musical) {
        Assert.notNull(musical, "musical cannot be null");

        if (Objects.nonNull(this.musical)) {
            this.musical.getDetailImages().remove(this);
        }
        this.musical = musical;
        musical.getDetailImages().add(this);
    }


}
