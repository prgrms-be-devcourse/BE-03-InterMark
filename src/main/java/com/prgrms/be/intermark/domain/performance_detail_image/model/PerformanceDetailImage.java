package com.prgrms.be.intermark.domain.performance_detail_image.model;

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
import javax.validation.constraints.NotNull;

import com.prgrms.be.intermark.domain.performance.model.Performance;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
