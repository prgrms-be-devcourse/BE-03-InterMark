package com.prgrms.be.intermark.domain.util;

import com.prgrms.be.intermark.domain.actor.model.Actor;
import com.prgrms.be.intermark.domain.casting.model.Casting;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CastingProvider {

    public static Casting createCasting(Actor actor, Musical musical) {
        return Casting.builder()
                .actor(actor)
                .musical(musical)
                .build();
    }
}
