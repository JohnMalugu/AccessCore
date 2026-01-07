package com.jcmlabs.AccessCore.UserManagement.Repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.jcmlabs.AccessCore.UserManagement.Entities.UserAccountEntity;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccountEntity,Long> {
    Optional<UserAccountEntity> findFirstByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.enabled = true")
    Optional<UserAccountEntity> findActiveByUsername(@Param("username") String username);
}