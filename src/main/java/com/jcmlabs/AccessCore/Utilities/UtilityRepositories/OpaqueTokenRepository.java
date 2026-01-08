package com.jcmlabs.AccessCore.Utilities.UtilityRepositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jcmlabs.AccessCore.Utilities.ConfigurationUtilities.TokenType;
import com.jcmlabs.AccessCore.Utilities.UtilityEntities.OpaqueTokenEntity;

public interface OpaqueTokenRepository extends JpaRepository<OpaqueTokenEntity, String> {

    Optional<OpaqueTokenEntity> findFirstByTokenValueAndActiveTrue(String tokenValue);

    Optional<OpaqueTokenEntity> findFirstByUsernameAndTokenTypeAndActiveTrue(String username,TokenType tokenType);

    List<OpaqueTokenEntity> findAllByUsernameAndActiveTrue(String username);

    List<OpaqueTokenEntity> findAllByUsernameAndTokenTypeAndActiveTrue(String username, TokenType tokenType);
}
