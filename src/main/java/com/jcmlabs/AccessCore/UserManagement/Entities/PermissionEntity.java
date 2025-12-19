package com.jcmlabs.AccessCore.UserManagement.Entities;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SoftDelete;

import com.jcmlabs.AccessCore.Utilities.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "permissions")
@SoftDelete
public class PermissionEntity extends BaseEntity{

    @Column()
    private String name;

    @Column
    private String displayName;

    @Column
    private String groupName;

    @Column
    private String description;

    @ManyToMany(mappedBy = "permissions")
    private List<RoleEntity> roles = new ArrayList<>();
}
