package com.slpixe.springshorturl;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse {

    private String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

}
