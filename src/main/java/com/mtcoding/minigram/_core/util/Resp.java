package com.mtcoding.minigram._core.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@AllArgsConstructor
@Data
public class Resp<T> {
    private Integer status;
    private String msg;
    private T body;

    public static <B> ResponseEntity<Resp<B>> ok(B body) {
        Resp<B> resp = new Resp<>(200, "성공", body);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    public static ResponseEntity<Resp<?>> fail(HttpStatus status, String msg) {
        Resp<?> resp = new Resp<>(status.value(), msg, null);
        return new ResponseEntity<>(resp, status);
    }

    public static <B> ResponseEntity<Resp<B>> okAndHeader(B body, String key, String value) {
        MultiValueMap headers = new LinkedMultiValueMap();
        headers.add(key, value);
        Resp<B> resp = new Resp<>(200, "성공", body);
        return new ResponseEntity<>(resp, headers, HttpStatus.OK);
    }

    public static Resp<?> fail(Integer status, String msg) {
        Resp<?> resp = new Resp<>(status, msg, null);
        return resp;
    }
}