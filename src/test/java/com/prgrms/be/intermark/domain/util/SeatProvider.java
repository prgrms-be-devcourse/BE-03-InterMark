package com.prgrms.be.intermark.domain.util;

import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SeatProvider {

    public static Seat createSeat(Stadium stadium) {
        return Seat.builder()
                .rowNum("A")
                .columnNum(1)
                .stadium(stadium)
                .build();
    }

    public static Seat createSeat(String rowNum, int columnNum, Stadium stadium) {
        return Seat.builder()
                .rowNum(rowNum)
                .columnNum(columnNum)
                .stadium(stadium)
                .build();
    }
}
