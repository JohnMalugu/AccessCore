package com.jcmlabs.AccessCore.UserManagement.ServiceImplementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jcmlabs.AccessCore.UserManagement.Entities.UserAccountEntity;
import com.jcmlabs.AccessCore.UserManagement.Repositories.UserAccountRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserAccountServiceImplementation implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserAccountEntity> optionalUser = userAccountRepository.findFirstByUsername(username);
        if(optionalUser.isPresent()){
            UserAccountEntity user = optionalUser.get();
            String[] authorities = user.getAuthorities() != null  && !user.getAuthorities().isEmpty() ? getAuthorities(user) : new String[0];
            return User.builder().username(user.getUsername()).password(user.getPassword()).roles(authorities).build();
        } else {
            throw new UsernameNotFoundException("User with given username " + username + " Could not be found");
        }
        
    }
    
    private String[] getAuthorities(UserAccountEntity user) {
		List<String> authorities = new ArrayList<>();
		for (GrantedAuthority authority : user.getAuthorities()) {
			authorities.add(authority.getAuthority());
		}
        return authorities.toArray(new String[0]);
	}
}
