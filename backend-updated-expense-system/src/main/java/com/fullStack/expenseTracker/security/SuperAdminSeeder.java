package com.fullStack.expenseTracker.security;

import com.fullStack.expenseTracker.enums.ERole;
import com.fullStack.expenseTracker.models.Role;
import com.fullStack.expenseTracker.models.User;
import com.fullStack.expenseTracker.repository.RoleRepository;
import com.fullStack.expenseTracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SuperAdminSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    @Transactional   // ✅ IMPORTANT FIX
    public void run(String... args) {

        boolean exists = userRepository.findAll().stream()
                .anyMatch(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName().equals(ERole.ROLE_SUPER_ADMIN)));

        if (!exists) {
            User superAdmin = new User();
            superAdmin.setUsername("superadmin");
            superAdmin.setEmail("superadmin@gmail.com");
            superAdmin.setPassword(encoder.encode("123456789"));
            superAdmin.setEnabled(true);

            Role role = roleRepository.findByName(ERole.ROLE_SUPER_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role not found"));

            superAdmin.setRoles(Set.of(role));

            userRepository.save(superAdmin);

            System.out.println("✅ Super Admin Created");
        }
    }
}