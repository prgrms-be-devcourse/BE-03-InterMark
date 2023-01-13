package com.prgrms.be.intermark.domain.ticket;

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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.prgrms.be.intermark.domain.schedule_seat.ScheduleSeat;
import com.prgrms.be.intermark.domain.user.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ticket")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Ticket {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Positive
	@Column(name = "price", nullable = false)
	private int price;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "status", nullable = false, length = 15)
	private TicketStatus ticketStatus;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_seat_id", referencedColumnName = "id")
	private ScheduleSeat scheduleSeat;

	@Builder
	public Ticket(int price, TicketStatus ticketStatus, User user, ScheduleSeat scheduleSeat) {
		this.price = price;
		this.ticketStatus = ticketStatus;
		this.user = user;
		this.scheduleSeat = scheduleSeat;
	}

	public void setUser(User user) {
		if (Objects.nonNull(this.user)) {
			this.user.getTickets().remove(this);
		}
		this.user = user;
		user.getTickets().add(this);
	}

	public void setScheduleSeat(ScheduleSeat scheduleSeat) {
		if (Objects.nonNull(this.scheduleSeat)) {
			this.scheduleSeat.getTickets().remove(this);
		}
		this.scheduleSeat = scheduleSeat;
		scheduleSeat.getTickets().add(this);
	}
}
