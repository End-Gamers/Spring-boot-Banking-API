package com.microfinanceBank.Employee.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class LoginDto {

    @NotNull
    private String password;
    @Email(message = "올바른 이메일 주소를 입력해 주세요.")
    @NotNull
    private String email;

}
