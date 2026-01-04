package com.jcmlabs.AccessCore.Shared.Repository;

import com.jcmlabs.AccessCore.Shared.Entity.RabbitmqMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RabbitmqMessageRepository extends JpaRepository<RabbitmqMessage,Long>, JpaSpecificationExecutor<RabbitmqMessage> {
}
