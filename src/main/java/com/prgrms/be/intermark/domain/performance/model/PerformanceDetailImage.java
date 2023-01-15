package com.prgrms.be.intermark.domain.performance.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "parformance_detail_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PerformanceDetailImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private Performance performance;

    @NotNull
    @Column(name = "image_url", nullable = false, length = 2000)
    private String imageUrl;

    @Builder
    public PerformanceDetailImage(Performance performance, String imageUrl) {
        this.performance = performance;
        this.imageUrl = imageUrl;
    }

    public void setPerformance(Performance performance){
        if(Objects.nonNull(this.performance)){
            this.performance.getPerformanceDetailImages().remove(this);
        }
        this.performance = performance;
        performance.getPerformanceDetailImages().add(this);
    }


}
