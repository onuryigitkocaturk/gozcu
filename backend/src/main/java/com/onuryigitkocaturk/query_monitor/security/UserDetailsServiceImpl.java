package com.onuryigitkocaturk.query_monitor.security;

import com.onuryigitkocaturk.query_monitor.model.User;
import com.onuryigitkocaturk.query_monitor.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// görevi kullanıcı adı verilince o kullanıcıyı veritabanından bulup Spring Security'nin anlayacağı hale getirmektir.,
// @Service ile @Component aslında aynı, @Service o bean'in service katmanına ait olduğunu belirtiyor.
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // UserDetails, Spring Security'den gelen bir interface.
    // UserDetails = kural, şablon
    // UserDetailsImpl = User entity'min bu kurala uydurulmuş hali.

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new UserDetailsImpl(user);
    }
}
