package com.prgrms.be.intermark.domain.musical.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.be.intermark.common.dto.page.PageListIndexSize;
import com.prgrms.be.intermark.common.dto.page.PageResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalCreateRequestDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSeatCreateRequestDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSeatGradeCreateRequestDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSummaryResponseDTO;
import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.musical.service.MusicalFacadeService;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.util.TestUtil;
import org.assertj.core.api.Assertions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @WithMockUser(username = "1", roles = {"USER"}, password = "")
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

    @WithMockUser(username = "1", roles = {"USER"}, password = "")
    @Test
    @DisplayName("Success - 뮤지컬 리스트 조회 시 뮤지컬 정보 리스트로 반환 - getAllMusicals")
    void getAllMusicalsSuccess() throws Exception {
        // given
        List<Musical> musicals = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            musicals.add(TestUtil.createMusical("title" + i, "description" + i, LocalDate.now(), LocalDate.now().plusDays(i), "thumnail" + i,
                    ViewRating.ALL, Genre.COMEDY, i, User.builder().nickname("nickName" + i).build(), Stadium.builder().name("stadium" + i).build()));
        }
        Pageable pageable = PageRequest.of(0, 10);
        Page<Musical> musicalPage = new PageImpl<>(musicals);
        PageResponseDTO<Musical, MusicalSummaryResponseDTO> result =
                new PageResponseDTO<>(musicalPage, MusicalSummaryResponseDTO::from, PageListIndexSize.MUSICAL_LIST_INDEX_SIZE);

        when(musicalFacadeService.findAllMusicals(any(Pageable.class)))
                .thenReturn(result);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/musicals")
                        .contentType("application/json")
                        .queryParam("page", "0")
                        .queryParam("size", "10")
        );

        // then
        verify(musicalFacadeService).findAllMusicals(any(Pageable.class));

        MvcResult mvcResult = resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("data").isArray())
                .andExpect(jsonPath("nowPageNumbers").isArray())
                .andExpect(jsonPath("nowPage").value(1))
                .andExpect(jsonPath("next").value(false))
                .andExpect(jsonPath("prev").value(false))
                .andDo(print())
                .andDo(document("Find All Musicals",
                        RequestDocumentation.requestParameters(
                                parameterWithName("page").description("조회할 페이지"),
                                parameterWithName("size").description("한 페이지에 들어갈 데이터 수")
                        ),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.ARRAY).description("조회한 데이터가 담긴 배열"),
                                fieldWithPath("data[0].musicalTitle").type(JsonFieldType.STRING).description("조회한 뮤지컬의 제목"),
                                fieldWithPath("data[0].stadiumName").type(JsonFieldType.STRING).description("조회한 뮤지컬이 진행되는 공연장의 이름"),
                                fieldWithPath("data[0].startDate").type(JsonFieldType.STRING).description("조회한 뮤지컬의 시작 날짜"),
                                fieldWithPath("data[0].endDate").type(JsonFieldType.STRING).description("조회한 뮤지컬의 끝나는 날짜"),
                                fieldWithPath("nowPageNumbers").type(JsonFieldType.ARRAY).description("화면에 표시해야하는 총 페이지 인덱스가 담긴 배열"),
                                fieldWithPath("nowPage").type(JsonFieldType.NUMBER).description("현재 페이지"),
                                fieldWithPath("prev").type(JsonFieldType.BOOLEAN).description("이전 페이지 버튼 유무"),
                                fieldWithPath("next").type(JsonFieldType.BOOLEAN).description("이후 페이지 버튼 유무")
                        )
                ))
                .andReturn();

        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        JSONArray data = jsonObject.getJSONArray("data");
        for (int i = 0; i < 10; i++) {
            String musicalTitle = data.getJSONObject(i).getString("musicalTitle");
            String stadiumName = data.getJSONObject(i).getString("stadiumName");
            String startDate = data.getJSONObject(i).getString("startDate");
            String endDate = data.getJSONObject(i).getString("endDate");

            Assertions.assertThat(result.getData().get(i).musicalTitle()).isEqualTo(musicalTitle);
            Assertions.assertThat(result.getData().get(i).stadiumName()).isEqualTo(stadiumName);
            Assertions.assertThat(result.getData().get(i).startDate()).isEqualTo(startDate);
            Assertions.assertThat(result.getData().get(i).endDate()).isEqualTo(endDate);
        }
    }
}