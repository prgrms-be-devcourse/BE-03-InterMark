package com.prgrms.be.intermark.auth;

import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import com.prgrms.be.intermark.domain.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

import static com.prgrms.be.intermark.auth.constant.JwtConstants.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OAuthController.class)
@AutoConfigureRestDocs
class OAuthControllerTest {

    @Value("${jwt.secret.access}")
    private String ACCESS_SECRET_KEY;

    @Value("${jwt.secret.refresh}")
    private String REFRESH_SECRET_KEY;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    TokenProvider tokenProvider;

    @Nested
    @DisplayName("tokenRefresh")
    @WithMockUser
    class TokenRefresh {

        Key accessKey;
        Key refreshKey;
        User user = User.builder()
                .social(SocialType.GOOGLE)
                .socialId("1")
                .role(UserRole.ROLE_USER)
                .nickname("이수영")
                .email("example1@gmail.com")
                .build();

        @PostConstruct
        void initialize() {
            accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(ACCESS_SECRET_KEY));
            refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(REFRESH_SECRET_KEY));
        }

        @Test
        @DisplayName("Success - refresh 토큰까지 만료되었는 경우 refresh 토큰 갱신 후 갱신된 access token 반환.")
        public void tokenRefreshSuccess() throws Exception {
            // given
            String accessToken = createExpiredAccessToken(1L, UserRole.ROLE_USER);
            String refreshToken = createRefreshToken(1L, UserRole.ROLE_USER);
            String newAccessToken = createAccessToken(1L, UserRole.ROLE_USER);
            // when
            when(tokenProvider.validate(anyString()))
                    .thenReturn(true);
            when(tokenProvider.getExpiredTokenClaims(anyString()))
                    .thenReturn(getExpiredClaims(accessToken));
            when(userService.changeRefreshToken(anyLong(), any(UserRole.class), anyString()))
                    .thenReturn(Optional.of(refreshToken));
            when(tokenProvider.createAceessToken(anyLong(), any(UserRole.class)))
                    .thenReturn(newAccessToken);
            ResultActions actions = mockMvc.perform(get("/refresh")
                    .header("Authorization", "Bearer " + accessToken)
                    .cookie(new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken)));
            // then
            verify(tokenProvider, times(2)).validate(anyString());
            verify(tokenProvider).getExpiredTokenClaims(anyString());
            verify(userService).changeRefreshToken(anyLong(), any(UserRole.class), anyString());
            verify(tokenProvider).createAceessToken(anyLong(), any(UserRole.class));
            actions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("token").value(newAccessToken))
                    .andDo(print());
        }

        @Test
        @DisplayName("Success - refresh 토큰까지 만료되지 않은 경우 들어온 access token 그대로 반환.")
        public void tokenRefreshNotExpiredSuccess() throws Exception {
            // given
            String accessToken = createAccessToken(1L, UserRole.ROLE_USER);
            String refreshToken = createRefreshToken(1L, UserRole.ROLE_USER);
            // when
            when(tokenProvider.validate(anyString()))
                    .thenReturn(true);
            when(tokenProvider.getExpiredTokenClaims(anyString()))
                    .thenReturn(getExpiredClaims(accessToken));
            ResultActions actions = mockMvc.perform(get("/refresh")
                    .header("Authorization", "Bearer " + accessToken)
                    .cookie(new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken)));
            // then
            verify(tokenProvider).validate(anyString());
            verify(tokenProvider).getExpiredTokenClaims(anyString());
            actions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("token").value(accessToken))
                    .andDo(print());
        }

        @Test
        @DisplayName("Fail - access token이 validate 하지 않은 경우 400 반환.")
        public void tokenRefreshNotValidAccessTokenFail() throws Exception {
            // given
            String accessToken = createAccessToken(1L, UserRole.ROLE_USER);
            String refreshToken = createRefreshToken(1L, UserRole.ROLE_USER);
            // when
            when(tokenProvider.validate(anyString()))
                    .thenReturn(false);
            ResultActions actions = mockMvc.perform(get("/refresh")
                    .header("Authorization", "Bearer " + accessToken)
                    .cookie(new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken)));
            // then
            verify(tokenProvider).validate(anyString());

            actions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value("access token이 validate 하지 않습니다."))
                    .andDo(print())
                    .andDo(document("refresh-access-token-not-valid",
                            responseFields(
                                    fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
                                    fieldWithPath("errors").type(JsonFieldType.ARRAY).optional().description("발생한 에러 리스트"),
                                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("예외 발생 시간")
                            )));
        }

        @Test
        @DisplayName("Fail - refresh token이 validate 하지 않은 경우 400 반환.")
        public void tokenRefreshNotValidRefreshTokenFail() throws Exception {
            // given
            String accessToken = createExpiredAccessToken(1L, UserRole.ROLE_USER);
            String refreshToken = createRefreshToken(1L, UserRole.ROLE_USER);
            // when
            when(tokenProvider.validate(accessToken))
                    .thenReturn(true);
            when(tokenProvider.getExpiredTokenClaims(accessToken))
                    .thenReturn(getExpiredClaims(accessToken));
            when(tokenProvider.validate(refreshToken))
                    .thenReturn(false);
            ResultActions actions = mockMvc.perform(get("/refresh")
                    .header("Authorization", "Bearer " + accessToken)
                    .cookie(new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken)));
            // then
            verify(tokenProvider).validate(accessToken);
            verify(tokenProvider).getExpiredTokenClaims(accessToken);
            verify(tokenProvider).validate(refreshToken);
            actions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value("refresh token이 validate 하지 않습니다."))
                    .andDo(print())
                    .andDo(document("refresh-refresh-token-not-valid",
                            responseFields(
                                    fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
                                    fieldWithPath("errors").type(JsonFieldType.ARRAY).optional().description("발생한 에러 리스트"),
                                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("예외 발생 시간")
                            )));
        }

        private Claims getExpiredClaims(String accessToken) {
            try {
                Jwts.parserBuilder()
                        .setSigningKey(accessKey).build()
                        .parseClaimsJws(accessToken)
                        .getBody();
            } catch (ExpiredJwtException e) {
                return e.getClaims();
            }
            return null;
        }

        private String createExpiredAccessToken(Long userId, UserRole role) {
            return Jwts.builder()
                    .setSubject(userId.toString())
                    .claim(AUTHORITIES_KEY, role)
                    .setExpiration(new Date(new Date().getTime()))
                    .signWith(accessKey, SignatureAlgorithm.HS256)
                    .compact();
        }

        private String createRefreshToken(Long userId, UserRole role) {
            return Jwts.builder()
                    .setSubject(userId.toString())
                    .claim(AUTHORITIES_KEY, role)
                    .setExpiration(new Date(new Date().getTime() + REFRESH_TOKEN_EXP))
                    .signWith(refreshKey, SignatureAlgorithm.HS256)
                    .compact();
        }

        private String createAccessToken(Long userId, UserRole role) {
            return Jwts.builder()
                    .setSubject(userId.toString())
                    .claim(AUTHORITIES_KEY, role)
                    .setExpiration(new Date(new Date().getTime() + ACCESS_TOKEN_EXP))
                    .signWith(accessKey, SignatureAlgorithm.HS256)
                    .compact();
        }
    }
}