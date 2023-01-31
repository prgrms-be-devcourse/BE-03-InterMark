package com.prgrms.be.intermark.domain.util;

import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StadiumProvider {

    public static Stadium createStadium() {
        return Stadium.builder()
                .name("예술의 전당")
                .address("서울특별시")
                .imageUrl("abcefg")
                .build();
    }
}
