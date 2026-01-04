package com.jcmlabs.AccessCore.Shared.Entity;

import com.jcmlabs.AccessCore.Utilities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rabbitmq_message")
public class RabbitmqMessage extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "payload", columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Column(name = "is_sent", columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isSent = Boolean.TRUE;

    @Column(name = "sent_time", nullable = false)
    @JdbcTypeCode(java.sql.Types.TIMESTAMP_WITH_TIMEZONE)
    private Instant sentTime = Instant.now();

    @Column(name = "is_acknowledged", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isAcknowledged = Boolean.FALSE;

    @Column(name = "acknowledged_time")
    @JdbcTypeCode(java.sql.Types.TIMESTAMP_WITH_TIMEZONE)
    private Instant acknowledgedTime;

    @Column(name = "error", columnDefinition = "TEXT")
    private String error;

    @Column(name = "number_of_retries")
    private Integer numberOfRetries = 0;
}
