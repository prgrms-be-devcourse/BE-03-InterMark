package com.prgrms.be.intermark.domain.seat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Range;

import com.prgrms.be.intermark.domain.performance_stadium.PerformanceStadium;
import com.prgrms.be.intermark.domain.schedule_seat.ScheduleSeat;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seat")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "row_number", nullable = false, length = 2)
    private String rowNumber;

    @Range(min = 1, max = 100)
    @Column(name = "column_number", nullable = false)
    private int columnNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_stadium_id")
    private PerformanceStadium performanceStadium;

    @OneToMany(mappedBy = "seat")
    private List<ScheduleSeat> scheduleSeats = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_grade_id", referencedColumnName = "id")
    private SeatGrade seatGrade;

    @Builder
    public Seat(String rowNumber, int columnNumber, PerformanceStadium performanceStadium, List<ScheduleSeat> scheduleSeats, SeatGrade seatGrade) {
        this.rowNumber = rowNumber;
        this.columnNumber = columnNumber;
        this.performanceStadium = performanceStadium;
        this.scheduleSeats = scheduleSeats;
        this.seatGrade = seatGrade;
    }

    public void setPerformanceStadium(PerformanceStadium performanceStadium) {
        if (Objects.nonNull(this.performanceStadium)) {
            this.performanceStadium.getSchedules().remove(this);
        }

        this.performanceStadium = performanceStadium;
        performanceStadium.getSeats().add(this);
    }

    public void setSeatGrade(SeatGrade seatGrade) {
        if (Objects.nonNull(this.seatGrade)) {
            this.seatGrade.getSeats().remove(this);
        }
        this.seatGrade = seatGrade;
        seatGrade.getSeats().add(this);
    }
}
