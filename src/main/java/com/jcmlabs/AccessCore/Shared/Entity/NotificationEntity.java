package com.jcmlabs.AccessCore.Shared.Entity;

import com.jcmlabs.AccessCore.Shared.Enums.NotificationStatus;
import com.jcmlabs.AccessCore.Shared.Enums.NotificationType;
import com.jcmlabs.AccessCore.Utilities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="notification")
@SoftDelete()
public class NotificationEntity extends BaseEntity implements Serializable {
    @Column()
    private String subject;

    @Column()
    private String emailTo;

    @Column()
    private String phoneTo;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String message;

    @Column(nullable=false)
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(nullable = false)
    private NotificationType notificationType;

    @Column()
    private String remarks;
}
