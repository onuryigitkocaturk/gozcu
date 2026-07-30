package com.onuryigitkocaturk.query_monitor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// @Configuration: bu sınıf bean tanımlayacak, uygulama açılırken Spring @Configuration işaret
// li her sınıfı tarar ve içindeki @Bean metodlarını bir kez çalıştırır.
@Configuration
public class PasswordEncoderConfig {

    // döndürdüğü nesneyi IoC container içine koy, ihtiyaç olan her yere spring dağıtacak.
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt kullandığımız hash algoritması
        return new BCryptPasswordEncoder();
    }
}
