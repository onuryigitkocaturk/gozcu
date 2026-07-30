package com.onuryigitkocaturk.query_monitor.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

// AuthenticationProviderConfig, UserDetailsService(kullanıcıyı nasıl bulacağaını bilen) ile
// PasswordEncoder(şifreyi nasıl karşılaştıracağını bilen) ikilisini birleştiren sınıfımız.
@Configuration
public class AuthenticationProviderConfig {

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                           PasswordEncoder passwordEncoder) {
        // DaoAuthenticationProvider, Spring Security'nin hazır sunduğu bir sınıf, kullanıcı adı + şifre ile
        // doğrulama yapmayı bilen bir mekanizma.
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    // Bu, bir üstteki metoddaki AuthenticationProvider (yani DaoAuthenticationProvider) bean'ini
    // parametre olarak alıp, onu bir ProviderManager'a sarıp AuthenticationManager olarak dışarı veren metod.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }
}
