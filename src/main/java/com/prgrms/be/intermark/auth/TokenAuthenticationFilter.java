package com.prgrms.be.intermark.auth;


import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static com.prgrms.be.intermark.util.HeaderUtil.getAccessToken;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String accessToken = getAccessToken(request);

        if (tokenProvider.validate(accessToken)) {
            Authentication authentication = tokenProvider.getAuthentication(accessToken);
            UserDetails userInToken = (UserDetails) authentication.getPrincipal();
            Long userIdInToken = Long.parseLong(userInToken.getUsername());
            UserRole userRoleInToken = UserRole.valueOf(userInToken.getAuthorities().toArray(new GrantedAuthority[0])[0].toString());
            Optional<User> optionalUserInDB = userRepository.findByIdAndIsDeletedFalse(userIdInToken);
            if(optionalUserInDB.isPresent()){
                User userInDB = optionalUserInDB.get();
                if(userInDB.getId() == userIdInToken && userInDB.getRole() == userRoleInToken){
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

        }

        filterChain.doFilter(request, response);
    }
}