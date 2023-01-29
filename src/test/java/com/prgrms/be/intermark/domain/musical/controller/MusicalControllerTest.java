package com.prgrms.be.intermark.domain.musical.controller;

import com.prgrms.be.intermark.common.dto.page.PageListIndexSize;
import com.prgrms.be.intermark.common.dto.page.PageResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSummaryResponseDTO;
import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.musical.service.MusicalFacadeService;
import com.prgrms.be.intermark.domain.musical.service.MusicalService;
import com.prgrms.be.intermark.domain.schedule.service.ScheduleService;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.ticket.service.TicketService;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.util.TestUtil;
import org.assertj.core.api.Assertions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MusicalController.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
class MusicalControllerTest {

    @InjectMocks
    private MusicalController musicalController;

    @MockBean
    private MusicalFacadeService musicalFacadeService;

    @MockBean
    private MusicalService musicalService;

    @MockBean
    private ScheduleService scheduleService;

    @MockBean
    private TicketService ticketService;

    @Autowired
    private MockMvc mockMvc;



    @WithMockUser(username = "1",roles = {"USER"},password = "")
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