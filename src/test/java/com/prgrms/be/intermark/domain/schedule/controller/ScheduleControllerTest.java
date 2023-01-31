package com.prgrms.be.intermark.domain.schedule.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.prgrms.be.intermark.domain.schedule.dto.ScheduleCreateRequestDTO;
import com.prgrms.be.intermark.domain.schedule.dto.ScheduleUpdateRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

@WebMvcTest(ScheduleController.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
class ScheduleControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    @WithMockUser
    @Test
    @DisplayName("Success - 스케줄을 등록하면 저장한 스케줄 URL 반환")
    void createScheduleSuccess() throws Exception {
        // given
        ScheduleCreateRequestDTO scheduleCreateRequestDTO = ScheduleCreateRequestDTO.builder()
                .musicalId(1L)
                .startTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .build();

        long scheduleId = 0L;

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.registerModule(new JavaTimeModule())
                                .writeValueAsString(scheduleCreateRequestDTO)).with(csrf())
        );

        // then
        resultActions.andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/schedules/" + scheduleId))
                .andDo(print())
                .andDo(document("Save Schedule",
                                requestFields(
                                        fieldWithPath("musicalId").type(JsonFieldType.NUMBER).description("스케줄을 추가할 뮤지컬 id"),
                                        fieldWithPath("startTime").type(JsonFieldType.STRING).description("시작시간 (yyyy-MM-dd HH:mm)")
                                ),
                                responseHeaders(
                                        headerWithName("Location").description("등록된 스케줄 id를 포함한 URI")
                                )
                        )
                );
    }

    @WithMockUser
    @Test
    @DisplayName("Success - 스케줄을 수정하면 수정한 스케줄 URL 반환")
    void updateScheduleSuccess() throws Exception {
        // given
        ScheduleUpdateRequestDTO scheduleUpdateRequestDTO = ScheduleUpdateRequestDTO.builder()
                .startTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .build();

        long scheduleId = 0L;

        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .put("/api/v1/schedules/{scheduleId}", scheduleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.registerModule(new JavaTimeModule())
                        .writeValueAsString(scheduleUpdateRequestDTO)).with(csrf())
        );

        // then
        resultActions.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("Update Schedule",
                                pathParameters(
                                        parameterWithName("scheduleId").description("수정할 스케줄 id")
                                ),
                                requestFields(
                                        fieldWithPath("startTime").type(JsonFieldType.STRING).description("시작시간 (yyyy-MM-dd HH:mm)")
                                )
                        )
                );
    }
}