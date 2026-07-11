package com.iyd.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class LoginDTO {
    @NotBlank(message = "手机号不能为空")
    private String phone;
    private String password;
    private String code;
    private String stage;
    private String platform;
}
