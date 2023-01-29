package com.prgrms.be.intermark.domain.musical.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.be.intermark.domain.musical.dto.MusicalCreateRequestDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSeatCreateRequestDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSeatGradeCreateRequestDTO;
import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.musical.service.MusicalFacadeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MusicalController.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
class MusicalControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MusicalFacadeService musicalFacadeService;

    private MusicalSeatGradeCreateRequestDTO seatGradeVIP = MusicalSeatGradeCreateRequestDTO.builder()
            .seatGradeName("VIP")
            .seatGradePrice(50000)
            .build();

    private MusicalSeatGradeCreateRequestDTO seatGradeR = MusicalSeatGradeCreateRequestDTO.builder()
            .seatGradeName("R")
            .seatGradePrice(30000)
            .build();

    private MusicalSeatCreateRequestDTO musicalSeatVIP = MusicalSeatCreateRequestDTO.builder()
            .seatId(1L)
            .seatGradeName(seatGradeVIP.seatGradeName())
            .build();

    private MusicalSeatCreateRequestDTO musicalSeatR = MusicalSeatCreateRequestDTO.builder()
            .seatId(1L)
            .seatGradeName(seatGradeR.seatGradeName())
            .build();

    private MusicalCreateRequestDTO musical = MusicalCreateRequestDTO.builder()
            .title("마르코팀 성장기")
            .viewRating(ViewRating.ADULT)
            .genre(Genre.DRAMA)
            .description("마르코팀의 성장기입니다.")
            .startDate(LocalDate.of(2022, 10, 12))
            .endDate(LocalDate.of(2023, 3, 15))
            .runningTime(100)
            .managerId(1L)
            .stadiumId(1L)
            .actorIds(List.of(1L, 2L))
            .seatGrades(List.of(seatGradeVIP, seatGradeR))
            .seats(List.of(musicalSeatVIP, musicalSeatR))
            .build();

    @Test
    @DisplayName("성공 - 정상적인 뮤지컬 값이 들어오면 등록에 성공한다 - createMusical")
    void createMusicalSuccess() throws Exception {
        // given
        MockMultipartFile createRequestDto = new MockMultipartFile("createRequestDto", "", "application/json", objectMapper.writeValueAsString(musical).getBytes());
        MockMultipartFile thumbnail = new MockMultipartFile("thumbnail", "thumbnail.jpg", "image/jpeg", "thumbnail".getBytes());
        MockMultipartFile detailImages1 = new MockMultipartFile("detailImages", "detail1.jpg", "image/jpeg", "detail1".getBytes());
        MockMultipartFile detailImages2 = new MockMultipartFile("detailImages", "detail2.jpg", "image/jpeg", "detail2".getBytes());

        when(musicalFacadeService.create(any(MusicalCreateRequestDTO.class), any(MultipartFile.class), anyList())).thenReturn(1L);

        // when
        ResultActions resultActions = mockMvc.perform(multipart("/api/v1/musicals")
                .file(createRequestDto)
                .file(thumbnail)
                .file(detailImages1)
                .file(detailImages2));

        // then
        verify(musicalFacadeService).create(any(MusicalCreateRequestDTO.class), any(MultipartFile.class), anyList());
        resultActions.andExpect(header().string("Location", "/api/v1/musicals/" + 1L))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("Musical/create",
                        requestPartFields("createRequestDto",
                                fieldWithPath("title").type(JsonFieldType.STRING).description("뮤지컬 제목"),
                                fieldWithPath("viewRating").type(JsonFieldType.STRING).description("뮤지컬 관람등급"),
                                fieldWithPath("genre").type(JsonFieldType.STRING).description("뮤지컬 장르"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("뮤지컬 설명"),
                                fieldWithPath("startDate").type(JsonFieldType.STRING).description("뮤지컬 시작일"),
                                fieldWithPath("endDate").type(JsonFieldType.STRING).description("뮤지컬 종료일"),
                                fieldWithPath("runningTime").type(JsonFieldType.NUMBER).description("뮤지컬 상영시간"),
                                fieldWithPath("managerId").type(JsonFieldType.NUMBER).description("뮤지컬 관리자 id"),
                                fieldWithPath("stadiumId").type(JsonFieldType.NUMBER).description("뮤지컬 공연장 id"),
                                fieldWithPath("actorIds").type(JsonFieldType.ARRAY).description("캐스팅된 배우들 id 목록"),
                                fieldWithPath("seatGrades").type(JsonFieldType.ARRAY).description("뮤지컬이 갖는 좌석등급 정보 목록"),
                                fieldWithPath("seatGrades[].seatGradeName").type(JsonFieldType.STRING).description("좌석등급 이름"),
                                fieldWithPath("seatGrades[].seatGradePrice").type(JsonFieldType.NUMBER).description("좌석등급 가격"),
                                fieldWithPath("seats").type(JsonFieldType.ARRAY).description("뮤지컬 좌석 정보 목록"),
                                fieldWithPath("seats[].seatId").type(JsonFieldType.NUMBER).description("좌석 id"),
                                fieldWithPath("seats[].seatGradeName").type(JsonFieldType.STRING).description("좌석에 배정될 좌석등급 이름")),
                        requestParts(
                                partWithName("createRequestDto").ignored(),
                                partWithName("thumbnail").description("뮤지컬 썸네일 이미지"),
                                partWithName("detailImages").description("뮤지컬 상세 이미지 목록")
                        ),

                        responseHeaders(
                                headerWithName("Location").description("생성된 뮤지컬 조회가 가능한 URL"))
                ));
    }
}