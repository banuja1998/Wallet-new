package com.fullStack.expenseTracker.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrganizationRequestDto {
    @NotBlank(message = "Organization name is required")
    private String name;

    @NotBlank(message = "Organization email is required")
    @Email(message = "Invalid organization email")
    private String email;

    private String phone;
    private String address;
}
