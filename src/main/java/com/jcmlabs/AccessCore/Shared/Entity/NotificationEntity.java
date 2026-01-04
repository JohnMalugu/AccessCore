package com.jcmlabs.AccessCore.Shared.Entity;

import com.jcmlabs.AccessCore.Utilities.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="notification")
@SoftDelete()
public class NotificationEntity extends BaseEntity {

}
