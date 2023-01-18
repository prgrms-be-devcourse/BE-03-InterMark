package com.prgrms.be.intermark.domain.casting;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.prgrms.be.intermark.domain.actor.Actor;
import com.prgrms.be.intermark.domain.performance.model.Performance;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private Actor actor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private Performance performance;

    @Builder
    public Casting(Actor actor, Performance performance) {
        this.actor = actor;
        this.performance = performance;
    }

    public void setActor(Actor actor) {
        if (this.actor != null) {
            this.actor.getCastings().remove(this);
        }
        this.actor = actor;
        actor.getCastings().add(this);
    }

    public void setPerformance(Performance performance) {
        if (this.performance != null) {
            this.performance.getCastings().remove(this);
        }

        this.performance = performance;
        performance.getCastings().add(this);
    }
}
