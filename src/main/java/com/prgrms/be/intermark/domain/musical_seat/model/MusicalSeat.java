package com.prgrms.be.intermark.domain.musical_seat.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.springframework.util.Assert;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "musical_seat",
        uniqueConstraints = {@UniqueConstraint(name = "musical_seat_uk", columnNames = {"musical_id", "seat_id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MusicalSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musical_id", referencedColumnName = "id", nullable = false)
    private Musical musical;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", referencedColumnName = "id", nullable = false)
    private Seat seat;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_grade_id", referencedColumnName = "id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private SeatGrade seatGrade;

    @Builder
    public MusicalSeat(Musical musical, Seat seat, SeatGrade seatGrade) {
        this.isDeleted = false;
        this.musical = musical;
        this.seat = seat;
        this.seatGrade = seatGrade;
    }

    public void updateMusicalSeat(Musical musical, Seat seat, SeatGrade seatGrade) {
        setMusical(musical);
        setSeat(seat);
        setSeatGrade(seatGrade);
    }

    public void deleteMusicalSeat() {
        this.isDeleted = true;
    }

    public void setMusical(Musical musical) {
        Assert.notNull(musical, "musical cannot be null");

        if (Objects.nonNull(this.musical)) {
            this.musical.getMusicalSeats().remove(this);
        }
        this.musical = musical;
        musical.getMusicalSeats().add(this);
    }

    public void setSeat(Seat seat) {
        Assert.notNull(seat, "seat cannot be null");

        this.seat = seat;
    }

    public void setSeatGrade(SeatGrade seatGrade) {
        Assert.notNull(seatGrade, "seatGrade cannot be null");

        if (Objects.nonNull(this.seatGrade)) {
            this.seatGrade.getMusicalSeats().remove(this);
        }
        this.seatGrade = seatGrade;
        seatGrade.getMusicalSeats().add(this);
    }
}
