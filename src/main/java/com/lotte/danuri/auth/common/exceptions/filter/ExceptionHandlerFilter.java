package com.lotte.danuri.auth.common.exceptions.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lotte.danuri.auth.common.exceptions.ErrorResponse;
import com.lotte.danuri.auth.common.exceptions.code.CommonErrorCode;
import com.lotte.danuri.auth.common.exceptions.exception.WrongLoginInfoException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper mapper;

    public ExceptionHandlerFilter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (WrongLoginInfoException e) {
            log.error("WrongLoginInfoException", e);
            setErrorResponse(CommonErrorCode.BAD_REQUEST_LOGIN.getHttpStatus(), response, e);
        }
    }

    public void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable ex) {

        response.setStatus(status.value());
        response.setContentType("application/json");
        ErrorResponse errorResponse = new ErrorResponse(CommonErrorCode.BAD_REQUEST_LOGIN);

        try {
            String result = mapper.writeValueAsString(errorResponse);
            log.info(result);
            response.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
}
