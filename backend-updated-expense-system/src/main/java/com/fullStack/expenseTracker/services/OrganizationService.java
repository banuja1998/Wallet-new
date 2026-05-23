package com.fullStack.expenseTracker.services;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.dto.requests.OrganizationRequestDto;
import com.fullStack.expenseTracker.models.Organization;
import org.springframework.http.ResponseEntity;

public interface OrganizationService {

    ResponseEntity<ApiResponseDto<?>> createOrganization(OrganizationRequestDto requestDto);

    ResponseEntity<ApiResponseDto<?>> getAllOrganizations();

    ResponseEntity<ApiResponseDto<?>> getOrganizationById(Long id);

    ResponseEntity<ApiResponseDto<?>> updateOrganization(Long id, OrganizationRequestDto requestDto);

    ResponseEntity<ApiResponseDto<?>> deleteOrganization(Long id);

    Organization findById(Long id);
}