package com.multishop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        String accept = Optional.ofNullable(request.getHeader("Accept")).orElse("");
        if (accept.contains("text/html")) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("status", 404);
            mav.addObject("error", "Not Found");
            mav.addObject("message", ex.getMessage());
            return mav;
        } else {
            Map<String, String> body = new HashMap<>();
            body.put("error", "Not Found");
            body.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }
    }
}
