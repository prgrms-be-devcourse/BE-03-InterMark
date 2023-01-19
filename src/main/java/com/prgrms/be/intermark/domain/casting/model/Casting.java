package com.prgrms.be.intermark.domain.casting.model;

import com.prgrms.be.intermark.domain.actor.model.Actor;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "casting")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Casting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", referencedColumnName = "id", nullable = false)
    private Actor actor;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musical_id", referencedColumnName = "id", nullable = false)
    private Musical musical;

    @Builder
    public Casting(Actor actor, Musical musical) {
        this.actor = actor;
        this.musical = musical;
    }

    public void setActor(Actor actor) {
        Assert.notNull(actor, "actor cannot be null");

        if (Objects.nonNull(this.actor)) {
            this.actor.getCastings().remove(this);
        }
        this.actor = actor;
        actor.getCastings().add(this);
    }

    public void setMusical(Musical musical) {
        Assert.notNull(musical, "musical cannot be null");

        if (Objects.nonNull(this.musical)) {
            this.musical.getCastings().remove(this);
        }

        this.musical = musical;
        musical.getCastings().add(this);
    }
}
