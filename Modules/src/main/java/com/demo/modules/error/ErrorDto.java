package com.demo.modules.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDto {
    private String code;
    private String msg;
    private String detail;
}
