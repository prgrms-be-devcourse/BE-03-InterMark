package com.prgrms.be.intermark.domain.schedule.controller;

import com.prgrms.be.intermark.domain.schedule.service.ScheduleService;
import com.prgrms.be.intermark.domain.schedule_seat.dto.ScheduleSeatResponseDTO;
import com.prgrms.be.intermark.domain.schedule_seat.dto.ScheduleSeatResponseDTOs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScheduleController.class)
@WithMockUser
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduleService scheduleService;

    @Test
    @DisplayName("Success - 해당 스케줄의 좌석 정보를 조회 성공 시 200 Ok - getScheduleSeats")
    void getScheduleSeatsSuccess() throws Exception {
        // given
        Long scheduleId = 1L;
        ScheduleSeatResponseDTO scheduleSeat1 = ScheduleSeatResponseDTO.builder()
                .scheduleId(scheduleId)
                .seatId(1L)
                .seatNum("A1")
                .isReserved(true)
                .build();
        ScheduleSeatResponseDTO scheduleSeat2 = ScheduleSeatResponseDTO.builder()
                .scheduleId(scheduleId)
                .seatId(2L)
                .seatNum("A2")
                .isReserved(true)
                .build();
        ScheduleSeatResponseDTOs scheduleSeats = ScheduleSeatResponseDTOs.builder()
                .scheduleSeats(List.of(scheduleSeat1, scheduleSeat2))
                .build();

        when(scheduleService.findScheduleSeats(scheduleId)).thenReturn(scheduleSeats);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/schedules/{scheduleId}/seats", scheduleId)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("scheduleSeats").isArray())
                .andExpect(jsonPath("$.scheduleSeats[0].seatId").isNumber())
                .andExpect(jsonPath("$.scheduleSeats[0].scheduleId").isNumber())
                .andExpect(jsonPath("$.scheduleSeats[0].isReserved").isBoolean())
                .andExpect(jsonPath("$.scheduleSeats[0].seatNum").isString())
                .andDo(print())
                .andDo(document("ScheduleSeats/get",
                        pathParameters(
                                parameterWithName("scheduleId").description("연관된 스케줄 id")
                        ),
                        responseFields(
                                fieldWithPath("scheduleSeats").type(JsonFieldType.ARRAY).description("스케줄 좌석 목록"),
                                fieldWithPath("scheduleSeats[].seatId").type(JsonFieldType.NUMBER).description("연관된 좌석 id"),
                                fieldWithPath("scheduleSeats[].scheduleId").type(JsonFieldType.NUMBER).description("연관된 스케줄 id"),
                                fieldWithPath("scheduleSeats[].isReserved").type(JsonFieldType.BOOLEAN).description("예매 여부"),
                                fieldWithPath("scheduleSeats[].seatNum").type(JsonFieldType.STRING).description("좌석 번호")
                        )));
    }
}