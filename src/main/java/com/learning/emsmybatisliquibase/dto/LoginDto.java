package com.learning.emsmybatisliquibase.dto;

import com.learning.emsmybatisliquibase.dto.pagination.RequestQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginDto {

    private String email;

    private String password;

    private RequestQuery requestQuery;

    public LoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
