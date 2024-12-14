package com.profac.app.utils.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
public class BusinessNotFoundException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 8703595096007076469L;

    public BusinessNotFoundException(String title, String message) {
        super(title, message);
    }
}
