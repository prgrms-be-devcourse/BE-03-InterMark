package com.prgrms.be.intermark.domain.musical.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.prgrms.be.intermark.common.dto.ImageResponseDTO;
import com.prgrms.be.intermark.common.dto.page.PageListIndexSize;
import com.prgrms.be.intermark.common.dto.page.PageResponseDTO;
import com.prgrms.be.intermark.common.service.ImageUploadService;
import com.prgrms.be.intermark.domain.actor.model.Actor;
import com.prgrms.be.intermark.domain.actor.service.ActorService;
import com.prgrms.be.intermark.domain.casting.model.Casting;
import com.prgrms.be.intermark.domain.casting.service.CastingService;
import com.prgrms.be.intermark.domain.musical.dto.MusicalCreateRequestDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalDetailResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSeatCreateRequestDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSeatGradeCreateRequestDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSummaryResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalUpdateRequestDTO;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.MusicalDetailImage;
import com.prgrms.be.intermark.domain.musical_seat.model.MusicalSeat;
import com.prgrms.be.intermark.domain.musical_seat.service.MusicalSeatService;
import com.prgrms.be.intermark.domain.schedule.service.ScheduleService;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.seat.service.SeatService;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import com.prgrms.be.intermark.domain.seatgrade.service.SeatGradeService;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.stadium.service.StadiumService;
import com.prgrms.be.intermark.domain.ticket.model.Ticket;
import com.prgrms.be.intermark.domain.ticket.service.TicketService;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MusicalFacadeService {

	private static final String THUMBNAIL_PATH = "musical/thumbnail/";
	private static final String DETAIL_IMAGES_PATH = "musical/detailImages/";

	private final MusicalService musicalService;
	private final StadiumService stadiumService;
	private final UserService userService;
	private final SeatGradeService seatGradeService;
	private final ImageUploadService imageUploadService;
	private final MusicalDetailImageService musicalDetailImageService;
	private final MusicalSeatService musicalSeatService;
	private final CastingService castingService;
	private final TicketService ticketService;
	private final ScheduleService scheduleService;
	private final SeatService seatService;
	private final ActorService actorService;

    @Transactional
    public Long create(
            MusicalCreateRequestDTO createRequestDto,
            MultipartFile thumbnail,
            List<MultipartFile> detailImages
    ) {
        Musical createdMusical = createRequestDto.toEntity();

        ImageResponseDTO thumbnailInfo = imageUploadService.uploadImage(thumbnail,THUMBNAIL_PATH);
        Stadium stadium = stadiumService.findById(createRequestDto.stadiumId());
        User manager = userService.findByIdForFacade(createRequestDto.managerId());
        setMusicalAssociation(createdMusical, thumbnailInfo, stadium, manager);
        Musical savedMusical = musicalService.save(createdMusical);

        List<ImageResponseDTO> detailImagesInfo = imageUploadService.uploadImages(detailImages,DETAIL_IMAGES_PATH);
        List<MusicalDetailImage> musicalDetailImages = setMusicalDetailImagesAssociation(detailImagesInfo, savedMusical);
        musicalDetailImageService.save(musicalDetailImages);

        List<SeatGrade> seatGrades = setSeatGradesAssociation(createRequestDto.seatGrades(), savedMusical);
        seatGradeService.save(seatGrades);

        List<MusicalSeat> musicalSeats = setMusicalSeatsAssocaition(createRequestDto.seats(), savedMusical);
        musicalSeatService.save(musicalSeats);

        List<Casting> castings = setCastingsAssociation(createRequestDto.actorIds(), savedMusical);
        castingService.save(castings);

        return savedMusical.getId();
    }

    @Transactional
    public void update(
            Long musicalId,
            MusicalUpdateRequestDTO musicalUpdateRequestDTO,
            MultipartFile thumbnailImage,
            List<MultipartFile> detailImages
    ) {
        Musical musical = musicalService.findMusicalById(musicalId);

        if (scheduleService.existsByMusical(musical)) {
            throw new IllegalArgumentException("이미 뮤지컬의 스케줄이 존재합니다.");
        }

        if (ticketService.existsByMusical(musical)) {
            throw new IllegalArgumentException("이미 예약된 뮤지컬입니다.");
        }

        ImageResponseDTO thumbnailInfo = imageUploadService.uploadImage(thumbnailImage, THUMBNAIL_PATH);
        Stadium stadium = stadiumService.findById(musicalUpdateRequestDTO.stadiumId());
        User manager = userService.findByIdForFacade(musicalUpdateRequestDTO.managerId());

        seatGradeService.update(musicalUpdateRequestDTO.seatGrades(), musical);
        musicalSeatService.update(musicalUpdateRequestDTO.seats(), musicalUpdateRequestDTO.stadiumId(), musical);
        castingService.update(musicalUpdateRequestDTO.actors(), musical);

        List<ImageResponseDTO> detailImagesInfo = imageUploadService.uploadImages(detailImages, DETAIL_IMAGES_PATH);
        musicalDetailImageService.update(detailImagesInfo, musical);
        musicalService.updateMusical(musical, musicalUpdateRequestDTO, thumbnailInfo.path(), stadium, manager);
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<Musical, MusicalSummaryResponseDTO> findAllMusicals(Pageable pageable) {
        Page<Musical> musicalPage = musicalService.findAllMusicals(pageable);
        return new PageResponseDTO<>(musicalPage, MusicalSummaryResponseDTO::from, PageListIndexSize.MUSICAL_LIST_INDEX_SIZE);
    }

    @Transactional(readOnly = true)
    public MusicalDetailResponseDTO findMusicalById(Long musicalId) {
        Musical musical = musicalService.findMusicalById(musicalId);
        return MusicalDetailResponseDTO.from(musical);
    }

    @Transactional
    public void deleteMusical(Long musicalId) {
        Musical musical = musicalService.findMusicalById(musicalId);

        boolean hasReservedTicket = musical.getTickets()
                .stream()
                .anyMatch(Ticket::isReserved);

        if (hasReservedTicket) {
            throw new RuntimeException("예매된 티켓이 있어 뮤지컬을 삭제할 수 없습니다");
        }

        musicalService.deleteMusical(musical);
        castingService.deleteAllByMusical(musical);
        musicalDetailImageService.deleteAllByMusical(musical);
        scheduleService.deleteAllByMusical(musical);
        seatGradeService.deleteAllByMusical(musical);
        musicalSeatService.deleteAllByMusical(musical);
    }

    private void setMusicalAssociation(Musical musical, ImageResponseDTO thumbnailInfo, Stadium stadium,
                                       User manager) {
        musical.setThumbnailUrl(thumbnailInfo.path());
        musical.setStadium(stadium);
        musical.setUser(manager);
    }

    private List<MusicalDetailImage> setMusicalDetailImagesAssociation(
            List<ImageResponseDTO> detailImagesInfo,
            Musical musical
    ) {
        return detailImagesInfo.stream()
                .map(imageResponse ->
                        MusicalDetailImage.builder()
                                .musical(musical)
                                .originalFileName(imageResponse.originalFileName())
                                .imageUrl(imageResponse.path())
                                .build()
                ).toList();
    }

    private List<SeatGrade> setSeatGradesAssociation(
            List<MusicalSeatGradeCreateRequestDTO> createRequestDTOs,
            Musical musical
    ) {
        return createRequestDTOs
                .stream()
                .map(seatGradeDTO -> {
                    SeatGrade seatGrade = seatGradeDTO.toEntity();
                    seatGrade.setMusical(musical);
                    return seatGrade;
                })
                .toList();
    }

    private List<MusicalSeat> setMusicalSeatsAssocaition(List<MusicalSeatCreateRequestDTO> createRequestDTOs, Musical musical) {
        return createRequestDTOs
                .stream()
                .map(musicalSeatDTO -> {
                    Seat seat = seatService.findById(musicalSeatDTO.seatId());
                    SeatGrade seatGrade = seatGradeService.findByNameAndMusical(musicalSeatDTO.seatGradeName(), musical);
                    return MusicalSeat.builder()
                            .seat(seat)
                            .musical(musical)
                            .seatGrade(seatGrade)
                            .build();
                })
                .toList();
    }

    private List<Casting> setCastingsAssociation(List<Long> actorIds, Musical musical) {
        return actorIds
                .stream()
                .map(actorId -> {
                    Actor actor = actorService.findById(actorId);
                    return Casting.builder()
                            .actor(actor)
                            .musical(musical)
                            .build();
                })
                .toList();
    }
}
