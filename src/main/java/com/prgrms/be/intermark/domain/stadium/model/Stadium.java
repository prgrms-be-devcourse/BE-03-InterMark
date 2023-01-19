package com.prgrms.be.intermark.domain.stadium.model;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.ticket.model.Ticket;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stadium")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Stadium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Column(name = "address", nullable = false, unique = true)
    private String address;

    @NotBlank
    @Column(name = "image_url", nullable = false, length = 2000)
    private String imageUrl;

    @OneToMany(mappedBy = "stadium")
    private List<Seat> seats = new ArrayList<>();

    @OneToMany(mappedBy = "stadium")
    private List<Ticket> tickets = new ArrayList<>();

    @OneToMany(mappedBy = "stadium")
    private List<Musical> musicals = new ArrayList<>();

    @Builder
    public Stadium(String name, String address, String imageUrl) {
        this.name = name;
        this.address = address;
        this.imageUrl = imageUrl;
    }
}
