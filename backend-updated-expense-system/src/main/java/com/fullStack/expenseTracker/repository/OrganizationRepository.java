package com.fullStack.expenseTracker.repository;

import com.fullStack.expenseTracker.models.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByEmailIgnoreCase(String email);
}
