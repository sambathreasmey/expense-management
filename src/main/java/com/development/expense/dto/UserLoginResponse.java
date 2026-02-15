package com.development.expense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserLoginResponse(
        Long id,
        String username,
        @JsonProperty("full_name")
        String fullName
) {}
