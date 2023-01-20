package com.prgrms.be.intermark.domain.musical.model;

import com.prgrms.be.intermark.domain.casting.model.Casting;
import com.prgrms.be.intermark.domain.musical_detail_image.model.MusicalDetailImage;
import com.prgrms.be.intermark.domain.musical_seat.model.MusicalSeat;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.seat.model.SeatGrade;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.ticket.model.Ticket;
import com.prgrms.be.intermark.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "musical")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Musical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank
    @Enumerated(value = EnumType.STRING)
    @Column(name = "view_rating", nullable = false, length = 10)
    private ViewRating viewRating;

    @NotBlank
    @Column(name = "thumbnail_url", nullable = false, length = 2000)
    private String thumbnailUrl;

    @NotBlank
    @Enumerated(value = EnumType.STRING)
    @Column(name = "genre", nullable = false, length = 20)
    private Genre genre;

    @NotNull
    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Positive
    @Column(name = "running_time", nullable = false)
    private int runningTime;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id", referencedColumnName = "id", nullable = false)
    private Stadium stadium;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "musical")
    private List<Casting> castings = new ArrayList<>();

    @OneToMany(mappedBy = "musical")
    private List<MusicalDetailImage> musicalDetailImages = new ArrayList<>();

    @OneToMany(mappedBy = "musical")
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "musical")
    private List<SeatGrade> seatGrades = new ArrayList<>();

    @OneToMany(mappedBy = "musical")
    private List<Ticket> tickets = new ArrayList<>();

    @OneToMany(mappedBy = "musical")
    private List<MusicalSeat> musicalSeats = new ArrayList<>();

    @Builder
    public Musical(String title, ViewRating viewRating, String thumbnailUrl, Genre genre, String description, LocalDate startDate, LocalDate endDate, int runningTime, Stadium stadium, User user) {
        this.title = title;
        this.viewRating = viewRating;
        this.thumbnailUrl = thumbnailUrl;
        this.genre = genre;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.runningTime = runningTime;
        this.isDeleted = false;
        this.stadium = stadium;
        this.user = user;
    }

    public void setStadium(Stadium stadium) {
        Assert.notNull(stadium, "Stadium cannot be null");

        if (Objects.nonNull(this.stadium)) {
            this.stadium.getMusicals().remove(this);
        }
        this.stadium = stadium;
        stadium.getMusicals().add(this);
    }

    public void setUser(User user) {
        Assert.notNull(user, "User cannot be null");

        this.user = user;
    }
}
