package com.prgrms.be.intermark.domain.actor.dto;

import com.prgrms.be.intermark.domain.actor.model.Actor;
import com.prgrms.be.intermark.domain.casting.model.Casting;
import lombok.Builder;

import java.util.List;

@Builder
public record ActorResponseDTO(String name, String profileImage) {

    public static ActorResponseDTO from(Actor actor) {
        return ActorResponseDTO.builder()
                .name(actor.getName())
                .profileImage(actor.getProfileImageUrl())
                .build();
    }

    public static List<ActorResponseDTO> listFromCastings(List<Casting> castings) {
        return castings.stream()
                .map(casting -> ActorResponseDTO.from(casting.getActor()))
                .toList();
    }
}
