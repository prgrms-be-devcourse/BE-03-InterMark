package com.prgrms.be.intermark.domain.seatgrade.model;

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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.Length;
import org.springframework.util.Assert;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical_seat.model.MusicalSeat;
import com.prgrms.be.intermark.domain.schedule_seat.model.ScheduleSeat;
import com.prgrms.be.intermark.domain.ticket.model.Ticket;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seat_grade",
		uniqueConstraints = {@UniqueConstraint(name = "musical_seat_grade_uk", columnNames = {"musical_id", "name"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SeatGrade {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Length(max = 10)
	@Column(name = "name", nullable = false, length = 10)
	private String name;

	@Positive
	@Column(name = "price")
	private int price;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "musical_id", referencedColumnName = "id", nullable = false)
	private Musical musical;

	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted;

	@OneToMany(mappedBy = "seatGrade")
	private List<ScheduleSeat> scheduleSeats = new ArrayList<>();

	@OneToMany(mappedBy = "seatGrade")
	private List<Ticket> tickets = new ArrayList<>();

	@NotNull
	@OneToMany(mappedBy = "seatGrade")
	private List<MusicalSeat> musicalSeats = new ArrayList<>();

	@Builder
	public SeatGrade(String name, int price, Musical musical) {
		this.name = name;
		this.price = price;
		this.musical = musical;
		this.isDeleted = false;
	}

	public void deleteSeatGrade() {
		this.isDeleted = true;
	}

	public void setMusical(Musical musical) {
		Assert.notNull(musical, "musical cannot be null");

		if (Objects.nonNull(this.musical)) {
			this.musical.getSeatGrades().remove(this);
		}

		this.musical = musical;
		musical.getSeatGrades().add(this);
	}
}
