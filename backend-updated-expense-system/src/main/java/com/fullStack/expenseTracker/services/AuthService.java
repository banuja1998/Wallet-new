package com.fullStack.expenseTracker.services;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.dto.requests.CreateUserRequestDto;
import com.fullStack.expenseTracker.dto.requests.ResetPasswordRequestDto;
import com.fullStack.expenseTracker.dto.requests.SignUpRequestDto;
import com.fullStack.expenseTracker.exceptions.*;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<ApiResponseDto<?>> save(SignUpRequestDto signUpRequestDto)
            throws UserAlreadyExistsException, UserServiceLogicException;

    ResponseEntity<ApiResponseDto<?>> createAdminBySuperAdmin(CreateUserRequestDto requestDto)
            throws UserAlreadyExistsException, UserServiceLogicException, RoleNotFoundException;

    ResponseEntity<ApiResponseDto<?>> createUserByAdmin(CreateUserRequestDto requestDto)
            throws UserAlreadyExistsException, UserServiceLogicException, RoleNotFoundException;

    ResponseEntity<ApiResponseDto<?>> verifyRegistrationVerification(String code)
            throws UserVerificationFailedException;

    ResponseEntity<ApiResponseDto<?>> resendVerificationCode(String email)
            throws UserNotFoundException, UserServiceLogicException;

    ResponseEntity<ApiResponseDto<?>> verifyEmailAndSendForgotPasswordVerificationEmail(String email)
            throws UserServiceLogicException, UserNotFoundException;

    ResponseEntity<ApiResponseDto<?>> verifyForgotPasswordVerification(String code)
            throws UserVerificationFailedException, UserServiceLogicException;

    ResponseEntity<ApiResponseDto<?>> resetPassword(ResetPasswordRequestDto resetPasswordDto)
            throws UserNotFoundException, UserServiceLogicException;
}