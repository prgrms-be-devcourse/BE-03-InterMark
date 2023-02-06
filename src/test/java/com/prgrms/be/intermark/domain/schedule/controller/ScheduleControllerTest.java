package com.prgrms.be.intermark.domain.schedule.controller;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.prgrms.be.intermark.domain.schedule.dto.ScheduleCreateRequestDTO;
import com.prgrms.be.intermark.domain.schedule.dto.ScheduleFindResponseDTO;
import com.prgrms.be.intermark.domain.schedule.dto.ScheduleUpdateRequestDTO;
import com.prgrms.be.intermark.domain.schedule.service.ScheduleService;
import com.prgrms.be.intermark.domain.schedule_seat.dto.ScheduleSeatResponseDTO;
import com.prgrms.be.intermark.domain.schedule_seat.dto.ScheduleSeatResponseDTOs;

@WebMvcTest(ScheduleController.class)
@WithMockUser
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
class ScheduleControllerTest {

	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	@Autowired
	private MockMvc mockMvc;

	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	@Autowired
	private ObjectMapper mapper = new ObjectMapper();

	@MockBean
	private ScheduleService scheduleService;

	@Test
	@DisplayName("Success - 스케줄을 등록하면 저장한 스케줄 URL 반환 - createScheduleSuccess")
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

	@Test
	@DisplayName("Success - 스케줄을 수정하면 수정한 스케줄 URL 반환 - updateScheduleSuccess")
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

	@Test
	@DisplayName("Success - 스케줄을 삭제하면 No Content 반환 - deleteScheduleSuccess")
	void deleteScheduleSuccess() throws Exception {
		// given
		long scheduleId = 0L;

		// when
		ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
				.delete("/api/v1/schedules/{scheduleId}", scheduleId).with(csrf())
		);

		// then
		resultActions.andExpect(status().isNoContent())
				.andDo(print())
				.andDo(document("Delete Schedule",
								pathParameters(
										parameterWithName("scheduleId").description("삭제할 스케줄 id")
								)
						)
				);
	}

	@Nested
	@DisplayName("getSchedule")
	class GetSchedule {

		@Test
		@DisplayName("Success - 해당 스케줄 상세 정보를 조회 성공 시 200 Ok")
		void getScheduleSuccess() throws Exception {
			// given
			Long scheduleId = 1L;
			ScheduleFindResponseDTO findScheduleInfo = ScheduleFindResponseDTO.builder()
				.isDeleted(false)
				.musicalName("인터마크")
				.stadiumName("프로그래머스 교육장")
				.startTime(LocalDateTime.of(2023, 3, 15, 18, 30))
				.endTime(LocalDateTime.of(2023, 3, 15, 19, 0))
				.build();

			when(scheduleService.findSchedule(scheduleId)).thenReturn(findScheduleInfo);

			// when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/schedules/{scheduleId}", scheduleId)
				.accept(MediaType.APPLICATION_JSON)
				.with(csrf())
			);

			// then
			resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("isDeleted").isBoolean())
				.andExpect(jsonPath("musicalName").isString())
				.andExpect(jsonPath("stadiumName").isString())
				.andExpect(jsonPath("startTime").isString())
				.andExpect(jsonPath("endTime").isString())
				.andDo(print())
				.andDo(document("Schedule/get",
					pathParameters(
						parameterWithName("scheduleId").description("조회할 스케줄 id")
					),
					responseFields(
						fieldWithPath("isDeleted").type(JsonFieldType.BOOLEAN).description("삭제 여부"),
						fieldWithPath("musicalName").type(JsonFieldType.STRING).description("연관된 뮤지컬 제목"),
						fieldWithPath("stadiumName").type(JsonFieldType.STRING).description("연관된 공연장 이름"),
						fieldWithPath("startTime").type(JsonFieldType.STRING).description("뮤지컬 시작 시간"),
						fieldWithPath("endTime").type(JsonFieldType.STRING).description("뮤지컬 종료 시간")
					)));
		}

		@Test
		@DisplayName("Fail - 조회하고자 하는 스케줄이 없을 시 404 Not Found")
		void getScheduleFail() throws Exception {
			// given
			Long scheduleId = 1L;
			doThrow(EntityNotFoundException.class).when(scheduleService).findSchedule(scheduleId);

			// when
			ResultActions resultActions = mockMvc.perform(get("/api/v1/schedules/{scheduleId}", scheduleId)
				.accept(MediaType.APPLICATION_JSON)
				.with(csrf())
			);

			// then
			resultActions.andExpect(status().isNotFound())
				.andDo(print())
				.andDo(document("Schedule/get",
					pathParameters(
						parameterWithName("scheduleId").description("조회할 스케줄 id")
					)));
		}
	}
}