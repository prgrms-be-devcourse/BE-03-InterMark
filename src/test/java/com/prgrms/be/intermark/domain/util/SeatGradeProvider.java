package com.prgrms.be.intermark.domain.util;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SeatGradeProvider {

    public static SeatGrade createSeatGrade(Musical musical) {
        return SeatGrade.builder()
                .name("VIP")
                .price(10000)
                .musical(musical)
                .build();
    }

    public static SeatGrade createSeatGrade(String name, int price, Musical musical) {
        return SeatGrade.builder()
                .name(name)
                .price(price)
                .musical(musical)
                .build();
    }
}
