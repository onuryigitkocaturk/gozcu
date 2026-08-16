package com.onuryigitkocaturk.query_monitor.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

// İki bean var: authenticationProvider kullanıcıyı bulup şifreyi karşılaştıran gerçek işi yapıyor;
// authenticationManager ise bunu sarıp UserController'ın kullandığı AuthenticationManager interface'i olarak dışarı veriyor.
// İki katman olmasının sebebi Spring Security'nin tasarımı — birden fazla provider olabilir,
// manager bunları yönetip isteği ilgili provider'a devreder.
@Configuration
public class AuthenticationProviderConfig {

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                           PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }
}
