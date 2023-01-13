package com.prgrms.be.intermark.domain.seat;

import com.prgrms.be.intermark.domain.performance_stadium.PerformanceStadium;
import com.prgrms.be.intermark.domain.schedule_seat.ScheduleSeat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "seat")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "row", nullable = false, length = 2)
    private String row;

    @Range(min = 1, max = 100)
    @Column(name = "column", nullable = false)
    private int column;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_stadium_id")
    private PerformanceStadium performanceStadium;

    @OneToMany(mappedBy = "seat")
    private List<ScheduleSeat> scheduleSeats = new ArrayList<>();

    @Builder
    public Seat(String row, int column, PerformanceStadium performanceStadium, List<ScheduleSeat> scheduleSeats) {
        this.row = row;
        this.column = column;
        this.performanceStadium = performanceStadium;
        this.scheduleSeats = scheduleSeats;
    }

    public void setPerformanceStadium(PerformanceStadium performanceStadium) {
        if (Objects.nonNull(this.performanceStadium)) {
            this.performanceStadium.getSchedules().remove(this);
        }

        this.performanceStadium = performanceStadium;
        performanceStadium.getSeats().add(this);
    }
}
