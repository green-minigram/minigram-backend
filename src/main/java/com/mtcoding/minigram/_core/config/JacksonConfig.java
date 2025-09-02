package com.mtcoding.minigram._core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Java 8 날짜/시간 모듈 등록
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // LocalDateTime 직렬화 방식 정의
        javaTimeModule.addSerializer(
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))
        );

        // LocalDate 직렬화 방식 정의
        javaTimeModule.addSerializer(
                new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT))
        );

        objectMapper.registerModule(javaTimeModule);

        // 날짜를 타임스탬프가 아닌 문자열로 직렬화
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return objectMapper;
    }
}
