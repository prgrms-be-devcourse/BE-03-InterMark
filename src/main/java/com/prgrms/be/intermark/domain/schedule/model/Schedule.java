package com.prgrms.be.intermark.domain.schedule.model;

import java.time.LocalDateTime;
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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.springframework.util.Assert;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.schedule_seat.model.ScheduleSeat;
import com.prgrms.be.intermark.domain.ticket.model.Ticket;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "schedule",
        uniqueConstraints = {@UniqueConstraint(name = "musical_start_time_uk", columnNames = {"musical_id", "start_time"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musical_id", referencedColumnName = "id", nullable = false)
    private Musical musical;

    @OneToMany(mappedBy = "schedule")
    private List<Ticket> tickets = new ArrayList<>();

    @OneToMany(mappedBy = "schedule")
    private List<ScheduleSeat> scheduleSeats = new ArrayList<>();

    @Builder
    public Schedule(LocalDateTime startTime, LocalDateTime endTime, boolean isDeleted, Musical musical) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.isDeleted = isDeleted;
        this.musical = musical;
    }

    public boolean isOver(LocalDateTime time) {
        return this.endTime.isBefore(time);
    }

    public void setMusical(Musical musical) {
        Assert.notNull(musical, "Musical cannot be null");

        if (Objects.nonNull(this.musical)) {
            this.musical.getSchedules().remove(this);
        }

        this.musical = musical;
        musical.getSchedules().add(this);
    }

    public void setScheduleTime(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void deleteSchedule() {
        isDeleted = true;
    }
}
