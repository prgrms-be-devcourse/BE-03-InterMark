package com.prgrms.be.intermark.config;

import com.prgrms.be.intermark.auth.CustomOauth2UserService;
import com.prgrms.be.intermark.auth.OAuth2AuthenticationSuccessHandler;
import com.prgrms.be.intermark.auth.TokenProvider;
import com.prgrms.be.intermark.domain.user.UserRole;
import com.prgrms.be.intermark.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
    @Value("${jwt.secret.access}")
    private String accessSecret;
    @Value("${jwt.secret.refresh}")
    private String refreshSecret;

    private final CustomOauth2UserService customOauth2UserService;
    private final UserService userService;
    private final TokenProvider tokenProvider;
    public SpringSecurityConfig(CustomOauth2UserService customOauth2UserService, UserService userService, TokenProvider tokenProvider) {
        this.customOauth2UserService = customOauth2UserService;
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .headers()
                    .frameOptions().disable()
                .and()
                    .csrf().disable()
                    .formLogin().disable()
                    .httpBasic().disable()
                    .authorizeRequests()
                    .antMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**").permitAll()
                    .antMatchers("/api/**").hasAnyAuthority(UserRole.USER.getKey())
                    .antMatchers("/api/**/admin/**").hasAnyAuthority(UserRole.ADMIN.getKey())
                    .anyRequest().authenticated()
                .and()
                    .logout()
                    .logoutSuccessUrl("/")
                .and()
                    .oauth2Login()
                    .authorizationEndpoint()
                    .baseUri("/oauth2/authorization") //로그인페이지를 받기위한 서버의 엔드포인트 설정
                .and()
                    .redirectionEndpoint()
                    .baseUri("/*/oauth2/code/*")
                .and()
                    .userInfoEndpoint()
                    .userService(customOauth2UserService)
                .and()
                    .successHandler(oAuth2AuthenticationSuccessHandler(userService));
        return httpSecurity.build();
    }

    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler(UserService userService) {
        return new OAuth2AuthenticationSuccessHandler(userService,tokenProvider);
    }
}
