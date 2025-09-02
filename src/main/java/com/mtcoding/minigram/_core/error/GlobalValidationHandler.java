package com.mtcoding.minigram._core.error;

import com.mtcoding.minigram._core.error.ex.ExceptionApi400;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.List;

@Slf4j
@Aspect
@Component
public class GlobalValidationHandler {
    
    @Before("@annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void badRequestAdvice(JoinPoint jp) {
        Object[] args = jp.getArgs();
        for (Object arg : args) {

            if (arg instanceof Errors) {
                log.warn("유효성 검증 실패로 400 예외 처리 예정");
                Errors errors = (Errors) arg;

                if (errors.hasErrors()) {
                    List<FieldError> fErrors = errors.getFieldErrors();

                    for (FieldError fieldError : fErrors) {
                        log.warn("검증 실패 필드: {}, 메시지: {}", fieldError.getField(), fieldError.getDefaultMessage());
                        throw new ExceptionApi400(fieldError.getField() + ":" + fieldError.getDefaultMessage());
                    }
                }
            }
        }
    }
}