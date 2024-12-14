package com.profac.app.utils.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
public class BusinessBadRequestException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 8703595096007076469L;

    public BusinessBadRequestException(String title, String message) {
        super(title, message);
    }
    public BusinessBadRequestException(String message) {
        super(message);
    }
}
