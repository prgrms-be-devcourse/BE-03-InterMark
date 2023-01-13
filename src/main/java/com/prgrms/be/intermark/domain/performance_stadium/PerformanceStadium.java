package com.prgrms.be.intermark.domain.performance_stadium;

import com.prgrms.be.intermark.domain.performance.Performance;
import com.prgrms.be.intermark.domain.stadium.Stadium;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "performance_stadium")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PerformanceStadium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", referencedColumnName = "id")
    private Performance performance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id", referencedColumnName = "id")
    private Stadium stadium;

    @Builder
    public PerformanceStadium(Performance performance, Stadium stadium) {
        this.performance = performance;
        this.stadium = stadium;
    }
}
