package com.prgrms.be.intermark.domain.schedule_seat.model;

import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.seat.model.SeatGrade;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "schedule_seat",
        uniqueConstraints = {@UniqueConstraint(name = "schedule_seat_uk", columnNames = {"schedule_id", "seat_id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ScheduleSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_reserved")
    private boolean isReserved;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", referencedColumnName = "id", nullable = false)
    private Schedule schedule;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", referencedColumnName = "id", nullable = false)
    private Seat seat;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_grade_id", referencedColumnName = "id", nullable = false)
    private SeatGrade seatGrade;

    @Builder
    public ScheduleSeat(boolean isReserved, Schedule schedule, Seat seat, SeatGrade seatGrade) {
        this.isReserved = isReserved;
        this.schedule = schedule;
        this.seat = seat;
        this.seatGrade = seatGrade;
    }

    public void setSchedule(Schedule schedule) {
        Assert.notNull(schedule, "schedule cannot be null");

        if (Objects.nonNull(this.schedule)) {
            this.schedule.getScheduleSeats().remove(this);
        }

        this.schedule = schedule;
        schedule.getScheduleSeats().add(this);
    }

    public void setSeat(Seat seat) {
        Assert.notNull(seat, "seat cannot be null");

        this.seat = seat;
    }

    public void setSeatGrade(SeatGrade seatGrade) {
        Assert.notNull(seatGrade, "seatGrade cannot be null");

        if (Objects.nonNull(this.seatGrade)) {
            this.seatGrade.getScheduleSeats().remove(this);
        }

        this.seatGrade = seatGrade;
        seatGrade.getScheduleSeats().add(this);
    }
}