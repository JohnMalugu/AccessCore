package com.jcmlabs.AccessCore.UserManagement.Entities;

import java.util.HashSet;
import java.util.Set;

import com.jcmlabs.AccessCore.Utilities.BaseEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Audited
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
@SQLDelete(sql = "UPDATE roles SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class RoleEntity extends BaseEntity {

    @Column()
    private String name;

    @Column
    private String displayName;

    @Column
    private String description;

    @ManyToMany(mappedBy = "roles")
    private Set<UserAccountEntity> users = new HashSet<>();


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<PermissionEntity> permissions = new HashSet<>();

}
