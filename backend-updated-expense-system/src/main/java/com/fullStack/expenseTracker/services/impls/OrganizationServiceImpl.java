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
                    .body(new ApiResponseDto<>(
                            ApiResponseStatus.FAILED,
                            HttpStatus.BAD_REQUEST,
                            "Organization name already exists"
                    ));
        }

        if (organizationRepository.existsByEmailIgnoreCase(requestDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(
                            ApiResponseStatus.FAILED,
                            HttpStatus.BAD_REQUEST,
                            "Organization email already exists"
                    ));
        }

        try {

            Organization organization = new Organization();

            organization.setName(requestDto.getName());
            organization.setEmail(requestDto.getEmail());
            organization.setPhone(requestDto.getPhone());
            organization.setAddress(requestDto.getAddress());
            organization.setActive(true);

            Organization savedOrganization = organizationRepository.save(organization);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.CREATED,
                            mapToDto(savedOrganization)
                    ));

        } catch (Exception e) {

            log.error("Create Organization Error : ", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(
                            ApiResponseStatus.FAILED,
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to create organization"
                    ));
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getAllOrganizations() {

        List<OrganizationResponseDto> organizations = organizationRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();

        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        ApiResponseStatus.SUCCESS,
                        HttpStatus.OK,
                        organizations
                )
        );
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getOrganizationById(Long id) {

        Organization organization = findById(id);

        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        ApiResponseStatus.SUCCESS,
                        HttpStatus.OK,
                        mapToDto(organization)
                )
        );
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> updateOrganization(Long id,
                                                                OrganizationRequestDto requestDto) {

        Organization organization = findById(id);

        if (organizationRepository.existsByNameIgnoreCaseAndIdNot(
                requestDto.getName(), id)) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(
                            ApiResponseStatus.FAILED,
                            HttpStatus.BAD_REQUEST,
                            "Organization name already exists"
                    ));
        }

        if (organizationRepository.existsByEmailIgnoreCaseAndIdNot(
                requestDto.getEmail(), id)) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(
                            ApiResponseStatus.FAILED,
                            HttpStatus.BAD_REQUEST,
                            "Organization email already exists"
                    ));
        }

        organization.setName(requestDto.getName());
        organization.setEmail(requestDto.getEmail());
        organization.setPhone(requestDto.getPhone());
        organization.setAddress(requestDto.getAddress());

        Organization updatedOrganization = organizationRepository.save(organization);

        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        ApiResponseStatus.SUCCESS,
                        HttpStatus.OK,
                        mapToDto(updatedOrganization)
                )
        );
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> deleteOrganization(Long id) {

        Organization organization = findById(id);

        organizationRepository.delete(organization);

        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        ApiResponseStatus.SUCCESS,
                        HttpStatus.OK,
                        "Organization deleted successfully"
                )
        );
    }

    @Override
    public Organization findById(Long id) {

        return organizationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Organization not found with ID : " + id));
    }

    private OrganizationResponseDto mapToDto(Organization organization) {

        return new OrganizationResponseDto(
                organization.getId(),
                organization.getName(),
                organization.getEmail(),
                organization.getPhone(),
                organization.getAddress(),
                organization.isActive()
        );
    }
}