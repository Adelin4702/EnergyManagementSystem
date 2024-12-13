package ro.tuc.ds2020.config;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) {
        // For the sake of simplicity, we will create a mock user here. In a real scenario,
        // you would fetch the user from the database and validate the password and roles.
        return User.builder()
                .username(username)
                .password("{noop}password") // {noop} indicates no encoding for the password, change for real-world usage
                .roles("USER") // Set roles as needed
                .build();
    }
}
