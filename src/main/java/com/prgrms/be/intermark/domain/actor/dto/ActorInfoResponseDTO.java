package com.prgrms.be.intermark.domain.actor.dto;

import com.prgrms.be.intermark.domain.actor.Actor;
import lombok.Builder;

@Builder
public record ActorInfoResponseDTO(String name, String imageUrl) {

    public static ActorInfoResponseDTO from(Actor actor) {
        return ActorInfoResponseDTO.builder()
                .name(actor.getName())
                .imageUrl(actor.getImageUrl())
                .build();
    }
}
