package com.fullStack.expenseTracker.controllers;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.dto.requests.CreateUserRequestDto;
import com.fullStack.expenseTracker.exceptions.RoleNotFoundException;
import com.fullStack.expenseTracker.exceptions.UserAlreadyExistsException;
import com.fullStack.expenseTracker.exceptions.UserNotFoundException;
import com.fullStack.expenseTracker.exceptions.UserServiceLogicException;
import com.fullStack.expenseTracker.services.AuthService;
import com.fullStack.expenseTracker.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/mywallet/management")
public class UserManagementController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/admins")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<?>> createAdmin(@RequestBody @Valid CreateUserRequestDto requestDto)
            throws UserAlreadyExistsException, UserServiceLogicException, RoleNotFoundException {
        return authService.createAdminBySuperAdmin(requestDto);
    }

    @PostMapping("/users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDto<?>> createUser(@RequestBody @Valid CreateUserRequestDto requestDto)
            throws UserAlreadyExistsException, UserServiceLogicException, RoleNotFoundException {
        return authService.createUserByAdmin(requestDto);
    }

    @GetMapping("/admins")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<?>> getAllAdmins(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String searchKey)
            throws RoleNotFoundException, UserServiceLogicException {
        return userService.getAllUsersByRole(pageNumber, pageSize, searchKey, "admin");
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<?>> getAllUsers(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String searchKey)
            throws RoleNotFoundException, UserServiceLogicException {
        return userService.getAllUsersByRole(pageNumber, pageSize, searchKey, "user");
    }

    @PutMapping("/toggle-status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<?>> toggleUserStatus(@RequestParam long userId)
            throws UserNotFoundException, UserServiceLogicException {
        return userService.enableOrDisableUser(userId);
    }
}