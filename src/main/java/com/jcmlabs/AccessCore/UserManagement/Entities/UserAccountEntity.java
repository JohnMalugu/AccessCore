package com.jcmlabs.AccessCore.UserManagement.Entities;

import java.time.Instant;
import java.util.*;

import jakarta.persistence.*;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.envers.Audited;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.jcmlabs.AccessCore.Utilities.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Audited
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_accounts")
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

	@Column()
	private boolean accountNonExpired = true;

	@Column()
	private boolean credentialsNonExpired = true;

	@Column()
	private boolean accountNonLocked = true;

	@Column()
	private Instant lastLoginAt;

	@Column()
	private Instant passwordChangedAt;

	@Column()
	private String lastModifiedIp;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "user_roles",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	private Set<RoleEntity> roles = new HashSet<>();


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
