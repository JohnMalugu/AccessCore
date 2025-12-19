package com.jcmlabs.AccessCore.UserManagement.Entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.SoftDelete;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.jcmlabs.AccessCore.Utilities.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_account")
@SoftDelete
public class UserAccountEntity extends BaseEntity{
    
    @Column()
    private String firstName;

    @Column()
    private String lastName;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<RoleEntity> roles;


    public Collection<? extends GrantedAuthority> getAuthorities() {
		if (roles == null || roles.isEmpty()) {
			return List.of();
		}
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		for (RoleEntity role : this.roles) {
			if (role.getPermissions() != null && !role.getPermissions().isEmpty()) {
				for (PermissionEntity permission : role.getPermissions()) {
					SimpleGrantedAuthority authority = new SimpleGrantedAuthority(permission.getName());
					authorities.add(authority);
				}
			}
		}
		return authorities;
	}
}
