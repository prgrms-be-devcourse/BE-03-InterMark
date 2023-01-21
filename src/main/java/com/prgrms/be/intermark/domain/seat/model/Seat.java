package com.prgrms.be.intermark.domain.seat.model;

import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "seat",
        uniqueConstraints = {@UniqueConstraint(name = "stadium_seat_uk", columnNames = {"stadium_id", "row_num", "column_num"})}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "row_num", nullable = false, length = 2)
    private String rowNum;

    @Range(min = 1, max = 100)
    @Column(name = "column_num", nullable = false)
    private int columnNum;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id", referencedColumnName = "id", nullable = false)
    private Stadium stadium;

    @Builder
    public Seat(String rowNum, int columnNum, Stadium stadium) {
        this.rowNum = rowNum;
        this.columnNum = columnNum;
        this.stadium = stadium;
    }

    public void setStadium(Stadium stadium) {
        Assert.notNull(stadium, "Stadium cannot be null");

        if (Objects.nonNull(this.stadium)) {
            this.stadium.getSeats().remove(this);
        }

        this.stadium = stadium;
        stadium.getSeats().add(this);
    }
}
