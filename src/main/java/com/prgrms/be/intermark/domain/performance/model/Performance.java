package com.prgrms.be.intermark.domain.performance.model;

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
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.prgrms.be.intermark.domain.casting.Casting;
import com.prgrms.be.intermark.domain.performance_stadium.PerformanceStadium;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "performance")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "running_time", nullable = false)
    private int runningTime;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(name = "possible_age", nullable = false, length = 10)
    private PerformanceRating possibleAge;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(name = "genre", nullable = false, length = 20)
    private Genre genre;

    @NotNull
    @Column(name = "thumbnail_url", nullable = false, length = 2000)
    private String thumbnailUrl;

    @NotNull
    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Column(name = "price", nullable = false)
    private int price;

    @OneToMany(mappedBy = "performance")
    private List<PerformanceStadium> performanceStadiums = new ArrayList<>();

    @OneToMany(mappedBy = "performance")
    private List<Casting> castings = new ArrayList<>();

    @OneToMany(mappedBy = "performance")
    private List<PerformanceDetailImage> performanceDetailImages = new ArrayList<>();
    @Builder
    public Performance(LocalDate startDate, LocalDate endDate, String name, int runningTime, PerformanceRating possibleAge, Genre genre, String thumbnailUrl, String description, int price) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.name = name;
        this.runningTime = runningTime;
        this.possibleAge = possibleAge;
        this.genre = genre;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
        this.price = price;
    }
}
