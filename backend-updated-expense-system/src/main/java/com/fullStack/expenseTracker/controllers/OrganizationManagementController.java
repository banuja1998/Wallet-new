package com.fullStack.expenseTracker.controllers;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.dto.requests.OrganizationRequestDto;
import com.fullStack.expenseTracker.services.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/mywallet/management/organizations")
public class OrganizationManagementController {

    @Autowired
    private OrganizationService organizationService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<?>> createOrganization(@RequestBody @Valid OrganizationRequestDto requestDto) {
        return organizationService.createOrganization(requestDto);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<?>> getAllOrganizations() {
        return organizationService.getAllOrganizations();
    }
}
