package com.prgrms.be.intermark.domain.musical.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.be.intermark.common.dto.page.PageListIndexSize;
import com.prgrms.be.intermark.common.dto.page.PageResponseDTO;
import com.prgrms.be.intermark.domain.actor.dto.ActorResponseDTO;
import com.prgrms.be.intermark.domain.actor.model.Actor;
import com.prgrms.be.intermark.domain.actor.model.Gender;
import com.prgrms.be.intermark.domain.casting.model.Casting;
import com.prgrms.be.intermark.domain.musical.dto.MusicalCreateRequestDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalDetailImageResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalDetailResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSeatCreateRequestDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSeatGradeCreateRequestDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSeatGradeUpdateRequestDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSeatUpdateRequestDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSummaryResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalUpdateRequestDTO;
import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.MusicalDetailImage;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.musical.service.MusicalFacadeService;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import com.prgrms.be.intermark.util.TestUtil;

@WebMvcTest(MusicalController.class)
@WithMockUser(username = "1", roles = {"USER"}, password = "")
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
            .title("???????????? ?????????")
            .viewRating(ViewRating.ADULT)
            .genre(Genre.DRAMA)
            .description("??????????????? ??????????????????.")
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
    @DisplayName("Success ???????????? ????????? ?????? ???????????? ????????? ???????????? - createMusical")
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
                .file(detailImages2)
                .with(csrf()));

        // then
        verify(musicalFacadeService).create(any(MusicalCreateRequestDTO.class), any(MultipartFile.class), anyList());
        resultActions.andExpect(header().string("Location", "/api/v1/musicals/" + 1L))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("Musical/create",
                        requestPartFields("createRequestDto",
                                fieldWithPath("title").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("viewRating").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("genre").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("startDate").type(JsonFieldType.STRING).description("????????? ?????????"),
                                fieldWithPath("endDate").type(JsonFieldType.STRING).description("????????? ?????????"),
                                fieldWithPath("runningTime").type(JsonFieldType.NUMBER).description("????????? ????????????"),
                                fieldWithPath("managerId").type(JsonFieldType.NUMBER).description("????????? ????????? id"),
                                fieldWithPath("stadiumId").type(JsonFieldType.NUMBER).description("????????? ????????? id"),
                                fieldWithPath("actorIds").type(JsonFieldType.ARRAY).description("???????????? ????????? id ??????"),
                                fieldWithPath("seatGrades").type(JsonFieldType.ARRAY).description("???????????? ?????? ???????????? ?????? ??????"),
                                fieldWithPath("seatGrades[].seatGradeName").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("seatGrades[].seatGradePrice").type(JsonFieldType.NUMBER).description("???????????? ??????"),
                                fieldWithPath("seats").type(JsonFieldType.ARRAY).description("????????? ?????? ?????? ??????"),
                                fieldWithPath("seats[].seatId").type(JsonFieldType.NUMBER).description("?????? id"),
                                fieldWithPath("seats[].seatGradeName").type(JsonFieldType.STRING).description("????????? ????????? ???????????? ??????")),
                        requestParts(
                                partWithName("createRequestDto").ignored(),
                                partWithName("thumbnail").description("????????? ????????? ?????????"),
                                partWithName("detailImages").description("????????? ?????? ????????? ??????")
                        ),
                        responseHeaders(
                                headerWithName("Location").description("????????? ????????? ????????? ????????? URL"))
                ));
    }

    @DisplayName("Success - ????????? ????????? ?????? ??? ????????? ?????? ???????????? ?????? - getAllMusicals")
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
                                parameterWithName("page").description("????????? ?????????"),
                                parameterWithName("size").description("??? ???????????? ????????? ????????? ???")
                        ),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.ARRAY).description("????????? ???????????? ?????? ??????"),
                                fieldWithPath("data[0].musicalTitle").type(JsonFieldType.STRING).description("????????? ???????????? ??????"),
                                fieldWithPath("data[0].stadiumName").type(JsonFieldType.STRING).description("????????? ???????????? ???????????? ???????????? ??????"),
                                fieldWithPath("data[0].startDate").type(JsonFieldType.STRING).description("????????? ???????????? ?????? ??????"),
                                fieldWithPath("data[0].endDate").type(JsonFieldType.STRING).description("????????? ???????????? ????????? ??????"),
                                fieldWithPath("nowPageNumbers").type(JsonFieldType.ARRAY).description("????????? ?????????????????? ??? ????????? ???????????? ?????? ??????"),
                                fieldWithPath("nowPage").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("prev").type(JsonFieldType.BOOLEAN).description("?????? ????????? ?????? ??????"),
                                fieldWithPath("next").type(JsonFieldType.BOOLEAN).description("?????? ????????? ?????? ??????")
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

    @Test
    @DisplayName("Success - ?????????id??? ?????? ??? ????????? ?????? ?????? ?????? - getMusical")
    void getMusicalSuccess() throws Exception {
        // given
        Long musicalId = 1L;
        User user = TestUtil.createUser(SocialType.GOOGLE, "socialId", "nickname", UserRole.ROLE_ADMIN, false, LocalDate.now(), "email@naver.com");
        Stadium stadium = TestUtil.createStadium("name", "address", "imageUrl");
        Musical musical = TestUtil.createMusical("title", "description", LocalDate.now(), LocalDate.now().plusDays(5),
                "thumbnailUrl", ViewRating.ALL, Genre.COMEDY, 60, user, stadium);
        Actor actor = TestUtil.createActor("actorName", LocalDate.now(), "profileUrl", Gender.MALE);
        Casting casting = TestUtil.createCasting(actor, musical);
        MusicalDetailImage musicalDetailImage = TestUtil.createMusicalDetailImage("imageUrl", "fileName");
        musicalDetailImage.setMusical(musical);

        MusicalDetailResponseDTO answer = MusicalDetailResponseDTO.builder()
                .musicalTitle(musical.getTitle())
                .startDate(musical.getStartDate())
                .endDate(musical.getEndDate())
                .rate(musical.getViewRating())
                .genre(musical.getGenre())
                .thumbnailUrl(musical.getThumbnailUrl())
                .description(musical.getDescription())
                .runningTime(musical.getRunningTime())
                .stadiumName(stadium.getName())
                .actors(ActorResponseDTO.listFromCastings(List.of(casting)))
                .images(MusicalDetailImageResponseDTO.listFrom(musical.getDetailImages()))
                .build();

        when(musicalFacadeService.findMusicalById(musicalId))
                .thenReturn(answer);

        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/musicals/{musicalId}", musicalId));

        // then
        verify(musicalFacadeService).findMusicalById(any(Long.class));

        MvcResult mvcResult = resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("musicalTitle").value(answer.musicalTitle()))
                .andExpect(jsonPath("startDate").value(answer.startDate().toString()))
                .andExpect(jsonPath("endDate").value(answer.endDate().toString()))
                .andExpect(jsonPath("rate").value(answer.rate().toString()))
                .andExpect(jsonPath("genre").value(answer.genre().toString()))
                .andExpect(jsonPath("thumbnailUrl").value(answer.thumbnailUrl()))
                .andExpect(jsonPath("description").value(answer.description()))
                .andExpect(jsonPath("runningTime").value(answer.runningTime()))
                .andExpect(jsonPath("stadiumName").value(answer.stadiumName()))
                .andExpect(jsonPath("actors").isArray())
                .andExpect(jsonPath("images").isArray())
                .andExpect(jsonPath("actors[0].name").value(answer.actors().get(0).name()))
                .andExpect(jsonPath("actors[0].profileImage").value(answer.actors().get(0).profileImage()))
                .andExpect(jsonPath("images[0].musicalDetailImageUrl").value(answer.images().get(0).musicalDetailImageUrl()))
                .andDo(print())
                .andDo(document("Find Musical Detail",
                        pathParameters(
                                parameterWithName("musicalId").description("????????? ????????? id")
                        ),
                        responseFields(
                                fieldWithPath("musicalTitle").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("startDate").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                                fieldWithPath("endDate").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                                fieldWithPath("rate").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                                fieldWithPath("genre").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("thumbnailUrl").type(JsonFieldType.STRING).description("????????? ????????? ????????? URL"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("runningTime").type(JsonFieldType.NUMBER).description("????????? ????????????"),
                                fieldWithPath("stadiumName").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("images").type(JsonFieldType.ARRAY).description("????????? ?????? ????????? ?????????"),
                                fieldWithPath("images[0].musicalDetailImageUrl").type(JsonFieldType.STRING).description("????????? ?????? ????????? URL"),
                                fieldWithPath("actors").type(JsonFieldType.ARRAY).description("????????? ?????? ?????? ?????????"),
                                fieldWithPath("actors[0].name").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("actors[0].profileImage").type(JsonFieldType.STRING).description("?????? ????????? ????????? URL")
                        )
                ))
                .andReturn();
    }

    @Test
    @DisplayName("Success - ?????? ?????? ????????? id ??? ???????????? ????????? ????????? ????????????. - deleteMusical")
    void deleteMusicalSuccess() throws Exception {
        // given
        Long musicalId = 1L;
        doNothing().when(musicalFacadeService).deleteMusical(musicalId);

        // when
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/musicals/{musicalId}", musicalId)
                .with(csrf()));

        // then
        verify(musicalFacadeService).deleteMusical(musicalId);
        resultActions.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("Musical/delete",
                        pathParameters(
                                parameterWithName("musicalId").description("????????? ????????? id")
                        )
                ));
    }

    @Test
    @DisplayName("Success - ?????? ?????? ????????? id ??? ???????????? ????????? ????????? ????????????. - updateMusical")
    void updateMusicalSuccess() throws Exception {
        // given
        Long musicalId = 1L;

        MusicalSeatGradeUpdateRequestDTO musicalSeatGradeUpdateRequestDTO = MusicalSeatGradeUpdateRequestDTO.builder()
                .seatGradeName("VIP")
                .seatGradePrice(10000)
                .build();

        MusicalSeatUpdateRequestDTO musicalSeatUpdateRequestDTO = MusicalSeatUpdateRequestDTO.builder()
                .seatId(1L)
                .seatGradeName("VIP")
                .build();

        MusicalUpdateRequestDTO musicalUpdateRequestDTO = MusicalUpdateRequestDTO.builder()
                .title("change title")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(10))
                .description("change description")
                .viewRating(ViewRating.ALL)
                .genre(Genre.COMEDY)
                .runningTime(60)
                .managerId(1L)
                .stadiumId(1L)
                .actors(List.of(1L, 2L, 3L))
                .seatGrades(List.of(musicalSeatGradeUpdateRequestDTO))
                .seats(List.of(musicalSeatUpdateRequestDTO))
                .build();

        MockMultipartFile request = new MockMultipartFile("musicalUpdateRequestDTO", "", "application/json", objectMapper.writeValueAsString(musicalUpdateRequestDTO).getBytes());
        MockMultipartFile thumbnail = new MockMultipartFile("thumbnail", "thumbnail.jpg", "image/jpeg", "thumbnail".getBytes());
        MockMultipartFile detailImages1 = new MockMultipartFile("detailImages", "detail1.jpg", "image/jpeg", "detail1".getBytes());
        MockMultipartFile detailImages2 = new MockMultipartFile("detailImages", "detail2.jpg", "image/jpeg", "detail2".getBytes());

        final MockMultipartHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders.multipart("/api/v1/musicals/{musicalId}", musicalId);
        requestBuilder.with(req -> {
            req.setMethod("PUT");
            return req;
        });

        doNothing()
                .when(musicalFacadeService).update(any(Long.class), any(MusicalUpdateRequestDTO.class), any(MultipartFile.class), anyList());

        // when
        ResultActions resultActions = mockMvc.perform(requestBuilder
                .file(request)
                .file(thumbnail)
                .file(detailImages1)
                .file(detailImages2)
                .with(csrf()));

        // then
        verify(musicalFacadeService).update(any(Long.class), any(MusicalUpdateRequestDTO.class), any(MultipartFile.class), anyList());

        resultActions.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("Update Musical",
                        pathParameters(
                                parameterWithName("musicalId").description("????????? id")
                        ),
                        requestPartFields("musicalUpdateRequestDTO",
                                fieldWithPath("title").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("startDate").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("endDate").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("viewRating").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("genre").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("runningTime").type(JsonFieldType.NUMBER).description("????????? ????????????"),
                                fieldWithPath("managerId").type(JsonFieldType.NUMBER).description("???????????? ????????? ????????? id"),
                                fieldWithPath("stadiumId").type(JsonFieldType.NUMBER).description("????????? id"),
                                fieldWithPath("actors").type(JsonFieldType.ARRAY).description("?????? ?????????"),
                                fieldWithPath("seatGrades").type(JsonFieldType.ARRAY).description("???????????? ?????????"),
                                fieldWithPath("seatGrades[0].seatGradeName").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
                                fieldWithPath("seatGrades[0].seatGradePrice").type(JsonFieldType.NUMBER).description("?????? ?????? ??????"),
                                fieldWithPath("seats").type(JsonFieldType.ARRAY).description("?????? ?????????"),
                                fieldWithPath("seats[0].seatId").type(JsonFieldType.NUMBER).description("?????? id"),
                                fieldWithPath("seats[0].seatGradeName").type(JsonFieldType.STRING).description("?????? ?????? ??????")
                        ),
                        requestParts(
                                partWithName("musicalUpdateRequestDTO").ignored(),
                                partWithName("thumbnail").description("????????? ????????? ?????????"),
                                partWithName("detailImages").description("????????? ?????? ????????? ??????")
                        )));
    }

}