package com.prgrms.be.intermark.domain.schedule_seat;

import com.prgrms.be.intermark.domain.performance_stadium.PerformanceStadium;
import com.prgrms.be.intermark.domain.schedule.Schedule;
import com.prgrms.be.intermark.domain.seat.Seat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "seat")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ScheduleSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    public void setSchedule(Schedule schedule) {
        if (Objects.nonNull(this.schedule)) {
            this.schedule.getScheduleSeats().remove(this);
        }

        this.schedule = schedule;
        schedule.getScheduleSeats().add(this);
    }

    public void setSeat(Seat seat) {
        if (Objects.nonNull(this.seat)) {
            this.seat.getScheduleSeats().remove(this);
        }

        this.seat = seat;
        seat.getScheduleSeats().add(this);
    }
}
