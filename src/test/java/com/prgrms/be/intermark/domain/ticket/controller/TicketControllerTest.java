package com.prgrms.be.intermark.domain.ticket.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.persistence.EntityNotFoundException;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.be.intermark.domain.ticket.dto.TicketCreateRequestDTO;
import com.prgrms.be.intermark.domain.ticket.service.TicketService;

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
    @DisplayName("Success - ?????? ?????? ??? created ?????? ?????? ?????? ??? Location ??? URI ?????? - createTicket")
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
                                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("??????????????? ????????? id"),
                                        fieldWithPath("scheduleSeatId").type(JsonFieldType.NUMBER).description("??????????????? ??????????????? id")
                                ),
                                responseHeaders(
                                        headerWithName("Location").description("????????? ticket ??? URI")
                                )
                        )
                );
    }

    @Nested
    @DisplayName("deleteTicket")
    class DeleteTicket {

        @Test
        @DisplayName("Success - ?????? ?????? ?????? id ??? ???????????? ?????? ????????? ???????????? 204 No Content")
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
                                    parameterWithName("ticketId").description("????????? ?????? id")
                            )));
        }

        @Test
        @DisplayName("Fail - ?????? ?????? ?????? id ??? ????????? ?????? ????????? ???????????? 404 Not Found")
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
                                    parameterWithName("ticketId").description("????????? ?????? id")
                            )));
        }
    }
}