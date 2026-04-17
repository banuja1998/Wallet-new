package com.fullStack.expenseTracker.security;

import com.fullStack.expenseTracker.models.User;
import com.fullStack.expenseTracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        // Try to find by email first
        User user = userRepository.findByEmail(login)
                .or(() -> userRepository.findByUsername(login)) // fallback to username
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User Not Found with email or username: " + login));

        return UserDetailsImpl.build(user);
    }
}
