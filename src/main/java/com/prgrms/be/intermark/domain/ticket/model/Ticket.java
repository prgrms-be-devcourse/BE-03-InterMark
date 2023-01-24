package com.prgrms.be.intermark.domain.ticket.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.user.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ticket", uniqueConstraints = {@UniqueConstraint(name = "ticket_uk", columnNames = {"schedule_id", "seat_id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Ticket {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Enumerated(value = EnumType.STRING)
	@Column(name = "status", nullable = false, length = 15)
	private TicketStatus ticketStatus;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
	private User user;

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

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "musical_id", referencedColumnName = "id", nullable = false)
	private Musical musical;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stadium_id", referencedColumnName = "id", nullable = false)
	private Stadium stadium;

	@Builder
	private Ticket(TicketStatus ticketStatus, User user, Schedule schedule, Seat seat, SeatGrade seatGrade, Musical musical, Stadium stadium) {
		Assert.notNull(user, "사용자가 존재하지 않습니다.");
		Assert.notNull(schedule, "스케줄이 존재하지 않습니다.");
		Assert.notNull(seat, "좌석이 존재하지 않습니다.");
		Assert.notNull(seatGrade, "좌석등급이 존재하지 않습니다.");
		Assert.notNull(musical, "뮤지컬이 존재하지 않습니다.");
		Assert.notNull(stadium, "공연장이 존재하지 않습니다.");

		if (schedule.isDeleted()) {
			throw new IllegalArgumentException("삭제된 스케줄입니다.");
		}

		this.ticketStatus = ticketStatus;
		this.user = user;
		this.schedule = schedule;
		this.seat = seat;
		this.seatGrade = seatGrade;
		this.musical = musical;
		this.stadium = stadium;
	}

	public boolean isDeleted() {
		return this.ticketStatus == TicketStatus.CANCELLED;
	}
	public void deleteTicket() {
		this.ticketStatus = TicketStatus.CANCELLED;
	}

	public void setUser(User user) {
		Assert.notNull(user, "user cannot be null");

		if (Objects.nonNull(this.user)) {
			this.user.getTickets().remove(this);
		}
		this.user = user;
		user.getTickets().add(this);
	}

	public void setSchedule(Schedule schedule) {
		Assert.notNull(schedule, "schedule cannot be null");

		if (Objects.nonNull(this.schedule)) {
			this.schedule.getTickets().remove(this);
		}
		this.schedule = schedule;
		schedule.getTickets().add(this);
	}

	public void setSeat(Seat seat) {
		Assert.notNull(seat, "seat cannot be null");

		this.seat = seat;
	}

	public void setSeatGrade(SeatGrade seatGrade) {
		Assert.notNull(seatGrade, "seatGrade cannot be null");

		if (Objects.nonNull(this.seatGrade)) {
			this.seatGrade.getTickets().remove(this);
		}
		this.seatGrade = seatGrade;
		seatGrade.getTickets().add(this);
	}

	public void setMusical(Musical musical) {
		Assert.notNull(musical, "musical cannot be null");

		if (Objects.nonNull(this.musical)) {
			this.musical.getTickets().remove(this);
		}
		this.musical = musical;
		musical.getTickets().add(this);
	}

	public void setStadium(Stadium stadium) {
		Assert.notNull(stadium, "stadium cannot be null");

		if (Objects.nonNull(this.stadium)) {
			this.stadium.getTickets().remove(this);
		}
		this.stadium = stadium;
		stadium.getTickets().add(this);
	}
}
