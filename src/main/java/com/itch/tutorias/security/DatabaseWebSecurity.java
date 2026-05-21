package com.itch.tutorias.security;



import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.provisioning.JdbcUserDetailsManager;

import org.springframework.security.provisioning.UserDetailsManager;

import org.springframework.security.web.SecurityFilterChain;



import javax.sql.DataSource;



@Configuration

@EnableWebSecurity

public class DatabaseWebSecurity {

    @Bean

    public UserDetailsManager users(DataSource dataSource) {

        JdbcUserDetailsManager dbUsers = new JdbcUserDetailsManager(dataSource);

        dbUsers.setUsersByUsernameQuery(

            "SELECT numero_identificacion AS username, contrasena_hash AS password, CASE WHEN estado = 'activo' THEN 1 ELSE 0 END AS enabled FROM usuario WHERE numero_identificacion = ?"

        );

        dbUsers.setAuthoritiesByUsernameQuery(

            "SELECT u.numero_identificacion AS username, UPPER(p.nombre) AS authority " +

            "FROM usuario u " +

            "JOIN usuario_perfil up ON u.id = up.usuario_id " +

            "JOIN perfil p ON up.perfil_id = p.id " +

            "WHERE u.numero_identificacion = ?"

        );



        return dbUsers;

    }



    @Bean

    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(authorize -> authorize

            .requestMatchers("/", "/login", "/registro", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
            // Búsquedas públicas: accesibles sin autenticación
            .requestMatchers("/busqueda/**").permitAll()
            .requestMatchers("/asignacion/ver/**").hasAnyAuthority("ADMINISTRADOR", "TUTOR", "TUTORADO")
            .requestMatchers("/usuario/**").hasAuthority("ADMINISTRADOR")
            .requestMatchers("/carrera/**", "/grupo/**", "/periodo/**", "/asignacion/**").hasAuthority("ADMINISTRADOR")
            // Actividades PAT generales: solo Admin DDA
            .requestMatchers("/pat/general/**").hasAuthority("ADMINISTRADOR")
            // Actividades PAT por carrera: Coordinador de Carrera
            .requestMatchers("/coordinador/**").hasAuthority("COORDINADOR_CARRERA")
            // Actividades PAT (vista legacy) y sesiones: Admin y Tutor
            .requestMatchers("/pat/**", "/sesion/**").hasAnyAuthority("ADMINISTRADOR", "TUTOR")
            .requestMatchers("/tutor/**").hasAuthority("TUTOR")
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


    //funcion para encriptar

    @Bean

    public PasswordEncoder passwordEncoder() {

        return PasswordEncoderFactories.createDelegatingPasswordEncoder();

    }



}

