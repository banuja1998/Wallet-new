package com.fullStack.expenseTracker.dto.reponses;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserResponseDto {

    private Long id;
    private String username;
    private String email;
    private boolean enabled;
    private Long organizationId;
    private String organizationName;
    private Double expense;
    private Double income;
    private Integer noOfTransactions;
}
