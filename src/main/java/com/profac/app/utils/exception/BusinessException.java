package com.profac.app.utils.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
public class BusinessException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 8703595096007076469L;
    private String title;

    public BusinessException(String title, String message) {
        super(message);
        this.title = title;
    }
    public BusinessException(String message) {
        super(message);
    }
}
