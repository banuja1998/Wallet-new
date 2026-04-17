package com.fullStack.expenseTracker.dto.reponses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrganizationResponseDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private boolean active;
}
