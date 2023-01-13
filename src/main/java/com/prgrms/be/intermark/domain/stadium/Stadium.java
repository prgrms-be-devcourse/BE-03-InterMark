package com.prgrms.be.intermark.domain.stadium;

import com.prgrms.be.intermark.domain.performance_stadium.PerformanceStadium;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    @NotNull
    @Column(name = "address", nullable = false)
    private String address;

    @NotNull
    @Column(name = "image_url", nullable = false, length = 2000)
    private String imageUrl;

    @OneToMany(mappedBy = "stadium")
    private List<PerformanceStadium> performanceStadiums = new ArrayList<>();

    @Builder
    public Stadium(String name, String address, String imageUrl) {
        this.name = name;
        this.address = address;
        this.imageUrl = imageUrl;
    }
}
