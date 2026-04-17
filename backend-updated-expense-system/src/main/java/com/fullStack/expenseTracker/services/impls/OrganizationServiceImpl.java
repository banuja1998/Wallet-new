package com.fullStack.expenseTracker.services.impls;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.dto.reponses.OrganizationResponseDto;
import com.fullStack.expenseTracker.dto.requests.OrganizationRequestDto;
import com.fullStack.expenseTracker.enums.ApiResponseStatus;
import com.fullStack.expenseTracker.models.Organization;
import com.fullStack.expenseTracker.repository.OrganizationRepository;
import com.fullStack.expenseTracker.services.OrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OrganizationServiceImpl implements OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Override
    public ResponseEntity<ApiResponseDto<?>> createOrganization(OrganizationRequestDto requestDto) {
        if (organizationRepository.existsByNameIgnoreCase(requestDto.getName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(ApiResponseStatus.FAILED, HttpStatus.BAD_REQUEST,
                            "Organization name is already taken"));
        }
        if (organizationRepository.existsByEmailIgnoreCase(requestDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(ApiResponseStatus.FAILED, HttpStatus.BAD_REQUEST,
                            "Organization email is already taken"));
        }

        try {
            Organization organization = new Organization();
            organization.setName(requestDto.getName());
            organization.setEmail(requestDto.getEmail());
            organization.setPhone(requestDto.getPhone());
            organization.setAddress(requestDto.getAddress());
            organization.setActive(true);
            Organization saved = organizationRepository.save(organization);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponseDto<>(ApiResponseStatus.SUCCESS, HttpStatus.CREATED,
                            new OrganizationResponseDto(saved.getId(), saved.getName(), saved.getEmail(),
                                    saved.getPhone(), saved.getAddress(), saved.isActive())));
        } catch (Exception e) {
            log.error("Failed to create organization", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(ApiResponseStatus.FAILED, HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to create organization"));
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getAllOrganizations() {
        List<OrganizationResponseDto> list = organizationRepository.findAll().stream()
                .map(org -> new OrganizationResponseDto(org.getId(), org.getName(), org.getEmail(),
                        org.getPhone(), org.getAddress(), org.isActive()))
                .toList();
        return ResponseEntity.ok(new ApiResponseDto<>(ApiResponseStatus.SUCCESS, HttpStatus.OK, list));
    }

    @Override
    public Organization findById(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found with id " + id));
    }
}
