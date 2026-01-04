package com.jcmlabs.AccessCore.Shared.Repository;

import com.jcmlabs.AccessCore.Shared.Entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long>, JpaSpecificationExecutor<NotificationEntity> {
    Optional<NotificationEntity> findFirstByUuid(String uuid);
}
