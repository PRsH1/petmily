package com.pet.petmily.user.handler;

import com.pet.petmily.user.service.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final LoginAttemptService loginAttemptService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");

        String email = request.getParameter("email");

        if (exception instanceof LockedException) {
            response.getWriter().write(exception.getMessage());
        } else if (exception instanceof DisabledException) {
            response.getWriter().write(exception.getMessage());
        } else {
            if (email != null && !email.isBlank()) {
                loginAttemptService.loginFailed(email);
            }
            response.getWriter().write("로그인 실패! 이메일이나 비밀번호를 확인해주세요.");
        }
        log.info("로그인 실패: {}", exception.getMessage());
    }
}
