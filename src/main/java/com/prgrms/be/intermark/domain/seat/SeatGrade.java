package com.prgrms.be.intermark.domain.seat;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.Length;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seat_grade")
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
	@Max(100)
	@Column(name = "percent", nullable = false)
	private Double percent;

	@OneToMany(mappedBy = "seatGrade")
	private List<Seat> seats = new ArrayList<>();

	@Builder
	public SeatGrade(String name, Double percent, List<Seat> seats) {
		this.name = name;
		this.percent = percent;
		this.seats = seats;
	}
}
