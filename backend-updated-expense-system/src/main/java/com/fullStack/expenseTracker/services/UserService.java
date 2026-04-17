package com.fullStack.expenseTracker.services;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.exceptions.RoleNotFoundException;
import com.fullStack.expenseTracker.exceptions.UserNotFoundException;
import com.fullStack.expenseTracker.exceptions.UserServiceLogicException;
import com.fullStack.expenseTracker.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {

    ResponseEntity<ApiResponseDto<?>> getAllUsers(int pageNumber, int pageSize, String searchKey)
            throws RoleNotFoundException, UserServiceLogicException;

    ResponseEntity<ApiResponseDto<?>> getAllUsersByRole(int pageNumber, int pageSize, String searchKey, String role)
            throws RoleNotFoundException, UserServiceLogicException;

    ResponseEntity<ApiResponseDto<?>> enableOrDisableUser(long userId)
            throws UserNotFoundException, UserServiceLogicException;

    ResponseEntity<ApiResponseDto<?>> uploadProfileImg(String email, MultipartFile file)
            throws UserServiceLogicException, UserNotFoundException;

    ResponseEntity<ApiResponseDto<?>> getProfileImg(String email)
            throws UserNotFoundException, IOException, UserServiceLogicException;

    ResponseEntity<ApiResponseDto<?>> deleteProfileImg(String email)
            throws UserServiceLogicException, UserNotFoundException;

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User findByEmail(String email) throws UserNotFoundException;

    User findByUsername(String username) throws UserNotFoundException;
}