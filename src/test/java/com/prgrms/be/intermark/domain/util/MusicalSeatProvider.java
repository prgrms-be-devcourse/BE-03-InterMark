package com.prgrms.be.intermark.domain.util;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical_seat.model.MusicalSeat;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MusicalSeatProvider {

    public static MusicalSeat createMusicalSeat(Musical musical, Seat seat, SeatGrade seatGrade) {
        return MusicalSeat.builder()
                .musical(musical)
                .seat(seat)
                .seatGrade(seatGrade)
                .build();
    }
}
