package com.fullStack.expenseTracker.services.impls;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.enums.ApiResponseStatus;
import com.fullStack.expenseTracker.dto.requests.CreateUserRequestDto;
import com.fullStack.expenseTracker.dto.requests.ResetPasswordRequestDto;
import com.fullStack.expenseTracker.dto.requests.SignUpRequestDto;
import com.fullStack.expenseTracker.exceptions.*;
import com.fullStack.expenseTracker.factories.RoleFactory;
import com.fullStack.expenseTracker.models.Role;
import com.fullStack.expenseTracker.models.User;
import com.fullStack.expenseTracker.models.Organization;
import com.fullStack.expenseTracker.repository.UserRepository;
import com.fullStack.expenseTracker.services.OrganizationService;
import com.fullStack.expenseTracker.services.AuthService;
import com.fullStack.expenseTracker.services.NotificationService;
import com.fullStack.expenseTracker.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RoleFactory roleFactory;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OrganizationService organizationService;

    @Value("${app.verificationCodeExpirationMs}")
    private long EXPIRY_PERIOD;

    @Override
    public ResponseEntity<ApiResponseDto<?>> save(SignUpRequestDto signUpRequestDto)
            throws UserAlreadyExistsException, UserServiceLogicException {

        if (userService.existsByUsername(signUpRequestDto.getUserName())) {
            throw new UserAlreadyExistsException("Registration Failed: username is already taken!");
        }

        if (userService.existsByEmail(signUpRequestDto.getEmail())) {
            throw new UserAlreadyExistsException("Registration Failed: email is already taken!");
        }

        try {
            User user = createUser(signUpRequestDto);
            userRepository.save(user);
            notificationService.sendUserRegistrationVerificationEmail(user);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.CREATED,
                            "Verification email has been successfully sent!"
                    )
            );
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage(), e);
            throw new UserServiceLogicException("Registration failed: Something went wrong!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> createAdminBySuperAdmin(CreateUserRequestDto requestDto)
            throws UserAlreadyExistsException, UserServiceLogicException, RoleNotFoundException {
        return createManagedUser(requestDto, "admin", "Admin created successfully!");
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> createUserByAdmin(CreateUserRequestDto requestDto)
            throws UserAlreadyExistsException, UserServiceLogicException, RoleNotFoundException {
        return createManagedUser(requestDto, "user", "User created successfully!");
    }

    private ResponseEntity<ApiResponseDto<?>> createManagedUser(
            CreateUserRequestDto requestDto,
            String roleName,
            String successMessage
    ) throws UserAlreadyExistsException, UserServiceLogicException, RoleNotFoundException {

        if (userService.existsByUsername(requestDto.getUserName())) {
            throw new UserAlreadyExistsException("Creation Failed: username is already taken!");
        }

        if (userService.existsByEmail(requestDto.getEmail())) {
            throw new UserAlreadyExistsException("Creation Failed: email is already taken!");
        }

        try {
            User user = new User();
            user.setUsername(requestDto.getUserName());
            user.setEmail(requestDto.getEmail());
            user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
            user.setEnabled(true);
            user.setVerificationCode(null);
            user.setVerificationCodeExpiryTime(null);
            user.setRoles(Set.of(roleFactory.getInstance(roleName)));

            if ("admin".equalsIgnoreCase(roleName)) {

                if (requestDto.getOrganizationId() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                            new ApiResponseDto<>(
                                    ApiResponseStatus.FAILED,
                                    HttpStatus.BAD_REQUEST,
                                    "Organization id is required when creating an admin"
                            )
                    );
                }

                Organization organization = organizationService.findById(requestDto.getOrganizationId());

                if (organization == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                            new ApiResponseDto<>(
                                    ApiResponseStatus.FAILED,
                                    HttpStatus.BAD_REQUEST,
                                    "Organization not found"
                            )
                    );
                }

                user.setOrganization(organization);

            } else if ("user".equalsIgnoreCase(roleName)) {

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication == null || authentication.getName() == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                            new ApiResponseDto<>(
                                    ApiResponseStatus.FAILED,
                                    HttpStatus.UNAUTHORIZED,
                                    "Authenticated admin not found"
                            )
                    );
                }

                String loggedInValue = authentication.getName();
                log.info("Creating managed user by authenticated value: {}", loggedInValue);

                User admin = userRepository.findByUsername(loggedInValue).orElse(null);

                if (admin == null) {
                    admin = userRepository.findByEmail(loggedInValue).orElse(null);
                }

                if (admin == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                            new ApiResponseDto<>(
                                    ApiResponseStatus.FAILED,
                                    HttpStatus.UNAUTHORIZED,
                                    "Admin account not found for authenticated value: " + loggedInValue
                            )
                    );
                }

                boolean isAdmin = admin.getRoles() != null &&
                        admin.getRoles().stream().anyMatch(role -> "ROLE_ADMIN".equals(role.getName().name()));

                if (!isAdmin) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                            new ApiResponseDto<>(
                                    ApiResponseStatus.FAILED,
                                    HttpStatus.FORBIDDEN,
                                    "Only admins can create users"
                            )
                    );
                }

                if (admin.getOrganization() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                            new ApiResponseDto<>(
                                    ApiResponseStatus.FAILED,
                                    HttpStatus.BAD_REQUEST,
                                    "Admin must belong to an organization before creating users"
                            )
                    );
                }

                user.setOrganization(admin.getOrganization());
            }

            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.CREATED,
                            successMessage
                    )
            );

        } catch (RoleNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Managed user creation failed: {}", e.getMessage(), e);
            throw new UserServiceLogicException("Failed to create account: Try again later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> verifyRegistrationVerification(String code)
            throws UserVerificationFailedException {

        User user = userRepository.findByVerificationCode(code);

        if (user == null || user.isEnabled()) {
            throw new UserVerificationFailedException("Verification failed: invalid verification code!");
        }

        long currentTimeInMs = System.currentTimeMillis();
        long codeExpiryTimeInMillis = user.getVerificationCodeExpiryTime().getTime();

        if (currentTimeInMs > codeExpiryTimeInMillis) {
            throw new UserVerificationFailedException("Verification failed: expired verification code!");
        }

        user.setVerificationCode(null);
        user.setVerificationCodeExpiryTime(null);
        user.setEnabled(true);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                new ApiResponseDto<>(
                        ApiResponseStatus.SUCCESS,
                        HttpStatus.ACCEPTED,
                        "Verification successful: User account has been successfully created!"
                )
        );
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> resendVerificationCode(String email)
            throws UserNotFoundException, UserServiceLogicException {

        User user = userService.findByEmail(email);

        try {
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiryTime(calculateCodeExpirationTime());
            user.setEnabled(false);
            userRepository.save(user);
            notificationService.sendUserRegistrationVerificationEmail(user);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            "Verification email has been resent successfully!"
                    )
            );
        } catch (Exception e) {
            log.error("Registration verification failed: {}", e.getMessage(), e);
            throw new UserServiceLogicException("Registration failed: Something went wrong!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> verifyEmailAndSendForgotPasswordVerificationEmail(String email)
            throws UserServiceLogicException, UserNotFoundException {

        if (userService.existsByEmail(email)) {
            try {
                User user = userService.findByEmail(email);
                user.setVerificationCode(generateVerificationCode());
                user.setVerificationCodeExpiryTime(calculateCodeExpirationTime());
                userRepository.save(user);

                notificationService.sendForgotPasswordVerificationEmail(user);

                return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                        new ApiResponseDto<>(
                                ApiResponseStatus.SUCCESS,
                                HttpStatus.ACCEPTED,
                                "Verification successful: Email sent successfully!"
                        )
                );
            } catch (Exception e) {
                log.error("Reset password email verification failed: {}", e.getMessage(), e);
                throw new UserServiceLogicException("Verification failed: Something went wrong!");
            }
        }

        throw new UserNotFoundException("Verification failed: User not found with email " + email + "!");
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> verifyForgotPasswordVerification(String code)
            throws UserVerificationFailedException, UserServiceLogicException {

        User user = userRepository.findByVerificationCode(code);

        if (user == null) {
            throw new UserVerificationFailedException("Verification failed: invalid verification code!");
        }

        long currentTimeInMs = System.currentTimeMillis();
        long codeExpiryTimeInMillis = user.getVerificationCodeExpiryTime().getTime();

        if (currentTimeInMs > codeExpiryTimeInMillis) {
            throw new UserVerificationFailedException("Verification failed: expired verification code!");
        }

        try {
            user.setVerificationCode(null);
            user.setVerificationCodeExpiryTime(null);
            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.ACCEPTED,
                            "Verification successful: User account has been verified!"
                    )
            );
        } catch (Exception e) {
            log.error("Reset password verification failed: {}", e.getMessage(), e);
            throw new UserServiceLogicException("Verification failed: Something went wrong!" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> resetPassword(ResetPasswordRequestDto resetPasswordDto)
            throws UserNotFoundException, UserServiceLogicException {

        if (userService.existsByEmail(resetPasswordDto.getEmail())) {
            try {
                User user = userService.findByEmail(resetPasswordDto.getEmail());

                if (resetPasswordDto.getCurrentPassword() != null
                        && !resetPasswordDto.getCurrentPassword().isEmpty()) {

                    if (!passwordEncoder.matches(resetPasswordDto.getCurrentPassword(), user.getPassword())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new ApiResponseDto<>(
                                        ApiResponseStatus.FAILED,
                                        HttpStatus.BAD_REQUEST,
                                        "Reset password not successful: current password is incorrect!!"
                                )
                        );
                    }
                }

                user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
                userRepository.save(user);

                return ResponseEntity.status(HttpStatus.CREATED).body(
                        new ApiResponseDto<>(
                                ApiResponseStatus.SUCCESS,
                                HttpStatus.CREATED,
                                "Reset successful: Password has been successfully reset!"
                        )
                );
            } catch (Exception e) {
                log.error("Resetting password failed: {}", e.getMessage(), e);
                throw new UserServiceLogicException("Failed to reset your password: Try again later!");
            }
        }

        throw new UserNotFoundException("User not found with email " + resetPasswordDto.getEmail());
    }

    private User createUser(SignUpRequestDto signUpRequestDto) throws RoleNotFoundException {
        return new User(
                signUpRequestDto.getUserName(),
                signUpRequestDto.getEmail(),
                passwordEncoder.encode(signUpRequestDto.getPassword()),
                generateVerificationCode(),
                calculateCodeExpirationTime(),
                false,
                determineRoles(signUpRequestDto.getRoles())
        );
    }

    private String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 1000000));
    }

    private Date calculateCodeExpirationTime() {
        long currentTimeInMs = System.currentTimeMillis();
        return new Date(currentTimeInMs + EXPIRY_PERIOD);
    }

    private Set<Role> determineRoles(Set<String> strRoles) throws RoleNotFoundException {
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            roles.add(roleFactory.getInstance("user"));
        } else {
            for (String role : strRoles) {
                roles.add(roleFactory.getInstance(role));
            }
        }

        return roles;
    }
}