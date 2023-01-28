package com.prgrms.be.intermark.domain.ticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.be.intermark.domain.musical.service.MusicalFacadeService;
import com.prgrms.be.intermark.domain.schedule.service.ScheduleService;
import com.prgrms.be.intermark.domain.ticket.dto.TicketCreateRequestDTO;
import com.prgrms.be.intermark.domain.ticket.service.TicketService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
class TicketControllerTest {

    @InjectMocks
    private TicketController ticketController;

    @MockBean
    private TicketService ticketService;

    @MockBean
    private MusicalFacadeService musicalFacadeService;

    @MockBean
    private ScheduleService scheduleService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Success - 예매 등록 시 created 상태 코드 반환 및 Location 에 URI 반환 - createTicket")
    void getAllMusicalsSuccess() throws Exception {
        // given
        TicketCreateRequestDTO request = TicketCreateRequestDTO.builder()
                .userId(1L)
                .scheduleSeatId(1L)
                .build();

        Long savedTicketId = 1L;

        when(ticketService.createTicket(any(TicketCreateRequestDTO.class)))
                .thenReturn(savedTicketId);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/tickets")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request))
        );

        // then
        verify(ticketService).createTicket(any(TicketCreateRequestDTO.class));

        resultActions.andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/tickets/" + savedTicketId))
                .andDo(print())
                .andDo(document("Save Ticket",
                                requestFields(
                                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("예매하려는 사용자 id"),
                                        fieldWithPath("scheduleSeatId").type(JsonFieldType.NUMBER).description("예매하려는 스케줄좌석 id")
                                ),
                                responseHeaders(
                                        headerWithName("Location").description("예매된 ticket 의 URI")
                                )
                        )
                );
    }
}