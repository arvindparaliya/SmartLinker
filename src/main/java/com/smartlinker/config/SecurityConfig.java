package com.smartlinker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.smartlinker.services.impl.SecurityCustomUserDetailService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityCustomUserDetailService userDetailService;

    @Autowired
    private OAuthAuthenicationSuccessHandler handler;

    @Autowired
    private AuthFailtureHandler authFailtureHandler;

    // Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Dao Authentication Provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Authentication Manager bean
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                   .authenticationProvider(authenticationProvider())
                   .build();
    }

    // Security Filter Chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/user/**").authenticated()  
                .anyRequest().permitAll()                     
        );

        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/authenticate")
                .defaultSuccessUrl("/user/profile", true)
                .usernameParameter("email")    
                .passwordParameter("password")
                .failureHandler(authFailtureHandler)
                .permitAll()
        );

        http.oauth2Login(oauth -> oauth
                .loginPage("/login")
                .successHandler(handler)
        );

        http.logout(logout -> logout
                .logoutUrl("/do-logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
        );

        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}

    // ===========================================

//     @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     @Autowired
//     private SecurityCustomUserDetailService userDetailService;

//     @Autowired
//     private OAuthAuthenicationSuccessHandler handler;

//     @Autowired
//     private AuthFailtureHandler authFailtureHandler;

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     @Bean
//     public DaoAuthenticationProvider authenticationProvider() {
//         DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//         provider.setUserDetailsService(userDetailService);
//         provider.setPasswordEncoder(passwordEncoder());
//         return provider;
//     }

//     // Explicitly register the authenticationProvider
//     @Bean
//     public AuthenticationManager authManager(HttpSecurity http) throws Exception {
//         return http.getSharedObject(AuthenticationManagerBuilder.class)
//                 .authenticationProvider(authenticationProvider())
//                 .build();
//     }

//     @Bean
// public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {

//     http.authenticationManager(authManager); /

//     http.authorizeHttpRequests(authorize -> {
//         authorize.requestMatchers("/user/profile/**").authenticated();
//         authorize.anyRequest().permitAll();
//     });

//     http.formLogin(formLogin -> {
//         formLogin.loginPage("/login");
//         formLogin.loginProcessingUrl("/authenticate");
//         formLogin.defaultSuccessUrl("/user/profile", true);
//         formLogin.usernameParameter("email"); 
//         formLogin.passwordParameter("password");
//         formLogin.failureHandler(authFailtureHandler);
//     });

//     http.oauth2Login(oauth -> {
//         oauth.loginPage("/login");
//         oauth.successHandler(handler);
//     });

//     http.logout(logoutForm -> {
//         logoutForm.logoutUrl("/do-logout");
//         logoutForm.logoutSuccessUrl("/login?logout=true");
//     });

//     http.csrf(AbstractHttpConfigurer::disable);

//     return http.build();
// }
// }
    

    /*
     * Alternative in-memory authentication example (commented out)
     * This shows how to configure test users directly in code
     */
    // @Bean
    // public UserDetailsService userDetailsService() {
    //     UserDetails admin = User
    //         .withDefaultPasswordEncoder()
    //         .username("admin123")
    //         .password("admin123")
    //         .roles("ADMIN", "USER")
    //         .build();
    //
    //     UserDetails user = User
    //         .withDefaultPasswordEncoder()
    //         .username("user123")
    //         .password("password")
    //         .build();
    //
    //     return new InMemoryUserDetailsManager(admin, user);
    // }
// }