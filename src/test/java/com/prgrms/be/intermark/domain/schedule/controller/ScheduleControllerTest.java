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
	@DisplayName("Success - ???????????? ???????????? ????????? ????????? URL ?????? - createScheduleSuccess")
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
										fieldWithPath("musicalId").type(JsonFieldType.NUMBER).description("???????????? ????????? ????????? id"),
										fieldWithPath("startTime").type(JsonFieldType.STRING).description("???????????? (yyyy-MM-dd HH:mm)")
								),
								responseHeaders(
										headerWithName("Location").description("????????? ????????? id??? ????????? URI")
								)
						)
				);
	}

	@Test
	@DisplayName("Success - ?????? ???????????? ?????? ????????? ?????? ?????? ??? 200 Ok - getScheduleSeats")
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
								parameterWithName("scheduleId").description("????????? ????????? id")
						),
						responseFields(
								fieldWithPath("scheduleSeats").type(JsonFieldType.ARRAY).description("????????? ?????? ??????"),
								fieldWithPath("scheduleSeats[].seatId").type(JsonFieldType.NUMBER).description("????????? ?????? id"),
								fieldWithPath("scheduleSeats[].scheduleId").type(JsonFieldType.NUMBER).description("????????? ????????? id"),
								fieldWithPath("scheduleSeats[].isReserved").type(JsonFieldType.BOOLEAN).description("?????? ??????"),
								fieldWithPath("scheduleSeats[].seatNum").type(JsonFieldType.STRING).description("?????? ??????")
						)));
	}

	@Test
	@DisplayName("Success - ???????????? ???????????? ????????? ????????? URL ?????? - updateScheduleSuccess")
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
										parameterWithName("scheduleId").description("????????? ????????? id")
								),
								requestFields(
										fieldWithPath("startTime").type(JsonFieldType.STRING).description("???????????? (yyyy-MM-dd HH:mm)")
								)
						)
				);
	}

	@Test
	@DisplayName("Success - ???????????? ???????????? No Content ?????? - deleteScheduleSuccess")
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
										parameterWithName("scheduleId").description("????????? ????????? id")
								)
						)
				);
	}

	@Nested
	@DisplayName("getSchedule")
	class GetSchedule {

		@Test
		@DisplayName("Success - ?????? ????????? ?????? ????????? ?????? ?????? ??? 200 Ok")
		void getScheduleSuccess() throws Exception {
			// given
			Long scheduleId = 1L;
			ScheduleFindResponseDTO findScheduleInfo = ScheduleFindResponseDTO.builder()
				.isDeleted(false)
				.musicalName("????????????")
				.stadiumName("?????????????????? ?????????")
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
						parameterWithName("scheduleId").description("????????? ????????? id")
					),
					responseFields(
						fieldWithPath("isDeleted").type(JsonFieldType.BOOLEAN).description("?????? ??????"),
						fieldWithPath("musicalName").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
						fieldWithPath("stadiumName").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
						fieldWithPath("startTime").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
						fieldWithPath("endTime").type(JsonFieldType.STRING).description("????????? ?????? ??????")
					)));
		}

		@Test
		@DisplayName("Fail - ??????????????? ?????? ???????????? ?????? ??? 404 Not Found")
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
						parameterWithName("scheduleId").description("????????? ????????? id")
					)));
		}
	}
}