package com.jcmlabs.AccessCore.UserManagement.Entities;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.SoftDelete;

import com.jcmlabs.AccessCore.Utilities.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "roles")
@SoftDelete
public class RoleEntity extends BaseEntity{
    
    @Column()
    private String name;

    @Column
    private String displayName;

    @Column
    private String description;

    @ManyToAny(fetch = FetchType.EAGER)
    List<PermissionEntity> permissions = new ArrayList<>();
}
