package com.itch.tutorias.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class DatabaseWebSecurity {

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Bean
    public UserDetailsManager users(DataSource dataSource, PasswordEncoder encoder) {
        JdbcUserDetailsManager dbUsers = new JdbcUserDetailsManager(dataSource);
        dbUsers.setUsersByUsernameQuery(
            "SELECT numero_identificacion AS username, contrasena_hash AS password, CASE WHEN estado = 'activo' THEN 1 ELSE 0 END AS enabled FROM usuario WHERE numero_identificacion = ?"
        );
        dbUsers.setAuthoritiesByUsernameQuery(
            "SELECT numero_identificacion AS username, UPPER(rol) AS authority FROM usuario WHERE numero_identificacion = ?"
        );

        return new UserDetailsManager() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                if (adminUsername.equals(username)) {
                    // Recrear el usuario cada vez para evitar que Spring Security 
                    // borre la contraseña (eraseCredentialsAfterAuthentication)
                    return User.builder()
                            .username(adminUsername)
                            .password("{noop}" + adminPassword)
                            .authorities("ADMIN")
                            .build();
                }
                return dbUsers.loadUserByUsername(username);
            }

            @Override
            public void createUser(UserDetails user) { dbUsers.createUser(user); }
            @Override
            public void updateUser(UserDetails user) { dbUsers.updateUser(user); }
            @Override
            public void deleteUser(String username) { dbUsers.deleteUser(username); }
            @Override
            public void changePassword(String oldPassword, String newPassword) { dbUsers.changePassword(oldPassword, newPassword); }
            @Override
            public boolean userExists(String username) {
                if (adminUsername.equals(username)) return true;
                return dbUsers.userExists(username); 
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/", "/login", "/registro", "/usuario/guardar", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
            .requestMatchers("/asignacion/ver/**").hasAnyAuthority("ADMIN", "TUTOR", "TUTORADO")
            .requestMatchers("/usuario/**", "/carrera/**", "/periodo/**", "/asignacion/**").hasAuthority("ADMIN")
            .requestMatchers("/pat/**", "/tutor/**", "/sesion/**").hasAnyAuthority("TUTOR", "ADMIN")
            .requestMatchers("/tutorado/**").hasAuthority("TUTORADO")
            .anyRequest().authenticated()
        );

        http.formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
        );

        return http.build();
    }

    // funciÃ³n para codificar contraseÃ±as utilizando el delegating password encoder de Spring Security
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
