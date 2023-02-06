package com.prgrms.be.intermark.domain.ticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.be.intermark.domain.ticket.dto.TicketCreateRequestDTO;
import com.prgrms.be.intermark.domain.ticket.service.TicketService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityNotFoundException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
@WithMockUser(username = "1", roles = {"ADMIN"}, password = "")
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
class TicketControllerTest {

    @MockBean
    private TicketService ticketService;

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
                        .content(mapper.writeValueAsString(request)).with(csrf())
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

    @Nested
    @DisplayName("deleteTicket")
    class DeleteTicket {

        @Test
        @DisplayName("Success - 입력 받은 티켓 id 에 해당하는 티켓 환불에 성공하면 204 No Content")
        void deleteTicketSuccess() throws Exception {
            // given
            Long ticketId = 1L;
            doNothing().when(ticketService).deleteTicket(ticketId);

            // when
            ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                    .delete("/api/v1/tickets/{ticketId}", ticketId)
                    .with(csrf()));

            // then
            resultActions.andExpect(status().isNoContent())
                    .andDo(print())
                    .andDo(document("Ticket/delete",
                            pathParameters(
                                    parameterWithName("ticketId").description("삭제할 티켓 id")
                            )));
        }

        @Test
        @DisplayName("Fail - 입력 받은 티켓 id 가 없거나 이미 환불된 티켓이면 404 Not Found")
        void deleteTicketFailByNoTicket() throws Exception {
            // given
            Long ticketId = 1L;
            doThrow(EntityNotFoundException.class).when(ticketService).deleteTicket(ticketId);

            // when
            ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/tickets/{ticketId}", ticketId)
                    .with(csrf()));

            // then
            resultActions.andExpect(status().isNotFound())
                    .andDo(print())
                    .andDo(document("Ticket/delete",
                            pathParameters(
                                    parameterWithName("ticketId").description("삭제할 티켓 id")
                            )));
        }
    }
}