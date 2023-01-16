package com.prgrms.be.intermark.domain.schedule_seat;

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

import com.prgrms.be.intermark.domain.schedule.Schedule;
import com.prgrms.be.intermark.domain.seat.Seat;
import com.prgrms.be.intermark.domain.ticket.Ticket;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "schedule_seat")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ScheduleSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_reserved")
    private boolean isReserved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @OneToMany(mappedBy = "scheduleSeat")
    private List<Ticket> tickets = new ArrayList<>();

    @Builder
    public ScheduleSeat(boolean isReserved, Schedule schedule, Seat seat,
        List<Ticket> tickets) {
        this.isReserved = isReserved;
        this.schedule = schedule;
        this.seat = seat;
        this.tickets = tickets;
    }

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
