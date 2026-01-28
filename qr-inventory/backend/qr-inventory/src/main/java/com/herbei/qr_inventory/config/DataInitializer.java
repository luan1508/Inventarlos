package com.herbei.qr_inventory.config;

import com.herbei.qr_inventory.model.Role;
import com.herbei.qr_inventory.model.User;
import com.herbei.qr_inventory.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer 
{
    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) 
    {
        return args -> {
            // Prüfe ob bereits User existieren
            if (userRepository.count() == 0) 
            {
                // Admin-User erstellen
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRoles(Set.of(Role.ROLE_ADMIN));
                admin.setEnabled(true);
                userRepository.save(admin);
                
                System.out.println("✓ Admin-User erstellt: username=admin, password=admin123");

                // Normaler User erstellen
                User user = new User();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setRoles(Set.of(Role.ROLE_USER));
                user.setEnabled(true);
                userRepository.save(user);
                
                System.out.println("✓ Standard-User erstellt: username=user, password=user123");
            }
        };
    }
}
