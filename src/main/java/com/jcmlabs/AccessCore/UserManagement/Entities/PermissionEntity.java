package com.jcmlabs.AccessCore.UserManagement.Entities;


import com.jcmlabs.AccessCore.Utilities.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import org.hibernate.envers.Audited;

@Audited
@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "permissions")
@SQLDelete(sql = "UPDATE permissions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class PermissionEntity extends BaseEntity{

    @Column()
    private String name;

    @Column
    private String displayName;

    @Column
    private String groupName;

    @Column
    private String description;

    @Builder.Default
    @ManyToMany(mappedBy = "permissions")
    private Set<RoleEntity> roles = new HashSet<>();

}

