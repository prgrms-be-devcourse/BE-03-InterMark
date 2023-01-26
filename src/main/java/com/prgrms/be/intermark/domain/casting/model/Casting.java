package com.prgrms.be.intermark.domain.casting.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.util.Assert;

import com.prgrms.be.intermark.domain.actor.model.Actor;
import com.prgrms.be.intermark.domain.musical.model.Musical;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "casting")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Casting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

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
        this.isDeleted = false;
        this.actor = actor;
        this.musical = musical;
    }

    public void deleteCasting() {
        this.isDeleted = true;
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
