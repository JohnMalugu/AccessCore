package com.jcmlabs.AccessCore.Shared.Entity;

import com.jcmlabs.AccessCore.Shared.Enums.NotificationStatus;
import com.jcmlabs.AccessCore.Shared.Enums.NotificationType;
import com.jcmlabs.AccessCore.Utilities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.io.Serializable;

import jakarta.persistence.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "notification")
@SoftDelete
public class NotificationEntity extends BaseEntity implements Serializable {

    @Column(length = 150)
    private String subject;

    @Column(length = 320)
    private String emailTo;

    @Column(length = 20)
    private String phoneTo;

    @Column(nullable = false, length = 120)
    private String fullName;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType notificationType;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String remarks;
}
